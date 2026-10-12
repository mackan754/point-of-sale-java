package se.kth.iv1350.pos.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import se.kth.iv1350.pos.util.Money;

/**
 * Immutable snapshot of a sale, used to pass sale information between layers
 * and to external systems.
 */
public final class SaleDTO {

    private final LocalDateTime startTime;
    private final List<SaleItemDTO> items;
    private final Money totalPriceIncludingVat;
    private final Money discount;
    private final Money amountToPay;
    private final Money vat;

    /**
     * Creates a new instance. The item list is copied, so that the snapshot can not be changed.
     *
     * @param startTime              When the sale was started.
     * @param items                  The items in the sale, in the order they were first registered.
     * @param totalPriceIncludingVat The sum of all item prices including VAT, before discounts.
     * @param discount               The total discount given, including VAT.
     * @param amountToPay            The amount the customer has to pay, including VAT.
     * @param vat                    The VAT included in {@code amountToPay}, that is, after discounts.
     */
    public SaleDTO(LocalDateTime startTime, List<SaleItemDTO> items, Money totalPriceIncludingVat,
                   Money discount, Money amountToPay, Money vat) {
        this.startTime = startTime;
        this.items = List.copyOf(items);
        this.totalPriceIncludingVat = totalPriceIncludingVat;
        this.discount = discount;
        this.amountToPay = amountToPay;
        this.vat = vat;
    }

    /**
     * @return When the sale was started.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * @return The items in the sale, in the order they were first registered. The list can not be modified.
     */
    public List<SaleItemDTO> getItems() {
        return items;
    }

    /**
     * @return The sum of all item prices including VAT, before discounts.
     */
    public Money getTotalPriceIncludingVat() {
        return totalPriceIncludingVat;
    }

    /**
     * @return The total discount given, including VAT.
     */
    public Money getDiscount() {
        return discount;
    }

    /**
     * @return The amount the customer has to pay, including VAT.
     */
    public Money getAmountToPay() {
        return amountToPay;
    }

    /**
     * @return The VAT included in the amount to pay, that is, after discounts.
     */
    public Money getVat() {
        return vat;
    }

    /**
     * @return The amount the customer has to pay, excluding VAT.
     */
    public Money getAmountToPayExcludingVat() {
        return amountToPay.minus(vat);
    }

    /**
     * @param itemId The identifier of the item to look for.
     * @return The sale line for the specified item, or an empty {@code Optional} if the
     *         item is not part of the sale.
     */
    public Optional<SaleItemDTO> findItem(String itemId) {
        return items.stream()
                .filter(saleItem -> saleItem.getItem().getId().equals(itemId))
                .findFirst();
    }
}
