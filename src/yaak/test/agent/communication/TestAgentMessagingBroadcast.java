package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;

import junit.framework.*;

/**
* <p><code>TestAgentMessagingBroadcast</code> tests broadcast messaging
* within an agent context.</p>
* @author Jerry Smith
* @version $Id: TestAgentMessagingBroadcast.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentMessagingBroadcast extends TestCase {
  private AgentContext jupiter, saturn;
  private Agent a, b, c, d, e;

  public TestAgentMessagingBroadcast(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentMessagingBroadcast.class);
  }

  protected void setUp() {
    jupiter = AgentSystem.createAgentContext("Jupiter");
    assertEquals("Jupiter", jupiter.getContextName());
    saturn = AgentSystem.createAgentContext("Saturn");
    assertEquals("Saturn", saturn.getContextName());
    try {
      a = jupiter.createAgent(yaak.test.agent.MyTestAgent.class, "A");
      b = jupiter.createAgent(yaak.test.agent.MyTestAgent.class, "B");
      c = jupiter.createAgent(yaak.test.agent.MyTestAgent.class, "C");
      a.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(a.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });
      b.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(b.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });
    }
    catch (AgentException ex) {
      fail("Cannot create agents for Jupiter.");
    }
    try {
      d = saturn.createAgent(yaak.test.agent.MyTestAgent.class, "D");
      e = saturn.createAgent(yaak.test.agent.MyTestAgent.class, "E");
      d.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(d.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });
      e.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(e.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });
    }
    catch (AgentException ex) {
      fail("Cannot create agents for Saturn.");
    }
  }

  protected void tearDown() {
    // nothing
  }

  public void testMessaging() {
    try {
      //
      // Test fully specified target:
      //
      c.publishMessage("@Jupiter", new Message("Hello, all."));
      //
      // Test partially specified target ==> local context:
      //
      c.publishMessage("@", new Message("Hello, again."));
      //
      // Test empty target specification ==> local context:
      //
      c.publishMessage("", new Message("Goodbye, all."));
      //
      // Test fully specified target other than home context:
      //
      c.publishMessage("@Saturn", new Message("Hello, everyone at Saturn."));
    }
    catch (CommunicationException e) {
      fail("Exception sending message.");
    }
  }
}
