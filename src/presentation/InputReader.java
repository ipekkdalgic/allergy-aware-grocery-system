package presentation;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.regex.Pattern;

//Reads and validates console input values used by the menu classes.
public class InputReader {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final Scanner scanner;

    public InputReader() {
        this.scanner = new Scanner(System.in);
    }

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Invalid choice. Please enter a value between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty.");
                continue;
            }
            return input;
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            String email = readNonEmptyString(prompt).toLowerCase();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email. Please enter a valid email such as name@example.com.");
                continue;
            }
            return email;
        }
    }

    public int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value <= 0) {
                System.out.println("Value must be greater than zero.");
                continue;
            }
            return value;
        }
    }

    public int readNonNegativeInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value < 0) {
                System.out.println("Value cannot be negative.");
                continue;
            }
            return value;
        }
    }

    public BigDecimal readPositiveMoney(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be greater than zero.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number such as 120.50.");
            }
        }
    }

    public String readDigitsExact(String prompt, int length) {
        while (true) {
            String input = readNonEmptyString(prompt);
            if (!input.matches("\\d{" + length + "}")) {
                System.out.println("Invalid input. Please enter exactly " + length + " digits.");
                continue;
            }
            return input;
        }
    }

    public int readExpiryMonth(String prompt) {
        return readIntInRange(prompt, 1, 12);
    }

    public int readExpiryYear(String prompt) {
        while (true) {
            int year = readInt(prompt);
            if (year < 2026 || year > 2100) {
                System.out.println("Invalid year. Please enter a year between 2026 and 2100.");
                continue;
            }
            return year;
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            }
            if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            System.out.println("Invalid choice. Please enter Y or N.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
