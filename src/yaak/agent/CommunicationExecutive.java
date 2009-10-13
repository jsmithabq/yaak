package yaak.agent;

/**
* <p><code>CommunicationExecutive</code> provides limited access to
* executive services from the communication layer.</p>
* @author Jerry Smith
* @version $Id: CommunicationExecutive.java 6 2005-06-08 17:00:15Z jsmith $
* @see yaak.agent.communication.Executive
*/


class CommunicationExecutive extends yaak.agent.communication.Executive
    { // default privacy
  CommunicationExecutive(AgentContainer container) { // default privacy
    super(container);
  }
}
