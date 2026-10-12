package se.kth.iv1350.pos.model;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1350.pos.util.Money;

/**
 * Applies several discounts after each other (the Composite pattern). Each
 * discount is applied to the price that remains after the previous one.
 * With no discounts added, the price is left unchanged.
 */
public class CompositeDiscount implements DiscountStrategy {

    private final List<DiscountStrategy> discounts = new ArrayList<>();

    /**
     * Adds a discount, which will be applied after the ones already added.
     *
     * @param discount The discount to add.
     */
    public void add(DiscountStrategy discount) {
        discounts.add(discount);
    }

    @Override
    public Money apply(Money price) {
        Money discountedPrice = price;
        for (DiscountStrategy discount : discounts) {
            discountedPrice = discount.apply(discountedPrice);
        }
        return discountedPrice;
    }
}
