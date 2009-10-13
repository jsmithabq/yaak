package yaak.schedule;

import yaak.core.YaakSystem;

/**
* <p><code>PooledThread</code> adds the capability for setting the
* current runnable object.</p>
* @author Jerry Smith
* @version $Id: PooledThread.java 6 2005-06-08 17:00:15Z jsmith $
* @see java.lang.Thread
*/

public class PooledThread extends Thread {
  private boolean idle = true;
  private boolean oopsInterrupted = false;
  private boolean quit = false;
  private Runnable runMe = new Runnable() {
    public void run() {
    }
  };
//  private ThreadPool tp = null; // not currently used

/********************************************************
  public PooledThread(ThreadPool tp, Runnable runMe) {
    this.tp = tp;
    this.runMe = runMe;
    YaakSystem.getLogger().fine("thread created.");
  }

  public PooledThread(ThreadPool tp) {
    this(tp, null);
  }

  public PooledThread() {
    this(null, null);
  }
********************************************************/

  /**
  * <p>Instantiates a thread with the runnable object set to
  * null.</p>
  * @see yaak.schedule.PooledThread#setRunnable
  */

  public PooledThread() {
  }

/********************************************************
  public void setThreadPool(ThreadPool tp) {
    if (this.tp != null) {
      return;
    }
    this.tp = tp;
    YaakSystem.getLogger().fine("setting thread pool object.");
  }
********************************************************/

  /**
  * <p>Associates a runnable object with the thread.</p>
  * @param runMe The runnable object that implements a
  * task.
  */

  public final void setRunnable(Runnable runMe) {
    if (!idle) {
      return;
    }
    if (runMe == null) {
      this.runMe = runMe;
      idle = true;
      return;
    }
    synchronized (this.runMe) {
      this.runMe = runMe;
      idle = false;
    }
    YaakSystem.getLogger().fine("(re)setting runnable object.");
  }

  /**
  * <p>Retrieves the runnable object.</p>
  * @return The runnable object associated with this
  * thread instance.
  */

  public final Runnable getRunnable() {
    synchronized (runMe) {
      return runMe;
    }
  }

  /**
  * <p>Retrieves the idle state.</p>
  * @return Whether or not the thread has completed
  * its task.
  */

  public final boolean isIdle() {
    return idle;
  }

  /**
  * <p>Retrieves the interrupted state.</p>
  * @return Whether or not the thread has been
  * interrupted.
  */

  public final boolean hasBeenInterrupted() {
    return oopsInterrupted;
  }

  /**
  * <p>Runs the scheduled task (the <code>Runnable</code> object's
  * <code>run()</code> method), setting the idle state before and
  * after the execution sequence.</p>  
  */

  synchronized public final void run() {
    while (!quit) {
      YaakSystem.getLogger().finer("begin wait()...");
      try { wait(); }
      catch (InterruptedException e) {
        oopsInterrupted = true;
        YaakSystem.getLogger().finer("thread interrupted...");
      }
      YaakSystem.getLogger().finer("end wait()...");
      if (!oopsInterrupted && runMe != null) { // set to null during shutdown
        synchronized (runMe) {
          runMe.run();
          idle = true;
        }
      }
    }
    YaakSystem.getLogger().fine("terminating...");
  }

  /**
  * <p>Shuts down the thread.</p>
  */

  synchronized public final void shutdown() {
    quit = true;
    runMe = null; // release object reference for GC
    notify();
  }
}
