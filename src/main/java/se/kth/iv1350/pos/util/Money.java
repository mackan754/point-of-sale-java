package se.kth.iv1350.pos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An immutable monetary amount, stored with two decimals.
 * <p>
 * {@link BigDecimal} is used instead of {@code double} so that amounts such as
 * 0.1 + 0.2 are represented exactly, and instead of {@code int} so that prices
 * with öre (cents) and percentage based calculations do not lose precision.
 */
public final class Money implements Comparable<Money> {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    /** An amount of zero. */
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    /**
     * Creates a new instance, rounding the amount to two decimals.
     *
     * @param amount The amount, must not be {@code null}.
     */
    public Money(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        this.amount = amount.setScale(SCALE, ROUNDING);
    }

    /**
     * Creates an amount from a string, for example {@code "12.50"}.
     *
     * @param amount The amount as a decimal string.
     * @return The corresponding amount.
     */
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    /**
     * Creates an amount from a whole number, for example {@code 100}.
     *
     * @param amount The amount.
     * @return The corresponding amount.
     */
    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    /**
     * @return The amount as a {@link BigDecimal} with two decimals.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param other The amount to add.
     * @return The sum of this amount and {@code other}.
     */
    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    /**
     * @param other The amount to subtract.
     * @return This amount minus {@code other}.
     */
    public Money minus(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    /**
     * @param factor The number to multiply by, for example a quantity.
     * @return This amount multiplied by {@code factor}.
     */
    public Money times(int factor) {
        return new Money(amount.multiply(BigDecimal.valueOf(factor)));
    }

    /**
     * @param rate The rate to multiply by, for example {@code 0.25} for 25 %.
     * @return This amount multiplied by {@code rate}, rounded to two decimals.
     */
    public Money times(BigDecimal rate) {
        return new Money(amount.multiply(rate));
    }

    /**
     * @return {@code true} if this amount is less than zero.
     */
    public boolean isNegative() {
        return amount.signum() < 0;
    }

    /**
     * @param other The amount to compare with.
     * @return {@code true} if this amount is strictly less than {@code other}.
     */
    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    /**
     * @param other The amount to compare with.
     * @return The smaller of this amount and {@code other}.
     */
    public Money min(Money other) {
        return isLessThan(other) ? this : other;
    }

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Money)) {
            return false;
        }
        return amount.equals(((Money) other).amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    /**
     * @return The amount with two decimals followed by the currency, for example {@code "12.50 SEK"}.
     */
    @Override
    public String toString() {
        return amount.toPlainString() + " SEK";
    }
}
