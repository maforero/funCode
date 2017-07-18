package messages.workers;

import akka.actor.ActorRef;

import java.io.Serializable;

public class WorkerMessage implements Serializable{

    public ActorRef getWorker() {
        return worker;
    }

    private ActorRef worker;

    public WorkerMessage(){

    }

    public WorkerMessage(ActorRef worker){
        this.worker = worker;
    }

}
