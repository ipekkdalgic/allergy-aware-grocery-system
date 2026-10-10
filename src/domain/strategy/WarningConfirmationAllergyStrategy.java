package domain.strategy;

import domain.enums.Allergen;
import domain.model.AllergyCheckResult;
import domain.model.Customer;
import domain.model.Product;
import java.util.EnumSet;
import java.util.Set;

//Checks direct matches between a customer's allergy profile and a product's declared allergens.
public class WarningConfirmationAllergyStrategy implements AllergyCheckStrategy {
    @Override
    public AllergyCheckResult check(Customer customer, Product product) {
        Set<Allergen> matched = EnumSet.noneOf(Allergen.class);
        //Direct matching is used: a warning is created when any product allergen exists in the profile.
        for (Allergen allergen : product.getAllergens()) {
            if (customer.getAllergyProfile().hasAllergy(allergen)) {
                matched.add(allergen);
            }
        }

        if (matched.isEmpty()) {
            return new AllergyCheckResult(true, matched, product.getAllergens(), "No matching allergens found.");
        }

        return new AllergyCheckResult(false, matched, product.getAllergens(), "This product matches the customer's allergy profile.");
    }
}
