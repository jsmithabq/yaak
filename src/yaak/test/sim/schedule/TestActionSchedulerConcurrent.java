package yaak.test.sim.schedule;

import yaak.core.YaakSystem;
import yaak.sim.schedule.Action;
import yaak.sim.schedule.ActionGroup;
import yaak.sim.schedule.ActionScheduler;
import yaak.sim.schedule.ScheduleException;

import java.util.Vector;

import junit.framework.*;

/**
* <p><code>TestActionSchedulerConcurrent</code> tests basic scheduling for
* actions and action groups.</p>
* @author Jerry Smith
* @version $Id: TestActionSchedulerConcurrent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestActionSchedulerConcurrent extends TestCase {
  private Vector groupActions = new Vector();
  private ActionGroup actionGroup;

  static {
    yaak.core.YaakSystem.initFramework();
  }

  public TestActionSchedulerConcurrent(String name) {
    super(name);
    makeActions();
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestActionSchedulerConcurrent.class);
  }

  private void makeActions() {
    groupActions.add(new Action() {
      public void execute() {
        System.out.println("group action 1...");
      }
    });
    groupActions.add(new Action() {
      public void execute() {
        System.out.println("group action 2...");
      }
    });
    groupActions.add(new Action() {
      public void execute() {
        System.out.println("group action 3...");
      }
    });
    actionGroup = new ActionGroup(groupActions);
  }

  protected void setUp() {
  }

  protected void tearDown() {
    // nothing
  }

  public void testScheduleWithActionGroupConcurrent() {
    System.out.println("");
    try {
      ActionScheduler scheduler = new ActionScheduler();
      actionGroup.setConcurrent(true);
      scheduler.addAction(actionGroup, 1);
      scheduler.start();
    }
    catch (ScheduleException e) {
      YaakSystem.getLogger().warning("exception scheduling concurrent actions.");
      e.printStackTrace();
    }
  }
}
