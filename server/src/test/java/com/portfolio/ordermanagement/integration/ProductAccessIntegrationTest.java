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

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void adminShouldCreateProductButCustomerShouldNot() throws Exception {

        User admin = createUser("admin-" + System.nanoTime() + "@example.com", Role.ADMIN);
        User customer = createUser("customer-" + System.nanoTime() + "@example.com", Role.CUSTOMER);

        String adminToken = jwtService.generateToken(admin);
        String customerToken = jwtService.generateToken(customer);

        String categoryJson = """
                {
                  "name": "Electronics-%s",
                  "description": "Electronic devices"
                }
                """.formatted(System.nanoTime());

        String categoryResponse = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = extractId(categoryResponse);

        String productJson = """
                {
                  "name": "Laptop Pro",
                  "description": "Powerful laptop",
                  "price": 1499.99,
                  "stockQuantity": 5,
                  "imageUrl": "https://example.com/laptop.jpg",
                  "categoryId": %d
                }
                """.formatted(categoryId);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.categoryId").value(categoryId));

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
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

    private Long extractId(String json) {
        String marker = "\"id\":";
        int start = json.indexOf(marker) + marker.length();
        int end = json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return Long.parseLong(json.substring(start, end).trim());
    }
}