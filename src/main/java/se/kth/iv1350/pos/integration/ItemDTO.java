package se.kth.iv1350.pos.integration;

import java.math.BigDecimal;
import java.util.Objects;

import se.kth.iv1350.pos.util.Money;

/**
 * Immutable description of an item as stored in the inventory system.
 */
public final class ItemDTO {

    private final String id;
    private final String name;
    private final String description;
    private final Money price;
    private final BigDecimal vatRate;

    /**
     * Creates a new instance.
     *
     * @param id          The unique identifier (barcode) of the item.
     * @param name        The name printed on receipts.
     * @param description A short description of the item, for example its category.
     * @param price       The price of one unit, excluding VAT.
     * @param vatRate     The VAT rate as a fraction, for example {@code 0.12} for 12 %.
     * @throws IllegalArgumentException If the price or VAT rate is negative.
     */
    public ItemDTO(String id, String name, String description, Money price, BigDecimal vatRate) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(vatRate, "vatRate must not be null");
        if (price.isNegative()) {
            throw new IllegalArgumentException("price must not be negative: " + price);
        }
        if (vatRate.signum() < 0) {
            throw new IllegalArgumentException("vatRate must not be negative: " + vatRate);
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.vatRate = vatRate;
    }

    /**
     * @return The unique identifier (barcode) of the item.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The name printed on receipts.
     */
    public String getName() {
        return name;
    }

    /**
     * @return A short description of the item, for example its category.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The price of one unit, excluding VAT.
     */
    public Money getPrice() {
        return price;
    }

    /**
     * @return The VAT rate as a fraction, for example {@code 0.12} for 12 %.
     */
    public BigDecimal getVatRate() {
        return vatRate;
    }

    /**
     * @return The price of one unit, including VAT.
     */
    public Money getPriceIncludingVat() {
        return price.plus(price.times(vatRate));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ItemDTO)) {
            return false;
        }
        ItemDTO that = (ItemDTO) other;
        return id.equals(that.id)
                && name.equals(that.name)
                && description.equals(that.description)
                && price.equals(that.price)
                && vatRate.equals(that.vatRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, vatRate);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
