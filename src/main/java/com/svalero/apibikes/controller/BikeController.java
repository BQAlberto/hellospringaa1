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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class BikeController {

    @Autowired
    private BikeService bikeService;
    private final Logger logger = LoggerFactory.getLogger(BikeController.class);

    @GetMapping("/bikes")
    public List<BikeOutDto> filterBikes(
            @RequestParam(required = false, defaultValue = "") String brand,
            @RequestParam(required = false, defaultValue = "") String model,
            @RequestParam(required = false, defaultValue = "") String color) {

        logger.info("Filtrando bicicletas con parámetros: brand={}, model={}, color={}", brand, model, color);

        List<BikeOutDto> filteredBikes = bikeService.filterBikes(brand, model, color);

        logger.info("Operación completada: Se obtuvieron {} bicicletas filtradas", filteredBikes.size());

        return filteredBikes;
    }

    @GetMapping("/bikes/{bikeId}")
    public ResponseEntity<Bike> getBike(@PathVariable long bikeId) throws BikeNotFoundException {
        logger.info("BEGIN getBike");
        Bike bike = bikeService.get(bikeId);
        logger.info("END getBike");
        return new ResponseEntity<>(bike, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/bikes")
    public ResponseEntity<BikeOutDto> addBike(@PathVariable long userId, @Valid @RequestBody BikeRegistrationDto bike) throws UserNotFoundException {
        logger.info("BEGIN addBike");
        BikeOutDto newBike = bikeService.add(userId, bike);
        logger.info("END addBike");
        return new ResponseEntity<>(newBike, HttpStatus.CREATED);
    }

    @PutMapping("/bikes/{bikeId}")
    public ResponseEntity<BikeOutDto>modifyBike(@PathVariable long bikeId, @Valid @RequestBody BikeInDto bike) throws BikeNotFoundException {
        logger.info("BEGIN modifyBike");
        BikeOutDto modifiedBike = bikeService.modify(bikeId, bike);
        logger.info("END modifyBike");
        return new ResponseEntity<>(modifiedBike, HttpStatus.OK);
    }

    @DeleteMapping("/bikes/{bikeId}")
    public ResponseEntity<Void> removeBike(@PathVariable long bikeId) throws BikeNotFoundException{
        logger.info("BEGIN removeBike");
        bikeService.remove(bikeId);
        logger.info("END removeBike");
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBikeNotFoundException(BikeNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        logger.error(exception.getMessage(), exception);
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