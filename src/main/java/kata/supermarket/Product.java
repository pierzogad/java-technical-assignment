package kata.supermarket;

import java.math.BigDecimal;

public class Product extends PromotionalProduct {

    private final BigDecimal pricePerUnit;

    public Product(final BigDecimal pricePerUnit) {
        super(null);
        this.pricePerUnit = pricePerUnit;
    }

    public Product(final BigDecimal pricePerUnit, String promotionId) {
        super(promotionId);
        this.pricePerUnit = pricePerUnit;
    }

    BigDecimal pricePerUnit() {
        return pricePerUnit;
    }

    public Item oneOf() {
        return new ItemByUnit(this);
    }
}
