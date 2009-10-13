package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;

/**
* <p><code>XTestAgentMessagingInterAppZ</code> tests agent communication
* across agent contexts.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterAppZ.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterAppZ {
  private static AgentContext z;
  private static Agent agentZ;

  public XTestAgentMessagingInterAppZ() {
    initServices();
  }

  public static void main(String[] args) {
    XTestAgentMessagingInterAppZ appZ = new XTestAgentMessagingInterAppZ();
    appZ.waitAround();
  }

  private void initServices() {
    z = AgentSystem.createAgentContext("Z");
    try {
      agentZ = z.createAgent("yaak.test.agent.MyTestAgent", "AgentZ");
      agentZ.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(agentZ.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });
    }
    catch (AgentException e) {
      System.out.println("Cannot create agents:");
      e.printStackTrace();
    }
  }

  private void waitAround() {
    while (true) {
      try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
  }
}
