package se.kth.iv1350.pos.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.kth.iv1350.pos.util.Money;

class InventorySystemTest {

    private static final ItemDTO MILK =
            new ItemDTO("32001", "Milk", "Dairy", Money.of(12), new BigDecimal("0.12"));

    private InventorySystem inventory;

    @BeforeEach
    void setUp() {
        inventory = new InventorySystem();
        inventory.addItem(MILK, 10);
    }

    @Test
    void findExistingItem() throws ItemNotFoundException {
        assertEquals(MILK, inventory.findItem("32001"));
    }

    @Test
    void findUnknownItemThrows() {
        ItemNotFoundException e = assertThrows(ItemNotFoundException.class, () -> inventory.findItem("99999"));
        assertEquals("99999", e.getItemId());
    }

    @Test
    void unreachableDatabaseIsSimulated() {
        assertThrows(DatabaseFailureException.class,
                () -> inventory.findItem(InventorySystem.UNREACHABLE_DATABASE_ITEM_ID));
    }

    @Test
    void registerSaleReducesStock() throws ItemNotFoundException {
        SaleItemDTO soldMilk = new SaleItemDTO(MILK, 3, Money.of(36), Money.of("4.32"));
        SaleDTO sale = new SaleDTO(LocalDateTime.now(), List.of(soldMilk),
                Money.of("40.32"), Money.ZERO, Money.of("40.32"), Money.of("4.32"));

        inventory.registerSale(sale);

        assertEquals(7, inventory.getStock("32001"));
    }

    @Test
    void loadsSeedDataFromCsv() throws ItemNotFoundException {
        InventorySystem seeded = InventorySystem.fromCsv("/inventory.csv");

        ItemDTO milk = seeded.findItem("32001");
        assertEquals("Milk", milk.getName());
        assertEquals(Money.of(12), milk.getPrice());
        assertEquals(new BigDecimal("0.12"), milk.getVatRate());
        assertEquals(10, seeded.getStock("32001"));
    }
}
