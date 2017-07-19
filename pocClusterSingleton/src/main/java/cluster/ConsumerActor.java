package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import messages.master.Work;


import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class ConsumerActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private static ActorRef master;

    public ConsumerActor(){

    }

    public ConsumerActor(String masterPath) {
        this.master = getContext().actorFor(masterPath);
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        Match(message).of(
            Case(instanceOf(Work.class),
                work -> {
                    log.info("Adding work to backlog in master: " + master.path());
                    master.forward(work, getContext());
                    return work;
            }),
            Case($(), o -> {
                unhandled(o);
                return null;
            })
        );
    }



    public static ActorRef initialize(Integer port){
        Config config = ConfigFactory
                .parseString("consumer-actor.akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load()).getConfig("consumer-actor");
        final ActorSystem actorSystem = ActorSystem.create("master-actorsystem", config);

        ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(actorSystem).withRole("master");

       master = actorSystem.actorOf(ClusterSingletonProxy.props("/user/master", proxySettings),
                "masterProxy");

        return actorSystem.actorOf(Props.create(ConsumerActor.class),"Consumer");
    }

}
