package yaak.agent.communication;

/**
* <p><code>StringMessage</code> encodes an agent identifier and location.</p>
* @author Jerry Smith
* @version $Id: StringMessage.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class StringMessage extends Message {
  /**
  * <p>Creates and initializes the message.  This constructor does
  * <em>not</em> clone the encapsulated object--for convenience only.</p>
  * @param message The existing message object.
  */

  public StringMessage(StringMessage message) {
    this(message.getSource(), message.getTarget(), message.getText());
  }

  /**
  * <p>Creates and initializes the message with encapsulated text.</p>
  * @param text The message text.
  */

  public StringMessage(String text) {
    super(text);
  }

  /**
  * <p>Not for public consumption.  Creates and initializes the
  * message with encapsulated text.</p>
  * @param source The source agent.
  * @param target The target agent.
  * @param sObj The message text.
  */

  StringMessage(AgentAddress source, AgentAddress target,
      String text) { // default privacy
    super(text);
    setSource(source);
    setTarget(target);
  }

  /**
  * <p>Returns the encapsulated object.</p>
  * @return The object.
  */

  public String getText() {
    return (String) super.getObject();
  }
}
