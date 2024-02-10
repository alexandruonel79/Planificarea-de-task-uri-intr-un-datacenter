/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {
    int lastId = 0;
    int numberOfHosts;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        numberOfHosts = hosts.size();
    }

    @Override
    public void addTask(Task task) {
        if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            this.hosts.get(lastId).addTask(task);
            int aux = lastId;
            // update using the formula
            lastId = ((aux + 1) % numberOfHosts);
        } else if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            int minQueueSize = hosts.get(0).getQueueSize();
            int smallQueueSizeHost = 0;
            // go through all the hosts
            for (int i = 0; i < numberOfHosts; i++) {
                // get the one with the min queue size
                if (hosts.get(i).getQueueSize() < minQueueSize) {
                    minQueueSize = hosts.get(i).getQueueSize();
                    smallQueueSizeHost = i;
                } else if (hosts.get(i).getQueueSize() == minQueueSize && i < smallQueueSizeHost) {
                    smallQueueSizeHost = i;
                }
            }
            // add to the min queue size host
            hosts.get(smallQueueSizeHost).addTask(task);
        } else if (algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            // same as shortest queue
            long minQueueWorkLeft = hosts.get(0).getWorkLeft();
            int pozitionLeastWork = 0;

            for (int i = 0; i < numberOfHosts; i++) {
                // approximate with 'granularity'
                float dif = Math.abs(hosts.get(i).getWorkLeft() - minQueueWorkLeft);
                if (dif < 10 && pozitionLeastWork > i) {
                    pozitionLeastWork = i;
                } else if (hosts.get(i).getWorkLeft() < minQueueWorkLeft) {
                    minQueueWorkLeft = hosts.get(i).getWorkLeft();
                    pozitionLeastWork = i;
                }
            }
            hosts.get(pozitionLeastWork).addTask(task);
        } else if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            // the easiest one
            if (task.getType() == TaskType.SHORT) {
                hosts.get(0).addTask(task);
            } else if (task.getType() == TaskType.MEDIUM) {
                hosts.get(1).addTask(task);
            } else if (task.getType() == TaskType.LONG) {
                hosts.get(2).addTask(task);
            }
        }
    }
}
