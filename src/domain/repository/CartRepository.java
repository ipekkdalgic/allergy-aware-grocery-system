package domain.repository;

import domain.model.Cart;
import java.util.Optional;

public interface CartRepository {
    void save(Cart cart);
    Optional<Cart> findById(String id);
    Optional<Cart> findByCustomerId(String customerId);
}
