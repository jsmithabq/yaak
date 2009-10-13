package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentException;
import yaak.agent.AgentID;
import yaak.agent.AgentContext;
import yaak.agent.AgentSystem;
import yaak.agent.communication.AgentAddress;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageFilter;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;
import yaak.util.Util;

/**
* <p><code>XTestAgentMessageFiltering</code> tests message filtering.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessageFiltering.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessageFiltering extends Agent {
  private static AgentContext context;
  private static Agent roberto, alicia, telma;

  public static void main(String[] args) {
    context = AgentSystem.createAgentContext("Jupiter");
/*****
    //
    // By default, only deliver messages from Roberto:
    //
    context.registerDefaultMessageFilter(new MessageFilter() {
      public boolean allow(Message msg) {
        AgentAddress source = msg.getSource();
        String agentName = source.getAgentName();
        return agentName.equals("Roberto");
      }
    });
*****/
    try {
      roberto = context.createAgent("yaak.test.agent.MyTestAgent", "Roberto");
      roberto.registerForMessages(new MessageListener() {
        public void onMessage(Message msg) {
          System.out.println("I (Roberto) received a message: ");
          System.out.println(msg.getObject());
        }
      });
      roberto.registerFilterForMessages(new MessageFilter() {
        public boolean allow(Message msg) {
          String text = (String) msg.getObject();
          return text.indexOf("ice cream") != -1;
        }
      });
      alicia = context.createAgent("yaak.test.agent.MyTestAgent", "Alicia");
      telma = context.createAgent("yaak.test.agent.MyTestAgent", "Telma");
      telma.registerForMessages(new MessageListener() {
        public void onMessage(Message msg) {
          System.out.println("I (Telma) received a message: ");
          System.out.println(msg.getObject());
        }
      });
    }
    catch (AgentException e) {
      YaakSystem.getLogger().fine("Cannot create agents.");
      e.printStackTrace();
    }
    try {
      //
      // Send messages to each other independently, testing
      // each address constructor, send format, and filtering:
      //
      roberto.sendMessage(
        telma.getAgentSelfAddress(),
        new Message("This is a secret message from Roberto."));
      Util.sleepSeconds(1);
      telma.sendMessage(
        roberto.getAgentSelfAddress(),
        new Message("This is a secret message from Telma."));
      Util.sleepSeconds(1);
      roberto.sendMessage(
        roberto.getAgentSelfAddressAsString(),
        new Message("Hello, self."));
      Util.sleepSeconds(1);
      telma.sendMessage(
        roberto.getAgentSelfAddress(),
        new Message("Roberto, do you like ice cream?"));
      Util.sleepSeconds(1);
      telma.sendMessage(
        roberto.getAgentSelfAddress(),
        new Message("Roberto, do you like candy?"));
      Util.sleepSeconds(1);
      telma.sendMessage(
        roberto.getAgentSelfAddress(),
        new Message("Roberto, let's go get some ice cream."));
      Util.sleepSeconds(1);
      alicia.sendMessage(
        telma.getAgentSelfAddress(),
        new Message("This is a message from Alicia."));
      Util.sleepSeconds(1);
    }
    catch (CommunicationException e) {
      YaakSystem.getLogger().fine("Cannot send message(s).");
      e.printStackTrace();
    }
  }
}
