package yaak.model.service;

/**
* <p><code>ServiceListener</code> allows agents to register with an
* arena to provide services.</p>
* @author Jerry Smith
* @version $Id: ServiceListener.java 13 2006-02-12 18:30:51Z jsmith $
*/

public interface ServiceListener {
  /**
  * <p>Provides the contract service.</p>
  * @return The object implementing contracted service.
  */

  Service getService();

  /**
  * <p>Identifies the contract service.</p>
  * @return The <code>Class</code> object identifying the service.
  */

  Class getServiceType();

  /**
  * <p>Identifies the contract service.</p>
  * @return The string identifying the service's interface.
  */

  String getServiceTypeAsString();

  /**
  * <p>Implements the registered service provided by the agent.</p>
  * @param notification The notification object generated by the
  * service arena.
  * @return The arbitrary result object.
  */

  Object performService(ServiceNotification notification);

  /**
  * <p>Handles, if possible, the requested service.  The service arena
  * will identify the requested service, as well as provide the
  * standard notification data.</p>
  * @param service The requested service.
  * @param notification The notification object generated by the
  * service arena.
  * @return The arbitrary result object.
  */

  Object performService(Service service, ServiceNotification notification);

  /**
  * <p>Indicates whether or not the agent is equipped to provide
  * the specified service.  An agent may agree to perform a
  * service other than the registered service.</p>
  * @param service The requested service.
  * @return The agents willingness to initiate the service request.
  */

  boolean canDo(Service service);
}