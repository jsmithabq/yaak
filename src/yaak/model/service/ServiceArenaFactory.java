package yaak.model.service;

/**
* <p><code>ServiceArenaFactory</code> creates service arenas.</p>
* @author Jerry Smith
* @version $Id: ServiceArenaFactory.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class ServiceArenaFactory {
  private ServiceArenaFactory() {
  }

  /**
  * <p>Retrieves a service arena.</p>
  * @return The service arena.
  */

  public static ServiceArena getInstance() {
    return ServiceArenaImpl.getInstance();
  }

  /**
  * <p>Retrieves a service arena.</p>
  * param arenaName The logical name for the service arena.
  * @return The service arena.
  */

  public static ServiceArena getInstance(String arenaName) {
    return ServiceArenaImpl.getInstance(arenaName);
  }
}
