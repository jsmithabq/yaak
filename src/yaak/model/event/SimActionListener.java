package yaak.model.event;

import java.util.EventListener;

/**
* <p><code>SimActionListener</code> provides notifications services for
* significant events in the simulation application, for example,
* significant phases of the action-handling process.</p>
* @author Jerry Smith
* @version $Id: SimActionListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface SimActionListener extends EventListener {
  /**
  * <p>Invoked each time the simulation application processes an action
  * on behalf of the domain model.</p>
  * @param event The event object.
  */

  void actionPerformed(SimActionEvent event);
}
