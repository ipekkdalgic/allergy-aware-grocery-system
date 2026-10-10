package presentation;

import domain.enums.Allergen;
import domain.model.Cart;
import domain.model.CartItem;
import domain.model.Customer;
import domain.model.Order;
import domain.model.OrderItem;
import domain.model.PaymentResult;
import domain.model.Product;
import java.util.List;
import java.util.Set;

//Formats and prints products, carts, customers, orders, and payment results to the console.
public class ConsolePrinter {
    public void printHeader(String title) {
        System.out.println();
        System.out.println("====================================");
        System.out.println(" " + title);
        System.out.println("====================================");
    }

    public void printProducts(List<Product> products, boolean showInactive) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        System.out.printf("%-4s | %-6s | %-22s | %-10s | %-12s | %-5s | %-8s | %-20s%n",
                "No", "Code", "Product Name", "Category", "Price", "Stock", "Active", "Allergens");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (!showInactive && !product.isActive()) {
                continue;
            }
            System.out.printf("%-4d | %-6s | %-22s | %-10s | %-12s | %-5d | %-8s | %-20s%n",
                    i + 1,
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.isActive() ? "YES" : "NO",
                    formatAllergens(product.getAllergens()));
        }
    }

    public void printCart(Cart cart) {
        if (cart == null || cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        int index = 1;
        for (CartItem item : cart.getItems()) {
            System.out.println(index + ". " + item.getProduct().getName());
            System.out.println("   Product Code: " + item.getProduct().getId());
            System.out.println("   Quantity: " + item.getQuantity());
            System.out.println("   Unit Price: " + item.getProduct().getPrice());
            System.out.println("   Total: " + item.getLineTotal());
            if (item.hasAllergyWarning()) {
                System.out.println("   Allergy Status: WARNING");
                System.out.println("   Matched Allergens: " + formatAllergens(item.getMatchedAllergens()));
                System.out.println("   Added with user confirmation: " + (item.isAddedWithUserConfirmation() ? "YES" : "NO"));
            } else {
                System.out.println("   Allergy Status: SAFE");
            }
            index++;
        }
        System.out.println("------------------------------------");
        System.out.println("Cart Total: " + cart.calculateTotal());
    }

    public void printOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        System.out.printf("%-4s | %-6s | %-10s | %-12s | %-10s%n", "No", "Order", "Customer", "Status", "Total");
        System.out.println("------------------------------------------------------");
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.printf("%-4d | %-6s | %-10s | %-12s | %-10s%n",
                    i + 1, order.getId(), order.getCustomerId(), order.getStatus(), order.getTotalAmount());
        }
    }

    public void printOrderDetails(Order order) {
        System.out.println("Order ID: " + order.getId());
        System.out.println("Customer ID: " + order.getCustomerId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Total: " + order.getTotalAmount());
        System.out.println("Items:");
        for (OrderItem item : order.getItems()) {
            System.out.println("- " + item.getProductName() + " x" + item.getQuantity() + " | " + item.getLineTotal());
            if (item.hasAllergyWarning()) {
                System.out.println("  Allergy Warning: " + formatAllergens(item.getMatchedAllergens()));
            }
        }
        if (!order.getNotifications().isEmpty()) {
            System.out.println("Notifications / Logs:");
            for (String notification : order.getNotifications()) {
                System.out.println("- " + notification);
            }
        }
    }

    public void printCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        System.out.printf("%-4s | %-6s | %-20s | %-30s | %-15s | %-20s%n", "No", "ID", "Name", "Email", "Wallet", "Allergies");
        System.out.println("------------------------------------------------------------------------------------------------");
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            System.out.printf("%-4d | %-6s | %-20s | %-30s | %-15s | %-20s%n",
                    i + 1,
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getWalletBalance() + " TRY",
                    customer.getAllergyProfile());
        }
    }

    public void printPaymentResult(PaymentResult paymentResult) {
        if (paymentResult == null) {
            return;
        }
        System.out.println("Payment Status: " + paymentResult.getStatus());
        if (paymentResult.getTransactionId() != null) {
            System.out.println("Transaction ID: " + paymentResult.getTransactionId());
        }
        if (paymentResult.getAuthorizationCode() != null) {
            System.out.println("Authorization Code: " + paymentResult.getAuthorizationCode());
        }
        System.out.println(paymentResult.getMessage());
    }

    public String formatAllergens(Set<Allergen> allergens) {
        return allergens == null || allergens.isEmpty() ? "None" : allergens.toString();
    }
}
