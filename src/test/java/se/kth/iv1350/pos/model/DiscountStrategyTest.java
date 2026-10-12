package se.kth.iv1350.pos.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import se.kth.iv1350.pos.integration.DiscountDTO;
import se.kth.iv1350.pos.util.Money;

class DiscountStrategyTest {

    @Test
    void percentageDiscount() {
        assertEquals(Money.of(90), new PercentageDiscount(10).apply(Money.of(100)));
        assertEquals(Money.ZERO, new PercentageDiscount(100).apply(Money.of(100)));
    }

    @Test
    void percentageOutsideRangeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(-1));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(101));
    }

    @Test
    void fixedAmountDiscount() {
        assertEquals(Money.of(70), new FixedAmountDiscount(Money.of(30)).apply(Money.of(100)));
    }

    @Test
    void fixedAmountDiscountNeverGoesBelowZero() {
        assertEquals(Money.ZERO, new FixedAmountDiscount(Money.of(150)).apply(Money.of(100)));
    }

    @Test
    void compositeAppliesDiscountsInOrder() {
        CompositeDiscount discount = new CompositeDiscount();
        discount.add(new PercentageDiscount(10));
        discount.add(new FixedAmountDiscount(Money.of(50)));

        assertEquals(Money.of(130), discount.apply(Money.of(200)));
    }

    @Test
    void emptyCompositeGivesNoDiscount() {
        assertEquals(Money.of(200), new CompositeDiscount().apply(Money.of(200)));
    }

    @Test
    void createdFromDiscountDTO() {
        assertEquals(new PercentageDiscount(new BigDecimal("15")),
                DiscountStrategy.from(new DiscountDTO(DiscountDTO.Type.PERCENTAGE, new BigDecimal("15"))));
        assertEquals(new FixedAmountDiscount(Money.of(20)),
                DiscountStrategy.from(new DiscountDTO(DiscountDTO.Type.FIXED_AMOUNT, new BigDecimal("20"))));
    }
}
