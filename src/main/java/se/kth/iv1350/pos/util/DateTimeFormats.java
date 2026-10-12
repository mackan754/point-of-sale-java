package se.kth.iv1350.pos.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The date and time format used everywhere in the application, so that receipts
 * and log files look the same.
 */
public final class DateTimeFormats {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeFormats() {
    }

    /**
     * @param time The time to format.
     * @return The time formatted as {@code yyyy-MM-dd HH:mm:ss}.
     */
    public static String format(LocalDateTime time) {
        return time.format(FORMATTER);
    }
}
