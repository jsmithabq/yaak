package yaak.core;

/**
* <p><code>YaakException</code> is the base exception class.</p>
* @author Jerry Smith
* @version $Id: YaakException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class YaakException extends Exception {
  /**
  * <p>Instantiates an exception.</p>
  */

  public YaakException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public YaakException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public YaakException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public YaakException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
