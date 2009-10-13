package yaak.sim.schedule;

/**
* <p><code>Action</code> encapsulates operations that are scheduled
* for execution by a scheduler.</p>
* @author Jerry Smith
* @version $Id: Action.java 6 2005-06-08 17:00:15Z jsmith $
* @see yaak.sim.schedule.Scheduler
*/

abstract public class Action implements Runnable {

  /**
  * <p>Creates an action object.</p>
  */

  public Action() {
  }

  /**
  * <p>Invokes the action's
  * {@link yaak.sim.schedule.Action#execute execute} method.</p>
  */

  public void run() {
    execute();
  }

  /**
  * <p>Implements the action-specific behavior/operations.</p>
  */

  abstract public void execute();
}
