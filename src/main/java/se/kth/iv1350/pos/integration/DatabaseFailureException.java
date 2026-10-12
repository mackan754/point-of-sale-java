package se.kth.iv1350.pos.integration;

/**
 * Thrown when the database can not be reached. This is unchecked since the
 * caller can not do anything to prevent it, only report it.
 */
public class DatabaseFailureException extends RuntimeException {

    /**
     * @param message A description of the failure.
     */
    public DatabaseFailureException(String message) {
        super(message);
    }
}
