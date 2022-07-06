package kata.supermarket.discounts;

import kata.supermarket.DiscountScheme;
import kata.supermarket.Item;
import kata.supermarket.ItemByWeight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class BuyNGetMFree implements DiscountScheme {

    private final int buyAmount;
    private final int freeAmount;

    public BuyNGetMFree(int buyAmount, int freeAmount) {
        this.buyAmount = buyAmount;
        this.freeAmount = freeAmount;
    }

    @Override
    public BigDecimal calculateDiscount(Collection<Item> items) {
        items.stream()
                .filter(item -> item instanceof ItemByWeight)
                .findAny()
                .ifPresent(item -> {throw new IllegalArgumentException("Discount applies to items by unit");});
        List<Item> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(Item::price).reversed());

        int idx = 0;
        int toProcess = sorted.size();
        BigDecimal discount = BigDecimal.ZERO;

        while (toProcess > buyAmount) {
            idx += buyAmount;
            toProcess -= buyAmount;

            int discounted = Math.min(toProcess, freeAmount);
            for (int i = 0 ; i < discounted ; i++) {
                discount = discount.add(sorted.get(idx).price());
                ++idx;
                --toProcess;
            }
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}
