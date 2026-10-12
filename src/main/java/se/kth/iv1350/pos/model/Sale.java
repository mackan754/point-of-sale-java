package se.kth.iv1350.pos.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.kth.iv1350.pos.integration.ItemDTO;
import se.kth.iv1350.pos.integration.SaleDTO;
import se.kth.iv1350.pos.integration.SaleItemDTO;
import se.kth.iv1350.pos.util.Money;

/**
 * One single sale made by one single customer and paid with one payment.
 * <p>
 * All totals are calculated from the registered items when they are requested,
 * so they can never get out of sync with the items.
 */
public class Sale {

    private final LocalDateTime startTime;
    private final Map<String, SaleItem> items = new LinkedHashMap<>();
    private final CompositeDiscount discounts = new CompositeDiscount();
    private boolean paid;

    /**
     * Starts a new sale at the current time.
     */
    public Sale() {
        this(LocalDateTime.now());
    }

    /**
     * Starts a new sale at the specified time.
     *
     * @param startTime When the sale started.
     */
    public Sale(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Adds units of an item to the sale. If the item is already part of the sale,
     * its quantity is increased.
     *
     * @param item     The item to add.
     * @param quantity The number of units to add.
     * @throws IllegalArgumentException If the quantity is not positive.
     * @throws IllegalStateException    If the sale has already been paid.
     */
    public void addItem(ItemDTO item, int quantity) {
        requireNotPaid();
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
        items.computeIfAbsent(item.getId(), id -> new SaleItem(item)).increaseQuantity(quantity);
    }

    /**
     * Adds a discount to the sale. Discounts are applied in the order they are added.
     *
     * @param discount The discount to add.
     * @throws IllegalStateException If the sale has already been paid.
     */
    public void addDiscount(DiscountStrategy discount) {
        requireNotPaid();
        discounts.add(discount);
    }

    /**
     * @return The sum of all item prices before discounts, excluding VAT.
     */
    public Money getTotalPrice() {
        return items.values().stream().map(SaleItem::getTotalPrice).reduce(Money.ZERO, Money::plus);
    }

    /**
     * @return The sum of the VAT for all items before discounts.
     */
    public Money getTotalVat() {
        return items.values().stream().map(SaleItem::getTotalVat).reduce(Money.ZERO, Money::plus);
    }

    /**
     * @return The sum of all item prices before discounts, including VAT.
     */
    public Money getTotalPriceIncludingVat() {
        return getTotalPrice().plus(getTotalVat());
    }

    /**
     * Discounts are applied to the price including VAT, since that is the price
     * the customer sees. A fixed amount discount of 50 SEK therefore lowers the
     * amount to pay by exactly 50 SEK.
     *
     * @return The amount the customer has to pay, that is, the price including VAT minus all discounts.
     */
    public Money getAmountToPay() {
        return discounts.apply(getTotalPriceIncludingVat());
    }

    /**
     * VAT is only paid on what the customer actually pays, so a discount also
     * reduces the VAT. The discount is spread over all items in proportion to
     * their price, which means the VAT of every item, whatever its VAT rate,
     * is reduced by the same fraction as the total price.
     *
     * @return The VAT included in the amount to pay.
     */
    public Money getVatToPay() {
        Money totalPriceIncludingVat = getTotalPriceIncludingVat();
        if (totalPriceIncludingVat.equals(Money.ZERO)) {
            return Money.ZERO;
        }
        BigDecimal payableFraction = getAmountToPay().getAmount()
                .divide(totalPriceIncludingVat.getAmount(), MathContext.DECIMAL64);
        return getTotalVat().times(payableFraction);
    }

    /**
     * Pays for the sale. A sale can only be paid once.
     *
     * @param amountPaid The amount the customer paid.
     * @return The change to give back to the customer.
     * @throws InsufficientPaymentException If the amount paid is less than the amount to pay.
     * @throws IllegalStateException        If the sale has already been paid.
     */
    public Money pay(Money amountPaid) throws InsufficientPaymentException {
        requireNotPaid();
        Money amountToPay = getAmountToPay();
        if (amountPaid.isLessThan(amountToPay)) {
            throw new InsufficientPaymentException(amountPaid, amountToPay);
        }
        paid = true;
        return amountPaid.minus(amountToPay);
    }

    /**
     * @return {@code true} if the sale has been paid.
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * @return An immutable snapshot of the current state of the sale.
     */
    public SaleDTO toDTO() {
        List<SaleItemDTO> itemSnapshots = items.values().stream().map(SaleItem::toDTO).toList();
        Money totalPriceIncludingVat = getTotalPriceIncludingVat();
        Money amountToPay = getAmountToPay();
        return new SaleDTO(startTime, itemSnapshots, totalPriceIncludingVat,
                totalPriceIncludingVat.minus(amountToPay), amountToPay, getVatToPay());
    }

    private void requireNotPaid() {
        if (paid) {
            throw new IllegalStateException("The sale has already been paid.");
        }
    }
}
