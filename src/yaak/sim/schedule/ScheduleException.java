package yaak.sim.schedule;

/**
*<p><code>ScheduleException</code> reports extraordinary
* operations and/or states during scheduling.</p>
* @author Jerry Smith
* @version $Id: ScheduleException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ScheduleException extends RuntimeException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public ScheduleException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public ScheduleException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public ScheduleException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public ScheduleException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
