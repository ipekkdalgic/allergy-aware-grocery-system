import infrastructure.config.ApplicationContext;
import presentation.AdminMenu;
import presentation.ConsoleApp;
import presentation.ConsolePrinter;
import presentation.CustomerMenu;
import presentation.InputReader;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = ApplicationContext.getInstance();
        InputReader inputReader = new InputReader();
        ConsolePrinter printer = new ConsolePrinter();

        CustomerMenu customerMenu = new CustomerMenu(
                inputReader,
                printer,
                context.getCustomerService(),
                context.getCatalogService(),
                context.getCartService(),
                context.getOrderService(),
                context.getCheckoutFacade()
        );

        AdminMenu adminMenu = new AdminMenu(
                inputReader,
                printer,
                context.getAdminService()
        );

        ConsoleApp app = new ConsoleApp(inputReader, printer, customerMenu, adminMenu);
        app.start();
    }
}
