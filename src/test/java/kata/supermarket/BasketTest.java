package kata.supermarket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasketTest {

    public static final String TEST_PROMOTION_ONE = "some promotion";
    public static final String TEST_PROMOTION_TWO = "other promotion";
    public static final PromotionsRepository promotionsRepository = new PromotionsRepository();

    @DisplayName("basket provides its total value when containing...")
    @MethodSource
    @ParameterizedTest(name = "{0}")
    void basketProvidesTotalValue(String description, String expectedTotal, Iterable<Item> items) {
        final Basket basket = new Basket(promotionsRepository);
        items.forEach(basket::add);
        assertEquals(new BigDecimal(expectedTotal), basket.total());
    }

    static Stream<Arguments> basketProvidesTotalValue() {
        return Stream.of(
                noItems(),
                aSingleItemPricedPerUnit(),
                multipleItemsPricedPerUnit(),
                aSingleItemPricedByWeight(),
                multipleItemsPricedByWeight(),
                withPromotionalItem(),
                withRepeatedPromotionalItem()
        );
    }

    private static Arguments withPromotionalItem() {
        promotionsRepository.add(TEST_PROMOTION_ONE, list -> new BigDecimal("0.54"));
        promotionsRepository.add(TEST_PROMOTION_TWO, list -> new BigDecimal("0.10"));
        return Arguments.of("Mix of promotional and regular items", "2.65",
                Arrays.asList(aPromotionalPackOfDigestives(TEST_PROMOTION_ONE),
                        aPromotionalPintOfMilk(TEST_PROMOTION_TWO),
                        twoFiftyGramsOfAmericanSweets()));
    }

    private static Arguments withRepeatedPromotionalItem() {
        promotionsRepository.add(TEST_PROMOTION_ONE, list -> new BigDecimal("0.54"));
        // promotion for TEST_PROMOTION_ONE applied ONCE for all.
        // 3 * 1.55 - 0.54 = 4.11
        return Arguments.of("Mix of promotional and regular items", "4.11",
                Arrays.asList(aPromotionalPackOfDigestives(TEST_PROMOTION_ONE),
                        aPromotionalPackOfDigestives(TEST_PROMOTION_ONE),
                        aPromotionalPackOfDigestives(TEST_PROMOTION_ONE)));
    }

    @Test
    public void missingPromotionProducesNPE() {
        final Basket basket = new Basket(promotionsRepository);
        basket.add(aPromotionalPintOfMilk(TEST_PROMOTION_ONE));
        Assertions.assertThrows(NullPointerException.class, basket::total);
    }

    @Test
    public void exceptionFromPromotionCalculationIsPropagated() {
        promotionsRepository.add(TEST_PROMOTION_ONE, list -> {throw new RuntimeException("boom");});
        final Basket basket = new Basket(promotionsRepository);
        basket.add(aPromotionalPintOfMilk(TEST_PROMOTION_ONE));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, basket::total, "boom");
    }

    private static Arguments aSingleItemPricedByWeight() {
        return Arguments.of("a single weighed item", "1.25", Collections.singleton(twoFiftyGramsOfAmericanSweets()));
    }

    private static Arguments multipleItemsPricedByWeight() {
        return Arguments.of("multiple weighed items", "1.85",
                Arrays.asList(twoFiftyGramsOfAmericanSweets(), twoHundredGramsOfPickAndMix())
        );
    }

    private static Arguments multipleItemsPricedPerUnit() {
        return Arguments.of("multiple items priced per unit", "2.04",
                Arrays.asList(aPackOfDigestives(), aPintOfMilk()));
    }

    private static Arguments aSingleItemPricedPerUnit() {
        return Arguments.of("a single item priced per unit", "0.49", Collections.singleton(aPintOfMilk()));
    }

    private static Arguments noItems() {
        return Arguments.of("no items", "0.00", Collections.emptyList());
    }

    private static Item aPintOfMilk() {
        return new Product(new BigDecimal("0.49")).oneOf();
    }

    private static Item aPromotionalPintOfMilk(String promotionId) {
        return new Product(new BigDecimal("0.49"), promotionId).oneOf();
    }

    private static Item aPackOfDigestives() {
        return new Product(new BigDecimal("1.55")).oneOf();
    }

    private static Item aPromotionalPackOfDigestives(String promotionId) {
        return new Product(new BigDecimal("1.55"), promotionId).oneOf();
    }


    private static WeighedProduct aKiloOfAmericanSweets() {
        return new WeighedProduct(new BigDecimal("4.99"));
    }

    private static Item twoFiftyGramsOfAmericanSweets() {
        return aKiloOfAmericanSweets().weighing(new BigDecimal(".25"));
    }

    private static WeighedProduct aKiloOfPickAndMix() {
        return new WeighedProduct(new BigDecimal("2.99"));
    }

    private static Item twoHundredGramsOfPickAndMix() {
        return aKiloOfPickAndMix().weighing(new BigDecimal(".2"));
    }
}