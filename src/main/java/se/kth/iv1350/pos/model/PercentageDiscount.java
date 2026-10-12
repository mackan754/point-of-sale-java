package se.kth.iv1350.pos.model;

import java.math.BigDecimal;
import java.util.Objects;

import se.kth.iv1350.pos.util.Money;

/**
 * Deducts a percentage of the price.
 */
public final class PercentageDiscount implements DiscountStrategy {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final BigDecimal percentage;

    /**
     * Creates a new instance.
     *
     * @param percentage The percentage to deduct, between 0 and 100.
     * @throws IllegalArgumentException If the percentage is not between 0 and 100.
     */
    public PercentageDiscount(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "percentage must not be null");
        if (percentage.signum() < 0 || percentage.compareTo(HUNDRED) > 0) {
            throw new IllegalArgumentException("percentage must be between 0 and 100: " + percentage);
        }
        this.percentage = percentage;
    }

    /**
     * Creates a discount from a whole percentage, for example {@code 10} for 10 %.
     *
     * @param percentage The percentage to deduct, between 0 and 100.
     * @throws IllegalArgumentException If the percentage is not between 0 and 100.
     */
    public PercentageDiscount(int percentage) {
        this(BigDecimal.valueOf(percentage));
    }

    /**
     * @return The percentage to deduct.
     */
    public BigDecimal getPercentage() {
        return percentage;
    }

    @Override
    public Money apply(Money price) {
        BigDecimal remainingFraction = BigDecimal.ONE.subtract(percentage.movePointLeft(2));
        return price.times(remainingFraction);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PercentageDiscount)) {
            return false;
        }
        return percentage.compareTo(((PercentageDiscount) other).percentage) == 0;
    }

    @Override
    public int hashCode() {
        return percentage.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return percentage.toPlainString() + " % off";
    }
}
