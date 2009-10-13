package yaak.test.agent;

import yaak.agent.Agent;
import yaak.agent.AgentID;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;

import junit.framework.*;

/**
* <p><code>TestAgentContextAgent</code> tests agent creation
* within an agent context.</p>
* @author Jerry Smith
* @version $Id: TestAgentContextAgent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentContextAgent extends TestCase {
  private AgentContext context;
  private Agent agent1, agent2;

  public TestAgentContextAgent(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentContextAgent.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("Jupiter");
    assertEquals("Jupiter", context.getContextName());
    try {
      //
      // Create an instance of an agent, setting the agent's symbolic name:
      //
      agent1 = context.createAgent(yaak.test.agent.MyTestAgent.class, "Agent1");
      assertEquals("Agent1", agent1.getAgentName());
      //
      // Create another instance, setting the name separately:
      //
      agent2 = context.createAgent(yaak.test.agent.MyTestAgent.class);
      agent2.setAgentName("Agent2");
      assertEquals("Agent2", agent2.getAgentName());
    }
    catch (AgentException e) {
      fail("Cannot create agents.");
    }
  }

  protected void tearDown() {
    // nothing
  }

  public void testGetAgentIDs() {
    AgentID[] ids = context.getAgentIDs();
    assertTrue("There are two agents.", ids.length == 2);
  }
}
