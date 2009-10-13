package yaak.agent;

import java.io.Serializable;
import java.net.InetAddress;

/**
* <p><code>AgentID</code> implements an identifier for managing
* unique access to each agent resident in an agent context.</p>
* @author Jerry Smith
* @version $Id: AgentID.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class AgentID implements Cloneable, Serializable {
  private Long value = new Long(0);

  AgentID(long value) { // default privacy
    this.value = new Long(value);
  }

  AgentID(String str) { // default privacy
    this.value = new Long(str);
  }

  AgentID(AgentID id) { // default privacy
    this.value = new Long(id.longValue());
  }

  static AgentID generateID() { // default privacy
    //
    // Currently, obtain an ID from 'AgentSystem' and then
    // pass it through without further modification.
    //
    long id = AgentSystem.getNextSystemUniqueID();
    return new AgentID(id);
  }

  static AgentID generateID(long id) { // default privacy
    //
    // Currently, pass through without further modification.
    //
    return new AgentID(id);
  }

  long longValue() { // default privacy
    return value.longValue();
  }

  /**
  * <p>Provides the hashcode for the ID.</p>
  * @return The hash of the underlying ID.
  */

  public int hashCode() {
    return value.hashCode();
  }

  /**
  * <p>Tests whether or not another object has the same numeric value as
  * the underlying ID.</p>
  * @param obj The comparison object.
  * @return <code>true</code> if the values are equal,
  * otherwise <code>false</code>.
  * <p>This class and method are under construction.</p>
  */

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    AgentID id = null;
    try {
      id = (AgentID) obj;
    }
    catch (Exception e) {
      return false;
    }
    return longValue() == id.longValue();
  }

  /**
  * <p>Tests whether or not another object has a smaller numeric value than
  * the underlying ID.</p>
  * @param obj The comparison object.
  * @return <code>true</code> if this ID is less
  * than the argument, otherwise <code>false</code>.
  */

  public boolean isLessThan(Object obj) {
    AgentID id = null;
    try {
      id = (AgentID) obj;
    }
    catch (Exception e) {
      return false;
    }
    return longValue() < id.longValue();
  }

  /**
  * <p>Clones the <code>AgentID</code> instance.</p>
  * @return The cloned instance.
  */

  public Object clone() {
    return new AgentID(this);
  }

  /**
  * <p>Provides the ID in string form.</p>
  * @return The agent ID as a string.
  */

  public String toString() {
    return value.toString();
  }
}
