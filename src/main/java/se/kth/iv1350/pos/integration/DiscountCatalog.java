package se.kth.iv1350.pos.integration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fake external discount database, kept in memory. It knows which discounts
 * each customer is eligible for.
 */
public class DiscountCatalog {

    private final Map<String, List<DiscountDTO>> discountsByCustomer = new HashMap<>();

    /**
     * Creates an empty catalog.
     */
    public DiscountCatalog() {
    }

    /**
     * Creates a catalog filled with the discounts in a CSV file on the classpath.
     * The file must have the columns {@code customerId, type, value}, where type is
     * {@code PERCENTAGE} or {@code FIXED_AMOUNT}.
     *
     * @param resourceName The classpath location of the file, for example {@code "/discounts.csv"}.
     * @return A catalog containing the discounts in the file.
     */
    public static DiscountCatalog fromCsv(String resourceName) {
        DiscountCatalog catalog = new DiscountCatalog();
        for (String[] row : CsvResource.readRows(resourceName)) {
            DiscountDTO discount = new DiscountDTO(DiscountDTO.Type.valueOf(row[1]), new BigDecimal(row[2]));
            catalog.addDiscount(row[0], discount);
        }
        return catalog;
    }

    /**
     * Makes a customer eligible for a discount.
     *
     * @param customerId The customer.
     * @param discount   The discount.
     */
    public void addDiscount(String customerId, DiscountDTO discount) {
        discountsByCustomer.computeIfAbsent(customerId, id -> new ArrayList<>()).add(discount);
    }

    /**
     * @param customerId The customer.
     * @return All discounts the customer is eligible for, in the order they should be applied.
     *         The list is empty if the customer is unknown or has no discounts.
     */
    public List<DiscountDTO> findDiscounts(String customerId) {
        return List.copyOf(discountsByCustomer.getOrDefault(customerId, List.of()));
    }
}
