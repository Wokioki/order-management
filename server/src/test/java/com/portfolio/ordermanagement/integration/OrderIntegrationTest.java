package com.portfolio.ordermanagement.integration;

import com.portfolio.ordermanagement.entity.Role;
import com.portfolio.ordermanagement.entity.User;
import com.portfolio.ordermanagement.repository.UserRepository;
import com.portfolio.ordermanagement.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createOrderAndCancelOrder_shouldDecreaseAndRestoreStock() throws Exception {
        User admin = createUser("admin-" + System.nanoTime() + "@example.com", Role.ADMIN);
        User customer = createUser("customer-" + System.nanoTime() + "@example.com", Role.CUSTOMER);

        String adminToken = jwtService.generateToken(admin);
        String customerToken = jwtService.generateToken(customer);

        Long categoryId = createCategory(adminToken);
        Long productId = createProduct(adminToken, categoryId, 5);

        String createOrderJson = """
                {
                  "items": [
                    {
                      "productId": %d,
                      "quantity": 2
                    }
                  ]
                }
                """.formatted(productId);

        String orderResponse = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(2999.98))
                .andExpect(jsonPath("$.items[0].productId").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long orderId = extractLong(orderResponse, "id");

        mockMvc.perform(get("/api/products/" + productId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(3));

        String cancelJson = """
                {
                  "status": "CANCELLED"
                }
                """;

        mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cancelJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/api/products/" + productId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(5));
    }

    private Long createCategory(String adminToken) throws Exception {
        String categoryJson = """
                {
                  "name": "Electronics-%s",
                  "description": "Electronic devices"
                }
                """.formatted(System.nanoTime());

        String response = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractLong(response, "id");
    }

    private Long createProduct(String adminToken, Long categoryId, int stockQuantity) throws Exception {
        String productJson = """
                {
                  "name": "Laptop Pro",
                  "description": "Powerful laptop",
                  "price": 1499.99,
                  "stockQuantity": %d,
                  "imageUrl": "https://example.com/laptop.jpg",
                  "categoryId": %d
                }
                """.formatted(stockQuantity, categoryId);

        String response = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractLong(response, "id");
    }

    private User createUser(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);

        return userRepository.save(user);
    }

    private Long extractLong(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);

        if (!matcher.find()) {
            throw new IllegalStateException("Field not found in JSON: " + fieldName);
        }

        return Long.parseLong(matcher.group(1));
    }
}