package yaak.sim.schedule;

import yaak.concurrent.DefaultExecutor;
import yaak.core.YaakSystem;

import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
* <p><code>ActionGroup</code> encapsulates actions that are scheduled
* for execution by a scheduler.</p>
* @author Jerry Smith
* @version $Id: ActionGroup.java 6 2005-06-08 17:00:15Z jsmith $
* @see yaak.sim.schedule.Scheduler
*/

public class ActionGroup extends Action {
  private Vector actions = new Vector();
  private boolean concurrent = false;

  /**
  * <p>Instantiates an object for encapsulating one or more
  * actions.  The actions are added programmatically.</p>
  */

  public ActionGroup() {
    super();
  }

  /**
  * <p>Instantiates an object for encapsulating one or more
  * actions.</p>
  * @param actions The list of actions.
  */

  public ActionGroup(List actions) {
    super();
    copyActionsFromList(actions);
  }

  /**
  * <p>Instantiates an object for encapsulating one or more
  * actions.</p>
  * @param actionGroup An action group from which to obtain
  * the actions.
  */

  public ActionGroup(ActionGroup actionGroup) {
    this.actions = (Vector) actionGroup.getActions();
  }

  /**
  * <p>Configures the action group to execute actions concurrently.
  * By default, actions execute serially, <em>not</em> concurrently.</p>
  * @param concurrent Whether or not the actions should
  * be executed concurrently (<code>true</code>) or serially
  * (<code>false</code>).
  */

  public void setConcurrent(boolean concurrent) {
    this.concurrent = concurrent;
  }

  /**
  * <p>Gets the execution model state for the action group.</p>
  * @return Whether or not the actions should
  * be executed concurrently (<code>true</code>) or serially
  * (<code>false</code>).
  */

  public boolean getConcurrent() {
    return concurrent;
  }

  /**
  * <p>Whether or not the current execution model is concurrent, as
  * opposed to, serial execution of actions.</p>
  * @return Whether or not the actions should
  * be executed concurrently (<code>true</code>) or serially
  * (<code>false</code>).
  */

  public boolean isConcurrent() {
    return concurrent;
  }

  /**
  * <p>Stores the actions for the action group.</p>
  * @param actions The list of actions.
  */

  public void setActions(List actions) {
    copyActionsFromList(actions);
  }

  /**
  * <p>Provides access to the group's actions.  This method returns
  * a reference to the "encapsulated" data structure, allowing
  * modifications to the list of actions.</p>
  * @return The list of actions.
  */

  public List getActions() {
    return actions;
  }

  /**
  * <p>Iterates over the actions, executing each action's operations.</p>
  */

  public void execute() {
    if (actions == null) {
      return;
    }
    if (!concurrent) {
      for (int i = 0; i < actions.size(); i++) {
        ((Action) actions.elementAt(i)).execute();
      }
    }
    else {
      //
      // Randomize the order in a nondestructive way on
      // _each_ execution, because the serial or concurrent
      // model can be changed dynamically.
      //
      Random randy = new Random();
      int[] randomOrder = new int[actions.size()];
      for (int i = 0; i < randomOrder.length; i++) {
        randomOrder[i] = i;
      }
      for (int i = randomOrder.length; i > 1; i--) {
        int pos = randy.nextInt(i);
        int temp = randomOrder[i - 1];
        randomOrder[i - 1] = randomOrder[pos];
        randomOrder[pos] = temp;
      }
      for (int i = 0; i < randomOrder.length; i++) {
        new DefaultExecutor().execute((Runnable) actions.elementAt(randomOrder[i]));
      }
    }
  }

  private void copyActionsFromList(List actions) {
    this.actions.clear();
    for (int i = 0; i < actions.size(); i++) {
      try {
        Action action = (Action) actions.get(i); // force typechecking
        this.actions.add(action);
      }
      catch (ClassCastException e) {
        throw new IllegalArgumentException(
          "Elements must be instances of 'yaak.sim.schedule.Action'.");
      }
      catch (Exception e) {
        YaakSystem.getLogger().warning("unknown exception.");
        e.printStackTrace();
      }
    }
  }
}
