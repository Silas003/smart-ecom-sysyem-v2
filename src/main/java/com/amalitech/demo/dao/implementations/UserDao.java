package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.config.DatabaseConfig;
import com.amalitech.demo.dao.interfaces.UserInterface;
import com.amalitech.demo.dto.request.UserRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
@Repository
public class UserDao  implements UserInterface {
    DatabaseConfig databaseConfig;
    @Override
    public void create(UserRequest userRequest)  {
        String sql = "INSERT INTO users(username, email, password, user_role) VALUES (?, ?, ?, ?)";
        try(Connection connection = databaseConfig.getConnection();
        ){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userRequest.getUsername());
            preparedStatement.setString(2, userRequest.getEmail());
            preparedStatement.setString(3, userRequest.getPassword());
            preparedStatement.setString(4, userRequest.getUserRole());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void findById() {

    }

    @Override
    public void findAll() {

    }
}
