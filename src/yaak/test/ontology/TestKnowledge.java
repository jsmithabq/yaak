package yaak.test.ontology;

import yaak.agent.Agent;
import yaak.agent.AgentID;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.ontology.Knowledge;
import yaak.model.service.ServiceArena;
import yaak.model.service.ServiceArenaFactory;
import yaak.test.model.GreetingAgent;


import junit.framework.*;

/**
* <p><code>TestKnowledge</code> tests basic services.</p>
* @author Jerry Smith
* @version $Id: TestKnowledge.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class TestKnowledge extends TestCase {
  private AgentContext context;
  private Agent greetingAgent, joe;
  private ServiceArena arena;

  public TestKnowledge(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestKnowledge.class);
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
    arena.addKnowledgeListener((GreetingAgent) greetingAgent);
  }

  protected void tearDown() {
    context.dispose();
    arena.dispose();
  }

  public void testGetAgentIDs() {
    AgentID[] ids = context.getAgentIDs();
    assertTrue("There are two agents.", ids.length == 2);
  }

  public void testKnowledge() {
    Knowledge knowledge = new Knowledge() {
      public String[] getPropertyNames() {
        return null; // dummy
      }
      public Class getType(String property) {
        return null; // dummy
      }
    };
    Knowledge k1 = arena.requestKnowledge(knowledge, joe);
    assertTrue("The type is 'Knowledge'.",
      Knowledge.class.isInstance(k1));
    Knowledge k2 = arena.requestKnowledge(Knowledge.class, joe);
    assertTrue("The type is 'Knowledge'.",
      Knowledge.class.isInstance(k2));
    String[] properties = k2.getPropertyNames();
    assertTrue("A single property exists.",
      properties.length == 1);
    assertTrue("The property 'alias' exists.",
      properties[0].equals("alias"));
  }
}
