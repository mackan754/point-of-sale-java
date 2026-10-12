package se.kth.iv1350.pos.model;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1350.pos.util.Money;
import se.kth.iv1350.pos.util.RevenueObserver;

/**
 * The cash register. Keeps track of how much cash it contains and of the total
 * revenue, and notifies {@link RevenueObserver}s when the revenue changes
 * (the Observer pattern).
 */
public class CashRegister {

    private final List<RevenueObserver> revenueObservers = new ArrayList<>();
    private Money balance;
    private Money totalRevenue = Money.ZERO;

    /**
     * Creates an empty cash register.
     */
    public CashRegister() {
        this(Money.ZERO);
    }

    /**
     * Creates a cash register that initially contains the specified amount.
     *
     * @param initialBalance The cash in the register when the program starts.
     */
    public CashRegister(Money initialBalance) {
        this.balance = initialBalance;
    }

    /**
     * @param observer Will be notified every time the total revenue changes.
     */
    public void addRevenueObserver(RevenueObserver observer) {
        revenueObservers.add(observer);
    }

    /**
     * Registers a paid sale. The register's balance and the total revenue both
     * increase by the cost of the sale, since any change has been given back.
     *
     * @param saleAmount The amount the sale cost.
     */
    public void registerPayment(Money saleAmount) {
        balance = balance.plus(saleAmount);
        totalRevenue = totalRevenue.plus(saleAmount);
        revenueObservers.forEach(observer -> observer.totalRevenueUpdated(totalRevenue));
    }

    /**
     * @return The cash currently in the register.
     */
    public Money getBalance() {
        return balance;
    }

    /**
     * @return The total revenue of all sales registered so far.
     */
    public Money getTotalRevenue() {
        return totalRevenue;
    }
}
