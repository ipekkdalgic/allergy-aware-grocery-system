package domain.model;

import domain.enums.Allergen;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class AllergyProfile {
    private final Set<Allergen> allergens;

    public AllergyProfile(Set<Allergen> allergens) {
        if (allergens == null || allergens.isEmpty()) {
            this.allergens = EnumSet.noneOf(Allergen.class);
        } else {
            this.allergens = EnumSet.copyOf(allergens);
        }
    }

    public boolean hasAllergy(Allergen allergen) {
        return allergens.contains(allergen);
    }

    public boolean isEmpty() {
        return allergens.isEmpty();
    }

    public Set<Allergen> getAllergens() {
        return Collections.unmodifiableSet(allergens);
    }

    @Override
    public String toString() {
        return allergens.isEmpty() ? "None" : allergens.toString();
    }
}
