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

public class BuyNForM implements DiscountScheme {

    private final int buyAmount;
    private final BigDecimal offerPrice;

    public BuyNForM(int buyAmount, BigDecimal offerPrice) {
        assert buyAmount > 1;
//        assert offerPrice > 0
        this.offerPrice = offerPrice.setScale(2);
        this.buyAmount = buyAmount;
    }

    @Override
    public BigDecimal calculateDiscount(Collection<Item> items) {
        items.stream()
                .filter(item -> item instanceof ItemByWeight)
                .findAny()
                .ifPresent(item -> {throw new IllegalArgumentException("Discount applies to items by unit");});

        List<Item> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(Item::price));


        BigDecimal actualPrice = sorted.stream()
                .map(Item::price)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .setScale(2);
        BigDecimal offeredPrice = offerPrice.multiply(BigDecimal.valueOf(sorted.size() / buyAmount));

        int nonOfferedCount = sorted.size() % buyAmount;
        for (int idx = 0 ; idx < nonOfferedCount ; idx++) {
            offeredPrice = offeredPrice.add(sorted.get(idx).price());
        }

        return actualPrice.subtract(offeredPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
