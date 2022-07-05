package kata.supermarket;

import java.math.BigDecimal;
import java.util.Collection;

public interface DiscountScheme {
    BigDecimal calculateDiscount(Collection<Item> items);
}
