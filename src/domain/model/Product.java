package domain.model;

import domain.enums.Allergen;
import domain.enums.Category;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Represents a grocery product with price, stock, active status, and declared allergens.
 */
public class Product {
    private final String id;
    private final String name;
    private final Category category;
    private final Money price;
    private int stockQuantity;
    private final Set<Allergen> allergens;
    private boolean active;

    public Product(String id, String name, Category category, Money price, int stockQuantity, Set<Allergen> allergens) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        if (allergens == null || allergens.isEmpty()) {
            this.allergens = EnumSet.noneOf(Allergen.class);
        } else {
            this.allergens = EnumSet.copyOf(allergens);
        }
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Money getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public Set<Allergen> getAllergens() {
        return Collections.unmodifiableSet(allergens);
    }

    public boolean isActive() {
        return active;
    }

    public boolean hasEnoughStock(int quantity) {
        return quantity > 0 && stockQuantity >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException("Insufficient stock for product " + name + ".");
        }
        stockQuantity -= quantity;
    }

    public void updateStock(int newStockQuantity) {
        if (newStockQuantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        this.stockQuantity = newStockQuantity;
    }

    public void disable() {
        this.active = false;
    }
}
