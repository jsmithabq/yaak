package yaak.schedule;

/**
* <p><code>ScheduledThread</code> extends <code>Thread</code>, adding
* the capability for tracking whether or not the task has started
* and/or finished.</p>
* @author Jerry Smith
* @version $Id: ScheduledThread.java 6 2005-06-08 17:00:15Z jsmith $
* @see java.lang.Thread
*/

public class ScheduledThread extends Thread {
  private boolean started = false;
  private boolean finished = false;
  private Runnable runMe;

  /**
  * <p>Instantiates a thread with the specified runnable object.</p>
  * @param runMe An object that implements
  * <code>java.lang.Runnable</code>.
  * @see java.lang.Runnable
  */

  public ScheduledThread(Runnable runMe) {
    this.runMe = runMe;
  }

  /**
  * <p>Determines whether or not the thread has started.</p>
  * @return Whether or not the thread has started.
  */

  public final boolean isStarted() {
    return started;
  }

  /**
  * <p>Determines whether or not the thread has finished.</p>
  * @return Whether or not the thread has finished.
  */

  public final boolean isFinished() {
    return finished;
  }

  /**
  * <p>Sets the started state and starts the thread.</p>
  */

  public final void startIt() {
    started = true;
    start();
  }

  /**
  * <p>Runs the runnable object.</p>
  */

  public final void run() {
    runMe.run();
    finished = true;
  }
}
