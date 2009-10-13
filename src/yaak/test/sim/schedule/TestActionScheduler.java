package yaak.test.sim.schedule;

import yaak.core.YaakSystem;
import yaak.sim.schedule.Action;
import yaak.sim.schedule.ActionGroup;
import yaak.sim.schedule.ActionScheduler;
import yaak.sim.schedule.ScheduleException;

import java.util.Vector;

import junit.framework.*;

/**
* <p><code>TestActionScheduler</code> tests basic scheduling for
* actions and action groups.</p>
* @author Jerry Smith
* @version $Id: TestActionScheduler.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestActionScheduler extends TestCase {
  private Action action1, action2, action3;
  private Vector groupActions = new Vector();
  private ActionGroup actionGroup;

  static {
    yaak.core.YaakSystem.initFramework();
  }

  public TestActionScheduler(String name) {
    super(name);
    makeActions();
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestActionScheduler.class);
  }

  private void makeActions() {
    action1 = new Action() {
      public void execute() {
        System.out.println("action 1...");
      }
    };
    action2 = new Action() {
      public void execute() {
        System.out.println("action 2...");
      }
    };
    action3 = new Action() {
      public void execute() {
        System.out.println("action 3...");
      }
    };
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

  public void testScheduleWithActions() {
    System.out.println("");
    try {
      ActionScheduler scheduler = new ActionScheduler();
      scheduler.addAction(action1, 1);
      scheduler.addAction(action2, 2);
      scheduler.addAction(action3, 3);
      scheduler.start();
    }
    catch (ScheduleException e) {
      YaakSystem.getLogger().warning("exception scheduling actions.");
      e.printStackTrace();
    }
  }

  public void testScheduleWithActionsAndActionGroup() {
    System.out.println("");
    try {
      ActionScheduler scheduler = new ActionScheduler();
      scheduler.addAction(action1, 1);
      scheduler.addAction(actionGroup, 2);
      scheduler.addAction(action2, 3);
      scheduler.addAction(action3, 4);
      scheduler.start();
    }
    catch (ScheduleException e) {
      YaakSystem.getLogger().warning("exception scheduling actions.");
      e.printStackTrace();
    }
  }
}
