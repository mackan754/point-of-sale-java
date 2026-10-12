package se.kth.iv1350.pos.model;

import se.kth.iv1350.pos.integration.ItemDTO;
import se.kth.iv1350.pos.integration.SaleItemDTO;
import se.kth.iv1350.pos.util.Money;

/**
 * One line of a sale: an item and how many units of it the customer is buying.
 */
class SaleItem {

    private final ItemDTO item;
    private int quantity;

    /**
     * Creates a sale line with zero units of the specified item.
     *
     * @param item The item.
     */
    SaleItem(ItemDTO item) {
        this.item = item;
    }

    /**
     * @param additionalUnits The number of units to add, must be positive.
     */
    void increaseQuantity(int additionalUnits) {
        quantity += additionalUnits;
    }

    /**
     * @return The price of all units, excluding VAT.
     */
    Money getTotalPrice() {
        return item.getPrice().times(quantity);
    }

    /**
     * @return The VAT for all units.
     */
    Money getTotalVat() {
        return getTotalPrice().times(item.getVatRate());
    }

    /**
     * @return An immutable snapshot of this sale line.
     */
    SaleItemDTO toDTO() {
        return new SaleItemDTO(item, quantity, getTotalPrice(), getTotalVat());
    }
}
