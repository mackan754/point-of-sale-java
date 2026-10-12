package se.kth.iv1350.pos.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import se.kth.iv1350.pos.controller.Controller;
import se.kth.iv1350.pos.integration.AccountingSystem;
import se.kth.iv1350.pos.integration.DiscountCatalog;
import se.kth.iv1350.pos.integration.InventorySystem;
import se.kth.iv1350.pos.integration.Printer;
import se.kth.iv1350.pos.model.CashRegister;
import se.kth.iv1350.pos.util.Logger;

class ViewTest {

    @Test
    void demoSaleRunsToCompletion() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(buffer, true);
        Controller controller = new Controller(
                InventorySystem.fromCsv("/inventory.csv"),
                DiscountCatalog.fromCsv("/discounts.csv"),
                new AccountingSystem(),
                new Printer(output),
                new CashRegister(),
                new SilentLogger());

        new View(controller, output).runDemoSale();

        String printout = buffer.toString();
        assertTrue(printout.contains("A new sale has been started."));
        assertTrue(printout.contains("Unknown item 99999"));
        assertTrue(printout.contains("Could not register item 00000"));
        assertTrue(printout.contains("Change to give the customer: 108.69 SEK"));
    }

    private static class SilentLogger implements Logger {
        @Override
        public void info(String message) {
        }

        @Override
        public void error(String message, Throwable exception) {
        }
    }
}
