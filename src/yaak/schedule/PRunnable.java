package yaak.schedule;

/**
* <p><code>PRunnable</code> prescribes an identifier for each runnable.</p>
* @author Jerry Smith
* @version $Id: PRunnable.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface PRunnable extends Runnable {
  /**
  * <p>Provides access to an arbitrary, application-specific ID, which
  * the scheduler uses to identify the runnable task in messages, etc.
  * @return The application-specific identifying
  * string.
  */

  String getID();
}
