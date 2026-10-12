package se.kth.iv1350.pos.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.kth.iv1350.pos.integration.AccountingSystem;
import se.kth.iv1350.pos.integration.DatabaseFailureException;
import se.kth.iv1350.pos.integration.DiscountCatalog;
import se.kth.iv1350.pos.integration.DiscountDTO;
import se.kth.iv1350.pos.integration.InventorySystem;
import se.kth.iv1350.pos.integration.ItemDTO;
import se.kth.iv1350.pos.integration.ItemNotFoundException;
import se.kth.iv1350.pos.integration.Printer;
import se.kth.iv1350.pos.integration.SaleDTO;
import se.kth.iv1350.pos.model.CashRegister;
import se.kth.iv1350.pos.model.InsufficientPaymentException;
import se.kth.iv1350.pos.util.Logger;
import se.kth.iv1350.pos.util.Money;

class ControllerTest {

    private static final ItemDTO MILK =
            new ItemDTO("32001", "Milk", "Dairy", Money.of(10), new BigDecimal("0.12"));

    private InventorySystem inventory;
    private AccountingSystem accounting;
    private CashRegister cashRegister;
    private List<Throwable> loggedErrors;
    private Controller controller;

    @BeforeEach
    void setUp() {
        inventory = new InventorySystem();
        inventory.addItem(MILK, 10);
        DiscountCatalog discounts = new DiscountCatalog();
        discounts.addDiscount("C-1", new DiscountDTO(DiscountDTO.Type.PERCENTAGE, BigDecimal.TEN));
        accounting = new AccountingSystem();
        cashRegister = new CashRegister();
        loggedErrors = new ArrayList<>();
        Logger logger = new Logger() {
            @Override
            public void info(String message) {
            }

            @Override
            public void error(String message, Throwable exception) {
                loggedErrors.add(exception);
            }
        };
        Printer silentPrinter = new Printer(new PrintStream(new ByteArrayOutputStream()));

        controller = new Controller(inventory, discounts, accounting, silentPrinter, cashRegister, logger);
        controller.startSale();
    }

    @Test
    void registerItemReturnsUpdatedSale() throws Exception {
        SaleDTO sale = controller.registerItem("32001", 2);

        assertEquals(Money.of("22.40"), sale.getAmountToPay());
        assertEquals(Money.of("2.40"), sale.getVat());
    }

    @Test
    void unknownItemIsReportedToCaller() {
        assertThrows(ItemNotFoundException.class, () -> controller.registerItem("99999", 1));
    }

    @Test
    void databaseFailureIsLoggedAndWrapped() {
        OperationFailedException e = assertThrows(OperationFailedException.class,
                () -> controller.registerItem(InventorySystem.UNREACHABLE_DATABASE_ITEM_ID, 1));

        assertInstanceOf(DatabaseFailureException.class, e.getCause());
        assertEquals(1, loggedErrors.size());
    }

    @Test
    void customerDiscountsAreApplied() throws Exception {
        controller.registerItem("32001", 10);

        SaleDTO sale = controller.applyCustomerDiscounts("C-1");

        assertEquals(Money.of("100.80"), sale.getAmountToPay());
        assertEquals(Money.of("10.80"), sale.getVat());
    }

    @Test
    void customerWithoutDiscountsPaysFullPrice() throws Exception {
        controller.registerItem("32001", 10);

        assertEquals(Money.of(112), controller.applyCustomerDiscounts("unknown").getAmountToPay());
    }

    @Test
    void payUpdatesAllSystemsAndReturnsChange() throws Exception {
        controller.registerItem("32001", 2);

        Money change = controller.pay(Money.of(50));

        assertEquals(Money.of("27.60"), change);
        assertEquals(8, inventory.getStock("32001"));
        assertEquals(1, accounting.getRecordedSales().size());
        assertEquals(Money.of("22.40"), cashRegister.getTotalRevenue());
    }

    @Test
    void payingTooLittleLeavesSaleOpen() throws Exception {
        controller.registerItem("32001", 2);

        assertThrows(InsufficientPaymentException.class, () -> controller.pay(Money.of(10)));
        assertEquals(Money.of("27.60"), controller.pay(Money.of(50)));
    }

    @Test
    void saleIsFinishedAfterPayment() throws Exception {
        controller.registerItem("32001", 1);
        controller.pay(Money.of(100));

        assertThrows(IllegalStateException.class, () -> controller.registerItem("32001", 1));
    }
}
