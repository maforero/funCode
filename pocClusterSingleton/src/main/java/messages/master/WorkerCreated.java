package messages.master;

import akka.actor.ActorRef;
import messages.workers.WorkerMessage;

import java.io.Serializable;

public class WorkerCreated extends WorkerMessage implements Serializable{

    public WorkerCreated(ActorRef worker) {
        super(worker);
    }
}
