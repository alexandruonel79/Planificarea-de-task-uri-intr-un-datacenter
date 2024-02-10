/* Implement this class. */

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MyHost extends Host {
    // current task ran
    Task currentTask;
    // queue that holds all the given tasks
    PriorityBlockingQueue<Task> priorityQueue;
    // used for shutting off the host
    volatile int hostIsRunning;
    // one means that a task is running
    // zero otherwise
    AtomicInteger somethingIsRunning;
    // global variable to save the time
    // the current task started running
    AtomicLong startTimeGlobal;

    public MyHost() {
        // initialise all variables
        hostIsRunning = 1;
        priorityQueue = new PriorityBlockingQueue<>(100, new RoundRobinComparator());
        somethingIsRunning = new AtomicInteger(0);
        startTimeGlobal = new AtomicLong(0);
    }

    @Override
    public void run() {
        // volatile variable for checking
        // host running status
        while (hostIsRunning != 0) {
            // while the priority queue is empty
            // wait
            synchronized (this) {
                try {
                    if (priorityQueue.isEmpty()) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // should not be empty here
            // checked anyway
            if (!priorityQueue.isEmpty()) {
                synchronized (this) {
                    // get the current task
                    currentTask = priorityQueue.poll();
                }
                // check task type
                if (!currentTask.isPreemptible()) {
                    // if it's not preeemptible
                    // run it until it finishes
                    while (currentTask.getLeft() > 0) {
                        // set the somethingIsRunning to 1
                        somethingIsRunning.set(1);
                        // get the start time
                        long start = System.currentTimeMillis();
                        // set the global start time
                        // used for least work left
                        startTimeGlobal.set(start);
                        try {
                            synchronized (this) {
                                wait(currentTask.getLeft());
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        long end = System.currentTimeMillis();
                        long passedTime = end - start;
                        // update the time remaining
                        currentTask.setLeft(currentTask.getLeft() - passedTime);
                    }
                    // set the task finished
                    currentTask.finish();
                    somethingIsRunning.set(0);
                    startTimeGlobal.set(0);
                } else {
                    // it is preemptible
                    try {
                        // get the start time
                        long start = System.currentTimeMillis();
                        somethingIsRunning.set(1);
                        startTimeGlobal.set(start);
                        // wait all the current task's time
                        synchronized (this) {
                            wait(currentTask.getLeft());
                        }
                        long end = System.currentTimeMillis();
                        long passedTime = end - start;
                        currentTask.setLeft(currentTask.getLeft() - passedTime);
                        somethingIsRunning.set(0);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // if there is no more time left running
                    // finish it
                    if (currentTask.getLeft() <= 0) {
                        currentTask.finish();
                        currentTask = null;
                        somethingIsRunning.set(0);
                        startTimeGlobal.set(0);
                    }
                    // if it's not finished
                    // put it back in the queue
                    if (currentTask != null) {
                        priorityQueue.add(currentTask);
                    }
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        // add and notify all
        synchronized (this) {
            priorityQueue.add(task);
            notifyAll();
        }
    }

    @Override
    public int getQueueSize() {
        // if a task is running
        // add +1 else send the queue's size
        synchronized (this) {
            if (somethingIsRunning.get() == 0) {
                return priorityQueue.size();
            } else {
                return priorityQueue.size() + 1;
            }
        }
    }

    @Override
    public long getWorkLeft() {
        synchronized (this) {
            long totalWorkLeft = 0;
            // go through all elements from the queue
            // add their time
            for (Task task : priorityQueue) {
                totalWorkLeft += task.getLeft();
            }
            // if a task is running
            // add it's time minus the spent time
            if (somethingIsRunning.get() == 1 && currentTask != null) {
                long spentTime = System.currentTimeMillis() - startTimeGlobal.get();
                totalWorkLeft += currentTask.getLeft() - spentTime;
            }
            return totalWorkLeft;
        }
    }

    @Override
    public void shutdown() {
        synchronized (this) {
            // set the variable and notifiyAll
            hostIsRunning = 0;
            notifyAll();
        }
    }
}
