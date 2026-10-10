package domain.strategy;

import domain.model.AllergyCheckResult;
import domain.model.Customer;
import domain.model.Product;

//Defines the contract for allergy checking algorithms.
public interface AllergyCheckStrategy {
    AllergyCheckResult check(Customer customer, Product product);
}
