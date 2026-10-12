package se.kth.iv1350.pos.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fake external accounting system, kept in memory. It keeps a record of all
 * completed sales.
 */
public class AccountingSystem {

    private final List<SaleDTO> recordedSales = new ArrayList<>();

    /**
     * Records a completed sale.
     *
     * @param sale The completed sale.
     */
    public void registerSale(SaleDTO sale) {
        recordedSales.add(sale);
    }

    /**
     * @return A read-only view of all recorded sales, oldest first.
     */
    public List<SaleDTO> getRecordedSales() {
        return Collections.unmodifiableList(recordedSales);
    }
}
