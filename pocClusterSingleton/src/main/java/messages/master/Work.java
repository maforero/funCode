package messages.master;

import java.io.Serializable;

public class Work implements Serializable, Comparable<Work>{

    private String description;

    public Work(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Work work) {
        if(work != null) {
            return description.compareTo(work.getDescription());
        }
        return -1;
    }
}
