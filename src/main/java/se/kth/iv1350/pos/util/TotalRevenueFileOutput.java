package se.kth.iv1350.pos.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Writes the total revenue to a file every time it changes.
 */
public class TotalRevenueFileOutput implements RevenueObserver, AutoCloseable {

    private final PrintWriter output;

    /**
     * Opens the specified file in append mode, creating it if it does not exist.
     *
     * @param file The file to write the total revenue to.
     * @throws UncheckedIOException If the file can not be opened.
     */
    public TotalRevenueFileOutput(Path file) {
        try {
            output = new PrintWriter(new FileWriter(file.toFile(), true), true);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not open revenue file " + file, e);
        }
    }

    @Override
    public void totalRevenueUpdated(Money totalRevenue) {
        output.printf("%s Total revenue: %s%n", DateTimeFormats.format(LocalDateTime.now()), totalRevenue);
    }

    @Override
    public void close() {
        output.close();
    }
}
