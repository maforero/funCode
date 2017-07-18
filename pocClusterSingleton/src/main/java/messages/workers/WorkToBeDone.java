package messages.workers;

import messages.master.Work;

import java.io.Serializable;

public class WorkToBeDone implements Serializable {

    private Work work;

    public WorkToBeDone(Work work) {
        this.work = work;
    }

    public Work getWork() {
        return work;
    }
}
