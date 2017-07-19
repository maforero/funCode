package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by maria.forero on 18/07/2017.
 */
public class ClusterKiller {

    public static void main(String[] args){
     killMasterAndStartAnotherWorker();
    }

    private static ActorRef getMasterProxy(){

        Config config = ConfigFactory
                .parseString("master.akka.remote.netty.tcp.port=" + 2580)
                .withFallback(ConfigFactory.load()).getConfig("master");

        final ActorSystem actorSystem = ActorSystem.create("master-actorsystem", config);

        ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(actorSystem).withRole("master");

        return actorSystem.actorOf(ClusterSingletonProxy.props("/user/master", proxySettings),
                "masterProxy");
    }

    public static void killMasterAndStartAnotherWorker(){
        ActorRef masterRef = getMasterProxy();
        masterRef.tell(PoisonPill.getInstance(),ActorRef.noSender());
        startAnotherWorker(masterRef);
    }

    public static void startAnotherWorker(ActorRef masterRef){
        Worker.initialize(2584, masterRef);
    }
}
