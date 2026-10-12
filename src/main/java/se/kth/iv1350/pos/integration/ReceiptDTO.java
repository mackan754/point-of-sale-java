package se.kth.iv1350.pos.integration;

import se.kth.iv1350.pos.util.Money;

/**
 * Everything that is printed on a receipt.
 */
public final class ReceiptDTO {

    private final SaleDTO sale;
    private final Money amountPaid;
    private final Money change;

    /**
     * Creates a new instance.
     *
     * @param sale       The completed sale.
     * @param amountPaid The amount the customer paid.
     * @param change     The change given back to the customer.
     */
    public ReceiptDTO(SaleDTO sale, Money amountPaid, Money change) {
        this.sale = sale;
        this.amountPaid = amountPaid;
        this.change = change;
    }

    /**
     * @return The completed sale.
     */
    public SaleDTO getSale() {
        return sale;
    }

    /**
     * @return The amount the customer paid.
     */
    public Money getAmountPaid() {
        return amountPaid;
    }

    /**
     * @return The change given back to the customer.
     */
    public Money getChange() {
        return change;
    }
}
