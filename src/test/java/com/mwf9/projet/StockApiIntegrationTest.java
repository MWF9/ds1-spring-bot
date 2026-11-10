package com.mwf9.projet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwf9.projet.model.Category;
import com.mwf9.projet.model.Customer;
import com.mwf9.projet.model.Product;
import com.mwf9.projet.repository.CategoryRepository;
import com.mwf9.projet.repository.CustomerRepository;
import com.mwf9.projet.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** End-to-end tests of the REST API on an in-memory H2 database. Each test is rolled back. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StockApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired CategoryRepository categories;
    @Autowired ProductRepository products;
    @Autowired CustomerRepository customers;

    Category category;
    Product product;
    Customer customer;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Test category");
        category = categories.save(category);

        product = new Product();
        product.setName("Test product");
        product.setSku("TST-001");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);
        product.setReorderLevel(2);
        product.setCategory(category);
        product = products.save(product);

        customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setEmail("test@example.com");
        customer = customers.save(customer);
    }

    private String json(Object body) throws Exception {
        return om.writeValueAsString(body);
    }

    @Test
    void createProduct_returns201AndIsReadable() throws Exception {
        Map<String, Object> body = Map.of("name", "Keyboard", "sku", "KB-1", "price", 25.5,
                "stockQuantity", 8, "reorderLevel", 3, "categoryId", category.getId());

        MvcResult created = mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.category.name").value("Test category"))
                .andReturn();

        long id = om.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("KB-1"));
    }

    @Test
    void createProduct_withInvalidData_returns400WithFieldErrors() throws Exception {
        Map<String, Object> body = Map.of("name", "", "sku", "X", "price", -5,
                "stockQuantity", 1, "reorderLevel", 1, "categoryId", category.getId());

        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").exists())
                .andExpect(jsonPath("$.details.price").exists());
    }

    @Test
    void getUnknownProduct_returns404() throws Exception {
        mvc.perform(get("/api/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 999999 not found"));
    }

    @Test
    void updateAndDeleteCategory() throws Exception {
        mvc.perform(put("/api/categories/" + category.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Renamed", "description", "new"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed"));

        // The category is used by a product, so deleting it must be refused with 409.
        mvc.perform(delete("/api/categories/" + category.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void placeOrder_decreasesStock_andCancelRestoresIt() throws Exception {
        Map<String, Object> order = Map.of("customerId", customer.getId(),
                "items", List.of(Map.of("productId", product.getId(), "quantity", 3)));

        MvcResult result = mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(30.0))
                .andReturn();
        assertEquals(7, products.findById(product.getId()).orElseThrow().getStockQuantity());

        long orderId = om.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(put("/api/orders/" + orderId + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "CANCELLED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        assertEquals(10, products.findById(product.getId()).orElseThrow().getStockQuantity());

        // A cancelled order cannot change status again.
        mvc.perform(put("/api/orders/" + orderId + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "SHIPPED"))))
                .andExpect(status().isConflict());
    }

    @Test
    void placeOrder_withNotEnoughStock_returns409() throws Exception {
        Map<String, Object> order = Map.of("customerId", customer.getId(),
                "items", List.of(Map.of("productId", product.getId(), "quantity", 100)));

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json(order)))
                .andExpect(status().isConflict());
    }

    @Test
    void lowStockQuery_returnsProductsAtOrBelowReorderLevel() throws Exception {
        product.setStockQuantity(1); // below the reorder level of 2
        products.save(product);

        mvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sku").value(hasItem("TST-001")));
    }

    @Test
    void statsQueries_rankCustomersAndProducts() throws Exception {
        Map<String, Object> order = Map.of("customerId", customer.getId(),
                "items", List.of(Map.of("productId", product.getId(), "quantity", 4)));
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json(order)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/orders/stats/top-customers?limit=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Customer"))
                .andExpect(jsonPath("$[0].totalSpent").value(40.0));

        mvc.perform(get("/api/orders/stats/best-sellers?limit=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Test product"))
                .andExpect(jsonPath("$[0].totalSold").value(4));
    }

    @Test
    void stockMovement_in_increasesStock() throws Exception {
        Map<String, Object> body = Map.of("productId", product.getId(), "type", "IN", "quantity", 5, "reason", "Restock");

        mvc.perform(post("/api/stock-movements").contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isCreated());
        assertEquals(15, products.findById(product.getId()).orElseThrow().getStockQuantity());
    }
}
