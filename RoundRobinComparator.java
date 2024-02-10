import java.util.Comparator;

public class RoundRobinComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getPriority() > t2.getPriority()) {
            return -1;
        } else if (t1.getPriority() < t2.getPriority()) {
            return 1;
        } else {
            if(t1.getStart() < t2.getStart()){
                return -1;
            } else if (t1.getStart() > t2.getStart()) {
                return 1;
            }else{
                return 0;
            }
        }
    }
}
