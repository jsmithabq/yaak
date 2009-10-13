package yaak.agent.communication.event;

import yaak.core.YaakSystem;

/**
* <p><code>CommunicationAdapter</code> provides an empty, or place-holder,
* implementation of the <code>CommunicationListener</code> interface.</p>
* @author Jerry Smith
* @version $Id: CommunicationAdapter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class CommunicationAdapter implements CommunicationListener {
  /**
  * <p>Instantiates the adapter.</p>
  */

  public CommunicationAdapter() {
  }

  /**
  * <p>Invoked each time the pod sends a message.</p>
  * @param event The event object.
  */

  public void messageSent(CommunicationEvent event) {
    YaakSystem.getLogger().fine("messageSent()...");
  }

  /**
  * <p>Invoked each time the pod receives a message.</p>
  * @param event The event object.
  */

  public void messageReceived(CommunicationEvent event) {
    YaakSystem.getLogger().fine("messageReceived()...");
  }
}
