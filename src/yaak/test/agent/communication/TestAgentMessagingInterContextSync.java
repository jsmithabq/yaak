package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.AgentAddress;
import yaak.core.YaakSystem;
import yaak.util.Util;

import junit.framework.*;

/**
* <p><code>TestAgentMessagingInterContextSync</code> tests synchronous
* agent-to-agent messaging across agent contexts.</p>
* @author Jerry Smith
* @version $Id: TestAgentMessagingInterContextSync.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentMessagingInterContextSync extends TestCase {
  private static final String SECRET_MESSAGE = "This is a secret message.";
  private AgentContext jupiter, saturn;
  private Agent mary, tom;
  private Thread thread;

  public TestAgentMessagingInterContextSync(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentMessagingInterContextSync.class);
  }

  protected void setUp() {
    jupiter = AgentSystem.createAgentContext("Jupiter");
    saturn = AgentSystem.createAgentContext("Saturn");
    try {
      mary = jupiter.createAgent("yaak.test.agent.MyTestAgent", "Mary");
      tom = saturn.createAgent("yaak.test.agent.MyTestAgent", "Tom");
    }
    catch (AgentException e) {
      fail("Cannot create agents.");
    }
    thread = new Thread(
      new Runnable() {
        public void run() {
          try {
            Message msg = tom.receiveMessage(mary.getAgentSelfAddress());
            assertTrue("Tom received a message.",
              SECRET_MESSAGE.equals((String) msg.getObject()));
            YaakSystem.getLogger().info(tom.getAgentSelfAddressAsString() +
              " received a message from " +
              msg.getSource().stringFormatAddress() + ": ");
            YaakSystem.getLogger().info("" + msg.getObject());
          }
          catch (CommunicationException e) {
            fail("Cannot receive message.");
          }
        }
      }
    );
  }

  protected void tearDown() {
    // nothing
  }

  public void testMessaging() {
    thread.start();
    Util.sleepSeconds(1); // must wait for thread to start...
    try {
      mary.sendMessage("Tom@Saturn", new Message(SECRET_MESSAGE));
    }
    catch (CommunicationException e) {
      fail("Exception sending message.");
    }
  }
}
