package com.svalero.apibikes.controller;

import com.svalero.apibikes.domain.Mechanic;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.domain.dto.MechanicInDto;
import com.svalero.apibikes.domain.dto.MechanicOutDto;
import com.svalero.apibikes.exception.MechanicNotFoundException;
import com.svalero.apibikes.service.MechanicService;
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
public class MechanicController {

    @Autowired
    private MechanicService mechanicService;

    private final Logger logger = LoggerFactory.getLogger(MechanicController.class);

    @GetMapping("/mechanics")
    public ResponseEntity<List<MechanicOutDto>> getAll(@RequestParam(value = "name", defaultValue = "") String name) {
        logger.info("BEGIN getAll");
        List<MechanicOutDto> mechanics = mechanicService.getAll(name);
        logger.info("END getAll");
        return new ResponseEntity<>(mechanics, HttpStatus.OK);
    }

    @GetMapping("/mechanics/{mechanicId}")
    public ResponseEntity<Mechanic> getMechanic(@PathVariable long mechanicId) throws MechanicNotFoundException {
        logger.info("BEGIN getMechanic");
        Mechanic mechanic = mechanicService.get(mechanicId);
        logger.info("END getMechanic");
        return new ResponseEntity<>(mechanic, HttpStatus.OK);
    }

    @PostMapping("/mechanics")
    public ResponseEntity<MechanicOutDto> addMechanic(@RequestBody MechanicInDto mechanicInDto) {
        logger.info("BEGIN addMechanic");
        MechanicOutDto newMechanic = mechanicService.add(mechanicInDto);
        logger.info("END addMechanic");
        return new ResponseEntity<>(newMechanic, HttpStatus.CREATED);
    }

    @PutMapping("/mechanics/{mechanicId}")
    public ResponseEntity<MechanicOutDto> modifyMechanic(@PathVariable long mechanicId, @Valid @RequestBody MechanicInDto mechanicInDto)
            throws MechanicNotFoundException {
        logger.info("BEGIN modifyMechanic");
        MechanicOutDto modifiedMechanic = mechanicService.modify(mechanicId, mechanicInDto);
        logger.info("END modifyMechanic");
        return new ResponseEntity<>(modifiedMechanic, HttpStatus.OK);
    }

    @DeleteMapping("/mechanics/{mechanicId}")
    public ResponseEntity<Void> removeMechanic(@PathVariable long mechanicId) throws MechanicNotFoundException {
        logger.info("BEGIN removeMechanic");
        mechanicService.remove(mechanicId);
        logger.info("END removeMechanic");
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMechanicNotFoundException(MechanicNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
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

