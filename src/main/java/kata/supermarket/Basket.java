package kata.supermarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Basket {
    private final List<Item> items;
    private final PromotionsRepository promotionsRepository;

    public Basket(PromotionsRepository promotionsRepository) {
        this.promotionsRepository = promotionsRepository;
        this.items = new ArrayList<>();
    }

    public void add(final Item item) {
        this.items.add(item);
    }

    List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal total() {
        return new TotalCalculator().calculate();
    }

    private class TotalCalculator {
        private final List<Item> items;

        TotalCalculator() {
            this.items = items();
        }

        private BigDecimal subtotal() {
            return items.stream().map(Item::price)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * TODO: This could be a good place to apply the results of
         *  the discount calculations.
         *  It is not likely to be the best place to do those calculations.
         *  Think about how Basket could interact with something
         *  which provides that functionality.
         */
        private BigDecimal discounts() {
            return items.stream()
                    .filter(item -> item.getPromotionId() != null)
                    .collect(Collectors.groupingBy(Item::getPromotionId))
                    .entrySet().stream()
                    .map(group -> promotionsRepository.get(group.getKey()).calculateDiscount(group.getValue()))
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        private BigDecimal calculate() {
            return subtotal().subtract(discounts());
        }
    }
}
