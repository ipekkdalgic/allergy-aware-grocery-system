package domain.model;

import domain.enums.Allergen;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class OrderItem {
    private final String productId;
    private final String productName;
    private final int quantity;
    private final Money unitPrice;
    private final boolean allergyWarning;
    private final Set<Allergen> matchedAllergens;

    public OrderItem(CartItem cartItem) {
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.quantity = cartItem.getQuantity();
        this.unitPrice = cartItem.getProduct().getPrice();
        this.allergyWarning = cartItem.hasAllergyWarning();
        this.matchedAllergens = cartItem.getMatchedAllergens().isEmpty()
                ? EnumSet.noneOf(Allergen.class)
                : EnumSet.copyOf(cartItem.getMatchedAllergens());
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getLineTotal() {
        return unitPrice.multiply(quantity);
    }

    public boolean hasAllergyWarning() {
        return allergyWarning;
    }

    public Set<Allergen> getMatchedAllergens() {
        return Collections.unmodifiableSet(matchedAllergens);
    }
}
