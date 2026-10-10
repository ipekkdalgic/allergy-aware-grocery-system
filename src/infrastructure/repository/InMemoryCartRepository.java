package infrastructure.repository;

import domain.model.Cart;
import domain.repository.CartRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

//Stores cart data in memory and supports lookup by customer id.
public class InMemoryCartRepository implements CartRepository {
    private final Map<String, Cart> cartsById = new LinkedHashMap<>();
    private final Map<String, Cart> cartsByCustomerId = new LinkedHashMap<>();

    @Override
    public void save(Cart cart) {
        cartsById.put(cart.getId(), cart);
        cartsByCustomerId.put(cart.getCustomerId(), cart);
    }

    @Override
    public Optional<Cart> findById(String id) {
        return Optional.ofNullable(cartsById.get(id));
    }

    @Override
    public Optional<Cart> findByCustomerId(String customerId) {
        return Optional.ofNullable(cartsByCustomerId.get(customerId));
    }
}
