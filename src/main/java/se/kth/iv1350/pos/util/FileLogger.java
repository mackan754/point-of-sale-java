package se.kth.iv1350.pos.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * A {@link Logger} that appends log messages, including stack traces, to a file.
 */
public class FileLogger implements Logger, AutoCloseable {

    private final PrintWriter logStream;

    /**
     * Opens the specified log file in append mode, creating it if it does not exist.
     *
     * @param logFile The file to write log messages to.
     * @throws UncheckedIOException If the file can not be opened.
     */
    public FileLogger(Path logFile) {
        try {
            logStream = new PrintWriter(new FileWriter(logFile.toFile(), true), true);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not open log file " + logFile, e);
        }
    }

    @Override
    public void info(String message) {
        write("INFO", message);
    }

    @Override
    public void error(String message, Throwable exception) {
        write("ERROR", message);
        exception.printStackTrace(logStream);
    }

    private void write(String level, String message) {
        logStream.printf("%s [%s] %s%n", DateTimeFormats.format(LocalDateTime.now()), level, message);
    }

    @Override
    public void close() {
        logStream.close();
    }
}
