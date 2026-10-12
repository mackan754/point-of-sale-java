package se.kth.iv1350.pos.view;

import java.io.PrintStream;

import se.kth.iv1350.pos.util.Money;
import se.kth.iv1350.pos.util.RevenueObserver;

/**
 * Shows the total revenue to the cashier every time it changes.
 */
public class TotalRevenueView implements RevenueObserver {

    private final PrintStream output;

    /**
     * Creates a view that prints to {@code System.out}.
     */
    public TotalRevenueView() {
        this(System.out);
    }

    /**
     * Creates a view that prints to the specified stream.
     *
     * @param output Where to print the total revenue.
     */
    public TotalRevenueView(PrintStream output) {
        this.output = output;
    }

    @Override
    public void totalRevenueUpdated(Money totalRevenue) {
        output.println(">>> Total revenue so far: " + totalRevenue);
    }
}
