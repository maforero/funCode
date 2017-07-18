package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.dispatch.Mapper;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * Created by maria.forero on 18/07/2017.
 */
public class ClusterKiller {

    public static void main(String[] args){
     killMaster();
    }

    public static void killMaster(){

        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2580).withFallback(ConfigFactory.load());

        final ActorSystem system = ActorSystem.create("ReceiverSys",config);

        String masterPath = "akka.tcp://master-actorsystem@127.0.0.1:2553/user/masterProxy";
        ActorSelection selection = system.actorSelection(masterPath);
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        Future<ActorRef> actorRefFuture = selection.resolveOne(timeout);

        ExecutionContext ec = system.dispatcher();

        actorRefFuture.flatMap(new Mapper<ActorRef, Future<Object>>() {
            @Override
            public Future<Object> apply(ActorRef actorRef) {
                System.out.println("killing Master: "+ actorRef);
                return Patterns.ask(actorRef, PoisonPill.getInstance(), timeout);
            }
        },ec);
    }
}
