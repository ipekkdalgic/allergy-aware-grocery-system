package domain.factory;

import domain.enums.Allergen;
import domain.enums.Category;
import domain.model.Money;
import domain.model.OperationResult;
import domain.model.Product;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Centralizes product creation, validation, and product id generation.
 */
public class ProductFactory {
    private int nextNumber = 1;

    public OperationResult<Product> createProduct(String name, Category category, BigDecimal price, int stockQuantity, Set<Allergen> allergens) {
        if (name == null || name.trim().isEmpty()) {
            return OperationResult.failure("Product name cannot be empty.");
        }
        if (category == null) {
            return OperationResult.failure("Product category must be selected.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return OperationResult.failure("Product price must be greater than zero.");
        }
        if (stockQuantity < 0) {
            return OperationResult.failure("Product stock cannot be negative.");
        }
        String productId = String.format("P%03d", nextNumber++);
        Product product = new Product(productId, name.trim(), category, new Money(price), stockQuantity, allergens);
        return OperationResult.success("Product created successfully.", product);
    }
}
