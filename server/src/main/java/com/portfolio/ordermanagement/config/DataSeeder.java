package com.portfolio.ordermanagement.config;

import com.portfolio.ordermanagement.entity.Category;
import com.portfolio.ordermanagement.entity.Product;
import com.portfolio.ordermanagement.entity.Role;
import com.portfolio.ordermanagement.entity.User;
import com.portfolio.ordermanagement.repository.CategoryRepository;
import com.portfolio.ordermanagement.repository.ProductRepository;
import com.portfolio.ordermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${seed.admin.password:admin123}")
    private String adminPassword;

    @Value("${seed.customer.email:customer@example.com}")
    private String customerEmail;

    @Value("${seed.customer.password:customer123}")
    private String customerPassword;

    @Bean
    CommandLineRunner seedDatabase() {
        return args -> {
            createAdminUser();
            createCustomerUser();

            Category electronics = createCategoryIfMissing(
                    "Electronics",
                    "Devices, gadgets and accessories"
            );

            Category books = createCategoryIfMissing(
                    "Books",
                    "Printed books and educational materials"
            );

            Category office = createCategoryIfMissing(
                    "Office",
                    "Office supplies and workplace essentials"
            );

            createProductIfMissing(
                    "Laptop Pro",
                    "Powerful laptop for work and development",
                    new BigDecimal("1499.99"),
                    10,
                    "https://example.com/laptop.jpg",
                    electronics
            );

            createProductIfMissing(
                    "Smartphone Ultra",
                    "Flagship smartphone with OLED display",
                    new BigDecimal("899.99"),
                    20,
                    "https://example.com/smartphone.jpg",
                    electronics
            );

            createProductIfMissing(
                    "Mechanical Keyboard",
                    "RGB mechanical keyboard for developers",
                    new BigDecimal("129.99"),
                    35,
                    "https://example.com/keyboard.jpg",
                    electronics
            );

            createProductIfMissing(
                    "Clean Code",
                    "Practical software engineering book",
                    new BigDecimal("39.99"),
                    15,
                    "https://example.com/clean-code.jpg",
                    books
            );

            createProductIfMissing(
                    "Office Chair",
                    "Ergonomic chair for comfortable work",
                    new BigDecimal("249.99"),
                    8,
                    "https://example.com/chair.jpg",
                    office
            );
        };
    }

    private void createAdminUser() {
        String normalizedEmail = normalizeEmail(adminEmail);

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return;
        }

        User admin = new User();
        admin.setEmail(normalizedEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }

    private void createCustomerUser() {
        String normalizedEmail = normalizeEmail(customerEmail);

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return;
        }

        User customer = new User();
        customer.setEmail(normalizedEmail);
        customer.setPassword(passwordEncoder.encode(customerPassword));
        customer.setFirstName("Demo");
        customer.setLastName("Customer");
        customer.setRole(Role.CUSTOMER);

        userRepository.save(customer);
    }

    private Category createCategoryIfMissing(
            String name,
            String description
    ) {
        return categoryRepository.findAll()
                .stream()
                .filter(category ->
                        category.getName().equalsIgnoreCase(name)
                )
                .findFirst()
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setDescription(description);

                    return categoryRepository.save(category);
                });
    }

    private void createProductIfMissing(
            String name,
            String description,
            BigDecimal price,
            int stockQuantity,
            String imageUrl,
            Category category
    ) {
        boolean productExists = productRepository.findAll()
                .stream()
                .anyMatch(product ->
                        product.getName().equalsIgnoreCase(name)
                );

        if (productExists) {
            return;
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setImageUrl(imageUrl);
        product.setCategory(category);

        productRepository.save(product);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}