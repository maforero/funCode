package messages.workers;

import akka.actor.ActorRef;

import java.io.Serializable;

public class WorkIsReady extends WorkerMessage implements Serializable {
    public WorkIsReady(ActorRef worker) {
        super(worker);
    }
}
