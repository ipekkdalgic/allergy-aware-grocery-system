package domain.model;

import domain.enums.Allergen;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class CartItem {
    private final Product product;
    private int quantity;
    private boolean allergyWarning;
    private final Set<Allergen> matchedAllergens;
    private boolean addedWithUserConfirmation;

    public CartItem(Product product, int quantity, boolean allergyWarning, Set<Allergen> matchedAllergens, boolean addedWithUserConfirmation) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.product = product;
        this.quantity = quantity;
        this.allergyWarning = allergyWarning;
        this.matchedAllergens = matchedAllergens == null || matchedAllergens.isEmpty()
                ? EnumSet.noneOf(Allergen.class)
                : EnumSet.copyOf(matchedAllergens);
        this.addedWithUserConfirmation = addedWithUserConfirmation;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        quantity += amount;
    }

    public boolean hasAllergyWarning() {
        return allergyWarning;
    }

    public void markAllergyWarning(Set<Allergen> allergens, boolean userConfirmed) {
        this.allergyWarning = true;
        if (allergens != null) {
            this.matchedAllergens.addAll(allergens);
        }
        this.addedWithUserConfirmation = this.addedWithUserConfirmation || userConfirmed;
    }

    public Set<Allergen> getMatchedAllergens() {
        return Collections.unmodifiableSet(matchedAllergens);
    }

    public boolean isAddedWithUserConfirmation() {
        return addedWithUserConfirmation;
    }

    public Money getLineTotal() {
        return product.getPrice().multiply(quantity);
    }
}
