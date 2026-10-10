package application.service;

import domain.model.AllergyCheckResult;
import domain.model.Cart;
import domain.model.CartItem;
import domain.model.Customer;
import domain.model.OperationResult;
import domain.model.Product;
import domain.repository.CartRepository;
import domain.repository.ProductRepository;
import java.util.Optional;

/**
 * Manages cart-related business operations.
 * It validates products, checks allergy risks, updates cart items, and supports checkout validation.
 */
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AllergyCheckService allergyCheckService;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, AllergyCheckService allergyCheckService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.allergyCheckService = allergyCheckService;
    }

    public OperationResult<Cart> getCart(Customer customer) {
        if (customer == null) {
            return OperationResult.failure("Customer must be logged in.");
        }
        return cartRepository.findByCustomerId(customer.getId())
                .map(cart -> OperationResult.success("Cart found.", cart))
                .orElseGet(() -> OperationResult.failure("Cart could not be found for this customer."));
    }

    public OperationResult<AllergyCheckResult> previewAddProduct(Customer customer, String productId, int quantity) {
        // Product, stock, and customer checks are completed before allergy checking.
        OperationResult<Product> validation = validateProductForCart(customer, productId, quantity);
        if (!validation.isSuccess()) {
            return OperationResult.failure(validation.getMessage());
        }
        AllergyCheckResult allergyCheckResult = allergyCheckService.check(customer, validation.getData());
        return OperationResult.success("Allergy check completed.", allergyCheckResult);
    }

    public OperationResult<CartItem> addProductToCart(Customer customer, String productId, int quantity, boolean userConfirmedRisk) {
        OperationResult<Product> validation = validateProductForCart(customer, productId, quantity);
        if (!validation.isSuccess()) {
            return OperationResult.failure(validation.getMessage());
        }
        Product product = validation.getData();
        AllergyCheckResult allergyCheckResult = allergyCheckService.check(customer, product);
        // Risky products are added only when the customer explicitly confirms the allergy warning.
        if (allergyCheckResult.hasWarning() && !userConfirmedRisk) {
            return OperationResult.failure("Product was not added because allergy warning was not confirmed.");
        }

        OperationResult<Cart> cartResult = getCart(customer);
        if (!cartResult.isSuccess()) {
            return OperationResult.failure(cartResult.getMessage());
        }
        Cart cart = cartResult.getData();
        cart.addItem(product, quantity, allergyCheckResult, userConfirmedRisk);
        cartRepository.save(cart);
        Optional<CartItem> addedItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equalsIgnoreCase(product.getId()))
                .findFirst();

        String message = allergyCheckResult.hasWarning()
                ? "Product added to cart with allergy warning."
                : "Product added to cart successfully.";
        return OperationResult.success(message, addedItem.orElse(null));
    }

    public OperationResult<Void> removeItem(Customer customer, int itemIndex) {
        OperationResult<Cart> cartResult = getCart(customer);
        if (!cartResult.isSuccess()) {
            return OperationResult.failure(cartResult.getMessage());
        }
        Cart cart = cartResult.getData();
        OperationResult<Void> result = cart.removeItemByIndex(itemIndex);
        if (result.isSuccess()) {
            cartRepository.save(cart);
        }
        return result;
    }

    public OperationResult<Void> validateCartForCheckout(Cart cart) {
        // Checkout validation protects the system from empty carts, inactive products, and insufficient stock.
        if (cart == null || cart.isEmpty()) {
            return OperationResult.failure("Checkout failed. Reason: Cart is empty.");
        }
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.isActive()) {
                return OperationResult.failure("Checkout failed. Reason: " + product.getName() + " is no longer available.");
            }
            if (!product.hasEnoughStock(item.getQuantity())) {
                return OperationResult.failure("Checkout failed. Reason: Insufficient stock for " + product.getName() + ". Available stock: " + product.getStockQuantity());
            }
        }
        return OperationResult.success("Cart is valid for checkout.");
    }

    public void decreaseStockAfterCheckout(Cart cart) {
        // Stock is changed only after checkout succeeds.
        for (CartItem item : cart.getItems()) {
            item.getProduct().decreaseStock(item.getQuantity());
            productRepository.save(item.getProduct());
        }
    }

    public void clearCart(Cart cart) {
        cart.clear();
        cartRepository.save(cart);
    }

    private OperationResult<Product> validateProductForCart(Customer customer, String productId, int quantity) {
        if (customer == null) {
            return OperationResult.failure("Please login before adding products to cart.");
        }
        if (productId == null || productId.trim().isEmpty()) {
            return OperationResult.failure("Product id cannot be empty.");
        }
        if (quantity <= 0) {
            return OperationResult.failure("Quantity must be greater than zero.");
        }
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return OperationResult.failure("Product not found.");
        }
        Product product = productOptional.get();
        if (!product.isActive()) {
            return OperationResult.failure("This product is not active and cannot be added to cart.");
        }
        if (!product.hasEnoughStock(quantity)) {
            return OperationResult.failure("Requested quantity exceeds available stock. Available stock: " + product.getStockQuantity());
        }
        return OperationResult.success("Product is valid.", product);
    }
}
