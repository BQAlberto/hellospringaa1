package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.*;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

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

    public UserOutDto add(UserInDto userInDto) {
        User user = new User();
        user.setUsername(userInDto.getUsername());
        user.setPassword(passwordEncoder.encode(userInDto.getPassword()));
        user.setName(userInDto.getName());
        user.setSurname(userInDto.getSurname());
        user.setEmail(userInDto.getEmail());
        //TODO mapear el resto de campos (usando ModelMapper)
        userRepository.save(user);

        UserOutDto userOutDto = new UserOutDto();
        userOutDto.setUsername(user.getUsername());
        userOutDto.setEmail(user.getEmail());
        userOutDto.setName(user.getName());
        userOutDto.setSurname(user.getSurname());
        //TODO Añadir todos los campos que quiero devolver en la respuesta
        return userOutDto;
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
