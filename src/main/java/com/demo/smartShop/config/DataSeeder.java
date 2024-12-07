package com.demo.smartShop.config;

import com.demo.smartShop.entity.Client;
import com.demo.smartShop.entity.Product;
import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.CustomerTier;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.repository.ClientRepository;
import com.demo.smartShop.repository.ProductRepository;
import com.demo.smartShop.repository.UserRepository;
import com.demo.smartShop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    @PostConstruct
    public void seedData() {
        log.info("Starting data seeding...");

        seedAdminUser();
        seedClients();
        seedProducts();

        log.info("Data seeding completed successfully!");
    }

    private void seedAdminUser() {
        if (userRepository.findByUsername("kawtar").isEmpty()) {
            User admin = User.builder()
                    .username("kawtar")
                    .password(PasswordUtil.hash("kawtar123"))
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("✓ Admin user 'kawtar' created successfully");
        } else {
            log.info("✓ Admin user 'kawtar' already exists");
        }
    }

    private void seedClients() {
        if (clientRepository.count() > 0) {
            log.info("✓ Clients already exist, skipping client seeding");
            return;
        }

        // Client 1: IBM - GOLD tier
        Client ibm = Client.builder()
                .nom("IBM")
                .email("contact@ibm.com")
                .tier(CustomerTier.GOLD)
                .totalOrders(15)
                .totalSpent(new BigDecimal("2450.75"))
                .firstOrderDate(LocalDateTime.now().minusMonths(8))
                .lastOrderDate(LocalDateTime.now().minusDays(5))
                .build();
        Client savedIbm = clientRepository.save(ibm);

        User ibmUser = User.builder()
                .username("ibm")
                .password(PasswordUtil.hash("IBM123"))
                .role(UserRole.CLIENT)
                .client(savedIbm)
                .build();
        userRepository.save(ibmUser);

        // Client 2: HPS - PLATINUM tier
        Client hps = Client.builder()
                .nom("HPS")
                .email("contact@hps.com")
                .tier(CustomerTier.PLATINUM)
                .totalOrders(32)
                .totalSpent(new BigDecimal("5890.50"))
                .firstOrderDate(LocalDateTime.now().minusYears(1))
                .lastOrderDate(LocalDateTime.now().minusDays(2))
                .build();
        Client savedHps = clientRepository.save(hps);

        User hpsUser = User.builder()
                .username("hps")
                .password(PasswordUtil.hash("HPS123"))
                .role(UserRole.CLIENT)
                .client(savedHps)
                .build();
        userRepository.save(hpsUser);

        // Client 3: Microsoft - SILVER tier
        Client microsoft = Client.builder()
                .nom("Microsoft")
                .email("contact@microsoft.com")
                .tier(CustomerTier.SILVER)
                .totalOrders(8)
                .totalSpent(new BigDecimal("890.25"))
                .firstOrderDate(LocalDateTime.now().minusMonths(4))
                .lastOrderDate(LocalDateTime.now().minusDays(12))
                .build();
        Client savedMicrosoft = clientRepository.save(microsoft);

        User microsoftUser = User.builder()
                .username("microsoft")
                .password(PasswordUtil.hash("MICROSOFT123"))
                .role(UserRole.CLIENT)
                .client(savedMicrosoft)
                .build();
        userRepository.save(microsoftUser);

        // Client 4: Oracle - BASIC tier
        Client oracle = Client.builder()
                .nom("Oracle")
                .email("contact@oracle.com")
                .tier(CustomerTier.BASIC)
                .totalOrders(2)
                .totalSpent(new BigDecimal("145.99"))
                .firstOrderDate(LocalDateTime.now().minusMonths(1))
                .lastOrderDate(LocalDateTime.now().minusDays(20))
                .build();
        Client savedOracle = clientRepository.save(oracle);

        User oracleUser = User.builder()
                .username("oracle")
                .password(PasswordUtil.hash("ORACLE123"))
                .role(UserRole.CLIENT)
                .client(savedOracle)
                .build();
        userRepository.save(oracleUser);

        // Client 5: Capgemini - SILVER tier
        Client capgemini = Client.builder()
                .nom("Capgemini")
                .email("contact@capgemini.com")
                .tier(CustomerTier.SILVER)
                .totalOrders(10)
                .totalSpent(new BigDecimal("1250.00"))
                .firstOrderDate(LocalDateTime.now().minusMonths(6))
                .lastOrderDate(LocalDateTime.now().minusDays(8))
                .build();
        Client savedCapgemini = clientRepository.save(capgemini);

        User capgeminiUser = User.builder()
                .username("capgemini")
                .password(PasswordUtil.hash("CAPGEMINI123"))
                .role(UserRole.CLIENT)
                .client(savedCapgemini)
                .build();
        userRepository.save(capgeminiUser);

        // Client 6: Accenture - GOLD tier
        Client accenture = Client.builder()
                .nom("Accenture")
                .email("contact@accenture.com")
                .tier(CustomerTier.GOLD)
                .totalOrders(18)
                .totalSpent(new BigDecimal("3120.80"))
                .firstOrderDate(LocalDateTime.now().minusMonths(10))
                .lastOrderDate(LocalDateTime.now().minusDays(3))
                .build();
        Client savedAccenture = clientRepository.save(accenture);

        User accentureUser = User.builder()
                .username("accenture")
                .password(PasswordUtil.hash("ACCENTURE123"))
                .role(UserRole.CLIENT)
                .client(savedAccenture)
                .build();
        userRepository.save(accentureUser);

        log.info("✓ Created 6 company clients with user accounts (IBM, HPS, Microsoft, Oracle, Capgemini, Accenture)");
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            log.info("✓ Products already exist, skipping product seeding");
            return;
        }

        // Ordinateurs portables
        Product laptop1 = Product.builder()
                .nom("Dell XPS 15")
                .description("PC portable Dell XPS 15")
                .price(new BigDecimal("1899.00"))
                .stock(12)
                .build();

        Product laptop2 = Product.builder()
                .nom("HP Pavilion 14")
                .description("PC portable HP ")
                .price(new BigDecimal("699.00"))
                .stock(20)
                .build();

        Product laptop3 = Product.builder()
                .nom("Lenovo ThinkPad X1 Carbon")
                .description("PC portable professionnel Lenovo")
                .price(new BigDecimal("2299.00"))
                .stock(8)
                .build();

        // Ordinateurs de bureau
        Product desktop = Product.builder()
                .nom("PC Gamer Asus ROG")
                .description("PC de bureau gaming Asus ROG")
                .price(new BigDecimal("3499.00"))
                .stock(5)
                .build();

        // Écrans
        Product monitor1 = Product.builder()
                .nom("Écran Dell UltraSharp 27\"")
                .description("Moniteur Dell")
                .price(new BigDecimal("549.00"))
                .stock(15)
                .build();

        Product monitor2 = Product.builder()
                .nom("Écran Gaming LG 32\" 144Hz")
                .description("Moniteur gaming LG")
                .price(new BigDecimal("449.00"))
                .stock(18)
                .build();

        // Claviers
        Product keyboard1 = Product.builder()
                .nom("Clavier Mécanique")
                .description("Clavier")
                .price(new BigDecimal("119.00"))
                .stock(35)
                .build();

        Product keyboard2 = Product.builder()
                .nom("Clavier Gaming")
                .description("Clavier mécanique")
                .price(new BigDecimal("159.00"))
                .stock(25)
                .build();

        // Souris
        Product mouse1 = Product.builder()
                .nom("Souris Logitech")
                .description("Souris sans fil ergonomique")
                .price(new BigDecimal("109.00"))
                .stock(40)
                .build();

        Product mouse2 = Product.builder()
                .nom("Souris Gaming")
                .description("Souris gaming")
                .price(new BigDecimal("89.00"))
                .stock(30)
                .build();

        // Casques
        Product headset1 = Product.builder()
                .nom("Casque Sony")
                .description("Casque sans fil Sony")
                .price(new BigDecimal("399.00"))
                .stock(22)
                .build();

        Product headset2 = Product.builder()
                .nom("Casque Gaming")
                .description("Casque gaming")
                .price(new BigDecimal("129.00"))
                .stock(28)
                .build();

        // Webcams
        Product webcam = Product.builder()
                .nom("Webcam Logitech")
                .description("Webcam Logitech")
                .price(new BigDecimal("199.00"))
                .stock(20)
                .build();

        // Accessoires
        Product mousepad = Product.builder()
                .nom("Tapis de Souris XXL RGB")
                .description("Tapis de souris gaming")
                .price(new BigDecimal("39.99"))
                .stock(50)
                .build();

        Product usbHub = Product.builder()
                .nom("Hub USB-C 7-en-1")
                .description("Hub USB-C multiport")
                .price(new BigDecimal("49.99"))
                .stock(45)
                .build();

        productRepository.save(laptop1);
        productRepository.save(laptop2);
        productRepository.save(laptop3);
        productRepository.save(desktop);
        productRepository.save(monitor1);
        productRepository.save(monitor2);
        productRepository.save(keyboard1);
        productRepository.save(keyboard2);
        productRepository.save(mouse1);
        productRepository.save(mouse2);
        productRepository.save(headset1);
        productRepository.save(headset2);
        productRepository.save(webcam);
        productRepository.save(mousepad);
        productRepository.save(usbHub);

        log.info("✓ Created 15 IT products (PCs, monitors, keyboards, mice, headsets, accessories)");
    }
}
