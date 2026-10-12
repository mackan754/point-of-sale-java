package se.kth.iv1350.pos.controller;

/**
 * Thrown when an operation could not be performed because of a technical
 * problem, for example that a database could not be reached. Hides the
 * technical details from the view.
 */
public class OperationFailedException extends Exception {

    /**
     * @param message A description of the operation that failed.
     * @param cause   The technical reason for the failure.
     */
    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
