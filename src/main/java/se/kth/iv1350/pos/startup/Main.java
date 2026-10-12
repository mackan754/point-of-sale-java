package se.kth.iv1350.pos.startup;

import java.nio.file.Path;

import se.kth.iv1350.pos.controller.Controller;
import se.kth.iv1350.pos.integration.AccountingSystem;
import se.kth.iv1350.pos.integration.DiscountCatalog;
import se.kth.iv1350.pos.integration.InventorySystem;
import se.kth.iv1350.pos.integration.Printer;
import se.kth.iv1350.pos.model.CashRegister;
import se.kth.iv1350.pos.util.FileLogger;
import se.kth.iv1350.pos.util.TotalRevenueFileOutput;
import se.kth.iv1350.pos.view.TotalRevenueView;
import se.kth.iv1350.pos.view.View;

/**
 * Starts the application: creates all objects, connects them and runs two demo sales.
 */
public final class Main {

    private static final Path LOG_FILE = Path.of("pos-log.txt");
    private static final Path REVENUE_FILE = Path.of("total-revenue.txt");

    private Main() {
    }

    /**
     * @param args Not used.
     */
    public static void main(String[] args) {
        try (FileLogger logger = new FileLogger(LOG_FILE);
             TotalRevenueFileOutput revenueFile = new TotalRevenueFileOutput(REVENUE_FILE)) {

            Controller controller = new Controller(
                    InventorySystem.fromCsv("/inventory.csv"),
                    DiscountCatalog.fromCsv("/discounts.csv"),
                    new AccountingSystem(),
                    new Printer(),
                    new CashRegister(),
                    logger);
            controller.addRevenueObserver(new TotalRevenueView());
            controller.addRevenueObserver(revenueFile);

            View view = new View(controller);
            view.runDemoSale();
            view.runDemoSale();
        }
    }
}
