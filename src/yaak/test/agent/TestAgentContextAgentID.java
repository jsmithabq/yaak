package yaak.test.agent;

import yaak.agent.Agent;
import yaak.agent.AgentID;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;

import junit.framework.*;

import java.util.List;

/**
* <p><code>TestAgentContextAgentID</code> tests agent context
* ID handling.</p>
* @author Jerry Smith
* @version $Id: TestAgentContextAgentID.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentContextAgentID extends TestCase {
  private AgentContext context;
  private Agent agent1, agent2;

  public TestAgentContextAgentID(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentContextAgentID.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("Jupiter");
    assertEquals("Jupiter", context.getContextName());
    try {
      //
      // Create an instance of an agent, setting the agent's symbolic name:
      //
      agent1 = context.createAgent("yaak.test.agent.MyTestAgent", "Agent1");
      assertEquals("Agent1", agent1.getAgentName());
      //
      // Create another instance, setting the name separately:
      //
      agent2 = context.createAgent("yaak.test.agent.MyTestAgent");
      agent2.setAgentName("Agent2");
      assertEquals("Agent2", agent2.getAgentName());
    }
    catch (AgentException e) {
      fail("Cannot create agents: " + e);
    }
  }

  protected void tearDown() {
    context.dispose();
  }

  public void testGetAgentIDs() {
    AgentID[] ids = context.getAgentIDs();
    assertTrue("Verify that there are two agents.", ids.length == 2);
  }

  public void testGetAgentNameByID() throws AgentException {
    AgentID id = agent1.getAgentID();
    assertTrue("The context finds Agent1's name via its ID.",
      "Agent1".equals(context.getAgentNameByID(id)));
    id = agent2.getAgentID();
    assertTrue("The context finds Agent2's name via its ID.",
      "Agent2".equals(context.getAgentNameByID(id)));
  }

  public void testGetAgentByIDList() throws AgentException {
    AgentID id = agent1.getAgentID();
    assertTrue("The context finds Agent1 by ID.",
      "Agent1".equals(context.getAgentNameByID(id)));
  }

  public void testDetermineAgentCount() {
    AgentID[] ids = context.getAgentIDs();
    List idList = context.getAgentIDList();
    assertTrue("Obtaining agent count in two different ways.",
    ids.length == idList.size());
  }
}
