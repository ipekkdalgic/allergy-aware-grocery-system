package infrastructure.repository;

import domain.model.Product;
import domain.repository.ProductRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Stores product data in memory during application runtime.
public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();

    @Override
    public void save(Product product) {
        products.put(product.getId().toUpperCase(), product);
    }

    @Override
    public Optional<Product> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(products.get(id.trim().toUpperCase()));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
