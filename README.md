*Yaak Agent Framework*
======================

Summary
-------

A context-based Java messaging framework

Overview
--------

Yaak is an agent- and context-based messaging framework.  Agent
contexts are fully nestable.

Todo
----

Yaak's dynamic discover capabilities for other sibling applications
requires additional work.

Agent Communication
-------------------

Yaak supports one communication channel per agent context (such that
the channel name is taken from the agent context name), plus an
arbitrary number of arbitrarily named private channels (adapters).

The communication can be configured for local or distributed
operation via the resource "yaak.agent.communication.isDistributed".
By setting this resource to false (the default), an application can
have any number of agent contexts per application, where the local
agent contexts communicate with each other via agent-to-agent message
passing.  When distributed communication is turned off, the actual
communication mechanics are implemented with method calls.

By setting this resource to true, that is,

yaak.agent.communication.isDistributed=true

an application enables _transparent_ communication among local and
remote agent contexts.  If, for example, two applications are active,
one application with the agent contexts "X" and "Y" and the other
application with the agent context "Z", these applications can send
messages to each other via agents in their respective contexts:

  ...
  agentX.sendMessage("AgentY@Y", new Message("Hello, AgentY."));
  ...
  agentX.sendMessage("AgentZ@Z", new Message("Hello, AgentZ."));
  ...

Here, application one has defined "AgentX" in agent context "X" and
sends a message to "AgentY" in agent context "Y" (defined in that
same application).  In exactly the same manner, "AgentX" sends a
message to "AgentZ" in agent context "Z" (defined in a second
application).  Both applications must be active at the same time;
there is no attempt to store undeliverable messages, due to the
second application not being active.

Note that neither application has to interact with the communication
server.  That is, simply configuring an application for distributed
agent services, leads to automatic launching of the communication
server, which is distributed/replicated across all currently active
applications.  Agent contexts are automatically discovered and
registered by each application whenever another application starts.

The agent context names are completely arbitrary, whatever makes
sense to the system of distributed application components.  There is
no required (or even optional) host-level configuration.  Depending
on the communication server's implementation, the applications may be
required to operate on the same subnet.  For example, the default
communication server uses multicast mechanics to discover agent
contexts in other active applications.  Thus, agent context names
must be unique across all participating applications.

The agent context's 'dispose()' method shuts down all services in the
framework-level communication layers, and then calls
'CommunicationServer.close()' for that agent context's communication
channel.  Depending on the communication server implementation, this
operation may or may not close/terminate all network sockets and
threads.  For the default communication server, invoking 'close()'
closes/frees all network resources, but does not free all server
threads; hence, you must invoke 'System.exit()' to exit the
application.  Keep in mind that the automatic, transparent replication
of communication across applications implies that each application
functions as a server.

Yaak's communication server implementation is totally pluggable.  The
default server exists in the package 'yaak.agent.communication.jmx'.
Yaak has no compile-time reference to this server, or any of its
classes.  For any application that uses Yaak, the entry point to the
communication services is established during application start-up.
Yaak automatically processes the resource
"yaak.agent.communication.server" to determine the server classname,
for example,

yaak.agent.communication.server=abc.xyz.MyCommunicationServer

The server implementation is arbitrary, but the Java classes must be
loadable via the classpath.

Yaak uses reflection to load the named class (and any subordinate
classes).  The only requirements are that the communication server
must:

1. Implement both 'yaak.agent.communication.CommunicationServer' and
   'yaak.agent.communication.CommunicationServerAdmin'.
2. Implement a public static method 'getServer()' that instantiates,
   activates, and returns a reference, of type
   'yaak.agent.communication.CommunicationServer' to the server.

Upon discovery/loading of the main class for the communication server,
Yaak invokes 'CommunicationServer.getServer()' to instantiate services.
The convenience method
'yaak.agent.AgentSystem.shutdownCommunicationServer()' is available for
shutting down the communication server, which is
implementation-specific.

See the API documentation for further details.


