package yaak.agent.communication;

import java.io.Serializable;

/**
* <p><code>AgentAddress</code> encodes an agent identifier and location.</p>
* @author Jerry Smith
* @version $Id: AgentAddress.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class AgentAddress implements Cloneable, Serializable {
  private String agentName;
  private String contextName;

  /**
  * <p>Instantiates an agent address.</p>
  * @param agentName The specified agent name.
  * @param contextName The specified context name.
  */

  public AgentAddress(String agentName, String contextName) {
    this.agentName = replaceEmptyWithNull(agentName);
    this.contextName = replaceEmptyWithNull(contextName);
  }

  /**
  * <p>Instantiates an agent address.</p>
  * @param address The specified agent and context
  * name in <code>"agentname@contextname"</code> format.
  */

  public AgentAddress(String address) {
    if (address == null) {
      agentName = "null";
      contextName = "null";
    }
    else {
      address = address.trim();
      int i = address.indexOf("@");
      if (i == -1) { // unknown ==> local
        agentName = replaceEmptyWithNull(new String(address));
        contextName = null;
      }
      else {
        agentName = address.substring(0, i);
        contextName = replaceEmptyWithNull(address.substring(i + 1));
      }
    }
  }

  /**
  * <p>Instantiates an agent address.</p>
  * @param address The specified agent address.
  */

  public AgentAddress(AgentAddress address) {
    this.agentName = address.getAgentName();
    this.contextName = address.getContextName();
  }

  private String replaceEmptyWithNull(String str) {
    return
      (str != null && str.length() == 0) ? null : str;
  }

  /**
  * <p>Returns the agent name.</p>
  * @return The agent name.
  */

  public String getAgentName() {
    return agentName;
  }

  /**
  * <p>Returns the context name.</p>
  * @return The context name.
  */

  public String getContextName() {
    return contextName;
  }

  /**
  * <p>Tests two addresses for equality.</p>
  * @return Whether or not the addresses reference
  * the same agent.
  */

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    AgentAddress address = null;
    if (obj instanceof AgentAddress) {
      address = (AgentAddress) obj;
    }
    else if (obj instanceof String) {
      address = new AgentAddress((String) obj);
    }
    else {
      return false;
    }
    return
      address != null &&
      agentName.equals(address.getAgentName()) &&
      contextName.equals(address.getContextName());
  }

  /**
  * <p>Provides the hashcode for the address.</p>
  * @return The hash of the address.
  */

  public int hashCode() {
    return stringFormatAddress().hashCode();
  }

  /**
  * <p>Clones the <code>AgentAddress</code> instance.</p>
  * @return The cloned instance.
  */

  public Object clone() {
    return new AgentAddress(this);
  }

  /**
  * <p>Constructs an address representation in string format, that is,
  * <code>"agentname@contextname"</code>.</p>
  * @return The address in string format.
  */

  public String stringFormatAddress() {
    return agentName + "@" + contextName;
  }

  /**
  * <p>Returns a string summary of the address.</p>
  * @return The address information.
  */

  public String toString() {
    return
      "[agentName = " + agentName + "], " +
      "[contextName = " + contextName + "]";
  }
}
