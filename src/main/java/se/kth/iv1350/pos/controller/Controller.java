package se.kth.iv1350.pos.controller;

import java.util.List;

import se.kth.iv1350.pos.integration.AccountingSystem;
import se.kth.iv1350.pos.integration.DatabaseFailureException;
import se.kth.iv1350.pos.integration.DiscountCatalog;
import se.kth.iv1350.pos.integration.DiscountDTO;
import se.kth.iv1350.pos.integration.InventorySystem;
import se.kth.iv1350.pos.integration.ItemDTO;
import se.kth.iv1350.pos.integration.ItemNotFoundException;
import se.kth.iv1350.pos.integration.Printer;
import se.kth.iv1350.pos.integration.ReceiptDTO;
import se.kth.iv1350.pos.integration.SaleDTO;
import se.kth.iv1350.pos.model.CashRegister;
import se.kth.iv1350.pos.model.DiscountStrategy;
import se.kth.iv1350.pos.model.InsufficientPaymentException;
import se.kth.iv1350.pos.model.Sale;
import se.kth.iv1350.pos.util.Logger;
import se.kth.iv1350.pos.util.Money;
import se.kth.iv1350.pos.util.RevenueObserver;

/**
 * The application's only controller. All calls from the view to the model and
 * to external systems pass through here. The controller contains no business
 * logic of its own; it only coordinates the other layers.
 */
public class Controller {

    private final InventorySystem inventorySystem;
    private final DiscountCatalog discountCatalog;
    private final AccountingSystem accountingSystem;
    private final Printer printer;
    private final CashRegister cashRegister;
    private final Logger logger;
    private Sale currentSale;

    /**
     * Creates a new instance.
     *
     * @param inventorySystem  Used to look up items and update stock levels.
     * @param discountCatalog  Used to look up customer discounts.
     * @param accountingSystem Used to record completed sales.
     * @param printer          Used to print receipts.
     * @param cashRegister     Keeps track of the cash and the total revenue.
     * @param logger           Used to log technical errors.
     */
    public Controller(InventorySystem inventorySystem, DiscountCatalog discountCatalog,
                      AccountingSystem accountingSystem, Printer printer,
                      CashRegister cashRegister, Logger logger) {
        this.inventorySystem = inventorySystem;
        this.discountCatalog = discountCatalog;
        this.accountingSystem = accountingSystem;
        this.printer = printer;
        this.cashRegister = cashRegister;
        this.logger = logger;
    }

    /**
     * @param observer Will be notified every time a sale is paid and the total revenue changes.
     */
    public void addRevenueObserver(RevenueObserver observer) {
        cashRegister.addRevenueObserver(observer);
    }

    /**
     * Starts a new sale. Must be called before anything else is done with the sale.
     */
    public void startSale() {
        currentSale = new Sale();
    }

    /**
     * Registers units of an item in the current sale.
     *
     * @param itemId   The identifier of the item.
     * @param quantity The number of units.
     * @return The state of the sale after the item was registered.
     * @throws ItemNotFoundException    If no item has the specified identifier.
     * @throws OperationFailedException If the item could not be looked up because of a technical problem.
     * @throws IllegalStateException    If no sale is in progress.
     */
    public SaleDTO registerItem(String itemId, int quantity) throws ItemNotFoundException, OperationFailedException {
        Sale sale = requireSaleInProgress();
        try {
            ItemDTO item = inventorySystem.findItem(itemId);
            sale.addItem(item, quantity);
            return sale.toDTO();
        } catch (DatabaseFailureException e) {
            logger.error("Could not look up item " + itemId, e);
            throw new OperationFailedException("Could not look up item " + itemId, e);
        }
    }

    /**
     * Applies all discounts the specified customer is eligible for to the current sale.
     *
     * @param customerId The customer's identifier.
     * @return The state of the sale after the discounts were applied. If the customer
     *         has no discounts, the sale is unchanged.
     * @throws IllegalStateException If no sale is in progress.
     */
    public SaleDTO applyCustomerDiscounts(String customerId) {
        Sale sale = requireSaleInProgress();
        List<DiscountDTO> discounts = discountCatalog.findDiscounts(customerId);
        discounts.stream().map(DiscountStrategy::from).forEach(sale::addDiscount);
        return sale.toDTO();
    }

    /**
     * Pays for the current sale, prints the receipt and reports the sale to the
     * external systems. The sale is finished afterwards.
     *
     * @param amountPaid The amount the customer paid.
     * @return The change to give back to the customer.
     * @throws InsufficientPaymentException If the amount paid is less than the amount to pay.
     * @throws IllegalStateException        If no sale is in progress.
     */
    public Money pay(Money amountPaid) throws InsufficientPaymentException {
        Sale sale = requireSaleInProgress();
        Money change = sale.pay(amountPaid);
        SaleDTO completedSale = sale.toDTO();

        cashRegister.registerPayment(completedSale.getAmountToPay());
        printer.printReceipt(new ReceiptDTO(completedSale, amountPaid, change));
        inventorySystem.registerSale(completedSale);
        accountingSystem.registerSale(completedSale);

        currentSale = null;
        return change;
    }

    private Sale requireSaleInProgress() {
        if (currentSale == null) {
            throw new IllegalStateException("No sale in progress. Call startSale() first.");
        }
        return currentSale;
    }
}
