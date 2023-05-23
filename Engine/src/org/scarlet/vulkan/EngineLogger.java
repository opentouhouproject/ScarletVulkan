package org.scarlet.vulkan;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default logging class.
 */
public final class EngineLogger {
    /**
     * Single instance of the class.
     */
    private static EngineLogger instance;

    /**
     * Java logger.
     */
    private Logger logger;

    /**
     * Retrieves the instance of the singleton class.
     * @return EngineLogger - Single instance of the class.
     */
    public static synchronized EngineLogger getInstance() {
        if (instance == null) {
            instance = new EngineLogger();
        }
        return instance;
    }

    /**
     * Private constructor.
     */
    private EngineLogger() {
        logger = Logger.getLogger("Scarlet Vulkan");
    }

    /**
     * Logs a message with severity level.
     * @param level The severity level of the message.
     * @param message The message to log.
     */
    public void log(Level level, String message) {
        logger.log(level, message);
    }

    /**
     * Logs a message with severity level and exception.
     * @param level The severity level of the message.
     * @param message The message to log.
     * @param throwable The exception to log.
     */
    public void log(Level level, String message, Throwable throwable) {
        logger.log(level, message, throwable);
    }

    /**
     * Logs a formatted message with severity level and exception.
     * @param level The severity level of the message.
     * @param message The message to log.
     * @param argument The arguments for the message.
     * @param throwable The exception to log.
     */
    public void log(Level level, String message, String argument, Throwable throwable) {
        String formattedMessage = String.format(message, argument);
        logger.log(level, formattedMessage, throwable);
    }
}