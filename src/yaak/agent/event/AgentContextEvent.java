package yaak.agent.event;

import yaak.agent.Agent;
import yaak.agent.AgentID;

/**
* <p><code>AgentContextEvent</code> provides infomation for objects that
* register for <code>AgentContextListener</code>-related agent life-cycle
* operations.</p>
* @author Jerry Smith
* @version $Id: AgentContextEvent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class AgentContextEvent extends AgentEvent {
  private Agent agent;
  private AgentID aid;

  /**
  * <p>Indicates an agent-created event.</p>
  */

  public static final int AGENT_CREATED = 1;

  /**
  * <p>Indicates an agent-disposed event.</p>
  */

  public static final int AGENT_DISPOSED = 2;

  /**
  * <p>Encapsulates event information for objects that register as context
  * listeners.</p>
  * @param eventID The type of event, for example, an
  * <code>AGENT_CREATED</code> operation.
  * @param source The source object (agent context) for the event.
  * @param agent The agent.
  * @param aid The agent ID.
  */

  public AgentContextEvent(int eventID, Object source, Agent agent,
      AgentID aid) {
    super(source, eventID);
    this.agent = agent; // pass in an agent reference
    this.aid = aid; // pass in an ID created by the agent context
  }

  /**
  * <p>Provides access to the associated agent ID.</p>
  * @return A copy of the agent ID (not
  * <code>null</code> following a disposal).
  */

  public AgentID getAgentID() {
    return aid;
  }
}
