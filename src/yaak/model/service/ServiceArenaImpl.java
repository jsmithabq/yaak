package yaak.model.service;

import java.util.Hashtable;
import java.util.Vector;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentSystem;
import yaak.core.YaakException;
import yaak.core.YaakSystem;

import yaak.ontology.Knowledge;
import yaak.ontology.KnowledgeException;
import yaak.ontology.KnowledgeListener;

/**
* <p><code>ServiceArenaImpl</code> provides an arena for coordinating
* agent services.</p>
* @author Jerry Smith
* @version $Id: ServiceArenaImpl.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class ServiceArenaImpl implements ServiceArena {
  private static final String DEFAULT_ARENA_NAME = "ServiceArena";
  private AgentContext arena;
  private Hashtable knowledge = new Hashtable();
  private Hashtable services = new Hashtable();
  private Thread thread = null;
  private boolean running = false;
  private Object lock = new Object();

  /**
  * <p>Retrieves a service arena.</p>
  * @return The service arena.
  */

  protected static ServiceArena getInstance() {
    return new ServiceArenaImpl();
  }

  /**
  * <p>Retrieves a service arena.</p>
  * @param name The symbolic name for the arena.
  * @return The service arena.
  */

  protected static ServiceArena getInstance(String name) {
    return new ServiceArenaImpl(name);
  }

  private ServiceArenaImpl() {
    this(DEFAULT_ARENA_NAME);
  }

  private ServiceArenaImpl(String name) {
    initServiceArena(name);
  }

  private void initServiceArena(String name) {
    try {
      arena = AgentSystem.createAgentContext(name);
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot initiate arena operations:");
      e.printStackTrace();
    }
  }

  /**
  * <p>Disposes of this arena entirely (all allocated resources).</p>
  */

  public void dispose() {
    arena.dispose();
  }

  /**
  * <p>Starts services for this arena.</p>
  */

  public void start() {
    if (running) {
      return;
    }
    synchronized (lock) {
      running = true;
    }
/**********************************************************
    thread = new Thread();
    thread.start();
**********************************************************/
  }

  /**
  * <p>Shuts down services for this arena.</p>
  */

  public void shutdown() {
    synchronized (lock) {
      running = false;
    }
/**********************************************************
    try {
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot shut down service arena:");
      e.printStackTrace();
    }
    thread = null;
**********************************************************/
  }

  /**
  * <p>Registers a knowledge provider.</p>
  * @param listener The to-be-registered provider for the advertised
  * knowledge.
  */

  public final void addKnowledgeListener(KnowledgeListener listener) {
    synchronized (knowledge) {
      String knowledgeType = (String) listener.getKnowledgeTypeAsString();
      YaakSystem.getLogger().fine("Adding knowledge type: '" +
        knowledgeType + "'.");
      Vector knowledgeListeners = (Vector) knowledge.get(knowledgeType);
      if (knowledgeListeners == null) { // first time
        knowledgeListeners = new Vector();
        knowledge.put(knowledgeType, knowledgeListeners);
      }
      knowledgeListeners.add(listener);
    }
  }

  /**
  * <p>Unregisters a knowledge provider.</p>
  * @param listener The registered provider for the advertised knowledge.
  */

  synchronized public final void removeKnowledgeListener(
      KnowledgeListener listener) {
    synchronized (knowledge) {
      String knowledgeType = (String) listener.getKnowledgeTypeAsString();
      Vector knowledgeListeners = (Vector) knowledge.get(knowledgeType);
      if (knowledgeListeners != null) {
        knowledgeListeners.remove(listener);
      }
    }
  }

  /**
  * <p>Requests that the arena negotiate the optimal knowledge request
  * with an appropriate agent.</p>
  * @param knowledge The requested knowledge.
  * @param agent The requesting agent.
  */

  synchronized public final Knowledge requestKnowledge(Knowledge knowledge,
      Agent agent) {
    Class[] interfaces = knowledge.getClass().getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      String interfaceName = interfaces[i].getName();
      YaakSystem.getLogger().fine("Next knowledge interface is: '" +
        interfaceName + "'.");
      if (Knowledge.class.isAssignableFrom(interfaces[i])) {
        YaakSystem.getLogger().fine("Qualifying knowledge: '" +
          interfaceName + "'.");
        return requestKnowledge(interfaces[i], agent);
      }
    }
    return null;
  }

  /**
  * <p>Requests that the arena negotiate the optimal knowledge request
  * with an appropriate agent.</p>
  * @param c The requested knowledge, as represented by a
  * <code>Class</code> object.
  * @param agent The requesting agent.
  * @throws KnowledgeException Unable to honor knowledge request.
  */

  synchronized public final Knowledge requestKnowledge(Class c, Agent agent) {
    synchronized (knowledge) {
      String knowledgeType = c.getName();
      Vector knowledgeListeners = (Vector) knowledge.get(knowledgeType);
      if (knowledgeListeners == null) {
        throw new KnowledgeException(
          "Requested knowledge not currently available: '" +
          knowledgeType + "'.");
      }
      for (int i = 0; i < knowledgeListeners.size(); i++) {
        KnowledgeListener listener = (KnowledgeListener)
          knowledgeListeners.elementAt(i);
        YaakSystem.getLogger().fine(
          "Executing getKnowledge() with listener: '" +
          listener.getClass().getName() + "'.");
        Knowledge k = listener.getKnowledge();
        if (k != null) {
          YaakSystem.getLogger().fine("Found knowledge: '" + k + "'.");
          return k;
        }
      }
      throw new KnowledgeException("Requested knowledge returned 'null'.");
    }
  }

  /**
  * <p>Registers a service provider.</p>
  * @param listener The to-be-registered provider for the advertised service.
  */

  public final void addServiceListener(ServiceListener listener) {
    synchronized (services) {
      String serviceType = (String) listener.getServiceTypeAsString();
      YaakSystem.getLogger().fine("Adding service type: '" +
        serviceType + "'.");
      Vector serviceListeners = (Vector) services.get(serviceType);
      if (serviceListeners == null) { // first time
        serviceListeners = new Vector();
        services.put(serviceType, serviceListeners);
      }
      serviceListeners.add(listener);
    }
  }

  /**
  * <p>Unregisters a service provider.</p>
  * @param listener The registered provider for the advertised service.
  */

  synchronized public final void removeServiceListener(
      ServiceListener listener) {
    synchronized (services) {
      String serviceType = (String) listener.getServiceTypeAsString();
      Vector serviceListeners = (Vector) services.get(serviceType);
      if (serviceListeners != null) {
        serviceListeners.remove(listener);
      }
    }
  }

  /**
  * <p>Requests that the arena negotiate the optimal service request
  * with an appropriate agent.</p>
  * @param service The requested service.
  * @param agent The requesting agent.
  */

  synchronized public final void requestService(Service service,
      Agent agent) {
    Class[] interfaces = service.getClass().getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      String interfaceName = interfaces[i].getName();
      YaakSystem.getLogger().fine("Next service interface is: '" +
        interfaceName + "'.");
      if (Service.class.isAssignableFrom(interfaces[i])) {
        YaakSystem.getLogger().fine("Qualifying service: '" +
          interfaceName + "'.");
        requestService(interfaces[i], agent);
        break; // only try the first one!
      }
    }
  }

  /**
  * <p>Requests that the arena negotiate the optimal service request
  * with an appropriate agent.</p>
  * @param c The requested service, as represented by a
  * <code>Class</code> object.
  * @param agent The requesting agent.
  * @throws KnowledgeException Unable to honor knowledge request.
  */

  synchronized public final void requestService(Class c, Agent agent) {
    synchronized (services) {
      String serviceType = c.getName();
      Vector serviceListeners = (Vector) services.get(serviceType);
      if (serviceListeners == null) {
        throw new ServiceException(
          "Requested service not currently available: '" +
          serviceType + "'.");
      }
      for (int i = 0; i < serviceListeners.size(); i++) {
        ServiceListener listener = (ServiceListener)
          serviceListeners.elementAt(i);
        ServiceNotification notification = new ServiceNotification(agent);
        YaakSystem.getLogger().fine(
          "Executing performService() with listener: '" +
          listener.getClass().getName() + "'.");
        Object result = listener.performService(notification);
        if (result != null) {
          YaakSystem.getLogger().fine(
            "Performed service with result: '" + result + "'.");
          return;
        }
      }
      throw new ServiceException("Requested service returned a null result.");
    }
  }
}
