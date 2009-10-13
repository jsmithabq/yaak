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
* <p><code>TestAgentMessagingInterContext</code> tests agent communication
* across agent contexts.</p>
* @author Jerry Smith
* @version $Id: TestAgentMessagingInterContext.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentMessagingInterContext extends TestCase {
  private AgentContext jupiter, saturn;
  private Agent mary, tom;

  public TestAgentMessagingInterContext(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentMessagingInterContext.class);
  }

  protected void setUp() {
    jupiter = AgentSystem.createAgentContext("Jupiter");
    saturn = AgentSystem.createAgentContext("Saturn");
    try {
      mary = jupiter.createAgent("yaak.test.agent.MyTestAgent", "Mary");
      tom = saturn.createAgent("yaak.test.agent.MyTestAgent", "Tom");
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
      mary.sendMessage("Tom@Saturn", new Message("Hello, Tom."));
    }
    catch (CommunicationException e) {
      fail("Exception sending message.");
    }
  }
}
