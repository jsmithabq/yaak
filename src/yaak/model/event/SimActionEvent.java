package yaak.model.event;

import yaak.model.SimAction;

/**
* <p><code>SimActionEvent</code> provides infomation for objects that
* register for <code>SimActionListener</code>-related simulation-aplication
* operations.</p>
* @author Jerry Smith
* @version $Id: SimActionEvent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class SimActionEvent extends ActionEvent {
  private SimAction action;

  /**
  * <p>The action has completed.</p>
  */

  public static final int ACTION_DONE = 1; // ???

  /**
  * <p>Encapsulates event information for objects that register as simulation
  * action listeners.</p>
  * @param source The source object for the event.
  * @param eventID The type of event.
  * @param action The action.
  */

  public SimActionEvent(Object source, int eventID, SimAction action) {
    super(source, eventID);
    this.action = action; // pass in an action reference
  }

  /**
  * <p>Provides access to the associated simulation action.</p>
  * @return The associated action.
  */

  public SimAction getAction() {
    return action;
  }
}
