package infrastructure.repository;

import domain.model.Customer;
import domain.repository.CustomerRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Stores customer data in memory during application runtime.
public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    @Override
    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return customers.values().stream()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }
}
