package yaak.sim.schedule;

/**
* <p><code>Scheduler</code> prescribes basic scheduling services.</p>
* @author Jerry Smith
* @version $Id: Scheduler.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface Scheduler {
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

  void addAction(Action action, long tick);

  /**
  * <p>Removes the action (or actions, in the case of an
  * {@link yaak.sim.schedule.ActionGroup ActionGroup}) for execution
  * at the specified tick.</p>
  * @param tick The scheduler's tick.
  */

  Action removeAction(long tick);

  /**
  * <p>Starts the scheduler.</p>
  */

  void start();

  /**
  * <p>Restarts the scheduler.</p>
  */

  void restart();

  /**
  * <p>Stops the scheduler.</p>
  */

  void stop();

  /**
  * <p>Resumes the scheduler at the next tick following a pause.</p>
  */

  void resume();

  /**
  * <p>Pauses the scheduler following the current tick.</p>
  */

  void pause();
}
