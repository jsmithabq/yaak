package yaak.schedule;

import yaak.util.Util;
import yaak.collection.Queue;
import yaak.core.YaakSystem;

import java.lang.reflect.Method;

//
// PScheduler uses a 'Queue', which (via 'ObjectPool') is already
// "heavily synchronized" w.r.t. the integrity of the queue.
// Hence, queue synchronization is necessary here only with
// respect to the scheduler's use/interpretation/processing of
// the queue.
//

/**
* <p><code>PScheduler</code> is a nonpriority-based scheduler that queues
* (runnable) tasks internally for automatic assignment to a thread
* pool.  Objects are transferred from the queue to the next available
* thread in first-in-first-out order.  The thread pool manages a fixed
* number of threads, which are either idle or running the assigned
* task(s).</p>
* <p>This scheduler implementation is designed for <i>course-grained</i>
* scheduling.  In its default configuration, it evaluates task completion
* once per second.  This interval is not adequate for scheduling highly
* volatile runnable tasks of short duration.  Moreover, it uses the
* default thread pool size, which (also) is not designed for a high
* number of tasks of short duration.</p>
* @author Jerry Smith
* @version $Id: PScheduler.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class PScheduler extends Thread {
  private static final int USE_THREAD_POOL_DEFAULT = -1;
  private Queue queue = new Queue();
  private ThreadPool tp = null;
  private long scheduleInterval = DEFAULT_SLEEP_INTERVAL;
  private long shutdownMonitorInterval = DEFAULT_SHUTDOWN_INTERVAL;
  private boolean quit = false;
  private Object lock = new Object();

  /**
  * <p>The default process-the-queue interval for the scheduler.</p>
  */

  public static final long DEFAULT_SLEEP_INTERVAL = 1000; // milliseconds

  /**
  * <p>The default shutdown monitoring interval for the scheduler.</p>
  */

  public static final long DEFAULT_SHUTDOWN_INTERVAL = 3000; // milliseconds

  /**
  * <p>Instantiates a <code>PScheduler</code> object using a thread pool
  * with the default number of threads.</p>
  * @see yaak.schedule.ThreadPool
  */

  public PScheduler() {
    this(USE_THREAD_POOL_DEFAULT);
  }

  /**
  * <p>Instantiates a <code>PScheduler</code> object using a thread pool
  * with the specified number of threads.</p>
  * @param numThreads The number of threads in the thread pool.
  * @see yaak.schedule.ThreadPool
  */

  public PScheduler(int numThreads) {
    tp = (numThreads == USE_THREAD_POOL_DEFAULT) ?
      new ThreadPool() : new ThreadPool(numThreads);
  }

  /**
  * <p>Sets the schedule interval for the scheduler thread, which awakes
  * periodically to assign waiting tasks from the queue to idle threads.</p>
  * @param scheduleInterval The schedule interval (in
  * milliseconds).
  */

  public final void setScheduleInterval(long scheduleInterval) {
    this.scheduleInterval = scheduleInterval;
  }

  /**
  * <p>Sets the interval for the shutdown monitoring phase, which monitors
  * graceful shutdown operations.</p>
  * @param shutdownMonitorInterval The monitoring interval
  * (in milliseconds).
  */

  public final void setShutdownMonitorInterval(long shutdownMonitorInterval) {
    this.shutdownMonitorInterval = shutdownMonitorInterval;
  }

  /**
  * <p>Determines whether or not the scheduler is available for
  * scheduling (adding) additional tasks.</p>
  * @return Whether or not the scheduler is available.
  */

  public final boolean isAvailable() {
    return !quit;
  }

  /**
  * <p>Implements the scheduling thread, which awakes periodically
  * to assign waiting tasks from the queue to idle threads.</p>
  */

  synchronized public final void run() {
    YaakSystem.getLogger().fine("thread pool capacity: " + tp.capacity());
    YaakSystem.getLogger().fine("thread pool size: " + tp.size());
    YaakSystem.getLogger().fine("started...");
    while (!quit || queue.size() > 0) {
      startScheduledTasks();
      try { wait(scheduleInterval); }
      catch (Exception ex) {
        // attempt to keep running here
      }
      YaakSystem.getLogger().finer("number objects in queue: " + queue.size());
    }
    YaakSystem.getLogger().fine("terminating scheduler process...");
  }

  //
  // startScheduledTasks() takes an object from the queue,
  // maps it to a thread from the pool, and removes the
  // object from the queue.
  //

  private void startScheduledTasks() {
    YaakSystem.getLogger().finer("check start queued tasks...");
    synchronized (queue) {
      while (queue.size() > 0 && tp.hasIdleThread()) {
        PRunnable r = (PRunnable) queue.peekNext();
        YaakSystem.getLogger().finer(
          "attempt to schedule task for worker object with ID " + getObjectID(r));
        try {
          tp.scheduleTask(r);
          queue.removeSafe(r);
        }
        catch (ThreadPoolException tpe) {
          YaakSystem.getLogger().fine(
            "cannot schedule task for worker object with ID " + getObjectID(r));
          tpe.printStackTrace();
        }
      }
    }
  }

  //
  // getObjectID() gets the (optional) object ID, stored in the
  // runnable object.
  //

  private String getObjectID(PRunnable r) {
    String id = "unknown";
    Class c = r.getClass();
    try {
      Method m = c.getDeclaredMethod("getID", null);
      id = (String) m.invoke(r, null);
    }
    catch (Exception e) {
      YaakSystem.getLogger().finer("exception:");
      e.printStackTrace();
    }
    return id;
  }

  /**
  * <p>Sets the runnable object--the object that implements the
  * scheduled work/task.</p>
  * @param runnable The object that implements
  * <code>PRunnable</code>.
  */

  public final void add(PRunnable runnable) {
    synchronized (lock) {
      if (quit) {
        YaakSystem.getLogger().fine("in shutdown phase: NOT adding a task...");
        return;
      }
    }
    YaakSystem.getLogger().fine("adding a task...");
    synchronized (queue) {
      queue.add(runnable); // this sync not important to logic of scheduler
    }
  }

  /**
  * <p>Begins shutdown operations for the scheduler.  This method
  * returns immediately; however, the scheduler continues to run until
  * the queue is empty.  Note that this method does <em>not</em>
  * shutdown the threads.</p>
  */

  public final void shutdown() {
    YaakSystem.getLogger().fine("begin shutdown phase...");
    synchronized (lock) {
      quit = true;
    }
  }

  /**
  * <p>Begins shutdown operations for the scheduler.  This method
  * returns immediately; however, the scheduler continues to run until
  * the queue is empty.</p>
  */

  public final void shutdownUponIdle() {
    YaakSystem.getLogger().fine("begin shutdown phase...");
    synchronized (lock) {
      quit = true;
    }
    tp.shutdownUponIdle();
  }

  /**
  * <p>Begins shutdown operations for the scheduler.  This method does
  * not return until the queue is empty and the thread pool has been
  * shutdown.</p>
  */

  public final void shutdownAndWait() {
    YaakSystem.getLogger().fine("begin shutdown phase...");
    synchronized (lock) {
      quit = true;
    }
    while (queue.size() > 0) {
      YaakSystem.getLogger().finer(
        "shutdown in progress: " + queue.size() + " task(s) remaining...");
      Util.sleepMilliseconds(shutdownMonitorInterval);
    }
    tp.shutdownUponIdle();
  }

  /**
  * <p>Clears out the queue and begins shutdown operations for the
  * scheduler.  This method returns immediately after clearing the
  * queue; note, however, that scheduled tasks continue to run until
  * completion.</p>
  */

  public final void deleteAndShutdown() {
    emptyTheQueue();
    YaakSystem.getLogger().fine("begin shutdown phase...");
    synchronized (lock) {
      quit = true;
    }
    tp.shutdownUponIdle();
  }

  /**
  * <p>Throws away worker operations scheduled in the queue and abruptly
  * whacks/interrupts each running worker process (thread)--for Microsoft
  * programmers only.</p>
  */

  public final void whack() {
    emptyTheQueue();
    synchronized (lock) {
      quit = true;
    }
    YaakSystem.getLogger().fine("begin whacking!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    tp.whack();
  }

  //
  // emptyTheQueue() removes every object from the queue:  poof!
  //

  private void emptyTheQueue() {
    YaakSystem.getLogger().fine("begin emptying queue...");
    synchronized (queue) {
      while (queue.size() > 0) {
        queue.remove();
      }
    }
  }
}
