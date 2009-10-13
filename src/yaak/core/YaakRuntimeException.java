package yaak.core;

/**
* <p><code>YaakRuntimeException</code> extends
* <code>RuntimeException</code>,
* as a base for reporting unchecked, agent-related exceptions.</p>
* @author Jerry Smith
* @version $Id: YaakRuntimeException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class YaakRuntimeException extends RuntimeException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public YaakRuntimeException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public YaakRuntimeException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public YaakRuntimeException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public YaakRuntimeException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
