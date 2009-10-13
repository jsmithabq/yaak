package yaak.core;

import yaak.core.YaakProperties;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* <p><code>YaakSystem</code> provides Yaak framework-related services.
* In particular, <code>YaakSystem</code> imposes operational behavior
* from a central location, which is sometimes preferable to littering
* the landscape with singleton behavior.</p>
* @author Jerry Smith
* @version $Id: YaakSystem.java 20 2006-02-14 16:38:43Z jsmith $
*/

public final class YaakSystem {
  private static final String Yaak_LOGGING = "yaak.logging";
  private static final String Yaak_LOGGING_DEFAULT = "info";
  private static Logger logger = null;
  private static String loggingLevel = null;
  private static boolean  propertiesLoaded = false;
  private static YaakProperties yaakProps = null;
  private static Logger rootLogger = Logger.getLogger("");

  static { // default privacy
    initFramework();
  }

  private YaakSystem() {
    // nothing
  }

  /**
  * <p>Kicks off centralized initialization operations, including
  * loading properties from <code>yaak.properties</code> and
  * setting up the application-level URL protocol/scheme.</p>
  */

  public static void initFramework() {
    initYaakProperties();
    setupLogging();
  }

  /**
  * <p>Provides framework-level, property look-up services.</p>
  * @param property The property for which to search.
  * @param defaultValue The optional/alternative value.
  * @return The property value.
  * @throws YaakException The system properties were not
  * loaded.
  */

  public static String getProperty(String property, String defaultValue)
      throws YaakException {
    return yaakProps.get(property, defaultValue);
  }

  private static void initYaakProperties() {
    if (!propertiesLoaded) { // limit to single prop set-up here only
      yaakProps = new YaakProperties();
      propertiesLoaded = yaakProps.load();
    }
  }

  private static void setupLogging() {
    if (logger != null) {
      return;
    }
    logger = Logger.getLogger(YaakSystem.class.getName());
    try {
      loggingLevel = YaakSystem.getProperty(Yaak_LOGGING, Yaak_LOGGING_DEFAULT);
      if (loggingLevel != null && loggingLevel.length() > 0) {
        loggingLevel = loggingLevel.toUpperCase();
        try {
          Level newLevel = Level.parse(loggingLevel);
          logger.setLevel(newLevel);
          rootLogger.info(logger.getName() + 
            " logging level is: " + logger.getLevel());
          Handler[] handlers = rootLogger.getHandlers();
          for (int i = 0; i < handlers.length; i++) {
            handlers[i].setLevel(newLevel);
            rootLogger.info("root logger handler[" + i + "]" +
              " logging level is: " + handlers[i].getLevel());
          }
        }
        catch (IllegalArgumentException e) {
          e.printStackTrace();
          throw new YaakRuntimeException("Logging not available!", e);
        }
      }
    }
    catch (YaakException e) {
      System.err.println("Exception setting up logging!");
      System.out.println("Exception setting up logging!");
      throw new YaakRuntimeException("Logging not available!", e);
    }
  }

  /**
  * <p>Provides access to framework-level logging services.</p>
  * @return The logger.
  */

  public static Logger getLogger() {
    return logger;
  }
}
