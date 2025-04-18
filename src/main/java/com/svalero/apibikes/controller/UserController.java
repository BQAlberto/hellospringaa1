package com.svalero.apibikes.controller;

import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.*;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<UserOutDto> filterusers(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String surname,
            @RequestParam(required = false, defaultValue = "") String email) {

        logger.info("BEGIN getAll");

        List<UserOutDto> filteredUsers = userService.filterUsers(name, surname, email);

        logger.info("END getAll");

        return filteredUsers;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUser(@PathVariable long userId) throws UserNotFoundException {
        logger.info("BEGIN getUser");
        User user = userService.get(userId);
        logger.info("END getUser");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserOutDto> addUser(@RequestBody UserInDto userInDto) {
        UserOutDto userOutDto = userService.add(userInDto);
        return new ResponseEntity<>(userOutDto, HttpStatus.CREATED);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserOutDto>modifyUser(@PathVariable long userId, @Valid @RequestBody UserInDto user) throws UserNotFoundException {
        logger.info("BEGIN modifyUser");
        UserOutDto modifiedUser = userService.modify(userId, user);
        logger.info("END modifyUser");
        return new ResponseEntity<>(modifiedUser, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable long userId) throws UserNotFoundException{
        logger.info("BEGIN removeUser");
        userService.remove(userId);
        logger.info("END removeUser");
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleUserNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse>MethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        logger.error(exception.getMessage(), exception);

        return new ResponseEntity<>(ErrorResponse.validationError(errors), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse error = ErrorResponse.generalError(500, "Internal server error");
        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}