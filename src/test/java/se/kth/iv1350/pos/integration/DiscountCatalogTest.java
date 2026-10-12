package se.kth.iv1350.pos.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class DiscountCatalogTest {

    @Test
    void unknownCustomerHasNoDiscounts() {
        assertTrue(new DiscountCatalog().findDiscounts("nobody").isEmpty());
    }

    @Test
    void discountsAreReturnedInOrderAdded() {
        DiscountCatalog catalog = new DiscountCatalog();
        DiscountDTO percentage = new DiscountDTO(DiscountDTO.Type.PERCENTAGE, BigDecimal.TEN);
        DiscountDTO fixed = new DiscountDTO(DiscountDTO.Type.FIXED_AMOUNT, BigDecimal.ONE);
        catalog.addDiscount("C-1", percentage);
        catalog.addDiscount("C-1", fixed);

        assertEquals(List.of(percentage, fixed), catalog.findDiscounts("C-1"));
    }

    @Test
    void loadsSeedDataFromCsv() {
        List<DiscountDTO> discounts = DiscountCatalog.fromCsv("/discounts.csv").findDiscounts("C-1001");

        assertEquals(List.of(
                new DiscountDTO(DiscountDTO.Type.PERCENTAGE, new BigDecimal("10")),
                new DiscountDTO(DiscountDTO.Type.FIXED_AMOUNT, new BigDecimal("50"))), discounts);
    }
}
