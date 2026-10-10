package infrastructure.config;

import application.facade.CheckoutFacade;
import application.service.AdminService;
import application.service.AllergyCheckService;
import application.service.CartService;
import application.service.CatalogService;
import application.service.CustomerService;
import application.service.OrderService;
import application.service.PaymentService;
import domain.factory.ProductFactory;
import domain.gateway.PaymentGateway;
import domain.repository.CartRepository;
import domain.repository.CustomerRepository;
import domain.repository.OrderRepository;
import domain.repository.ProductRepository;
import domain.strategy.WarningConfirmationAllergyStrategy;
import infrastructure.payment.ExternalPaymentGateway;
import infrastructure.payment.PaymentGatewayAdapter;
import infrastructure.repository.InMemoryCartRepository;
import infrastructure.repository.InMemoryCustomerRepository;
import infrastructure.repository.InMemoryOrderRepository;
import infrastructure.repository.InMemoryProductRepository;
import infrastructure.seed.ProductSeeder;

/**
 * Singleton composition root of the application.
 * It creates shared repositories, services, gateways, facades, and seed data manually.
 */
public class ApplicationContext {
    private static ApplicationContext instance;

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    private final CustomerService customerService;
    private final CatalogService catalogService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final AdminService adminService;
    private final CheckoutFacade checkoutFacade;

    private ApplicationContext() {
        //Shared dependencies are created once so all services work with the same runtime data.
        ProductFactory productFactory = new ProductFactory();
        this.customerRepository = new InMemoryCustomerRepository();
        this.productRepository = new InMemoryProductRepository();
        this.cartRepository = new InMemoryCartRepository();
        this.orderRepository = new InMemoryOrderRepository();

        new ProductSeeder(productRepository, productFactory).seed();

        //Strategy and adapter implementations are wired here through their abstractions.
        AllergyCheckService allergyCheckService = new AllergyCheckService(new WarningConfirmationAllergyStrategy());
        PaymentGateway paymentGateway = new PaymentGatewayAdapter(new ExternalPaymentGateway());

        this.customerService = new CustomerService(customerRepository, cartRepository);
        this.catalogService = new CatalogService(productRepository);
        this.cartService = new CartService(cartRepository, productRepository, allergyCheckService);
        this.orderService = new OrderService(orderRepository);
        this.paymentService = new PaymentService(paymentGateway);
        this.adminService = new AdminService(productRepository, orderRepository, customerRepository, productFactory, orderService);
        this.checkoutFacade = new CheckoutFacade(cartService, orderService, paymentService);
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }
    public CatalogService getCatalogService() {
        return catalogService;
    }
    public CartService getCartService() {
        return cartService;
    }
    public OrderService getOrderService() {
        return orderService;
    }
    public AdminService getAdminService() {
        return adminService;
    }
    public CheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }
}
