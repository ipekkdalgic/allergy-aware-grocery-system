package application.service;

import domain.enums.Allergen;
import domain.factory.CustomerBuilder;
import domain.model.AllergyProfile;
import domain.model.Cart;
import domain.model.Customer;
import domain.model.OperationResult;
import domain.repository.CartRepository;
import domain.repository.CustomerRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Handles customer registration and login use cases.
 * It keeps customer validation and cart creation outside the console menu layer.
 */
public class CustomerService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final AtomicInteger customerCounter = new AtomicInteger(0);

    public CustomerService(CustomerRepository customerRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    public OperationResult<Customer> register(String name, String email, Set<Allergen> allergens) {
        if (isBlank(name)) {
            return OperationResult.failure("Name cannot be empty.");
        }
        if (!isValidEmail(email)) {
            return OperationResult.failure("Invalid email. Email must contain a valid @ and domain part.");
        }
        if (customerRepository.findByEmail(email).isPresent()) {
            return OperationResult.failure("A customer with this email already exists. Please login instead.");
        }

        String id = String.format("C%03d", customerCounter.incrementAndGet());
        AllergyProfile allergyProfile = new AllergyProfile(allergens);
        OperationResult<Customer> buildResult = new CustomerBuilder()
                .withId(id)
                .withName(name.trim())
                .withEmail(email.trim().toLowerCase())
                .withAllergyProfile(allergyProfile)
                .withWalletBalance(BigDecimal.valueOf(500))
                .build();

        if (!buildResult.isSuccess()) {
            return buildResult;
        }

        Customer customer = buildResult.getData();
        customerRepository.save(customer);
        cartRepository.save(new Cart("CART-" + customer.getId(), customer.getId()));
        return OperationResult.success("Customer registered successfully.", customer);
    }

    public OperationResult<Customer> login(String email) {
        if (!isValidEmail(email)) {
            return OperationResult.failure("Invalid email format.");
        }
        return customerRepository.findByEmail(email)
                .map(customer -> OperationResult.success("Login successful.", customer))
                .orElseGet(() -> OperationResult.failure("No customer found with this email. Please register first."));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
