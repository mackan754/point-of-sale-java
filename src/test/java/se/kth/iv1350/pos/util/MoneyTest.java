package se.kth.iv1350.pos.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void amountsWithDifferentScaleAreEqual() {
        assertEquals(Money.of("12.5"), Money.of("12.500"));
        assertEquals(Money.of(12), Money.of("12.00"));
    }

    @Test
    void decimalArithmeticIsExact() {
        assertEquals(Money.of("0.30"), Money.of("0.10").plus(Money.of("0.20")));
    }

    @Test
    void minusCanGoBelowZero() {
        Money result = Money.of(3).minus(Money.of(10));
        assertEquals(Money.of(-7), result);
        assertTrue(result.isNegative());
    }

    @Test
    void timesQuantity() {
        assertEquals(Money.of("37.50"), Money.of("12.50").times(3));
    }

    @Test
    void timesRateRoundsHalfUp() {
        assertEquals(Money.of("1.25"), Money.of("4.99").times(new BigDecimal("0.25")));
    }

    @Test
    void comparisons() {
        assertTrue(Money.of(5).isLessThan(Money.of(6)));
        assertFalse(Money.of(6).isLessThan(Money.of(6)));
        assertEquals(Money.of(5), Money.of(5).min(Money.of(6)));
    }

    @Test
    void toStringShowsTwoDecimalsAndCurrency() {
        assertEquals("12.50 SEK", Money.of("12.5").toString());
    }

    @Test
    void nullAmountIsRejected() {
        assertThrows(NullPointerException.class, () -> new Money(null));
    }
}
