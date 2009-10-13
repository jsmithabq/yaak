package yaak.agent;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import yaak.agent.event.AgentContextListener;
import yaak.agent.event.AgentContextEvent;
import yaak.agent.communication.Executive;
import yaak.agent.communication.MessageFilter;
import yaak.core.YaakSystem;

/**
* <p><code>AgentContainer</code> implements
* {@link yaak.agent.AgentContext AgentContext}, providing an agent
* habitat accessible via the service methods defined by
* <code>AgentContext</code>.</p>
* <p>See {@link yaak.agent.AgentContext AgentContext} for usable services.</p>
* <p>This class is <em>not</em> a singleton and it is <em>not</em> public!</p>
* @author Jerry Smith
* @version $Id: AgentContainer.java 13 2006-02-12 18:30:51Z jsmith $
* @see yaak.agent.AgentContext
*/

public final class AgentContainer extends Agent
    implements AgentContext, PersonalAgentContext {
  private static final String DEFAULT_CONTEXT_NAME = "AgentContext";
  private String contextName = DEFAULT_CONTEXT_NAME;
  //
  // Maintain indexes (manually) on top of hash table (index the hash table!):
  //
  private Vector agentIndexID = new Vector();
  private Hashtable agentTable = new Hashtable();
  private HashMap decoration = new HashMap();
  //
  // The next object is synchronized separately:
  //
  private Vector contextListeners = new Vector();
  private CommunicationExecutive cExec;
  private MessageExecutive mExec;
  private Object lock = new Object();
  private boolean initialized = false;

  /**
  * <p>Provides an <code>AgentContext</code> object directly.  Applications
  * should not use this method directly because its continued availability
  * is not guaranteed.</p>
  * <p>Applications, by framework convention, create agents (specializations
  * of <code>Agent</code>) by requesting an agent from an agent context, which
  * (also by framework convention) is obtained via a request to
  * <code>AgentSystem</code>:</p>
  * <pre>
  *   ...
  *   AgentContext ac = AgentSystem.createAgentContext();
  *   ac.createAgent("Hello", null); // 'Hello' extends 'Agent'
  *   ...
  * </pre>
  * @return The object that implements the <code>AgentContext</code> interface.
  * @see yaak.agent.AgentSystem
  */

  static AgentContext getInstance() { // default privacy
    return new AgentContainer();
  }

  /**
  * <p>Provides an <code>AgentContext</code> object directly.  Applications
  * should not use this method directly because its continued availability
  * is not guaranteed.</p>
  * <p>Applications, by framework convention, create agents (specializations
  * of <code>Agent</code>) by requesting an agent from an agent context, which
  * (also by framework convention) is obtained via a request to
  * <code>AgentSystem</code>:</p>
  * <pre>
  *   ...
  *   AgentContext ac = AgentSystem.createAgentContext("MyAgentPod");
  *   ac.createAgent("Hello", null); // 'Hello' extends 'Agent'
  *   ...
  * </pre>
  * @param contextName The external/symbolic name for the context.
  * @return The object that implements the <code>AgentContext</code> interface.
  * @see yaak.agent.AgentSystem
  */

  static AgentContext getInstance(String contextName) { // default privacy
    return new AgentContainer(contextName);
  }

  /**
  * <p>Not for public consumption.</p>
  */

  protected AgentContainer() {
    this(null);
  }

  /**
  * <p>Not for public consumption.</p>
  * @param contextName The external/symbolic name for the context.
  */

  protected AgentContainer(String contextName) {
    super();
    if (contextName != null && contextName.length() != 0) {
      this.contextName = contextName;
    }
    init();
  }

  private void init() {
    cExec = new CommunicationExecutive(this);
    mExec = new MessageExecutive(this);
    initialized = true;
  }

  /**
  * <p>The state of this variable is only a guideline, because there are
  * an unlimited number of potenial channel negotiations with sibling
  * containers.</p>
  * @return Whether or not the initialization is complete.
  */

  public final boolean isInitialized() {
    return initialized;
  }

  /**
  * <p>The context name for the agent context.</p>
  * @return The context name.
  */

  public String getContextName() {
    return contextName;
  }

  void setContextName(String contextName) { // default privacy
    this.contextName = contextName;
  }

  /**
  * <p>The communication (layer) executive for the agent context.</p>
  * @return The executive.
  */

  public yaak.agent.communication.Executive getCommunicationExecutive() {
    return cExec;
  }

  /**
  * <p>The message (layer) executive for the agent context.</p>
  * @return The executive.
  */

  public yaak.agent.message.Executive getMessageExecutive() {
    return mExec;
  }

  /**
  * <p>Disposes of this agent context, which simply disposes of each
  * agent in this context and closes any communication-related
  * resources.</p>
  */

  synchronized public void dispose() {
    Enumeration e = agentTable.elements();
    while (e.hasMoreElements()) {
      Agent agent = (Agent) e.nextElement();
      try {
        YaakSystem.getLogger().finer(
         "dispose of agent: " + agent.getAgentName() + ".");
        disposeAgent(agent);
      }
      catch (AgentException ae) {
        // fail silently ???
      }
    }
    agentIndexID.clear();
    agentTable.clear();
    //
    // Should disposal be applied to agents only, or
    // to other objects such as decoration(s)???
    //
//    decoration.clear();
    getCommunicationExecutive().close();
  }

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  public Agent createAgent(Class c) throws AgentException {
    return createAgent(c, null, null);
  }

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  public Agent createAgent(Class c, String agentName)
      throws AgentException {
    return createAgent(c, null, agentName);
  }

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  public Agent createAgent(String classname) throws AgentException {
    return createAgent(classname, null, null);
  }

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  public Agent createAgent(String classname, String agentName)
      throws AgentException {
    return createAgent(classname, null, agentName);
  }

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

/*
  public Agent createAgent(String classname, Object args)
      throws AgentException {
    return createAgent(classname, args, null);
  }
*/

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  public Agent createAgent(String classname, Object args,
      String agentName) throws AgentException {
    Class c = null;
    try {
      c = Class.forName(classname);
    }
    catch (Exception e) {
      throw new AgentException("Cannot locate class.");
    }
    return createAgent(c, args, agentName);
  }

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

/*
  public Agent createAgent(Class c, Object args)
      throws AgentException {
    return createAgent(c, args, null);
  }
*/

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  //
  // For now, lock the container for operations against these
  // critical resorces:  agentIndexID and agentTable.
  // Later, verify possibility of resource-level locking to allow
  // greater concurrency, but only if deadlock can be avoided.
  //

  synchronized public Agent createAgent(Class c, Object args,
      String agentName) throws AgentException {
    if (c == null) {
      throw new AgentException("Class reference is null.");
    }
    Agent agent;
    try {
      agent = (Agent) c.newInstance();
      agent.setPersonalAgentContext(this);
    }
    catch (ClassCastException e) {
      throw new AgentException(
        "Class does not specialize 'yaak.agent.Agent'.");
    }
    catch (Exception e) {
      throw new AgentException("Cannot create class instance.");
    }
    AgentID id = AgentID.generateID();
    agent.setAgentID(id);
    if (!insertIntoIDIndex(id)) {
      throw new AgentException(
        "Cannot add agent due to uniqueness constraints.");
    }
    agent.setAgentName(agentName);
    agentTable.put(id.toString(), agent);
    agent.onCreation(args);
    notifyListenersOnCreate(
      new AgentContextEvent(AgentContextEvent.AGENT_CREATED, this,
        agent, id));
    return agent;
  }

  /**
  * <p>Disposes of an agent (removes it from the agent context).</p>
  * @param id The reference ID for the agent.
  * @throws AgentException Unable to locate the agent for disposal.
  */

  synchronized public void disposeAgent(AgentID id) throws AgentException {
    if (id == null) {
      throw new AgentException("AgentID reference is null.");
    }
    String sid = id.toString();
    Agent agent = (Agent) agentTable.get(sid);
    if (agent == null) {
      throw new AgentException(
        "No agent with the specified ID: " + sid + ".");
    }
    disposeAgent(agent);
  }

  /**
  * <p>Disposes of an agent (removes it from the agent context).</p>
  * @param agent The agent.
  * @throws AgentException The agent reference is null or invalid.
  */

  synchronized public void disposeAgent(Agent agent) throws AgentException {
    if (agent == null) {
      throw new AgentException("Agent reference is null.");
    }
    AgentID id = agent.getAgentID();
    agent.onDisposing();
//    if (agent instanceof yaak.agent.AgentContext) {
//      ((AgentContext) agent).dispose();
//    }
    agent.dispose();
    notifyListenersOnDispose(
      new AgentContextEvent(AgentContextEvent.AGENT_DISPOSED, this, agent, id));
    agentTable.remove(id.toString());
    removeFromIDIndex(id);
  }

  /**
  * <p>Registers a listener/observer for context-related events.</p>
  * @param listener The to-be-registered listener object.
  */

  public void addAgentContextListener(AgentContextListener listener) {
    synchronized (contextListeners) {
      contextListeners.add(listener);
    }
  }

  /**
  * <p>Unregisters a listener/observer for context-related events.</p>
  * @param listener The (registered) listener to unregister.
  */

  public void removeAgentContextListener(AgentContextListener listener) {
    synchronized (contextListeners) {
      contextListeners.remove(listener);
    }
  }

  private void notifyListenersOnCreate(AgentContextEvent event) {
    Vector acls;
    synchronized (contextListeners) {
      acls = (Vector) contextListeners.clone();
    }
    for (int i = 0; i < acls.size(); i++) {
      AgentContextListener acl = (AgentContextListener) acls.elementAt(i);
      acl.agentCreated(event);
    }
  }

  private void notifyListenersOnDispose(AgentContextEvent event) {
    Vector acls;
    synchronized (contextListeners) {
      acls = (Vector) contextListeners.clone();
    }
    for (int i = 0; i < acls.size(); i++) {
      AgentContextListener acl = (AgentContextListener) acls.elementAt(i);
      acl.agentDisposed(event);
    }
  }

  private Agent getAgentFromID(AgentID id) throws AgentException {
    Agent agent = (Agent) agentTable.get(id.toString());
    if (agent == null) {
      throw new AgentException(
        "No agent with the specified ID: " + id + ".");
    }
    return agent;
  }

  /**
  * <p>Returns a snapshot-like list of IDs, as an array, independent
  * of the dynamically changing context.  The elements are
  * {@link yaak.agent.AgentID AgentID} references.</p>
  * @return The array of agent IDs.
  */

  synchronized public AgentID[] getAgentIDs() {
    AgentID[] ids = new AgentID[agentIndexID.size()];
    for (int i = 0; i < agentIndexID.size(); i++) {
      ids[i] = (AgentID) agentIndexID.elementAt(i);
    }
    return ids;
  }

  /**
  * <p>Returns a snapshot-like list of IDs, as a {@link java.util.List List},
  * independent of the dynamically changing context.  The elements
  * are {@link yaak.agent.AgentID AgentID} references.</p>
  * @return The list of agent IDs.
  */

  synchronized public List getAgentIDList() {
    return (Vector) agentIndexID.clone();
  }

  /**
  * <p>Returns a snapshot-like list of agents, as an array, independent
  * of the dynamically changing context.  The elements are
  * {@link yaak.agent.Agent Agent} references.</p>
  * @return The array of agents.
  */

  synchronized public Agent[] getAgents() {
    Agent[] agents = new Agent[agentIndexID.size()];
    for (int i = 0; i < agentIndexID.size(); i++) {
      AgentID aid = (AgentID) agentIndexID.elementAt(i);
      try {
        agents[i] = (Agent) getAgentFromID(aid);
      }
      catch (Exception e) {
        //
        // Not clear that it's possible, so do not document this!
        //
        throw new AgentRuntimeException(
          "Whoa!  Internal error in " +
          getClass().getName() + ".getAgents()...");
      }
    }
    return agents;
  }

  /**
  * <p>Returns a snapshot-like list of agents, as a
  * {@link java.util.List List}, independent of the dynamically
  * changing context.  The elements are {@link yaak.agent.Agent Agent}
  * references.</p>
  * @return The list of agents.
  */

  synchronized public List getAgentList() {
    ArrayList agentList = new ArrayList(agentIndexID.size());
    for (int i = 0; i < agentIndexID.size(); i++) {
      AgentID aid = (AgentID) agentIndexID.elementAt(i);
      try {
        agentList.add(getAgentFromID(aid));
      }
      catch (Exception e) {
        //
        // Not clear that it's possible, so do not document this!
        //
        throw new RuntimeException(
          "Whoa!  Internal error in " +
          getClass().getName() + ".getAgentList()...");
      }
    }
    return agentList;
  }

  /**
  * <p>Provides look-up operations for the agent's name.</p>
  * @return The agent's name.
  * @throws AgentException Unable to locate the agent.
  */

  synchronized public String getAgentNameByID(AgentID id)
      throws AgentException {
    String sid = id.toString();
    Agent agent = (Agent) agentTable.get(sid);
    if (agent == null) {
      throw new AgentException(
        "No agent with the specified ID: " + sid + ".");
    }
    return agent.getAgentName();
  }

  /**
  * <p>Provides context-level storage for shared data (context
  * decorations).</p>
  * @param key The key for the indexed decoration.
  * @param object The to-be-stored decoration.
  */

  public void putDecoration(Object key, Object object) {
    synchronized (lock) {
      decoration.put(key, object);
    }
  }

  /**
  * <p>Provides look-up services for context-level decorations.</p>
  * @param key The key for the indexed decoration.
  * @return The decoration.
  */

  public Object getDecoration(Object key) {
    Object decor = null;
    synchronized (lock) {
      decor = decoration.get(key);
    }
    return decor; // allow stale reference
  }

  /**
  * <p>Makes the encapsulated, shared decorations available for direct
  * access.</p>
  */

  public Map getDecorations() {
    return decoration;
  }

  /**
  * <p>Clears all context-level decorations.</p>
  */

  public void clearDecorations() {
    synchronized (lock) {
      decoration.clear();
    }
  }

  /**
  * <p>Registers the <em>default</em> message filter for
  * <em>agent-directed</em> messages.</p>
  * @param filter The message filter.
  */

  public void registerDefaultMessageFilter(
      MessageFilter filter) {
    cExec.registerDefaultMessageFilter(filter);
  }

  /**
  * <p>Unregisters the <em>default</em> message filter for
  * <em>agent-directed</em> messages.</p>
  */

  public void unregisterDefaultMessageFilter() {
    cExec.unregisterDefaultMessageFilter();
  }

  //
  // Agent IDs are "indexed" in a vector in ascending order: 
  //

  boolean checkNoDuplicateID(AgentID id) { // default privacy
    return findPosForAgentID(id) != -1;
  }

  int findPosForAgentID(AgentID id) { // default privacy
    int limit = agentIndexID.size();
    for (int i = 0; i < limit; i++) {
      AgentID next = (AgentID) agentIndexID.elementAt(i);
      if (next.isLessThan(id)) {
        continue;
      }
      else if (next.equals(id)) {
        return -1;
      }
      else {
        return i;
      }
    }
    return limit;
  }

  boolean insertIntoIDIndex(AgentID id) { // default privacy
    int pos = findPosForAgentID(id);
    if (pos == -1) {
      return false;
    }
    else {
      agentIndexID.insertElementAt(id, pos);
      return true;
    }
  }

  boolean removeFromIDIndex(AgentID id) { // default privacy
    return agentIndexID.removeElement(id);
  }
}
