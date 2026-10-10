package presentation;

import application.facade.CheckoutFacade;
import application.service.CartService;
import application.service.CatalogService;
import application.service.CustomerService;
import application.service.OrderService;
import domain.enums.Allergen;
import domain.enums.PaymentMethod;
import domain.model.AllergyCheckResult;
import domain.model.Cart;
import domain.model.CartItem;
import domain.model.CheckoutResult;
import domain.model.Customer;
import domain.model.OperationResult;
import domain.model.Order;
import domain.model.PaymentRequest;
import domain.model.Product;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class CustomerMenu {
    private final InputReader inputReader;
    private final ConsolePrinter printer;
    private final CustomerService customerService;
    private final CatalogService catalogService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CheckoutFacade checkoutFacade;
    private Customer currentCustomer;

    public CustomerMenu(InputReader inputReader, ConsolePrinter printer, CustomerService customerService,
                        CatalogService catalogService, CartService cartService, OrderService orderService,
                        CheckoutFacade checkoutFacade) {
        this.inputReader = inputReader;
        this.printer = printer;
        this.customerService = customerService;
        this.catalogService = catalogService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.checkoutFacade = checkoutFacade;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printer.printHeader("CUSTOMER MENU" + (currentCustomer == null ? "" : " - Logged in as " + currentCustomer.getName()));
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Browse Products");
            System.out.println("4. Add Product to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Remove Product from Cart");
            System.out.println("7. Checkout");
            System.out.println("8. View My Orders");
            System.out.println("9. Logout");
            System.out.println("0. Back to Main Menu");

            int choice = inputReader.readIntInRange("Select option: ", 0, 9);
            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    browseProducts();
                    break;
                case 4:
                    addProductToCart();
                    break;
                case 5:
                    viewCart();
                    break;
                case 6:
                    removeProductFromCart();
                    break;
                case 7:
                    checkout();
                    break;
                case 8:
                    viewMyOrders();
                    break;
                case 9:
                    logout();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void register() {
        printer.printHeader("REGISTER CUSTOMER");
        String name = inputReader.readNonEmptyString("Enter your name: ");
        String email = inputReader.readEmail("Enter your email: ");
        Set<Allergen> allergens = readAllergens();
        OperationResult<Customer> result = customerService.register(name, email, allergens);
        System.out.println(result.getMessage());
        if (result.isSuccess()) {
            currentCustomer = result.getData();
            System.out.println("You are now logged in as " + currentCustomer.getName() + ".");
            System.out.println("Wallet Balance: " + currentCustomer.getWalletBalance() + " TRY");
            System.out.println("Allergy Profile: " + currentCustomer.getAllergyProfile());
        }
    }

    private void login() {
        printer.printHeader("LOGIN");
        String email = inputReader.readEmail("Enter your email: ");
        OperationResult<Customer> result = customerService.login(email);
        System.out.println(result.getMessage());
        if (result.isSuccess()) {
            currentCustomer = result.getData();
            System.out.println("Wallet Balance: " + currentCustomer.getWalletBalance() + " TRY");
            System.out.println("Allergy Profile: " + currentCustomer.getAllergyProfile());
        }
    }

    private void browseProducts() {
        printer.printHeader("PRODUCT CATALOG");
        printer.printProducts(catalogService.getActiveProducts(), false);
    }

    private void addProductToCart() {
        if (!ensureLoggedIn()) {
            return;
        }
        printer.printHeader("ADD PRODUCT TO CART");
        List<Product> products = catalogService.getActiveProducts();
        printer.printProducts(products, false);
        if (products.isEmpty()) {
            return;
        }
        int productChoice = inputReader.readIntInRange("Select product number from the list, or 0 to cancel: ", 0, products.size());
        if (productChoice == 0) {
            System.out.println("Add to cart cancelled.");
            return;
        }
        Product product = products.get(productChoice - 1);
        int quantity = inputReader.readPositiveInt("Enter quantity: ");

        OperationResult<AllergyCheckResult> preview = cartService.previewAddProduct(currentCustomer, product.getId(), quantity);
        if (!preview.isSuccess()) {
            System.out.println(preview.getMessage());
            return;
        }

        AllergyCheckResult allergyCheckResult = preview.getData();
        boolean confirmedRisk = false;
        if (allergyCheckResult.hasWarning()) {
            printer.printHeader("ALLERGY WARNING");
            System.out.println("WARNING: This product matches your allergy profile.");
            System.out.println("Product: " + product.getName());
            System.out.println("Matched allergens: " + printer.formatAllergens(allergyCheckResult.getMatchedAllergens()));
            System.out.println("Your allergy profile: " + currentCustomer.getAllergyProfile());
            confirmedRisk = inputReader.readYesNo("This product may be unsafe for you. Add anyway?");
            if (!confirmedRisk) {
                System.out.println("Product was not added to cart.");
                return;
            }
        }

        OperationResult<CartItem> result = cartService.addProductToCart(currentCustomer, product.getId(), quantity, confirmedRisk);
        System.out.println(result.getMessage());
    }

    private void viewCart() {
        if (!ensureLoggedIn()) {
            return;
        }
        printer.printHeader("MY CART");
        OperationResult<Cart> cartResult = cartService.getCart(currentCustomer);
        if (!cartResult.isSuccess()) {
            System.out.println(cartResult.getMessage());
            return;
        }
        printer.printCart(cartResult.getData());
    }

    private void removeProductFromCart() {
        if (!ensureLoggedIn()) {
            return;
        }
        printer.printHeader("REMOVE PRODUCT FROM CART");
        OperationResult<Cart> cartResult = cartService.getCart(currentCustomer);
        if (!cartResult.isSuccess()) {
            System.out.println(cartResult.getMessage());
            return;
        }
        Cart cart = cartResult.getData();
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        printer.printCart(cart);
        int itemChoice = inputReader.readIntInRange("Select cart item number to remove, or 0 to cancel: ", 0, cart.getItems().size());
        if (itemChoice == 0) {
            System.out.println("Remove operation cancelled.");
            return;
        }
        OperationResult<Void> result = cartService.removeItem(currentCustomer, itemChoice - 1);
        System.out.println(result.getMessage());
    }

    private void checkout() {
        if (!ensureLoggedIn()) {
            return;
        }
        printer.printHeader("CHECKOUT");
        OperationResult<Cart> cartResult = cartService.getCart(currentCustomer);
        if (!cartResult.isSuccess()) {
            System.out.println(cartResult.getMessage());
            return;
        }
        Cart cart = cartResult.getData();
        OperationResult<Void> cartValidation = cartService.validateCartForCheckout(cart);
        if (!cartValidation.isSuccess()) {
            System.out.println(cartValidation.getMessage());
            return;
        }

        boolean warningsConfirmed = true;
        if (cart.hasAllergyWarnings()) {
            printer.printHeader("CHECKOUT ALLERGY REVIEW");
            for (CartItem item : cart.getItems()) {
                if (item.hasAllergyWarning()) {
                    System.out.println("Risky product: " + item.getProduct().getName());
                    System.out.println("Matched allergens: " + printer.formatAllergens(item.getMatchedAllergens()));
                }
            }
            warningsConfirmed = inputReader.readYesNo("Do you understand the allergy warnings and want to continue checkout?");
            if (!warningsConfirmed) {
                System.out.println("Checkout cancelled. You can remove risky products from your cart.");
                return;
            }
        }

        PaymentRequest paymentRequest = readPaymentRequest(cart.calculateTotal().getAmount());
        if (paymentRequest == null) {
            System.out.println("Checkout cancelled.");
            return;
        }

        CheckoutResult checkoutResult = checkoutFacade.checkout(currentCustomer, paymentRequest, warningsConfirmed);
        System.out.println(checkoutResult.getMessage());
        printer.printPaymentResult(checkoutResult.getPaymentResult());
        if (checkoutResult.getOrder() != null) {
            printer.printOrderDetails(checkoutResult.getOrder());
        }
    }

    private PaymentRequest readPaymentRequest(BigDecimal amount) {
        printer.printHeader("PAYMENT");
        System.out.println("Cart Total: " + amount + " TRY");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. Wallet Balance");
        System.out.println("0. Cancel Checkout");
        int paymentChoice = inputReader.readIntInRange("Select payment method: ", 0, 3);
        if (paymentChoice == 0) {
            return null;
        }
        if (paymentChoice == 3) {
            System.out.println("Current wallet balance: " + currentCustomer.getWalletBalance() + " TRY");
            return PaymentRequest.wallet(amount);
        }

        PaymentMethod method = paymentChoice == 1 ? PaymentMethod.CREDIT_CARD : PaymentMethod.DEBIT_CARD;
        String cardHolderName = inputReader.readNonEmptyString("Card holder name: ");
        String cardNumber = inputReader.readDigitsExact("Card number (16 digits): ", 16);
        int expiryMonth = inputReader.readExpiryMonth("Expiry month (1-12): ");
        int expiryYear = inputReader.readExpiryYear("Expiry year: ");
        String cvv = inputReader.readDigitsExact("CVV (3 digits): ", 3);
        return PaymentRequest.card(method, amount, cardHolderName, cardNumber, expiryMonth, expiryYear, cvv);
    }

    private void viewMyOrders() {
        if (!ensureLoggedIn()) {
            return;
        }
        printer.printHeader("MY ORDERS");
        List<Order> orders = orderService.getOrdersByCustomer(currentCustomer.getId());
        printer.printOrders(orders);
        if (!orders.isEmpty()) {
            int choice = inputReader.readIntInRange("Select order number for details, or 0 to go back: ", 0, orders.size());
            if (choice > 0) {
                printer.printOrderDetails(orders.get(choice - 1));
            }
        }
    }

    private void logout() {
        currentCustomer = null;
        System.out.println("Logged out successfully.");
    }

    private boolean ensureLoggedIn() {
        if (currentCustomer == null) {
            System.out.println("Please login or register first.");
            return false;
        }
        return true;
    }

    private Set<Allergen> readAllergens() {
        Set<Allergen> selectedAllergens = EnumSet.noneOf(Allergen.class);
        Allergen[] allergens = Allergen.values();
        boolean selecting = true;
        while (selecting) {
            System.out.println("Select your allergies:");
            for (int i = 0; i < allergens.length; i++) {
                System.out.println((i + 1) + ". " + allergens[i]);
            }
            System.out.println("0. Finish");
            int choice = inputReader.readIntInRange("Your choice: ", 0, allergens.length);
            if (choice == 0) {
                selecting = false;
            } else {
                selectedAllergens.add(allergens[choice - 1]);
                System.out.println("Selected allergies: " + selectedAllergens);
            }
        }
        return selectedAllergens;
    }
}
