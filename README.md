# Allergy-Aware Grocery System

A Java console application that combines grocery shopping with allergen warnings based on customer allergy profiles.

Developed as a team project at Dokuz Eylül University to demonstrate object-oriented analysis and design, layered architecture, and design patterns.

## Features

- Customer registration and email-based login
- Customer allergy profiles and product allergen matching
- Allergy warnings requiring customer confirmation
- Product browsing and shopping cart management
- Stock and product availability validation
- Checkout with simulated payment processing
- Order lifecycle management
- Admin operations for products, inventory, and orders

## Technologies

- Java
- Object-Oriented Programming
- Layered Architecture
- In-Memory Repositories
- UML Modeling

## Architecture

The application separates responsibilities into four layers:

| Layer | Responsibility |
|---|---|
| Presentation | Console menus and user input |
| Application | Services and checkout coordination |
| Domain | Business models, rules, and interfaces |
| Infrastructure | In-memory storage, configuration, and simulated payment integration |

## Design Patterns

- **Strategy:** Allergy checking
- **State:** Order lifecycle transitions
- **Observer:** Order notifications and logs
- **Adapter:** Simulated payment gateway integration
- **Facade:** Checkout coordination
- **Builder:** Customer creation
- **Singleton:** Application context

## Getting Started

Install a Java Development Kit (JDK). JDK 17 or later is recommended.

### Run in IntelliJ IDEA

1. Download or clone this repository.
2. Open the project folder in IntelliJ IDEA.
3. Configure the project JDK.
4. Mark `src` as the Sources Root if necessary.
5. Run `src/Main.java`.

### Run from Terminal on macOS or Linux

From the repository root:

```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -cp out Main
```

The application opens a console menu with Customer Mode, Admin Mode, and Exit options.

## Example Workflow

1. Enter Customer Mode and register a customer.
2. Configure an allergy profile.
3. Browse products and add items to the cart.
4. Review and confirm any allergy warnings.
5. Complete checkout using the simulated payment flow.
6. Enter Admin Mode to manage inventory and update order statuses.

## Project Report

See [the project report](project-report.pdf) for requirements, UML diagrams, architecture, and design decisions.

## Limitations

- Data is stored in memory and resets when the application restarts.
- Payments are simulated; no real payment provider is connected.
- Login is simplified and does not provide production-grade authentication.
- Allergy checks match declared allergens against customer profiles.
- The application is an educational prototype and does not provide medical advice.
- A complete automated test suite is not included.
