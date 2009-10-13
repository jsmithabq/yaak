package yaak.core;

import yaak.util.StringUtil;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.logging.Logger;

/**
* <p><code>YaakProperties</code> reads framework- or application-level
* configuration settings (preferences).  This class can be instantiated
* multiple times to facilitate working with multiple preference nodes
* and/or an initial set-up properties file.</p>
* <p>At the framework level, however, <code>yaak.core.YaakSystem</code>
* instantiates this class once, and limits further framework-related
* usage.  That is, <code>YaakSystem</code> imposes operational behavior
* from a central location.</p>
* @author Jerry Smith
* @version $Id: YaakProperties.java 6 2005-06-08 17:00:15Z jsmith $
*/

//
// This class uses the global logger, because it processes framework-level
// properties, and the framework-level logger cannot be set up until after
// these properties have been established.
//

public class YaakProperties {
  private static final String PREFERENCES_NODE = "/yaak";
  private static final String PROPERTIES_FILE = "yaak.properties";
  private String node;
  private String propsFile;
  private Preferences prefs = Preferences.userRoot().node(PREFERENCES_NODE);
  private Properties props = null;
  private Logger rootLogger = Logger.getLogger("");

  /**
  * <p>Instantiates a <code>YaakProperties</code> object from the default
  * properties file.</p>
  */

  public YaakProperties() {
    this(PREFERENCES_NODE, PROPERTIES_FILE);
  }

  /**
  * <p>Instantiates a <code>YaakProperties</code> object from the specified
  * properties file.</p>
  * @param propsFile The properties file.
  */

  public YaakProperties(String propsFile) {
    this(PREFERENCES_NODE, propsFile);
  }

  /**
  * <p>Instantiates a <code>YaakProperties</code> object from the specified
  * properties file and/or preferences node.</p>
  * @param node The preferences node.
  * @param propsFile The properties file.
  */

  public YaakProperties(String node, String propsFile) {
    this.node = node;
    this.propsFile = propsFile;
  }

  /**
  * <p>Loads the first qualifying property file from the classpath, and
  * then attempts to set the global (framework- or application-level)
  * debug state.</p>
  * @return Whether or not the properties were loaded.
  */

  public boolean load() {
    try {
      propsFile = StringUtil.checkValue(propsFile, PROPERTIES_FILE);
      InputStream is = ClassLoader.getSystemResourceAsStream(propsFile);
      if (is == null) {
        rootLogger.warning(
          getClass().getName() + ": " + "can't get properties from: '" +
          propsFile + "'.");
        return false;
      }
      props = new Properties();
      props.load(is);
      is.close();
      rootLogger.fine("using properties from file '" +
        ClassLoader.getSystemResource(propsFile) + "'.");
      Enumeration keys = props.propertyNames();
      rootLogger.fine("setting initial user preferences...");
      while (keys.hasMoreElements()) {
        String key = (String) keys.nextElement();
        String value = props.getProperty(key);
        if (value != null) {
          prefs.put(key, value);
          rootLogger.fine("set user preference '" + key + "' to '" +
            value + "'.");
        }
      }
      prefs.flush();
      return true;
    }
    catch (Exception e) {
      rootLogger.severe("error getting properties:");
      e.printStackTrace();
      return false;
    }
  }

  /**
  * <p>Provides property look-up services.</p>
  * @param property The property for which to search.
  * @param defaultValue The optional/alternative value.
  * @return The property value.
  * @throws YaakException The system properties were not
  * loaded.
  */

  public String get(String property, String defaultValue)
      throws YaakException {
    //
    // This shouldn't happen, but it's up to the impl:
    //
    if (prefs == null) {
      throw new YaakException("System preferences were not available.");
    }
    String value = prefs.get(property, defaultValue);
    //
    // This shouldn't happen, but it's up to the impl:
    //
    if (value == null) {
      rootLogger.fine("property '" + property + "' is null.");
    }
    return value;
  }

  /**
  * <p>Makes the encapsulated <code>Properties</code> object available
  * for direct look-up operations.</p>
  */

  public Properties getProperties() {
    return props;
  }
}
