package application.service;

import domain.model.Product;
import domain.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides product listing operations for customer and admin flows.
 * Customer-facing catalog operations expose only active products.
 */
public class CatalogService {
    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(String productId) {
        return productRepository.findById(productId);
    }
}
