package yaak.concurrent;

/**
* <p><code>Executor</code> prescribes a simple contract for executing
* arbitrary operations.</p>
* @author Jerry Smith
* @version $Id: Executor.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface Executor {
  /**
  * <p>Executes the runnable object.</p>
  * @param runnable The object that implements
  * <code>Runnable</code>.
  */

  void execute(Runnable runnable);
}
