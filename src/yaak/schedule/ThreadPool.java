package yaak.schedule;

import yaak.core.YaakSystem;
import yaak.util.Util;

import java.util.Vector;

/**
* <p><code>ThreadPool</code> is a nonpriority-based thread  pool.  Objects
* can be assigned to idle threads from the pool.  The thread pool
* manages a <em>fixed</em> number of threads, which are either idle
* or running the assigned task(s).</p>
* @author Jerry Smith
* @version $Id: ThreadPool.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ThreadPool {
  private static final long SHUTDOWN_MONITOR_INTERVAL = 2000; // milliseconds
  private Vector threads = null;

  /**
  * <p>The default number of threads.</p>
  */

  public static final int DEFAULT_THREADS = 10;

  /**
  * <p>The maximum number of threads.</p>
  */

  public static final int MAX_THREADS = 30;

  /**
  * <p>Instantiates a <code>ThreadPool</code> object with the specified
  * number of threads.  Upon initialization, all threads are started
  * and placed in an idle state.  You can set the number of threads
  * to any value between 1 and the maximum allowable (inclusive).</p>
  * @see yaak.schedule.ThreadPool#DEFAULT_THREADS
  * @see yaak.schedule.ThreadPool#MAX_THREADS
  */

  public ThreadPool(int numThreads) {
    if (numThreads > 0 && numThreads <= MAX_THREADS) {
      init(numThreads);
    }
    else {
      YaakSystem.getLogger().fine(
        "requested maximum threads outside range [1," + MAX_THREADS + "].");
      init(DEFAULT_THREADS);
    }
  }

  /**
  * <p>Instantiates a <code>ThreadPool</code> object with the default
  * number of threads.</p>
  * @see yaak.schedule.ThreadPool#DEFAULT_THREADS
  */

  public ThreadPool() {
    this(DEFAULT_THREADS);
  }

  //
  // init() launches the specified number of threads, placing each
  // thread in a wait state.
  //

  private void init(int size) {
    threads = new Vector(size);
    for (int i = 0; i < size; i++) {
      launchAThread(i);
    }
  }

  /**
  * <p>The number of threads available for executing tasks.</p>
  * @return The thread pool size.
  */

  synchronized public final int size() {
    return (threads == null) ? -1 : threads.size();
  }

  /**
  * <p>The number of threads available for executing tasks.</p>
  * @return The thread pool capacity.
  */

  synchronized public final int capacity() {
    return (threads == null) ? -1 : threads.capacity();
  }

  /**
  * <p>Attempts to schedule (on a nonpriority basis) the task
  * for execution.  The task (runnable object) is associated with
  * the first available idle thread (if any), and the thread is
  * signaled (notified) for execution.</p>
  * @param runnable The object that implements
  * <code>Java.lang.Runnable</code>.
  * @throws ThreadPoolException The thread pool is
  * unavailable, possibly <code>null</code>.
  */

  synchronized public final void scheduleTask(Runnable runnable)
      throws ThreadPoolException {
    if (threads == null) {
      YaakSystem.getLogger().fine("thread pool unavailable...");
      throw new ThreadPoolException("Thread pool unavailable.");
    }
    int i = findIdleThread();
    if (i != -1) {
      YaakSystem.getLogger().finer("found idle thread at position " + i + ".");
      PooledThread pt = (PooledThread) threads.elementAt(i);
      pt.setRunnable(runnable);
      synchronized (pt) {
        YaakSystem.getLogger().finer("pt.notify()...");
        pt.notify();
      }
      YaakSystem.getLogger().fine("task scheduled for thread " + i);
    }
    else {
      throw new ThreadPoolException("Cannot schedule task.");
    }
  }

  /**
  * <p>Queries the thread pool for an idle thread.</p>
  * @return Whether or not there is an idle thread.
  */

  synchronized public final boolean hasIdleThread() {
    return threads != null && findIdleThread() != -1;
  }

  /**
  * <p>Monitors active threads and shuts down the thread pool, thread
  * by thread as they transition to an idle state after executing each
  * worker process.</p>
  */

  synchronized public final void shutdownUponIdle() {
    if (threads == null) {
      YaakSystem.getLogger().fine("thread pool unavailable...");
      return;
    }
    while (!threads.isEmpty()) {
      YaakSystem.getLogger().fine("pool size is now: " + threads.size());
      for (int i = 0; i < threads.size(); i++) {
        PooledThread pt = (PooledThread) threads.elementAt(i);
        if (pt.isIdle()) {
          pt.shutdown();
          threads.removeElementAt(i);
        }
      }
      try { wait(SHUTDOWN_MONITOR_INTERVAL); }
      catch (InterruptedException e) {
        // ignore interrupts -- in shutdown phase
      }
    }
    YaakSystem.getLogger().fine("all threads are now shutdown.");
  }

  /**
  * <p>Abruptly whacks/interrupts each thread in the pool--for
  * Microsoft programmers only.</p>
  */

  public final void whack() {
    if (threads == null) {
      YaakSystem.getLogger().fine("thread pool unavailable...");
      return;
    }
    YaakSystem.getLogger().fine("begin thread pool destruction...");
    for (int i = 0; i < threads.size(); i++) {
      PooledThread pt = (PooledThread) threads.elementAt(i);
      synchronized (pt) {
        pt.interrupt(); // what to do? -- only works if workers check
                        // for interruptions, e.g., isInterrupted()
        threads.removeElementAt(i);
      }
    }
    synchronized (threads) {
      threads = null;
    }
    Util.sleepSeconds(1);
    YaakSystem.getLogger().fine("RIP:  end of thread pool destruction.");
  }

  //
  // findIdleThread() scans the pool for an idle thread.
  //
  //

  private int findIdleThread() {
    for (int i = 0; i < threads.size(); i++) {
      PooledThread pt = (PooledThread) threads.elementAt(i);
      if (pt.hasBeenInterrupted()) {
        threads.removeElementAt(i);
        launchAThread(i);
      }
      if (pt.isIdle()) {
        return i;
      }
    }
    return -1;
  }

  //
  // launchThread() created a pooled thread and starts it.
  //

  private void launchAThread(int i) {
    PooledThread pt = new PooledThread();
    //
    // Synchronization is necessary if ThreadPool is enhanced
    // to support dynamic growth of number of threads in the
    // pool.  In this case, monitor/locking overhead is not
    // significant, so synchronize this operation anyway.
    //
    synchronized (pt) {
      threads.insertElementAt(pt, i);
      pt.start();
    }
  }
}
