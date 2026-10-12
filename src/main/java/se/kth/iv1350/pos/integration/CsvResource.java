package se.kth.iv1350.pos.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Reads simple comma separated files from the classpath. Used to load the
 * seed data of the fake databases.
 */
final class CsvResource {

    private CsvResource() {
    }

    /**
     * Reads all rows of a CSV file, skipping the header row, blank lines and
     * lines starting with {@code #}.
     *
     * @param resourceName The classpath location of the file, for example {@code "/inventory.csv"}.
     * @return The rows, each split into trimmed columns.
     * @throws IllegalArgumentException If the resource does not exist.
     * @throws UncheckedIOException     If the resource can not be read.
     */
    static List<String[]> readRows(String resourceName) {
        InputStream input = CsvResource.class.getResourceAsStream(resourceName);
        if (input == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceName);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1)
                    .map(String::strip)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(line -> line.split("\\s*,\\s*"))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + resourceName, e);
        }
    }
}
