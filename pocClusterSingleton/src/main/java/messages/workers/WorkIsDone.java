package messages.workers;

import akka.actor.ActorRef;
import messages.master.Work;

public class WorkIsDone extends WorkerMessage {
    public WorkIsDone(ActorRef worker) {
        super(worker);
    }
    Work work;
    public WorkIsDone(ActorRef worker, Work work) {
        super(worker);
        this.work = work;
    }

    public Work getWork() {
        return work;
    }
}
