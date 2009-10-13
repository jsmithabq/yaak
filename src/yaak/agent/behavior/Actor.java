package yaak.agent.behavior;

import yaak.agent.Agent;

/**
* <p><code>Actor</code> is the base actor agent for sense-reason-act
* operations.</p>
* @author Jerry Smith
* @version $Id: Actor.java 6 2005-06-08 17:00:15Z jsmith $
*/

abstract public class Actor extends Agent {
  private static final String DEFAULT_AGENT_NAME = "anonymous-actor";
  private boolean actCompleted = false;
  private Object actResultObject; // set below

  /**
  * <p>Not for public consumption.</p>
  * <p><code>Actor()</code> is never instantiated directly.</p>
  */

  protected Actor() {
    super();
    setAgentName(DEFAULT_AGENT_NAME);
  }

  /**
  * <p>Indicates whether or not the actor's {@link #act act()} method
  * has completed.</p>
  * @return The act status.
  */

  synchronized public boolean isActCompleted() {
    return actCompleted;
  }

  /**
  * <p>Retrieves the result of the actor's {@link #act act()} method.</p>
  * @return The object produced by {@link #act act()}.
  */

  synchronized public Object getActResult() {
    return actResultObject;
  }

  /**
  * <p>Performs agent-specific operation(s).  This method is executed
  * automatically when an actor's <code>run()</code> method is invoked.
  * Unlike with {@link yaak.agent.Agent Agent}, you cannot override this
  * method.</p>
  * <p>There are two approaches to associating executable "behavior"
  * with an actor:</p>
  * <ol>
  * <li>Specialize {@link yaak.agent.behavior.Actor Actor}, overriding
  * one or more of the following methods:
  * <ul>
  * <li>{@link #sense sense()}
  * <li>{@link #reason reason()}
  * <li>{@link #act act()}
  * </ul>
  * which exhausts the single inheritance possibility in order to
  * provide custom behavior.
  * <pre>
  *   ...
  *   class MyActor extends Actor {
  *     public boolean sense() {
  *       // sense, sense, ...
  *       // return true in order to continue with reason()
  *     }
  *     public boolean reason() {
  *       // think, think, ...
  *       // return true in order to continue with act()
  *     }
  *     public Object act() {
  *       // act, act, ...
  *       // return an arbitrary object, or null
  *     }
  *   }
  *   ...
  *   Actor actor = agentContext.createAgent(MyActor.class);
  *   ...
  *   new DefaultExecutor().execute(actor);
  *   ...
  * </pre>
  * <li>Set the agent behavior for any agent, which (the default)
  * <code>execute()</code> attempts to execute.
  * <pre>
  *   ...
  *   class MyActorBehavior implements ActorBehavior {
  *     public void execute() {
  *       // perform behavior (operations)
  *     }
  *   }
  *   ...
  *   Actor actor = agentContext.createAgent(AnyActor.class);
  *   ...
  *   actor.setAgentBehavior(new MyActorBehavior());
  *   ...
  *   new DefaultExecutor().execute(actor);
  *   ...
  * </pre>
  * </ol>
  * <p><em>Note that, with this execution option, whether or not
  * {@link #sense sense()}, {@link #reason reason()}, or
  * {@link #act act()} are actually invoked is totally (1) up to the
  * supplied behavior object, and (2) dependent (still) on (a) specializing
  * {@link yaak.agent.behavior.Actor Actor} to provide sense-reason-act
  * behavior, or (b) coding a custom sense-reason-act cycle within the
  * behavior object (ignoring {@link #sense sense()},
  * {@link #reason reason()}, and {@link #act act()} within
  * {@link yaak.agent.behavior.Actor Actor}).</em>  In other words, the
  * supplied behavior provides the sense-reason-act protocol for
  * {@link yaak.agent.behavior.Actor Actor}, optionally, using the
  * actor's supplied sense-reason-act operations or supplying
  * sense-reason-act protocol and operations of its own design.</p>
  * <p>The latter approach (setting the behavior object <em>and</em>
  * specializing {@link yaak.agent.behavior.Actor Actor} to provide the
  * actual sense-reason-act operations, is the default, in the sense that
  * {@link yaak.agent.behavior.Actor#execute Actor.execute()} attempts to
  * invoke {@link yaak.agent.behavior.ActorBehavior#execute(Actor actor)
  * ActorBehavior.execute(Actor actor)}
  * if the agent behavior is not <code>null</code>.</p>
  * <p>In both code segments above, the behavior is executed via the
  * following technique:</p>
  * <pre>
  *   new DefaultExecutor().execute(actor);
  * </pre>
  * <p>That is, the actor's operations are executed indirectly (with respect
  * to the thread context) by handing the actor off to other functionality
  * that establishes an execution context.  It's possible, of course, to
  * use any direct or indirect execution context, e.g.,</p>
  * <pre>
  *   // directly, within the current execution context:
  *   actor.run();
  *   // indirectly, using 'Thread' to establish an execution context:
  *   new Thread(actor).start();
  *   // indirectly, using a dedicated executor:
  *   new DefaultExecutor().execute(actor);
  *   // indirectly, using a thread-pool scheduler:
  *   scheduler.add(actor);
  * </pre>
  */

  public final void execute() {
    actCompleted = false;
    if (agentBehavior != null && agentBehavior instanceof ActorBehavior) {
      synchronized (this) {
        actResultObject = ((ActorBehavior) agentBehavior).execute(this);
      }
      return;
    }
    if (!sense()) {
      return;
    }
    if (!reason()) {
      return;
    }
    synchronized (this) {
      actResultObject = act();
      actCompleted = true;
    }
  }

  /**
  * <p>Performs an arbitrary, application-specific sensing operation.
  * The default {@link #sense sense()} is empty and returns
  * <code>true</code>.</p>
  * @return The sensing status.
  */

  protected boolean sense() {
    return true;
  }

  /**
  * <p>Performs an arbitrary, application-specific reasoning operation.
  * This method is executed only if {@link #sense sense()} returns
  * <code>true</code>, essentially, an indicator of whether or not to
  * continue the sense-reason-act sequence.  The default
  * {@link #reason reason()} is empty and returns <code>true</code>.</p>
  * @return The reasoning status.
  */

  protected boolean reason() {
    return true;
  }

  /**
  * <p>Performs an arbitrary, application-specific act operation.
  * This method is executed only if {@link #reason reason()} returns
  * <code>true</code>, essentially, an indicator of whether or not to
  * continue the sense-reason-act sequence.  The default
  * {@link #act act()} is empty and returns <code>null</code>.</p>
  * @return The object produced as a by-product of the act behavior.
  */

  protected Object act() {
    return null;
  }
}
