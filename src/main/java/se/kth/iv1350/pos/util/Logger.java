package se.kth.iv1350.pos.util;

/**
 * Something that can write log messages. Where the messages end up is decided
 * by the implementing class.
 */
public interface Logger {

    /**
     * Logs an informational message.
     *
     * @param message The message to log.
     */
    void info(String message);

    /**
     * Logs an error together with the exception that caused it.
     *
     * @param message   A description of what went wrong.
     * @param exception The exception that caused the error.
     */
    void error(String message, Throwable exception);
}
