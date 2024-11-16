package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.exception.BikeNotFoundException;
import com.svalero.apibikes.repository.BikeRepository;
import com.svalero.apibikes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAll() {
        List<User> allUsers = userRepository.findAll();
        return allUsers;
    }

    public User add(User user) {
        return userRepository.save(user);
    }

}
