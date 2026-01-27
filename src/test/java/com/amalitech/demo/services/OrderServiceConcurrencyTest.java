package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.OrderItemRequest;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.CategoryRepository;
import com.amalitech.demo.repository.InventoryRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderServiceConcurrencyTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    OrderService orderService;

    @Test
    void concurrentOrders_shouldCauseOptimisticLockingOrOneToFail() throws InterruptedException, ExecutionException {
        // seed category, product, inventory, user
        Category cat = new Category();
        cat.setName("C1");
        categoryRepository.save(cat);

        Product p = new Product();
        p.setName("P1");
        p.setPrice(10.0);
        p.setStockQuantity(10);
        p.setCategory(cat);
        productRepository.save(p);

        Inventory inv = new Inventory(p, 2, 0, "in_stock");
        inventoryRepository.save(inv);

        User u = new User();
        u.setUsername("user1");
        u.setEmail("u1@example.com");
        u.setPassword("pass1234");
        u.setUserRole("USER");
        userRepository.save(u);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(p.getId());
        item.setQuantity(2);

        OrderRequest req = new OrderRequest();
        req.setItems(List.of(item));

        ExecutorService ex = Executors.newFixedThreadPool(2);
        Callable<Boolean> task = () -> {
            try{
                orderService.createOrder( req);
                return true;
            }catch (Exception e){
                return false;
            }
        };

        Future<Boolean> f1 = ex.submit(task);
        Future<Boolean> f2 = ex.submit(task);

        boolean r1 = f1.get();
        boolean r2 = f2.get();

        // at least one should fail because only 2 items in stock
        assertTrue((r1 && !r2) || (!r1 && r2) || (!r1 && !r2));

        ex.shutdownNow();
    }
}
