package yaak.test.model;

import yaak.agent.Agent;
import yaak.ontology.Knowledge;
import yaak.ontology.KnowledgeException;
import yaak.ontology.KnowledgeListener;
import yaak.model.service.Service;
import yaak.model.service.ServiceListener;
import yaak.model.service.ServiceNotification;

/**
* <p><code>GreetingAgent</code> provides a service for issuing a
* greeting.</p>
* @author Jerry Smith
* @version $Id: GreetingAgent.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class GreetingAgent extends Agent implements KnowledgeListener,
    ServiceListener {
  String alias = "no-alias";
  String greetingPrefix = "Hello, ";
  String greetee = "stranger";
  String greetingPostfix = ".";
  GreetingService greetingService = new GreetingService() {
    public String makeGreeting() {
      return greetingPrefix + greetee + greetingPostfix;
    }
  };
  Knowledge knowledge = new Knowledge() {
    public String[] getPropertyNames() {
      String[] properties = {"alias"};
      return properties; // hard-coded for this example
    }
    public Class getType(String property) {
      if (property.equals("alias")) {
        return String.class;
      }
      else {
        throw new KnowledgeException("Property not found.");
      }
    }
  };

  public Knowledge getKnowledge() {
    return knowledge;
  }

  public Class getKnowledgeType() {
    return Knowledge.class;
  }

  public String getKnowledgeTypeAsString() {
    return getKnowledgeType().getName();
  }

  public String getAlias() { // an example of knowledge in property form
    return alias;
  }
 
  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Service getService() {
    return greetingService;
  }

  public Class getServiceType() {
    return GreetingService.class;
  }

  public String getServiceTypeAsString() {
    return getServiceType().getName();
  }

  public Object performService(final ServiceNotification notification) {
    Object resultObject = null;
    GreetingService greetingService = new GreetingService() {
      public String makeGreeting() {
        String greeting = greetingPrefix +
          notification.getRequestingAgent().getAgentName() +
          greetingPostfix;
        System.out.println(greeting);
        return greeting;
      }
    };
    resultObject = greetingService.makeGreeting();
    return resultObject;
  }
 
  public Object performService(Service service,
      ServiceNotification notification) {
    Object resultObject = null;
    if (canDo(service)) {
      resultObject = performService(notification);
    }
    return resultObject;
  }

  public boolean canDo(Service service) {
    return getServiceType().isInstance(service);
  }
}
