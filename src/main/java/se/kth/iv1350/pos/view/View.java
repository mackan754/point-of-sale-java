package se.kth.iv1350.pos.view;

import java.io.PrintStream;

import se.kth.iv1350.pos.controller.Controller;
import se.kth.iv1350.pos.controller.OperationFailedException;
import se.kth.iv1350.pos.integration.ItemNotFoundException;
import se.kth.iv1350.pos.integration.SaleDTO;
import se.kth.iv1350.pos.model.InsufficientPaymentException;
import se.kth.iv1350.pos.util.Money;

/**
 * Placeholder for the real user interface. Contains hard coded calls to the
 * controller that simulate what a cashier would do.
 */
public class View {

    private final Controller controller;
    private final PrintStream output;

    /**
     * Creates a view that prints to {@code System.out}.
     *
     * @param controller The controller to call.
     */
    public View(Controller controller) {
        this(controller, System.out);
    }

    /**
     * Creates a view that prints to the specified stream.
     *
     * @param controller The controller to call.
     * @param output     Where to print what the cashier would see.
     */
    public View(Controller controller, PrintStream output) {
        this.controller = controller;
        this.output = output;
    }

    /**
     * Simulates a complete sale, including an unknown item, a database failure,
     * a customer discount and payment.
     */
    public void runDemoSale() {
        controller.startSale();
        output.println("A new sale has been started.");

        registerItem("32001", 2);
        registerItem("32003", 4);
        registerItem("99999", 1);
        registerItem("00000", 1);
        registerItem("32004", 3);
        registerItem("32001", 2);

        SaleDTO discountedSale = controller.applyCustomerDiscounts("C-1001");
        output.println("Customer discounts applied: -" + discountedSale.getDiscount());
        output.println("Total to pay: " + discountedSale.getAmountToPay());

        pay(Money.of(350));
    }

    private void registerItem(String itemId, int quantity) {
        try {
            SaleDTO sale = controller.registerItem(itemId, quantity);
            sale.findItem(itemId).ifPresent(saleItem -> output.printf("Added %d x %s (%s each). In sale: %d.%n",
                    quantity, saleItem.getItem().getName(), saleItem.getItem().getPriceIncludingVat(), saleItem.getQuantity()));
            output.println("Running total incl. VAT: " + sale.getTotalPriceIncludingVat());
        } catch (ItemNotFoundException e) {
            output.println("Unknown item " + e.getItemId() + ", please check the identifier and try again.");
        } catch (OperationFailedException e) {
            output.println("Could not register item " + itemId + " right now, please try again.");
        }
    }

    private void pay(Money amountPaid) {
        output.println("Customer pays " + amountPaid);
        try {
            Money change = controller.pay(amountPaid);
            output.println("Change to give the customer: " + change);
        } catch (InsufficientPaymentException e) {
            output.println("Not enough paid, " + e.getAmountToPay() + " is required.");
        }
    }
}
