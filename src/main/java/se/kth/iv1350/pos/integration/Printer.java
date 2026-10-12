package se.kth.iv1350.pos.integration;

import java.io.PrintStream;

import se.kth.iv1350.pos.util.DateTimeFormats;

/**
 * The interface to the external receipt printer. This fake printer writes the
 * receipt to a {@link PrintStream} instead of to paper.
 */
public class Printer {

    private static final String SEPARATOR = "-".repeat(40);

    private final PrintStream output;

    /**
     * Creates a printer that prints to {@code System.out}.
     */
    public Printer() {
        this(System.out);
    }

    /**
     * Creates a printer that prints to the specified stream.
     *
     * @param output Where to print receipts.
     */
    public Printer(PrintStream output) {
        this.output = output;
    }

    /**
     * Prints a receipt.
     *
     * @param receipt The receipt to print.
     */
    public void printReceipt(ReceiptDTO receipt) {
        output.println(format(receipt));
    }

    private static String format(ReceiptDTO receipt) {
        SaleDTO sale = receipt.getSale();
        StringBuilder text = new StringBuilder();
        text.append(SEPARATOR).append(" RECEIPT ").append(SEPARATOR).append('\n');
        text.append("Time of sale: ").append(DateTimeFormats.format(sale.getStartTime())).append("\n\n");
        for (SaleItemDTO saleItem : sale.getItems()) {
            text.append(String.format("%-20s %3d x %14s %16s%n",
                    saleItem.getItem().getName(),
                    saleItem.getQuantity(),
                    saleItem.getItem().getPriceIncludingVat(),
                    saleItem.getTotalPriceIncludingVat()));
        }
        text.append('\n');
        appendRow(text, "Total:", sale.getTotalPriceIncludingVat().toString());
        appendRow(text, "Discount:", "-" + sale.getDiscount());
        appendRow(text, "To pay:", sale.getAmountToPay().toString());
        appendRow(text, "  of which VAT:", sale.getVat().toString());
        appendRow(text, "  excl. VAT:", sale.getAmountToPayExcludingVat().toString());
        appendRow(text, "Paid:", receipt.getAmountPaid().toString());
        appendRow(text, "Change:", receipt.getChange().toString());
        text.append(SEPARATOR).append("-- END --").append(SEPARATOR);
        return text.toString();
    }

    private static void appendRow(StringBuilder text, String label, String value) {
        text.append(String.format("%-20s %36s%n", label, value));
    }
}
