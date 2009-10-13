package yaak.schedule;

import yaak.core.YaakException;
import yaak.core.YaakRuntimeException;
import yaak.core.YaakSystem;

/**
* <p><code>GScheduler</code> provides a global (application-level)
* scheduler and thread pool using
* {@link yaak.schedule.PScheduler PScheduler}.</p>
* @author Jerry Smith
* @version $Id: GScheduler.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class GScheduler {
  private static final String GSCHEDULER_THREADS =
    "yaak.scheduler.threads";
  private static final String DEFAULT_GSCHEDULER_THREADS = "10";
  private static String numThreads = DEFAULT_GSCHEDULER_THREADS;
  private static PScheduler scheduler = null;

  static {
    YaakSystem.initFramework();
    setupConfiguration();
  }

  private GScheduler() {
  }

  private static void setupConfiguration() {
    try {
      numThreads =
        YaakSystem.getProperty(GSCHEDULER_THREADS,
          DEFAULT_GSCHEDULER_THREADS);
    }
    catch (YaakException e) {
      YaakSystem.getLogger().warning("cannot load properties: ");
      e.printStackTrace();
    }
  }

  /**
  * <p>Starts the global scheduler.</p>
  */

  public final static void start() {
    if (scheduler == null) {
      YaakSystem.getLogger().fine("initializing...");
      scheduler = new PScheduler(new Integer(numThreads).intValue());
      YaakSystem.getLogger().fine("starting...");
      scheduler.start();
    }
  }

  /**
  * <p>Shuts down the global scheduler.</p>
  */

  public final static void shutdown() {
    if (scheduler != null) {
      YaakSystem.getLogger().fine("shutting down...");
      scheduler.shutdownUponIdle();
      scheduler = null;
    }
  }

  /**
  * <p>Sets the runnable object--the object that implements the
  * scheduled work/task.</p>
  * @param runnable The object that implements
  * <code>PRunnable</code>.
  */

  public final static void add(PRunnable runnable) {
    if (scheduler == null) {
      throw new YaakRuntimeException(
        "must start scheduler before adding tasks.");
    }
    scheduler.add(runnable);
  }
}
