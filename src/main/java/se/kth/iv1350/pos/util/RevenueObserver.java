package se.kth.iv1350.pos.util;

/**
 * Gets notified every time the total revenue changes, that is, after each completed sale.
 */
public interface RevenueObserver {

    /**
     * Called when a sale has been paid for.
     *
     * @param totalRevenue The total revenue of all sales since the program started.
     */
    void totalRevenueUpdated(Money totalRevenue);
}
