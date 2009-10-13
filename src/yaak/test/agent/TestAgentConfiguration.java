package yaak.test.agent;

import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.core.YaakProperties;

import java.util.Properties;

import junit.framework.*;

/**
* <p><code>TestAgentConfiguration</code> tests extension of the
* <code>ConfigurableAgent</code> base class.</p>
* @author Jerry Smith
* @version $Id: TestAgentConfiguration.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentConfiguration extends TestCase {
  private AgentContext context;
  private MyConfigurableAgent agent;

  public TestAgentConfiguration(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentConfiguration.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("X");
  }

  protected void tearDown() {
    // nothing
  }

  public void testConfiguration() {
    try {
      YaakProperties yaakProps =
        new YaakProperties("myConfigurableAgent.properties");
      yaakProps.load();
      Properties props = yaakProps.getProperties();
      agent = (MyConfigurableAgent)
        context.createAgent("yaak.test.agent.MyConfigurableAgent",
        (Object) props, "MyConfigurableAgent");
    }
    catch (AgentException e) {
      fail("Exception creating and/or configuring agent.");
    }
    assertEquals("'v' has been set properly.",
      agent.getV(), true);
    assertEquals("'w' has been set properly.",
      agent.getW(), 40);
    assertTrue("'x' has been set properly.",
      Math.abs(agent.getX() - 60.0) < 0.01);
    assertTrue("'y' has been set properly.",
      Math.abs(agent.getY() - 80.0) < 0.01);
    assertEquals("'z' has been set properly.",
      agent.getZ(), "zzz...");
  }
}
