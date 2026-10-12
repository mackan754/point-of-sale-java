package se.kth.iv1350.pos.integration;

import se.kth.iv1350.pos.util.Money;

/**
 * Immutable snapshot of one line of a sale: an item and how many of it were bought.
 */
public final class SaleItemDTO {

    private final ItemDTO item;
    private final int quantity;
    private final Money totalPrice;
    private final Money totalVat;

    /**
     * Creates a new instance.
     *
     * @param item       The item that was bought.
     * @param quantity   The number of units bought.
     * @param totalPrice The price of all units, excluding VAT.
     * @param totalVat   The VAT for all units.
     */
    public SaleItemDTO(ItemDTO item, int quantity, Money totalPrice, Money totalVat) {
        this.item = item;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.totalVat = totalVat;
    }

    /**
     * @return The item that was bought.
     */
    public ItemDTO getItem() {
        return item;
    }

    /**
     * @return The number of units bought.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return The price of all units, excluding VAT.
     */
    public Money getTotalPrice() {
        return totalPrice;
    }

    /**
     * @return The VAT for all units.
     */
    public Money getTotalVat() {
        return totalVat;
    }

    /**
     * @return The price of all units, including VAT.
     */
    public Money getTotalPriceIncludingVat() {
        return totalPrice.plus(totalVat);
    }
}
