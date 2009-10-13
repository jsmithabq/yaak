package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;
import yaak.util.Util;

/**
* <p><code>XTestAgentMessagingInterAppW</code> tests agent communication
* across agent contexts.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterAppW.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterAppW {
  private static AgentContext w;
  private static Agent agentW;

  public XTestAgentMessagingInterAppW() {
    initServices();
  }

  public static void main(String[] args) {
    XTestAgentMessagingInterAppW appW = new XTestAgentMessagingInterAppW();
    Util.sleepSeconds(1);
    appW.testRemoteMessaging();
  }

  private void initServices() {
    w = AgentSystem.createAgentContext("W");
    try {
      agentW = w.createAgent("yaak.test.agent.MyTestAgent", "AgentW");
      agentW.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(agentW.getAgentSelfAddressAsString() +
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

  public void testRemoteMessaging() {
    try {
      agentW.sendMessage("AgentZ@Z", new Message("Hello, AgentZ."));
    }
    catch (CommunicationException e) {
      System.out.println("Exception sending message:");
      e.printStackTrace();
    }
  }
}
