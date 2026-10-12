package se.kth.iv1350.pos.model;

import se.kth.iv1350.pos.util.Money;

/**
 * Thrown when a customer pays less than the amount to pay for a sale.
 */
public class InsufficientPaymentException extends Exception {

    private final Money amountPaid;
    private final Money amountToPay;

    /**
     * @param amountPaid  The amount the customer paid.
     * @param amountToPay The amount the customer should have paid.
     */
    public InsufficientPaymentException(Money amountPaid, Money amountToPay) {
        super("Paid " + amountPaid + " but " + amountToPay + " is required.");
        this.amountPaid = amountPaid;
        this.amountToPay = amountToPay;
    }

    /**
     * @return The amount the customer paid.
     */
    public Money getAmountPaid() {
        return amountPaid;
    }

    /**
     * @return The amount the customer should have paid.
     */
    public Money getAmountToPay() {
        return amountToPay;
    }
}
