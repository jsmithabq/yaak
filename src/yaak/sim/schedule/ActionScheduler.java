package yaak.sim.schedule;

import java.util.Hashtable;

/**
* <p><code>ActionScheduler</code> provides basic scheduling services.
* Use {@link yaak.sim.schedule.ActionScheduler#start start} to start
* the scheduler.</p>
* <p><code>ActionScheduler</code> provides its own execution protocol.
* Although, mechanically, the scheduler can be passed to a thread for
* execution by that thread, the scheduler will throw an exception,
* terminating the managing thread.  Use
* {@link yaak.sim.schedule.ActionScheduler#start start} to start
* the scheduler.</p>
* @author Jerry Smith
* @version $Id: ActionScheduler.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ActionScheduler extends Action implements Scheduler {
  private static final int DEFAULT_INTERVAL = 1;
  private Thread thread = null;
  private Hashtable actionsTable = new Hashtable();
  private long interval = DEFAULT_INTERVAL;
  private long highestTick = 0, tick = 0;
  private boolean paused = false, started = false;

  /**
  * <p>Creates a simple scheduler.</p>
  */

  public ActionScheduler() {
    this(DEFAULT_INTERVAL);
  }

  /**
  * <p>Creates a simple scheduler.  If the interval is <code>2</code>,
  * the scheduler executes actions at ticks 0, 2, 4, and so on.</p>
  * @param interval The step interval.
  * @see yaak.sim.schedule.ActionScheduler#addAction
  */

  public ActionScheduler(long interval) {
    super();
    this.interval = interval;
  }

  /**
  * <p>Adds the action (or actions, in the case of an
  * {@link yaak.sim.schedule.ActionGroup ActionGroup}) for execution
  * at the specified tick.  Note:  Adding an action replaces any
  * existing action at that tick.  To add multiple actions at a
  * specific tick, encapsulate them in an action group and add the
  * group.</p>
  * @param action The action or action group.
  * @param tick The scheduler's tick.
  */

  public void addAction(Action action, long tick) {
    if (tick < 0) {
      throw new ScheduleException("Tick must be nonnegative: " + tick + ".");
    }
    if (tick > highestTick) {
      highestTick = tick;
    }
    actionsTable.put(new Long(tick), action);
  }

  /**
  * <p>Removes the action (or actions, in the case of an
  * {@link yaak.sim.schedule.ActionGroup ActionGroup}) for execution
  * at the specified tick.</p>
  * @param tick The scheduler's tick.
  * @return The action, or <code>null</code>, if
  * no action is found.
  */

  public Action removeAction(long tick) {
    if (tick < 0) {
      throw new ScheduleException("Tick must be nonnegative: " + tick + ".");
    }
    if (tick > highestTick) {
      throw new IllegalArgumentException(
        "Specified tick is greater than the current maximum.");
    }
    return (Action) actionsTable.remove(new Long(tick)); // can be null
  }

  /**
  * <p>Starts the scheduler.</p>
  */

  public void start() {
    if (started) {
      throw new ScheduleException("The scheduler is already started.");
    }
    paused = false;
    started = true;
    thread = new Thread(this);
    thread.start();
  }

  /**
  * <p>Restarts the scheduler.</p>
  */

  public void restart() { // allow a fall through to start()
    stop();
    tick = 0;
    start();
  }

  /**
  * <p>Stops the scheduler.</p>
  */

  public void stop() {
    if (started && thread != null) {
      thread.interrupt();
      thread = null;
    }
  }

  /**
  * <p>Resumes the scheduler at the next tick following a pause.</p>
  */

  public void resume() {
    if (!paused) {
      throw new ScheduleException(
        "The scheduler can only be resumed after a pause.");
    }
    start();
  }

  /**
  * <p>Pauses the scheduler following the current tick.</p>
  */

  public void pause() {
    paused = true;
    stop();
  }

  /**
  * <p>Invokes the action scheduler's
  * {@link yaak.sim.schedule.ActionScheduler#execute execute} method.</p>
  */

  public void run() {
    if (!started) {
      throw new UnsupportedOperationException(
        "Use start() to start the scheduler.");
    }
    executeActions();
  }

  /**
  * <p>Executes the scheduler.  Not for public consumption.</p>
  */

  public void execute() {
    if (started) {
      throw new ScheduleException(
        "The scheduler is already started.");
    }
    start();
  }

  private void executeActions() {
    while (!paused && thread != null && tick <= highestTick) {
      if ((tick % interval) == 0) {
        Action action = (Action) actionsTable.get(new Long(tick));
        if (action != null) {
          action.execute();
        }
      }
      tick++;
    }
  }
}
