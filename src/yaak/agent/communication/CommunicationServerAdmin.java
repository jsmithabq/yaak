package yaak.agent.communication;

/**
* <p><code>CommunicationServerAdmin</code> handles communication
* server management.</p>
* @author Jerry Smith
* @version $Id: CommunicationServerAdmin.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface CommunicationServerAdmin {
  /**
  * <p>Shuts down all network resources.  The communication services
  * are activated automatically (on demand) whenever an application
  * configures itself for distributed communications, based on the
  * resource <code>yaak.agent.communication.isDistributed</code>.
  * This method gracefully closes down all distributed resources, as
  * appropriate, given the communication server's implementation.</p>
  * <p>If the server, depending on its implementation and API usage,
  * still has dependent threads, the application will have to use
  * <code>System.exit()</code> to close the application.
  */

  void shutdown();
}
