package application.service;

import domain.enums.Allergen;
import domain.enums.Category;
import domain.enums.OrderStatus;
import domain.factory.ProductFactory;
import domain.model.Customer;
import domain.model.OperationResult;
import domain.model.Order;
import domain.model.Product;
import domain.repository.CustomerRepository;
import domain.repository.OrderRepository;
import domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Coordinates admin operations such as product management and order status updates.
 * The service keeps admin business logic separate from the console menu.
 */
public class AdminService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductFactory productFactory;
    private final OrderService orderService;

    public AdminService(ProductRepository productRepository, OrderRepository orderRepository,
                        CustomerRepository customerRepository, ProductFactory productFactory,
                        OrderService orderService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productFactory = productFactory;
        this.orderService = orderService;
    }

    public OperationResult<Product> addProduct(String name, Category category, BigDecimal price, int stockQuantity, Set<Allergen> allergens) {
        OperationResult<Product> result = productFactory.createProduct(name, category, price, stockQuantity, allergens);
        if (result.isSuccess()) {
            productRepository.save(result.getData());
        }
        return result;
    }

    public OperationResult<Void> updateStock(String productId, int newStock) {
        if (newStock < 0) {
            return OperationResult.failure("Invalid stock quantity. Stock cannot be negative.");
        }
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return OperationResult.failure("Product not found.");
        }
        Product product = productOptional.get();
        product.updateStock(newStock);
        productRepository.save(product);
        return OperationResult.success("Stock updated successfully.");
    }

    public OperationResult<Void> disableProduct(String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return OperationResult.failure("Product not found.");
        }
        Product product = productOptional.get();
        product.disable();
        productRepository.save(product);
        return OperationResult.success("Product disabled successfully.");
    }

    public OperationResult<Void> updateOrderStatus(Order order, OrderStatus targetStatus) {
        return orderService.moveToStatus(order, targetStatus);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
