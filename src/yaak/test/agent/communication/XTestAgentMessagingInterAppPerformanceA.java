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
* <p><code>XTestAgentMessagingInterAppPerformanceA</code> tests asychronous
* agent communication across agent applications.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterAppPerformanceA.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterAppPerformanceA {
  private static final int DEFAULT_SEND_COUNT = 10000;
  private static AgentContext a;
  private static int sendCount = DEFAULT_SEND_COUNT;
  private static Agent senderAgent;

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
    a = AgentSystem.createAgentContext("A");
    try {
      System.out.println("+++++Creating agent...");
      senderAgent = a.createAgent(
        "yaak.test.agent.MyTestAgent", "Sender");
    }
    catch (AgentException e) {
      YaakSystem.getLogger().fine("Cannot create agent.");
      e.printStackTrace();
    }
    System.out.println("+++++Agent created.");
    Util.sleepSeconds(5);
    AgentAddress receiverAddress =
      new AgentAddress("Receiver@B");
    long beginTime = System.currentTimeMillis();
    System.out.println("+++++Begin sending messages: " + new Date());
    try {
      Message aMessage = new Message("This is a message.");
      for (int i = 0; i < sendCount; i++) {
        senderAgent.sendMessage(receiverAddress, aMessage);
      }
      senderAgent.sendMessage(receiverAddress, new Message("stop")); // done
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
      "+++++Finish sending messages: " + new Date());
    System.out.println("+++++Elapsed time: " +
      (endTime - beginTime) / 1000 + " second(s).");
  }
}
