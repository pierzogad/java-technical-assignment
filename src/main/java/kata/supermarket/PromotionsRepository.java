package kata.supermarket;

import java.util.HashMap;
import java.util.Map;

public class PromotionsRepository {
    private final Map<String, DiscountScheme> promotions;

    public PromotionsRepository() {
        promotions = new HashMap<>();
    }

    public void add(String promotionId, DiscountScheme scheme) {
        promotions.put(promotionId, scheme);
    }

    public DiscountScheme get(String promotionId ) {
        return promotions.get(promotionId);
    }
}
