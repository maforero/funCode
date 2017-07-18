package cluster;

import akka.actor.ActorRef;
import messages.master.Work;
import java.io.FileNotFoundException;

public class ClusterManager {


    public static void main(String[] args) throws FileNotFoundException {
        launchCluster();
        launchCluster2();
    }

    public static void launchCluster(){
        ActorRef consumer = ConsumerActor.initialize(2552,"akka.tcp://master-actorsystem@127.0.0.1:2553/user/masterProxy");

        ActorRef masterRef = Master.initialize(2553);
        Worker.initialize(2554, masterRef);
        Worker.initialize(2555, masterRef);

        for(int i=0; i<5; i++){
            consumer.tell(new Work("Work "+i), ActorRef.noSender());
        }

    }


    public static void launchCluster2(){
        ActorRef consumer = ConsumerActor.initialize(2562,"akka.tcp://master-actorsystem@127.0.0.1:2553/user/masterProxy");

        ActorRef masterRef = Master.initialize(2563);
        Worker.initialize(2564, masterRef);
        Worker.initialize(2565, masterRef);

        for(int i=0; i<5; i++){
            consumer.tell(new Work("Work "+i), ActorRef.noSender());
        }

    }

}