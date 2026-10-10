package application.service;

import domain.model.AllergyCheckResult;
import domain.model.Customer;
import domain.model.Product;
import domain.strategy.AllergyCheckStrategy;

/**
 * Delegates allergy checking to an AllergyCheckStrategy.
 * This makes the allergy rule replaceable without changing cart or menu logic.
 */
public class AllergyCheckService {
    private final AllergyCheckStrategy allergyCheckStrategy;

    public AllergyCheckService(AllergyCheckStrategy allergyCheckStrategy) {
        this.allergyCheckStrategy = allergyCheckStrategy;
    }

    public AllergyCheckResult check(Customer customer, Product product) {
        return allergyCheckStrategy.check(customer, product);
    }
}
