package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import messages.master.Work;
import messages.master.WorkerCreated;
import messages.master.WorkerResquestsWork;
import messages.workers.NoWorkToBeDone;
import messages.workers.WorkComplete;
import messages.workers.WorkIsDone;
import messages.workers.WorkIsReady;
import messages.workers.WorkToBeDone;

import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;
import static util.LoggerMessage.FEC20000;
import static util.LoggerMessage.FEC20001;
import static util.LoggerMessage.FEC20002;
import static util.LoggerMessage.FEC20003;
import static util.LoggerMessage.FEC20004;
import static util.LoggerMessage.FEC20005;


public class Worker extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Cluster cluster = Cluster.get(getContext().system());
    private ActorRef master;


    public Worker() {

    }
    public Worker(final ActorRef master) {
        this.master = master;
        initState();
    }


    public void initState(){
        getContext().become(processIdleMessageProtocol);
    }

    @Override
    public void preStart() throws Exception {
       log.info("Notifiying to this master: "+master.path());
        notifyWorkerIsAlive();
    }

    private void notifyWorkerIsAlive() {
        master.tell(new WorkerCreated(getSelf()),ActorRef.noSender());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        Match(message).of(
                Case($(), o -> {
                    unhandled(o);
                    return null;

                })
        );
    }

    public Procedure<Object> processWorkingMessageProtocol = new Procedure<Object>() {
        @Override
        public void apply(Object message) throws Exception {
            Match(message).of(
                Case(instanceOf(WorkIsReady.class), msg -> {log.info(FEC20000.toString());return msg;}),
                Case(instanceOf(NoWorkToBeDone.class), msg -> {log.info(FEC20001.toString());return msg;}),
                Case(instanceOf(WorkToBeDone.class), msg -> {log.error(FEC20000.toString()); return msg;}),
                Case(instanceOf(WorkComplete.class), this::notifyMasterWorkIsCompleted));
        }

        private WorkComplete notifyMasterWorkIsCompleted(WorkComplete msg) {
            log.info(FEC20005.toString(), msg.getWork().getDescription());
            master.tell(new WorkIsDone(getSelf(), msg.getWork()), getSelf());
            master.tell(new WorkerResquestsWork(getSelf()), getSelf());
            getContext().become(processIdleMessageProtocol);
            return msg;
        }
    };


    public Procedure<Object> processIdleMessageProtocol = new Procedure<Object>() {
        @Override
        public void apply(Object message) throws Exception {
            Match(message).of(
                Case(instanceOf(WorkIsReady.class), this::sendWorkRequest),
                Case(instanceOf(WorkToBeDone.class), this::executeWork),
                Case(instanceOf(NoWorkToBeDone.class), msg -> {log.info(FEC20001.toString());return msg;})
            );
        }

        private WorkToBeDone executeWork(WorkToBeDone msg) {
            log.info(FEC20002.toString(), msg.getWork().getDescription());
            doWork(getSelf(), msg.getWork());
            getContext().become(processWorkingMessageProtocol);
            return msg;
        }

        private WorkIsReady sendWorkRequest(WorkIsReady msg) {
            log.info(FEC20003.toString());
            master.tell(new WorkerResquestsWork(getSelf()), getSelf());
            return msg;
        }
    };

    private void doWork(ActorRef actorRef, Work work) {
        try {
            log.info( FEC20004.toString(), work.getDescription());
            //TODO: Add the content related with the worker process, in the case of FEP insert the query to css db and launch the result to the topic
            actorRef.tell(new WorkComplete(work), getSelf());
        } catch(Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(getSelf());
    }

    public static Props props(ActorRef masterPath) {
        return Props.create(Worker.class, masterPath);
    }

    public static ActorRef initialize(Integer port, ActorRef masterPath){
        Config config = ConfigFactory.parseString("worker.akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load()).getConfig("worker");

        final ActorSystem actorSystem = ActorSystem.create("master-actorsystem", config);

        return actorSystem.actorOf(Worker.props(masterPath),"Worker");
    }


}
