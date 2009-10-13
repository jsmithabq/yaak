package yaak.test.model;

import yaak.model.service.Service;

import junit.framework.*;

/**
* <p><code>GreetingService</code> prescribes a service for issuing a
* greeting.</p>
* @author Jerry Smith
* @version $Id: GreetingService.java 13 2006-02-12 18:30:51Z jsmith $
*/

public interface GreetingService extends Service {
  String makeGreeting();
}
