package yaak.test.model;

import yaak.agent.Agent;
import yaak.agent.AgentID;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.model.service.ServiceArena;
import yaak.model.service.ServiceArenaFactory;

import junit.framework.*;

/**
* <p><code>TestServices</code> tests basic services.</p>
* @author Jerry Smith
* @version $Id: TestServices.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class TestServices extends TestCase {
  private AgentContext context;
  private Agent greetingAgent, joe;
  private ServiceArena arena;

  public TestServices(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestServices.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("Jupiter");
    assertEquals("Jupiter", context.getContextName());
    try {
      //
      // Create an instance of an agent, setting the agent's symbolic name:
      //
      greetingAgent = context.createAgent(
        yaak.test.model.GreetingAgent.class, "GreetingAgent");
      assertEquals("GreetingAgent", greetingAgent.getAgentName());
      //
      // Create another instance, setting the name separately:
      //
      joe = context.createAgent(yaak.test.agent.MyTestAgent.class);
      joe.setAgentName("Joe");
      assertEquals("Joe", joe.getAgentName());
    }
    catch (AgentException e) {
      fail("Cannot create agents.");
    }
    //
    // Create an instance of an arena and start its management services:
    //
    arena = ServiceArenaFactory.getInstance();
    arena.addServiceListener((GreetingAgent) greetingAgent);
  }

  protected void tearDown() {
    context.dispose();
    arena.dispose();
  }

  public void testGetAgentIDs() {
    AgentID[] ids = context.getAgentIDs();
    assertTrue("There are two agents.", ids.length == 2);
  }

  public void testServices() {
    GreetingService greetingService = new GreetingService() {
      public String makeGreeting() {
        return null; // dummy
      }
    };
    arena.requestService(greetingService, joe);
    arena.requestService(GreetingService.class, joe);
  }
}
