package se.kth.iv1350.pos.integration;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable description of a discount that a customer is eligible for.
 */
public final class DiscountDTO {

    /**
     * The different kinds of discount.
     */
    public enum Type {
        /** A percentage of the total price is deducted. */
        PERCENTAGE,
        /** A fixed amount is deducted from the total price. */
        FIXED_AMOUNT
    }

    private final Type type;
    private final BigDecimal value;

    /**
     * Creates a new instance.
     *
     * @param type  How the discount is calculated.
     * @param value The size of the discount. A percentage for {@link Type#PERCENTAGE},
     *              for example {@code 10} for 10 %, and an amount in SEK including VAT
     *              for {@link Type#FIXED_AMOUNT}.
     * @throws IllegalArgumentException If the value is negative, or if a percentage is above 100.
     */
    public DiscountDTO(Type type, BigDecimal value) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(value, "value must not be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException("discount must not be negative: " + value);
        }
        if (type == Type.PERCENTAGE && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("percentage must not exceed 100: " + value);
        }
        this.type = type;
        this.value = value;
    }

    /**
     * @return How the discount is calculated.
     */
    public Type getType() {
        return type;
    }

    /**
     * @return The size of the discount, a percentage or an amount in SEK depending on the type.
     */
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DiscountDTO)) {
            return false;
        }
        DiscountDTO that = (DiscountDTO) other;
        return type == that.type && value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return type + " " + value.toPlainString();
    }
}
