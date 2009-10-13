package yaak.model;

import java.util.Vector;

import yaak.agent.Agent;
import yaak.sim.schedule.Action;
import yaak.model.event.SimActionEvent;
import yaak.model.event.SimActionListener;

/**
* <p><code>SimAction</code> is the base simulation action for
* application-layer operations.</p>
* @author Jerry Smith
* @version $Id: SimAction.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class SimAction extends Action {
  private Vector actionListeners = null;
  protected Agent agent;

  /**
  * <p>Instantiates a base simulation action.</p>
  */

  public SimAction() {
    this(null);
  }

  /**
  * <p>Instantiates a base simulation action with respect to a
  * specific agent.</p>
  */

  public SimAction(Agent agent) {
    this.agent = agent;
  }

  /**
  * <p>Associates an agent with this action.</p>
  * @param agent The agent associated with the
  * simulation action.
  */

  public final void setAgent(Agent agent) {
    this.agent = agent;
  }

  /**
  * <p>Not for public consumption.</p>
  * <p>This method performs fundamental actions, as prescribed by
  * {@link yaak.model.SimAction#executeAction executeAction}.
  * It also performs domain layer operations, such as event
  * notifications, etc.</p>
  */

  public final void execute() {
    executeAction();
    SimActionEvent event =
      new SimActionEvent(agent, SimActionEvent.ACTION_DONE, this);
    notifyListenersOnAction(event);
  }

  /**
  * <p>Performs fundamental actions with respect to a simulation
  * agent.  The simulation application should override this
  * method to implement an action (operations).</p>
  */

  protected void executeAction() {
//    agent.run(); // ??? should this be the default?
  }

  /**
  * <p>Registers a listener/observer for simulation action events.</p>
  * @param listener The to-be-registered listener object.
  */

  public final void addSimActionListener(SimActionListener listener) {
    if (actionListeners == null) { // delayed creation
      actionListeners = new Vector(); // volatile???
    }
    synchronized (actionListeners) {
      actionListeners.add(listener);
    }
  }

  private void notifyListenersOnAction(SimActionEvent event) {
    if (actionListeners == null) {
      return;
    }
    Vector listeners;
    synchronized (actionListeners) {
      listeners = (Vector) actionListeners.clone();
    }
    for (int i = 0; i < listeners.size(); i++) {
      SimActionListener listener = (SimActionListener) listeners.elementAt(i);
      listener.actionPerformed(event);
    }
  }
}
