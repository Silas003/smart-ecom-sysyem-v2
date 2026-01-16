package com.amalitech.demo.config;


import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PreLoader {

    @Bean
    CommandLineRunner loadData(UserRepository userRepository){

        return args -> {
            userRepository.save(new User("AliceOp","w@gmail.com","password1","customer"));
            userRepository.save(new User("KofiOp","a@gmail.com","password1","customer"));
        };
    }
}
