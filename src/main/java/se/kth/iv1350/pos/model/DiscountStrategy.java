package se.kth.iv1350.pos.model;

import se.kth.iv1350.pos.integration.DiscountDTO;
import se.kth.iv1350.pos.util.Money;

/**
 * A way to reduce the price of a sale (the Strategy pattern).
 */
public interface DiscountStrategy {

    /**
     * Applies this discount to a price.
     *
     * @param price The price before the discount.
     * @return The price after the discount, never negative.
     */
    Money apply(Money price);

    /**
     * Creates the discount strategy that matches a discount from the discount catalog.
     *
     * @param discount The discount description.
     * @return A strategy that applies the described discount.
     */
    static DiscountStrategy from(DiscountDTO discount) {
        return switch (discount.getType()) {
            case PERCENTAGE -> new PercentageDiscount(discount.getValue());
            case FIXED_AMOUNT -> new FixedAmountDiscount(new Money(discount.getValue()));
        };
    }
}
