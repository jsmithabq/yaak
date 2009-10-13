package yaak.test.agent.behavior;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.concurrent.DefaultExecutor;

import junit.framework.*;

/**
* <p><code>TestActorBehaviorSenseReasonAct</code> tests behavior
* customization by
* <ul>
* <li>Extending the <code>Actor</code> base class.
* <li>Performing a sense operation.
* <li>Performing a reason operation.
* <li>Evaluating the result of the sense-reason-act cycle.
* </ul></p>
* @author Jerry Smith
* @version $Id: TestActorBehaviorSenseReasonAct.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestActorBehaviorSenseReasonAct extends TestCase {
  private AgentContext context;
  private SensitiveThinkingActor actor;

  public TestActorBehaviorSenseReasonAct(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestActorBehaviorSenseReasonAct.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("X");
    try {
      actor = (SensitiveThinkingActor)
        context.createAgent(
          yaak.test.agent.behavior.SensitiveThinkingActor.class);
    }
    catch (AgentException e) {
      fail("Cannot create agent.");
    }
  }

  protected void tearDown() {
    // nothing
  }

  public void testBehavior() {
    actor.run();
    assertEquals((String) actor.getActResult(),
      SensitiveThinkingActor.TEST_RESULT);
  }
}
