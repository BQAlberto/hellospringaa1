package com.svalero.apibikes.controller;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.dto.BikeInDto;
import com.svalero.apibikes.domain.dto.BikeOutDto;
import com.svalero.apibikes.domain.dto.BikeRegistrationDto;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.exception.BikeNotFoundException;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.service.BikeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @GetMapping("/bikes")
    public ResponseEntity<List<BikeOutDto>> getAll(@RequestParam(value = "brand", defaultValue = "") String brand,
                                                    @RequestParam(value = "model", defaultValue = "") String model) {
        return new ResponseEntity<>(bikeService.getAll(brand, model), HttpStatus.OK);
    }

    @GetMapping("/bikes/{bikeId}")
    public ResponseEntity<Bike> getBike(@PathVariable long bikeId) throws BikeNotFoundException {
        Bike bike = bikeService.get(bikeId);
        return new ResponseEntity<>(bike, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/bikes")
    public ResponseEntity<BikeOutDto> addBike(@PathVariable long userId, @Valid @RequestBody BikeRegistrationDto bike) throws UserNotFoundException {
        BikeOutDto newBike = bikeService.add(userId, bike);
        return new ResponseEntity<>(newBike, HttpStatus.CREATED);
    }

    @PutMapping("/bikes/{bikeId}")
    public ResponseEntity<BikeOutDto>modifyBike(@PathVariable long bikeId, @Valid @RequestBody BikeInDto bike) throws BikeNotFoundException {
        BikeOutDto modifiedBike = bikeService.modify(bikeId, bike);
        return new ResponseEntity<>(modifiedBike, HttpStatus.OK);
    }

    @DeleteMapping("/bikes/{bikeId}")
    public ResponseEntity<Void> removeBike(@PathVariable long bikeId) throws BikeNotFoundException{
        bikeService.remove(bikeId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBikeNotFoundException(BikeNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse>MethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(ErrorResponse.validationError(errors), HttpStatus.BAD_REQUEST);
    }
}