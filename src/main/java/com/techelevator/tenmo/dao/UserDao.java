package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.Username;

import java.util.List;

public interface UserDao {

    List<User> findAll();
    List<Username> findOtherUsers(String username);

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
}
