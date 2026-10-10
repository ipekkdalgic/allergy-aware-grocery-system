package presentation;

//Starts the main console menu and routes the user to customer or admin flows.
public class ConsoleApp {
    private final InputReader inputReader;
    private final ConsolePrinter printer;
    private final CustomerMenu customerMenu;
    private final AdminMenu adminMenu;

    public ConsoleApp(InputReader inputReader, ConsolePrinter printer, CustomerMenu customerMenu, AdminMenu adminMenu) {
        this.inputReader = inputReader;
        this.printer = printer;
        this.customerMenu = customerMenu;
        this.adminMenu = adminMenu;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printer.printHeader("ALLERGY AWARE GROCERY SYSTEM");
            System.out.println("1. Customer Mode");
            System.out.println("2. Admin Mode");
            System.out.println("3. Exit");
            int choice = inputReader.readIntInRange("Select option: ", 1, 3);
            switch (choice) {
                case 1:
                    customerMenu.start();
                    break;
                case 2:
                    adminMenu.start();
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
