package yaak.model.service;

import yaak.agent.Agent;

import yaak.ontology.Knowledge;
import yaak.ontology.KnowledgeListener;

/**
* <p>This <code>ServiceArena</code> interface represents an arena for
* coordinating agent services.  A service arena can be instantiated
* anywhere; however, {@link yaak.model.World World} provides an
* encapsulated arena for routine service delegation.</p>
* @author Jerry Smith
* @version $Id: ServiceArena.java 13 2006-02-12 18:30:51Z jsmith $
*/

public interface ServiceArena {
  /**
  * <p>Disposes of this arena entirely (all allocated resources).</p>
  */

  void dispose();

  /**
  * <p>Starts management services for this arena.</p>
  */

  void start();

  /**
  * <p>Shuts down management services for this arena.</p>
  */

  void shutdown();

  /**
  * <p>Registers a knowledge provider.</p>
  * @param listener The to-be-registered provider for the advertised knowledge.
  */

  void addKnowledgeListener(KnowledgeListener listener);

  /**
  * <p>Unregisters a knowledge provider.</p>
  * @param listener The registered provider for the advertised knowledge.
  */

  void removeKnowledgeListener(KnowledgeListener listener);

  /**
  * <p>Requests that the arena negotiate the optimal knowledge request
  * with an appropriate agent.</p>
  * @param knowledge The requested knowledge.
  * @param agent The requesting agent.
  */

  Knowledge requestKnowledge(Knowledge knowledge, Agent agent);

  /**
  * <p>Requests that the arena negotiate the optimal knowledge request
  * with an appropriate agent.</p>
  * @param c The requested knowledge, as represented by a
  * <code>Class</code> object.
  * @param agent The requesting agent.
  */

  Knowledge requestKnowledge(Class c, Agent agent);

  /**
  * <p>Registers a service provider.</p>
  * @param listener The to-be-registered provider for the advertised service.
  */

  void addServiceListener(ServiceListener listener);

  /**
  * <p>Unregisters a service provider.</p>
  * @param listener The registered provider for the advertised service.
  */

  void removeServiceListener(ServiceListener listener);

  /**
  * <p>Requests that the arena negotiate the optimal service request
  * with an appropriate agent.</p>
  * @param service The requested service.
  * @param agent The requesting agent.
  */

  void requestService(Service service, Agent agent);

  /**
  * <p>Requests that the arena negotiate the optimal service request
  * with an appropriate agent.</p>
  * @param c The requested service, as represented by a
  * <code>Class</code> object.
  * @param agent The requesting agent.
  */

  void requestService(Class c, Agent agent);
}
