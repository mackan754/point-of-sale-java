package se.kth.iv1350.pos.model;

import java.util.Objects;

import se.kth.iv1350.pos.util.Money;

/**
 * Deducts a fixed amount from the price. The price never goes below zero.
 */
public final class FixedAmountDiscount implements DiscountStrategy {

    private final Money amount;

    /**
     * Creates a new instance.
     *
     * @param amount The amount to deduct.
     * @throws IllegalArgumentException If the amount is negative.
     */
    public FixedAmountDiscount(Money amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.isNegative()) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        this.amount = amount;
    }

    /**
     * @return The amount to deduct.
     */
    public Money getAmount() {
        return amount;
    }

    @Override
    public Money apply(Money price) {
        return price.minus(amount.min(price));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FixedAmountDiscount)) {
            return false;
        }
        return amount.equals(((FixedAmountDiscount) other).amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public String toString() {
        return amount + " off";
    }
}
