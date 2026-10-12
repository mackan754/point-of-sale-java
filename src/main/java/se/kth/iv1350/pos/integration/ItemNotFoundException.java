package se.kth.iv1350.pos.integration;

/**
 * Thrown when an item identifier does not match any item in the inventory.
 * This is an expected situation, for example when a barcode is mistyped.
 */
public class ItemNotFoundException extends Exception {

    private final String itemId;

    /**
     * @param itemId The identifier that did not match any item.
     */
    public ItemNotFoundException(String itemId) {
        super("No item with identifier " + itemId + " exists.");
        this.itemId = itemId;
    }

    /**
     * @return The identifier that did not match any item.
     */
    public String getItemId() {
        return itemId;
    }
}
