package domain.model;

import domain.enums.Allergen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a customer's shopping cart during runtime.
 * Cart items can preserve allergy warning information until checkout.
 */
public class Cart {
    private final String id;
    private final String customerId;
    private final List<CartItem> items = new ArrayList<>();

    public Cart(String id, String customerId) {
        this.id = id;
        this.customerId = customerId;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean hasAllergyWarnings() {
        return items.stream().anyMatch(CartItem::hasAllergyWarning);
    }

    public void addItem(Product product, int quantity, AllergyCheckResult allergyCheckResult, boolean userConfirmedRisk) {
        Optional<CartItem> existing = items.stream()
                .filter(item -> item.getProduct().getId().equalsIgnoreCase(product.getId()))
                .findFirst();

        // If the product is already in the cart, only quantity and warning information are updated.
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.increaseQuantity(quantity);
            if (allergyCheckResult != null && allergyCheckResult.hasWarning()) {
                item.markAllergyWarning(allergyCheckResult.getMatchedAllergens(), userConfirmedRisk);
            }
            return;
        }

        boolean warning = allergyCheckResult != null && allergyCheckResult.hasWarning();
        Set<Allergen> matchedAllergens = warning ? allergyCheckResult.getMatchedAllergens() : Collections.emptySet();
        items.add(new CartItem(product, quantity, warning, matchedAllergens, userConfirmedRisk));
    }

    public OperationResult<Void> removeItemByIndex(int index) {
        if (index < 0 || index >= items.size()) {
            return OperationResult.failure("Invalid cart item selection.");
        }
        items.remove(index);
        return OperationResult.success("Item removed from cart.");
    }

    public Money calculateTotal() {
        Money total = Money.zero();
        for (CartItem item : items) {
            total = total.add(item.getLineTotal());
        }
        return total;
    }

    public void clear() {
        items.clear();
    }
}
