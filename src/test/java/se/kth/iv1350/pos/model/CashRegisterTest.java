package se.kth.iv1350.pos.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.kth.iv1350.pos.util.Money;

class CashRegisterTest {

    @Test
    void paymentIncreasesBalanceAndRevenue() {
        CashRegister register = new CashRegister(Money.of(500));

        register.registerPayment(Money.of(120));

        assertEquals(Money.of(620), register.getBalance());
        assertEquals(Money.of(120), register.getTotalRevenue());
    }

    @Test
    void observersAreNotifiedOfAccumulatedRevenue() {
        CashRegister register = new CashRegister();
        List<Money> notifications = new ArrayList<>();
        register.addRevenueObserver(notifications::add);

        register.registerPayment(Money.of(100));
        register.registerPayment(Money.of(50));

        assertEquals(List.of(Money.of(100), Money.of(150)), notifications);
    }
}
