package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.*;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<UserOutDto> filterUsers(String name, String surname, String email) {

        List<User> users = userRepository
                .findByNameContainingAndSurnameContainingAndEmailContaining(
                        name, surname, email);

        return modelMapper.map(users, new TypeToken<List<UserOutDto>>() {}.getType());
    }

    public User get(long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public UserOutDto modify(long userId, UserInDto userInDto) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        modelMapper.map(userInDto, user);
        userRepository.save(user);

        return modelMapper.map(user, UserOutDto.class);
    }

    public void remove(long id) throws UserNotFoundException {
        userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
    }
}
