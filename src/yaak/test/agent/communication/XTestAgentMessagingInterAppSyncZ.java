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
* <p><code>XTestAgentMessagingInterAppSyncZ</code> tests synchronized
* agent communication across agent contexts <em>and</em> across
* applications.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterAppSyncZ.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterAppSyncZ {
  private static AgentContext z;
  private static Agent agentZ;

  public XTestAgentMessagingInterAppSyncZ() {
    initServices();
  }

  public static void main(String[] args) {
    new XTestAgentMessagingInterAppSyncZ();
  }

  private void initServices() {
    z = AgentSystem.createAgentContext("Z");
    try {
      agentZ = z.createAgent("yaak.test.agent.MyTestAgent", "AgentZ");
      Message msg = agentZ.receiveMessage("AgentX@X");
      System.out.println(agentZ.getAgentSelfAddressAsString() +
        " received a message from " +
        msg.getSource().stringFormatAddress() + ": ");
      System.out.println("" + msg.getObject());
/*      agentZ.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(agentZ.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });*/
    }
    catch (AgentException e) {
      System.out.println("Cannot create agents:");
      e.printStackTrace();
    }
  }
}
