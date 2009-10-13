package yaak.agent.communication;

import java.io.Serializable;

/**
* <p><code>Message</code> encodes an agent identifier and location.</p>
* @author Jerry Smith
* @version $Id: Message.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class Message extends Payload {
  private AgentAddress source;
  private AgentAddress target;

  /**
  * <p>Creates and initializes the message.  This constructor does
  * <em>not</em> clone the encapsulated object--for convenience only.</p>
  * @param message The existing message object.
  */

  //
  // Implementation note:  do we want to provide clone() ???  No.
  //

  public Message(Message message) {
    this(message.getSource(), message.getTarget(),
      (Serializable) message.getObject());
  }

  /**
  * <p>Creates and initializes the message.</p>
  * @param sObj The message object.
  */

  public Message(Serializable sObj) {
    super(sObj);
  }

  /**
  * <p>Not for public consumption.  Creates and initializes the message.</p>
  * @param source The source agent.
  * @param target The target agent.
  * @param sObj The message object.
  */

  Message(AgentAddress source, AgentAddress target,
      Serializable sObj) { // default privacy
    super(sObj);
    this.source = source;
    this.target = target;
  }

  /**
  * <p>Returns the encapsulated object.</p>
  * @return The message object.
  */

/*********************************************************************
  public Object getObject() {
    return super.getObject(); // why not just inherit here ???
  }
/********************************************************************/

  /**
  * <p>Returns the address of the sending agent.</p>
  * @return The sending agent's address.
  */

  public AgentAddress getSource() {
    return source;
  }

  /**
  * <p>Sets the address of the sending agent.</p>
  * @param source The sending agent's address.
  */

  synchronized public void setSource(AgentAddress source) {
    this.source = source;
  }

  /**
  * <p>Returns the address of the receiving agent.</p>
  * @return The receiving agent's address.
  */

  public AgentAddress getTarget() {
    return target;
  }

  /**
  * <p>Sets the address of the receiving agent.</p>
  * @param target The receiving agent's address.
  */

  synchronized public void setTarget(AgentAddress target) {
    this.target = target;
  }
}
