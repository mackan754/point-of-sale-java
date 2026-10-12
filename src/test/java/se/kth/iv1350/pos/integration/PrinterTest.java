package se.kth.iv1350.pos.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.kth.iv1350.pos.util.Money;

class PrinterTest {

    @Test
    void receiptContainsItemsTotalsAndChange() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Printer printer = new Printer(new PrintStream(buffer, true));
        ItemDTO milk = new ItemDTO("32001", "Milk", "Dairy", Money.of(12), new BigDecimal("0.12"));
        SaleDTO sale = new SaleDTO(LocalDateTime.of(2024, 5, 18, 13, 37, 0),
                List.of(new SaleItemDTO(milk, 2, Money.of(24), Money.of("2.88"))),
                Money.of("26.88"), Money.ZERO, Money.of("26.88"), Money.of("2.88"));

        printer.printReceipt(new ReceiptDTO(sale, Money.of(50), Money.of("23.12")));

        String receipt = buffer.toString();
        assertTrue(receipt.contains("2024-05-18 13:37:00"));
        assertTrue(receipt.contains("Milk"));
        assertTrue(receipt.contains("26.88 SEK"));
        assertTrue(receipt.contains("23.12 SEK"));
    }
}
