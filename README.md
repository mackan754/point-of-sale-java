# Point of Sale

A cash register application for a retail store. It was originally built for the
course *IV1350 Object-Oriented Design* at KTH and later refactored.

The program runs two scripted sales that show the whole flow: registering items,
handling an unknown item and a database failure, applying customer discounts,
paying, printing a receipt and updating the external systems.

## Running

Requires Java 17+ and Maven.

```bash
mvn test          # run the unit tests
mvn compile exec:java   # run the demo
```

The demo writes technical errors to `pos-log.txt` and the total revenue to
`total-revenue.txt` in the working directory.

## Architecture

The code is split into layers, and dependencies only point downwards:

```
view → controller → model → integration → util
```

The view also uses the DTOs and exceptions that the controller returns or throws,
but it never calls the model or the external systems directly. There are no
circular dependencies between packages.

| Package       | Responsibility |
|---------------|----------------|
| `view`        | What the cashier sees. Contains no logic, only calls the controller and prints results. |
| `controller`  | Coordinates a sale. Passes calls on to the model and the external systems; contains no business rules. |
| `model`       | Business logic: the sale, its totals and VAT, discounts and the cash register. |
| `integration` | Interfaces to external systems (inventory, discounts, accounting, printer) and the immutable DTOs used to pass data between layers. |
| `util`        | Shared building blocks: `Money`, logging and the revenue observer interface. |
| `startup`     | Creates and connects all objects. |

### Design patterns

- **Strategy** – `DiscountStrategy` with `PercentageDiscount` and `FixedAmountDiscount`.
- **Composite** – `CompositeDiscount` applies several discounts in order.
- **Observer** – `CashRegister` notifies `RevenueObserver`s (`TotalRevenueView`, `TotalRevenueFileOutput`) after each sale.

### Data types

- Money is represented by `Money`, an immutable class around `BigDecimal` with
  two decimals, so prices such as 12.50 SEK and percentage discounts are exact.
- Quantities and stock levels are plain `int`s.
- Items have a price excluding VAT and a VAT rate (12 % for food, 25 % for other
  goods), and VAT is calculated per sale line.
- Data passed between layers uses immutable DTO classes (`ItemDTO`, `SaleDTO`, ...) with only `final` fields and getters.

### Discounts and VAT

Discounts are applied, in order, to the total price including VAT, since that is
the price the customer sees. VAT is only paid on what the customer actually pays,
so the discount is spread over all items in proportion to their price and the
VAT of every item is reduced by the same fraction. With mixed VAT rates this
means a 50 SEK discount on a sale of 112 SEK food (12 %) and 50 SEK soap (25 %)
lowers the VAT from 22.00 to 15.21 SEK.

### Fake databases

There is no real database. `InventorySystem` and `DiscountCatalog` keep their
data in hash maps that are filled from `src/main/resources/inventory.csv` and
`discounts.csv` at startup, so the test data can be changed without touching the code.

Looking up item `00000` simulates that the database server can not be reached.

| Item id | Item      | Price excl. VAT | VAT  |
|---------|-----------|-----------------|------|
| 32001   | Milk      | 12.00           | 12 % |
| 32002   | Banana    | 5.00            | 12 % |
| 32003   | Ice cream | 49.00           | 12 % |
| 32004   | Pasta     | 15.00           | 12 % |
| 32005   | Dish soap | 24.00           | 25 % |

Customer `C-1001` gets 10 % off and then 50 SEK off; customer `C-2002` gets 5 % off.
