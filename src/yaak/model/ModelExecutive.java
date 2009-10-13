package yaak.model;

/**
* <p><code>ModelExecutive</code> is the coordinating, organizing entity
* for handling domain operations.  It provides a domain-layer bridge to
* the simulation layer, which simply processes queued actions.</p>
* <p>ModelExecutive is, in essence, a domain engine that drives the
* simulation application.</p>
* @author Jerry Smith
* @version $Id: ModelExecutive.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ModelExecutive {
  protected World world;

  /**
  * <p>Not for public consumption.</p>
  */

  protected ModelExecutive() {
  }

  /**
  * <p>Not for public consumption.</p>
  */

  protected ModelExecutive(World world) {
    this.world = world;
  }

  /**
  * <p>Sets the world.</p>
  * @param world The simulation world/environment.
  */

  public void setWorld(World world) {
    this.world = world;
  }
}
