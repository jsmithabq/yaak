package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentException;
import yaak.agent.AgentContext;
import yaak.agent.AgentSystem;
import yaak.agent.communication.AgentAddress;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;
import yaak.util.Util;

import java.util.Date;

/**
* <p><code>XTestAgentMessagingInterContextPerformance</code> tests agent
* communication performance across agent contexts.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterContextPerformance.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterContextPerformance {
  private static final int DEFAULT_SEND_COUNT = 10000;
  private static AgentContext jupiter, saturn;
  private static int receiveCount = 0, sendCount = DEFAULT_SEND_COUNT;
  private static Agent receiverAgent, senderAgent;

  public static void main(String[] args) {
    if (args.length == 1) {
      try {
        sendCount = Integer.parseInt(args[0]);
      }
      catch (Exception e) {
        YaakSystem.getLogger().fine(
          "Could not process number-of-messages argument.");
        sendCount = DEFAULT_SEND_COUNT;
        return;
      }
    }
    jupiter = AgentSystem.createAgentContext("Jupiter");
    saturn = AgentSystem.createAgentContext("Saturn");
    try {
      System.out.println("+++++Creating agents...");
      senderAgent = jupiter.createAgent(
        "yaak.test.agent.MyTestAgent", "Sender");
      receiverAgent = saturn.createAgent(
        "yaak.test.agent.MyTestAgent", "Receiver");
      receiverAgent.registerForMessages(new MessageListener() {
        public void onMessage(Message msg) {
          receiveCount++;
        }
      });
    }
    catch (AgentException e) {
      YaakSystem.getLogger().fine("Cannot create agents.");
      e.printStackTrace();
    }
    System.out.println("+++++Agents created.");
    Util.sleepSeconds(1);
    AgentAddress receiverAddress =
      new AgentAddress("Receiver" + "@" + saturn.getContextName());
    long beginTime = System.currentTimeMillis();
    System.out.println("+++++Begin sending messages: " + new Date());
    try {
      for (int i = 0; i < sendCount; i++) {
        senderAgent.sendMessage(receiverAddress, new Message(new Integer(i)));
      }
    }
    catch (CommunicationException e) {
      YaakSystem.getLogger().fine("Cannot send message.");
      e.printStackTrace();
    }
    catch (Exception e) {
      YaakSystem.getLogger().fine("Unknown exception.");
      e.printStackTrace();
    }
    long endTime = System.currentTimeMillis();
    System.out.println(
      "+++++Finish sending/receiving messages: " + new Date());
    System.out.println(
      "+++++Received " + receiveCount + " message(s).");
    System.out.println("+++++Elapsed time: " +
      (endTime - beginTime) / 1000 + " second(s).");
  }
}
