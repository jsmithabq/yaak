package yaak.model.event;

import java.util.EventObject;

/**
* <p><code>ActionEvent</code> is the foundation class for event classes
* in the simulation model framework.  It provides an event ID state
* variable, which is inherited by all model-related event classes.</p>
* @author Jerry Smith
* @version $Id: ActionEvent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public abstract class ActionEvent extends EventObject {
  private int eventID;

  /**
  * <p>Initializes the event ID state variable and invokes the
  * <code>EventObject</code> superclass constructor.</p>
  * @param source The object in which the event originates.
  * @param eventID The event class-specific ID, or type.
  */

  public ActionEvent(Object source, int eventID) {
    super(source);
    this.eventID = eventID;
  }

  /**
  * <p>Provides access to the event type, or ID.</p>
  * @return The event class-specific ID, or type.
  */

  public int getID() {
    return eventID;
  }
}
