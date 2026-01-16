package com.amalitech.demo.config;


import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.CategoryRepository;
import com.amalitech.demo.repository.InventoryRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadLocalRandom;


@Configuration
public class PreLoader {

    @Bean
    CommandLineRunner loadData(UserRepository userRepository,
                               CategoryRepository categoryRepository, ProductRepository productRepository, InventoryRepository inventoryRepository) {

        return args -> {

            for (int i = 1; i <= 15; i++) {

                int r = ThreadLocalRandom.current().nextInt(1000, 10000);

                userRepository.save(
                        new User(
                                "AliceOp" + r,
                                String.format("%dw@gmail.com", r),
                                "password1",
                                "customer"
                        )
                );

                Category c = categoryRepository.save(
                        new Category("Category" + r)
                );

                Product p = new Product("Product" + r,
                        Double.valueOf(r),
                        Integer.valueOf(r),
                        c
                );
                productRepository.save(
                        p
                );

                inventoryRepository.save(
                        new Inventory(p,r,r,"storage location " + r)
                );
            }
        };
    }
}

