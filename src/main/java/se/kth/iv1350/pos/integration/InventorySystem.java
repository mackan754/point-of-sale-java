package se.kth.iv1350.pos.integration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import se.kth.iv1350.pos.util.Money;

/**
 * A fake external inventory system, kept in memory. It stores the item catalog
 * and the number of units in stock of each item.
 * <p>
 * Looking up the item with identifier {@value #UNREACHABLE_DATABASE_ITEM_ID}
 * simulates that the database server can not be reached.
 */
public class InventorySystem {

    /** Looking up this identifier makes the inventory throw a {@link DatabaseFailureException}. */
    public static final String UNREACHABLE_DATABASE_ITEM_ID = "00000";

    private final Map<String, ItemDTO> catalog = new HashMap<>();
    private final Map<String, Integer> stock = new HashMap<>();

    /**
     * Creates an empty inventory.
     */
    public InventorySystem() {
    }

    /**
     * Creates an inventory filled with the items in a CSV file on the classpath.
     * The file must have the columns {@code id, name, description, price, vatRate, stock}.
     *
     * @param resourceName The classpath location of the file, for example {@code "/inventory.csv"}.
     * @return An inventory containing the items in the file.
     */
    public static InventorySystem fromCsv(String resourceName) {
        InventorySystem inventory = new InventorySystem();
        for (String[] row : CsvResource.readRows(resourceName)) {
            ItemDTO item = new ItemDTO(row[0], row[1], row[2], Money.of(row[3]), new BigDecimal(row[4]));
            inventory.addItem(item, Integer.parseInt(row[5]));
        }
        return inventory;
    }

    /**
     * Adds an item to the inventory, replacing any existing item with the same identifier.
     *
     * @param item         The item to add.
     * @param initialStock The number of units in stock.
     */
    public void addItem(ItemDTO item, int initialStock) {
        catalog.put(item.getId(), item);
        stock.put(item.getId(), initialStock);
    }

    /**
     * Looks up an item.
     *
     * @param itemId The identifier of the item.
     * @return The item with the specified identifier.
     * @throws ItemNotFoundException     If no item has the specified identifier.
     * @throws DatabaseFailureException If the database can not be reached.
     */
    public ItemDTO findItem(String itemId) throws ItemNotFoundException {
        if (UNREACHABLE_DATABASE_ITEM_ID.equals(itemId)) {
            throw new DatabaseFailureException("The inventory database server could not be reached.");
        }
        ItemDTO item = catalog.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }
        return item;
    }

    /**
     * @param itemId The identifier of the item.
     * @return The number of units in stock, which may be negative if more units
     *         were sold than were registered.
     * @throws ItemNotFoundException If no item has the specified identifier.
     */
    public int getStock(String itemId) throws ItemNotFoundException {
        Integer unitsInStock = stock.get(itemId);
        if (unitsInStock == null) {
            throw new ItemNotFoundException(itemId);
        }
        return unitsInStock;
    }

    /**
     * Removes the sold items from the stock.
     *
     * @param sale The completed sale.
     */
    public void registerSale(SaleDTO sale) {
        for (SaleItemDTO soldItem : sale.getItems()) {
            stock.merge(soldItem.getItem().getId(), -soldItem.getQuantity(), Integer::sum);
        }
    }

    /**
     * @return A read-only view of the current stock levels, keyed by item identifier.
     */
    public Map<String, Integer> getStockLevels() {
        return Collections.unmodifiableMap(stock);
    }
}
