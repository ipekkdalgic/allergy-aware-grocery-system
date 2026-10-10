package infrastructure.seed;

import domain.enums.Allergen;
import domain.enums.Category;
import domain.factory.ProductFactory;
import domain.model.OperationResult;
import domain.model.Product;
import domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.EnumSet;

//Creates the initial sample product catalog when the application starts.
public class ProductSeeder {
    private final ProductRepository productRepository;
    private final ProductFactory productFactory;

    public ProductSeeder(ProductRepository productRepository, ProductFactory productFactory) {
        this.productRepository = productRepository;
        this.productFactory = productFactory;
    }

    public void seed() {
        add("Whole Wheat Bread", Category.BAKERY, "45.00", 30, EnumSet.of(Allergen.GLUTEN));
        add("Peanut Butter", Category.SPREAD, "120.00", 15, EnumSet.of(Allergen.PEANUT));
        add("Almond Milk", Category.BEVERAGE, "85.00", 20, EnumSet.of(Allergen.TREE_NUT));
        add("Rice", Category.GRAIN, "60.00", 50, EnumSet.noneOf(Allergen.class));
        add("Dark Chocolate", Category.SNACK, "75.00", 25, EnumSet.of(Allergen.DAIRY, Allergen.SOY));
        add("Chocolate Cake", Category.BAKERY, "150.00", 10, EnumSet.of(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGG));
    }

    private void add(String name, Category category, String price, int stock, EnumSet<Allergen> allergens) {
        OperationResult<Product> result = productFactory.createProduct(name, category, new BigDecimal(price), stock, allergens);
        if (result.isSuccess()) {
            productRepository.save(result.getData());
        }
    }
}
