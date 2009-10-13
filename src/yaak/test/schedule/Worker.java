package yaak.test.schedule;

import yaak.schedule.PRunnable;
import yaak.util.Util;

public class Worker implements PRunnable {
  int id;

  public Worker(int id) {
    this.id = id;
  }

  public String getID() {
    return "" + id;
  }

  public void run() {
    System.out.println("==== Worker " + id + " starting...");
    Util.sleepSeconds(20);
    System.out.println("==== Worker " + id + " finishing...");
  }
}
