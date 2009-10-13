package yaak.agent;

import yaak.core.YaakSystem;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;

/**
* <p><code>ConfigurableAgent</code> adds a default configuration
* capability.</p>
* @author Jerry Smith
* @version $Id: ConfigurableAgent.java 6 2005-06-08 17:00:15Z jsmith $
*/

abstract public class ConfigurableAgent extends Agent {
  /**
  * <p>Not for public consumption.</p>
  * <p><code>ConfigurableAgent()</code> is never instantiated directly.</p>
  */

  protected ConfigurableAgent() {
    super();
  }

  /**
  * <p>Application-defined operation(s) executed when an agent
  * is created.  This method is executed immediately after the agent
  * is installed into the agent context and initialized.  To provide
  * a custom creation operation, simply override this method.  The
  * argument to this method is specified during agent creation:</p>
  * <pre>
  *   Agent agent = context.createAgent("MyAgent", someObject, "007");
  * </pre>
  * <p>Default implementation:  This method expects a configuration object.
  * In this case, the object must be (because it's cast to) an instance of
  * <code>java.util.Properties</code>.</p>
  * <p>For each property (key-value pair), this initialization
  * process attempts to find a corresponding state (instance)
  * variable in the agent and then initialize it accordingly.
  * The initialization process supports the following data types:</p>
  * <ul>
  * <li>int
  * <li>float
  * <li>double
  * <li>boolean
  * <li>String
  * </ul>
  * <p>The keys in the <code>Properties</code> object must conform
  * to the following conventions:</p>
  * <ul>
  * <li>Keys have the same name as the agent instance variable, except
  * <li>Key names are appended with <code>"AsXXX"</code>, where
  * <code>"XXX"</code> is one of the following (case sensitive):
  *   <ul>
  *   <li>Int
  *   <li>Float
  *   <li>Double
  *   <li>Boolean
  *   <li>String
  *   </ul>
  * </ul>
  * <p>Note that this initialization process works for any specialization
  * of <code>ConfigurableAgent</code>.</p>
  * <p>There are many application-specific ways to establish the
  * key-value pairs stored in the <code>Properties</code> object.  Note
  * that the Yaak framework provides a class,
  * {@link yaak.core.YaakProperties YaakProperties}, which reads properties
  * from arbitrarily named files anywhere in the classpath, e.g.,</p>
  * <pre>
  *   YaakProperties yaakProps = new YaakProperties("myagent.properties");
  * </pre>
  * <p>Hence, to initialize an application-specific collection of agents
  * (specializations of <code>ConfigurableAgent</code>), simply create a
  * configuration directory, add it to the classpath, and read the
  * agent-specific properties files on demand during agent creation.</p>
  * @param args Any application-defined object.
  */

  protected void onCreation(Object args) {
    if (args == null) {
      return;
    }
    if (!(args instanceof Properties)) {
      throw new IllegalArgumentException(
        "Must be instance of 'java.util.Properties'.");
    }
    Properties props = (Properties) args;
    Enumeration keys = props.propertyNames();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = props.getProperty(key);
      setTypedProperty(key, value);
    }
  }

  private void setTypedProperty(String key, String value) {
    int indexAs = key.indexOf("As");
    if (indexAs == -1) {
      throw new IllegalArgumentException("Illegal implicit property type.");
    }
    String propName = key.substring(0, indexAs);
    String asType = key.substring(indexAs);
    Class c = getClass();
    Field f = null;
    //
    // Search the inheritance hierarchy:
    //
    while (f == null) {
      try {
        f = c.getDeclaredField(propName);
      }
      catch (NoSuchFieldException nsfe) {
        c = c.getSuperclass();
        if (c == null) {
          YaakSystem.getLogger().fine(
            "nonexistent field for '" + key + "', value = '" + value + "'.");
          return;
        }
      }
    }
    try {
      f.setAccessible(true);
      if (asType.endsWith("String")) {
        f.set(this, value);
      }
      else if (asType.endsWith("Int")) {
        f.set(this, new Integer(value));
      }
      else if (asType.endsWith("Float")) {
        f.set(this, new Float(value));
      }
      else if (asType.endsWith("Double")) {
        f.set(this, new Double(value));
      }
      else if (asType.endsWith("Boolean")) {
        f.set(this, new Boolean(value));
      }
      else {
        throw new IllegalArgumentException(
          "Unrecognized implicit property type.");
      }
    }
    catch (NumberFormatException nfe) {
      YaakSystem.getLogger().warning("number format exception for '" +
        key + "', value = '" + value + "'.");
    }
    catch (IllegalAccessException iae) {
      YaakSystem.getLogger().warning("illegal access exception for '" +
        key + "', value = '" + value + "'.");
    }
  }
}
