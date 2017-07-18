package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.control.Option;
import messages.master.Work;
import messages.master.WorkerCreated;
import messages.master.WorkerResquestsWork;
import messages.workers.NoWorkToBeDone;
import messages.workers.WorkIsDone;
import messages.workers.WorkIsReady;
import messages.workers.WorkToBeDone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class Master extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private Cluster cluster = Cluster.get(getContext().system());
    private Map<ActorRef,Option<Tuple2<ActorRef,Work>>> workersMap = new HashMap<>();
    private List<Tuple2<ActorRef,Work>> workList = new LinkedList<>();


    @Override
    public void preStart() throws Exception {
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        Match(message).of(
                Case(instanceOf(WorkerCreated.class) , this::addWorker),
                Case(instanceOf(WorkerResquestsWork.class), this::assignWork),
                Case(instanceOf(WorkIsDone.class), this::markWorkAsDone),
                Case(instanceOf(Terminated.class), this::retryFailedWork),
                Case(instanceOf(Work.class), this::receiveConsumerBacklog),
                Case($(), o -> {
                    unhandled(o);
                    return null;
                })
        );
    }

    private Work receiveConsumerBacklog(Work work) {
        log.info("Queueing work = " + work.getDescription());
        workList.add(Tuple.of(sender(),work));
        notifyWorkers();
        return work;
    }

    private Terminated retryFailedWork(Terminated msg) {
        Option<Tuple2<ActorRef, Work>> failedWorkOption = workersMap.get(msg.actor());
        if(isWorkerBusyAndRegistered(msg, failedWorkOption)){
            log.error("This worker "+ msg.actor() + "failed while processing "+ failedWorkOption);
            Tuple2<ActorRef,Work> failedWork = failedWorkOption.get();
            self().tell(failedWork._2,failedWork._1);
            workersMap.remove(msg.actor());
        }
        return msg;
    }

    private boolean isWorkerBusyAndRegistered(Terminated msg, Option<Tuple2<ActorRef, Work>> workO) {
        return workersMap.containsKey(msg.actor()) && workO.isDefined();
    }

    private WorkIsDone markWorkAsDone(WorkIsDone msg) {
        if(!workersMap.containsKey(msg.getWorker()))
            log.error("This worker is unknown for the master: ", msg.getWorker());
        else {
            log.info("This worker just notified the work has been completed... ");
            workersMap.put(msg.getWorker(), Option.some(Tuple.of(sender(),null)));
            workList.removeIf(tuple-> tuple._2.compareTo(msg.getWork()) == 0);
        }
        return msg;
    }

    private WorkerResquestsWork assignWork(WorkerResquestsWork msg) {
        log.info("Worker requests work " + msg.getWorker());
        if(workersMap.containsKey(msg.getWorker())){
            if(workList.isEmpty()){
                msg.getWorker().tell(new NoWorkToBeDone(), ActorRef.noSender());
            }else if(isWorkerIdleAndRegistered(msg)){
                ArrayList<Option<Tuple2<ActorRef,Work>>> workAssigned = new ArrayList<>(workersMap.values());

                workList.stream().forEach( workInList -> {
                            boolean isAlreadyAssign = workAssigned.stream().anyMatch(workInProgress ->  workInList._2.compareTo(workInProgress.get()._2) == 0);
                            if(!isAlreadyAssign){
                                workersMap.put(msg.getWorker(), Option.some(workInList));
                                msg.getWorker().tell(new WorkToBeDone(workInList._2),workInList._1);
                            }
                        }
                );

            }
        }
        return msg;
    }

    private boolean isWorkerIdleAndRegistered(WorkerResquestsWork msg) {
        return workersMap.get(msg.getWorker()).get()._2 == null;
    }

    private WorkerCreated addWorker(WorkerCreated msg) {
        log.info("Worker registration");
        getContext().watch(msg.getWorker());
        workersMap.put(msg.getWorker(), Option.some(Tuple.of(sender(),null)));
        notifyWorkers();
        return msg;
    }

    private void notifyWorkers() {
        if(!workList.isEmpty()){
            workersMap.forEach(
                    (worker, tupleOption) -> tupleOption.forEach(
                            tuple -> {
                                if(tuple._2 == null){
                                    getContext().actorSelection(worker.path())
                                            .tell(new WorkIsReady(getSelf()), ActorRef.noSender());
                                }
                            }));
        }
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(getSelf());
    }


    public static ActorRef initialize(Integer port){

        Config config = ConfigFactory
                .parseString("master.akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load()).getConfig("master");

        final ActorSystem actorSystem = ActorSystem.create("master-actorsystem", config);

        final ClusterSingletonManagerSettings settings =
                ClusterSingletonManagerSettings.create(actorSystem).withRole("master");

        actorSystem.actorOf(
                ClusterSingletonManager.props(
                        Props.create(Master.class, () -> new Master()),
                        PoisonPill.getInstance(),
                        settings),
                "master");

        ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(actorSystem).withRole("master");

        return actorSystem.actorOf(ClusterSingletonProxy.props("/user/master", proxySettings),
                "masterProxy");

    }

}
