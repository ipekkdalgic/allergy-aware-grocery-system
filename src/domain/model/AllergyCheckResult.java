package domain.model;

import domain.enums.Allergen;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class AllergyCheckResult {
    private final boolean safe;
    private final Set<Allergen> matchedAllergens;
    private final Set<Allergen> productAllergens;
    private final String message;

    public AllergyCheckResult(boolean safe, Set<Allergen> matchedAllergens, Set<Allergen> productAllergens, String message) {
        this.safe = safe;
        this.matchedAllergens = matchedAllergens == null || matchedAllergens.isEmpty()
                ? EnumSet.noneOf(Allergen.class)
                : EnumSet.copyOf(matchedAllergens);
        this.productAllergens = productAllergens == null || productAllergens.isEmpty()
                ? EnumSet.noneOf(Allergen.class)
                : EnumSet.copyOf(productAllergens);
        this.message = message;
    }

    public boolean isSafe() {
        return safe;
    }

    public boolean hasWarning() {
        return !safe;
    }

    public Set<Allergen> getMatchedAllergens() {
        return Collections.unmodifiableSet(matchedAllergens);
    }

    public Set<Allergen> getProductAllergens() {
        return Collections.unmodifiableSet(productAllergens);
    }

    public String getMessage() {
        return message;
    }
}
