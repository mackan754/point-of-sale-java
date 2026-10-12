package se.kth.iv1350.pos.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.kth.iv1350.pos.integration.ItemDTO;
import se.kth.iv1350.pos.integration.SaleDTO;
import se.kth.iv1350.pos.util.Money;

class SaleTest {

    private static final ItemDTO MILK =
            new ItemDTO("1", "Milk", "Dairy", Money.of(10), new BigDecimal("0.12"));
    private static final ItemDTO SOAP =
            new ItemDTO("2", "Soap", "Household", Money.of(20), new BigDecimal("0.25"));

    private Sale sale;

    @BeforeEach
    void setUp() {
        sale = new Sale(LocalDateTime.of(2024, 5, 18, 12, 0));
    }

    @Test
    void newSaleIsEmpty() {
        SaleDTO snapshot = sale.toDTO();
        assertTrue(snapshot.getItems().isEmpty());
        assertEquals(Money.ZERO, snapshot.getAmountToPay());
    }

    @Test
    void totalsIncludeVatPerItem() {
        sale.addItem(MILK, 2);
        sale.addItem(SOAP, 1);

        assertEquals(Money.of(40), sale.getTotalPrice());
        assertEquals(Money.of("7.40"), sale.getTotalVat());
        assertEquals(Money.of("47.40"), sale.getTotalPriceIncludingVat());
    }

    @Test
    void addingSameItemTwiceIncreasesQuantity() {
        sale.addItem(MILK, 2);
        sale.addItem(MILK, 3);

        SaleDTO snapshot = sale.toDTO();
        assertEquals(1, snapshot.getItems().size());
        assertEquals(5, snapshot.findItem("1").orElseThrow().getQuantity());
        assertEquals(Money.of(56), snapshot.getTotalPriceIncludingVat());
    }

    @Test
    void nonPositiveQuantityIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> sale.addItem(MILK, 0));
        assertThrows(IllegalArgumentException.class, () -> sale.addItem(MILK, -1));
    }

    @Test
    void discountsReduceAmountToPay() {
        sale.addItem(MILK, 10);
        sale.addDiscount(new PercentageDiscount(10));

        SaleDTO snapshot = sale.toDTO();
        assertEquals(Money.of(112), snapshot.getTotalPriceIncludingVat());
        assertEquals(Money.of("11.20"), snapshot.getDiscount());
        assertEquals(Money.of("100.80"), snapshot.getAmountToPay());
    }

    @Test
    void noDiscountMeansVatIsUnchanged() {
        sale.addItem(MILK, 2);
        sale.addItem(SOAP, 1);

        assertEquals(sale.getTotalVat(), sale.getVatToPay());
    }

    @Test
    void percentageDiscountReducesVatOfAllRatesBySameFraction() {
        sale.addItem(MILK, 10);
        sale.addItem(SOAP, 2);
        sale.addDiscount(new PercentageDiscount(10));

        SaleDTO snapshot = sale.toDTO();
        assertEquals(Money.of(162), snapshot.getTotalPriceIncludingVat());
        assertEquals(Money.of("145.80"), snapshot.getAmountToPay());
        assertEquals(Money.of("19.80"), snapshot.getVat());
        assertEquals(Money.of(126), snapshot.getAmountToPayExcludingVat());
    }

    @Test
    void fixedDiscountIsSpreadOverVatRatesInProportionToPrice() {
        sale.addItem(MILK, 10);
        sale.addItem(SOAP, 2);
        sale.addDiscount(new FixedAmountDiscount(Money.of(50)));

        // Milk is 112 of 162 SEK and soap 50 of 162 SEK, so 112 SEK is left to pay:
        // milk 77.43 SEK incl. 12 % VAT (8.30) and soap 34.57 SEK incl. 25 % VAT (6.91).
        SaleDTO snapshot = sale.toDTO();
        assertEquals(Money.of(112), snapshot.getAmountToPay());
        assertEquals(Money.of("15.21"), snapshot.getVat());
    }

    @Test
    void discountsAreAppliedInOrderToPriceIncludingVat() {
        sale.addItem(MILK, 10);
        sale.addDiscount(new PercentageDiscount(10));
        sale.addDiscount(new FixedAmountDiscount(Money.of(50)));

        // 112.00 * 0.9 = 100.80, minus 50 = 50.80. All of it has 12 % VAT: 50.80 * 12 / 112 = 5.44.
        SaleDTO snapshot = sale.toDTO();
        assertEquals(Money.of("50.80"), snapshot.getAmountToPay());
        assertEquals(Money.of("5.44"), snapshot.getVat());
    }

    @Test
    void discountLargerThanPriceMakesSaleFree() {
        sale.addItem(MILK, 1);
        sale.addDiscount(new FixedAmountDiscount(Money.of(1000)));

        SaleDTO snapshot = sale.toDTO();
        assertEquals(Money.ZERO, snapshot.getAmountToPay());
        assertEquals(Money.ZERO, snapshot.getVat());
    }

    @Test
    void emptySaleHasNoVat() {
        sale.addDiscount(new PercentageDiscount(10));

        assertEquals(Money.ZERO, sale.getVatToPay());
    }

    @Test
    void payReturnsChange() throws InsufficientPaymentException {
        sale.addItem(MILK, 1);

        assertEquals(Money.of("88.80"), sale.pay(Money.of(100)));
        assertTrue(sale.isPaid());
    }

    @Test
    void payingTooLittleIsRejected() {
        sale.addItem(MILK, 1);

        InsufficientPaymentException e =
                assertThrows(InsufficientPaymentException.class, () -> sale.pay(Money.of(10)));
        assertEquals(Money.of("11.20"), e.getAmountToPay());
    }

    @Test
    void paidSaleCanNotBeChanged() throws InsufficientPaymentException {
        sale.addItem(MILK, 1);
        sale.pay(Money.of(100));

        assertThrows(IllegalStateException.class, () -> sale.addItem(MILK, 1));
        assertThrows(IllegalStateException.class, () -> sale.pay(Money.of(100)));
    }

    @Test
    void snapshotIsNotAffectedByLaterChanges() {
        sale.addItem(MILK, 1);
        SaleDTO snapshot = sale.toDTO();

        sale.addItem(MILK, 1);

        assertEquals(1, snapshot.findItem("1").orElseThrow().getQuantity());
    }
}
