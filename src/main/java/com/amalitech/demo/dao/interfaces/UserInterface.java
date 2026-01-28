package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.dto.request.UserRequest;

import java.sql.SQLException;

public interface UserInterface {
    void  create(UserRequest userRequest) throws SQLException;
    void update();
    void delete();
    void findById();
    void findAll();
}
