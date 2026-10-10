package presentation;

import application.service.AdminService;
import domain.enums.Allergen;
import domain.enums.Category;
import domain.enums.OrderStatus;
import domain.model.OperationResult;
import domain.model.Order;
import domain.model.Product;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class AdminMenu {
    private static final String ADMIN_PASSWORD = "admin123";
    private final InputReader inputReader;
    private final ConsolePrinter printer;
    private final AdminService adminService;

    public AdminMenu(InputReader inputReader, ConsolePrinter printer, AdminService adminService) {
        this.inputReader = inputReader;
        this.printer = printer;
        this.adminService = adminService;
    }

    public void start() {
        printer.printHeader("ADMIN LOGIN");
        String password = inputReader.readNonEmptyString("Admin password: ");
        if (!ADMIN_PASSWORD.equals(password)) {
            System.out.println("Invalid admin password. Returning to main menu.");
            return;
        }

        boolean running = true;
        while (running) {
            printer.printHeader("ADMIN MENU");
            System.out.println("1. View Products");
            System.out.println("2. Add Product");
            System.out.println("3. Update Product Stock");
            System.out.println("4. Disable Product");
            System.out.println("5. View All Orders");
            System.out.println("6. View Order Details");
            System.out.println("7. Update Order Status");
            System.out.println("8. View Customers");
            System.out.println("0. Logout");

            int choice = inputReader.readIntInRange("Select option: ", 0, 8);
            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    addProduct();
                    break;
                case 3:
                    updateStock();
                    break;
                case 4:
                    disableProduct();
                    break;
                case 5:
                    viewAllOrders();
                    break;
                case 6:
                    viewOrderDetails();
                    break;
                case 7:
                    updateOrderStatus();
                    break;
                case 8:
                    viewCustomers();
                    break;
                case 0:
                    running = false;
                    System.out.println("Admin logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void viewProducts() {
        printer.printHeader("ALL PRODUCTS");
        printer.printProducts(adminService.getAllProducts(), true);
    }

    private void addProduct() {
        printer.printHeader("ADD NEW PRODUCT");
        String name = inputReader.readNonEmptyString("Enter product name: ");
        Category category = readCategory();
        BigDecimal price = inputReader.readPositiveMoney("Enter price: ");
        int stock = inputReader.readNonNegativeInt("Enter stock quantity: ");
        Set<Allergen> allergens = readAllergensForProduct();
        OperationResult<Product> result = adminService.addProduct(name, category, price, stock, allergens);
        System.out.println(result.getMessage());
        if (result.isSuccess()) {
            Product product = result.getData();
            System.out.println("Product Code: " + product.getId());
            System.out.println("Name: " + product.getName());
            System.out.println("Allergens: " + printer.formatAllergens(product.getAllergens()));
        }
    }

    private void updateStock() {
        printer.printHeader("UPDATE STOCK");
        List<Product> products = adminService.getAllProducts();
        printer.printProducts(products, true);
        if (products.isEmpty()) {
            return;
        }
        int productChoice = inputReader.readIntInRange("Select product number, or 0 to cancel: ", 0, products.size());
        if (productChoice == 0) {
            System.out.println("Operation cancelled.");
            return;
        }
        Product product = products.get(productChoice - 1);
        System.out.println("Current stock for " + product.getName() + ": " + product.getStockQuantity());
        int newStock = inputReader.readNonNegativeInt("Enter new stock quantity: ");
        OperationResult<Void> result = adminService.updateStock(product.getId(), newStock);
        System.out.println(result.getMessage());
    }

    private void disableProduct() {
        printer.printHeader("DISABLE PRODUCT");
        List<Product> products = adminService.getAllProducts();
        printer.printProducts(products, true);
        if (products.isEmpty()) {
            return;
        }
        int productChoice = inputReader.readIntInRange("Select product number to disable, or 0 to cancel: ", 0, products.size());
        if (productChoice == 0) {
            System.out.println("Operation cancelled.");
            return;
        }
        Product product = products.get(productChoice - 1);
        if (!product.isActive()) {
            System.out.println("Product is already disabled.");
            return;
        }
        boolean confirm = inputReader.readYesNo("Are you sure you want to disable " + product.getName() + "?");
        if (!confirm) {
            System.out.println("Operation cancelled.");
            return;
        }
        OperationResult<Void> result = adminService.disableProduct(product.getId());
        System.out.println(result.getMessage());
    }

    private void viewAllOrders() {
        printer.printHeader("ALL ORDERS");
        printer.printOrders(adminService.getAllOrders());
    }

    private void viewOrderDetails() {
        printer.printHeader("ORDER DETAILS");
        List<Order> orders = adminService.getAllOrders();
        printer.printOrders(orders);
        if (orders.isEmpty()) {
            return;
        }
        int orderChoice = inputReader.readIntInRange("Select order number, or 0 to cancel: ", 0, orders.size());
        if (orderChoice == 0) {
            return;
        }
        printer.printOrderDetails(orders.get(orderChoice - 1));
    }

    private void updateOrderStatus() {
        printer.printHeader("UPDATE ORDER STATUS");
        List<Order> orders = adminService.getAllOrders();
        printer.printOrders(orders);
        if (orders.isEmpty()) {
            return;
        }
        int orderChoice = inputReader.readIntInRange("Select order number, or 0 to cancel: ", 0, orders.size());
        if (orderChoice == 0) {
            return;
        }
        Order order = orders.get(orderChoice - 1);
        System.out.println("Current status: " + order.getStatus());
        List<OrderStatus> availableStatuses = order.getAvailableNextStatuses();
        if (availableStatuses.isEmpty()) {
            System.out.println("This order is in a final state. No further status transition is allowed.");
            return;
        }
        System.out.println("Available next statuses:");
        for (int i = 0; i < availableStatuses.size(); i++) {
            System.out.println((i + 1) + ". " + availableStatuses.get(i));
        }
        int statusChoice = inputReader.readIntInRange("Select new status, or 0 to cancel: ", 0, availableStatuses.size());
        if (statusChoice == 0) {
            System.out.println("Operation cancelled.");
            return;
        }
        OrderStatus targetStatus = availableStatuses.get(statusChoice - 1);
        OperationResult<Void> result = adminService.updateOrderStatus(order, targetStatus);
        System.out.println(result.getMessage());
        if (result.isSuccess()) {
            List<String> notifications = order.getNotifications();
            if (!notifications.isEmpty()) {
                System.out.println("Latest notification/log entries:");
                int start = Math.max(0, notifications.size() - 2);
                for (int i = start; i < notifications.size(); i++) {
                    System.out.println("- " + notifications.get(i));
                }
            }
        }
    }

    private void viewCustomers() {
        printer.printHeader("REGISTERED CUSTOMERS");
        printer.printCustomers(adminService.getAllCustomers());
    }

    private Category readCategory() {
        Category[] categories = Category.values();
        System.out.println("Select category:");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int choice = inputReader.readIntInRange("Your choice: ", 1, categories.length);
        return categories[choice - 1];
    }

    private Set<Allergen> readAllergensForProduct() {
        Set<Allergen> selectedAllergens = EnumSet.noneOf(Allergen.class);
        Allergen[] allergens = Allergen.values();
        boolean selecting = true;
        while (selecting) {
            System.out.println("Select declared allergens for this product:");
            for (int i = 0; i < allergens.length; i++) {
                System.out.println((i + 1) + ". " + allergens[i]);
            }
            System.out.println("0. Finish");
            int choice = inputReader.readIntInRange("Your choice: ", 0, allergens.length);
            if (choice == 0) {
                selecting = false;
            } else {
                selectedAllergens.add(allergens[choice - 1]);
                System.out.println("Selected allergens: " + selectedAllergens);
            }
        }
        return selectedAllergens;
    }
}
