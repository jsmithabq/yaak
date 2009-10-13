package yaak.concurrent;

/**
* <p><code>DefaultExecutor</code> provides a mechanism for executing
* arbitrary operations.</p>
* @author Jerry Smith
* @version $Id: DefaultExecutor.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class DefaultExecutor implements Executor {
  /**
  * <p>Instantiates a stateless execution-manager object.</p>
  */

  public DefaultExecutor() {
  }

  /**
  * <p>Executes the runnable object.  The mechanics are:</p>
  * <ul>
  * <li>Create a (new) thread
  * <li>Assign a runnable object to the thread
  * <li>Start the thread
  * </ul>
  * @param runnable The object that implements
  * <code>Runnable</code>.
  */

  public void execute(Runnable runnable) {
    new Thread(runnable).start();
  }
}
