package com.amalitech.demo.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConfig {
    private final String  DB_USERNAME = System.getenv("DB_USERNAME");
    private final String  DB_PASSWORD = System.getenv("DB_PASSWORD");
    private final String DRIVER = "org.postgresql.Driver";
    private final String url= "jdbc:postgresql://localhost:5432/smartEcom";
    private  Connection connection ;

    private void connect() throws ClassNotFoundException, SQLException {
         Class.forName(DRIVER);
         connection = DriverManager.getConnection(
                url,
                DB_USERNAME,
                DB_PASSWORD);
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection == null || connection.isClosed()){
            connect();
        }
        return connection;
    }


}
