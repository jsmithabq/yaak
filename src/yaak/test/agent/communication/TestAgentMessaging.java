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
* <p><code>TestAgentMessaging</code> tests agent-to-agent messaging
* within an agent context.</p>
* @author Jerry Smith
* @version $Id: TestAgentMessaging.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentMessaging extends TestCase {
  private AgentContext jupiter;
  private Agent mary, tom;

  public TestAgentMessaging(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentMessaging.class);
  }

  protected void setUp() {
    jupiter = AgentSystem.createAgentContext("Jupiter");
    assertEquals("Jupiter", jupiter.getContextName());
    try {
      mary = jupiter.createAgent("yaak.test.agent.MyTestAgent", "Mary");
      tom = jupiter.createAgent("yaak.test.agent.MyTestAgent", "Tom");
      tom.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(tom.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });
    }
    catch (AgentException e) {
      fail("Cannot create agents.");
    }
  }

  protected void tearDown() {
    // nothing
  }

  public void testMessaging() {
    try {
      //
      // test fully specified message:
      //
      mary.sendMessage("Tom@Jupiter", new Message("Hello, Tom."));
      //
      // test partially specified message:
      //
      mary.sendMessage("Tom", new Message("Goodbye, Tom."));
    }
    catch (CommunicationException e) {
      fail("Exception sending message.");
    }
  }
}
