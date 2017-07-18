package messages.master;

import akka.actor.ActorRef;
import messages.workers.WorkerMessage;

import java.io.Serializable;

public class WorkerResquestsWork extends WorkerMessage implements Serializable {
    public WorkerResquestsWork(ActorRef worker) {
        super(worker);
    }
}
