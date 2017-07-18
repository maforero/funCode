package messages.workers;

import messages.master.Work;

/**
 * Created by maria.forero on 06/04/2017.
 */
public class WorkComplete {
    private Work work;

    public WorkComplete(Work work) {
        this.work = work;
    }

    public Work getWork() {
        return work;
    }
}
