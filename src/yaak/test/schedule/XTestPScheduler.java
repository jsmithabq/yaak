package yaak.test.schedule;

import yaak.util.Util;
import yaak.schedule.PScheduler;

public class XTestPScheduler {
  public static void main(String[] args) {
    PScheduler scheduler = new PScheduler(5);
    scheduler.start();
    for (int i = 0; i < 50; i++) {
      if (scheduler.isAvailable()) {
        Worker w = new Worker(i);
        scheduler.add(w);
        Util.sleepSeconds(1);
      }
      else {
        System.out.println("Scheduler unavailable--in shutdown phase.");
      }
    }
    //
    // Test the shutdown functionality:
    //
//    scheduler.whack();
    scheduler.shutdownAndWait();
  }
}
