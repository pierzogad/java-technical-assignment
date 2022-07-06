package kata.supermarket.discounts;

import kata.supermarket.Item;
import kata.supermarket.Product;
import kata.supermarket.WeighedProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuyNGetMFreeTest {

    @DisplayName("Discount Scheme is correctly calculated ...")
    @MethodSource
    @ParameterizedTest(name = "{0}")
    public void testDiscountScheme(String description, int buyAmount, int freeAmount, String expectedValue, Collection<Item> items) {
        BuyNGetMFree scheme = new BuyNGetMFree(buyAmount, freeAmount);
        assertEquals(new BigDecimal(expectedValue), scheme.calculateDiscount(items));
    }

    static Stream<Arguments> testDiscountScheme() {
        return Stream.of(
                noItems(),
                aSingle(),
                exactMatch(),
                withExtraItems(),
                withDifferentPrices()
        );

    }

    private static Arguments noItems() {
        return Arguments.of("noItems", 2, 1, "0.00", Collections.emptyList());
    }

    private static Arguments aSingle() {
        Item anItem = getUnitItem("1.00");
        return Arguments.of("aSingle", 2, 1, "0.00", Collections.singleton(anItem));
    }

    private static Arguments exactMatch() {
        Item anItem = getUnitItem("1.01");
        return Arguments.of("exactMatch", 2, 1, "1.01", List.of(anItem, anItem, anItem));
    }

    private static Arguments withExtraItems() {
        Item anItem = getUnitItem("1.01");
        // promotion is: buy 1 get 1 free - hence 2 out of 5 are free
        return Arguments.of("withExtraItems", 1, 1, "2.02", List.of(anItem, anItem, anItem, anItem, anItem));
    }

    private static Arguments withDifferentPrices() {
        Item anItem = getUnitItem("1.01");
        Item cheaperItem = getUnitItem("1.00");
        Item yetCheaperItem = getUnitItem("0.99");
        // promotion is: buy 1 get 1 free -
        // should be 1.01 + 1.01 free, then 1.00 + 0.99 free
        return Arguments.of("withDifferentPrices", 1, 1, "2.00", List.of(anItem, cheaperItem, yetCheaperItem, anItem));
    }

    private static Item getUnitItem(String price) {
        return new Product(new BigDecimal(price)).oneOf();
    }

    @Test
    public void weightedItemThrowsIllegalArgumentException() {
        Item weightedItem = new WeighedProduct(new BigDecimal("1.75")).weighing(BigDecimal.ONE);
        BuyNGetMFree scheme = new BuyNGetMFree(1, 1);
        Assertions.assertThrows(IllegalArgumentException.class, () ->scheme.calculateDiscount(Collections.singleton(weightedItem)));
    }
}
