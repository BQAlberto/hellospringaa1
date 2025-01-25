package com.svalero.apibikes.controller;

import com.svalero.apibikes.domain.WorkShop;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.domain.dto.UserOutDto;
import com.svalero.apibikes.domain.dto.WorkShopInDto;
import com.svalero.apibikes.domain.dto.WorkShopOutDto;
import com.svalero.apibikes.exception.WorkShopNotFoundException;
import com.svalero.apibikes.service.WorkShopService;
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
public class WorkShopController {

    @Autowired
    private WorkShopService workShopService;
    private final Logger logger = LoggerFactory.getLogger(WorkShopController.class);

    @GetMapping("/workshops")
    public List<WorkShopOutDto> filterworkshops(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String address,
            @RequestParam(required = false, defaultValue = "") String email) {

        logger.info("BEGIN getAll");

        List<WorkShopOutDto> filteredWorkshops = workShopService.filterWorkshops(name, address, email);

        logger.info("END getAll");

        return filteredWorkshops;
    }

    @GetMapping("/workshops/{workShopId}")
    public ResponseEntity<WorkShop> getWorkShop(@PathVariable long workShopId) throws WorkShopNotFoundException {
        logger.info("BEGIN getWorkShop");
        WorkShop workShop = workShopService.get(workShopId);
        logger.info("END getWorkShop");
        return new ResponseEntity<>(workShop, HttpStatus.OK);
    }

    @PostMapping("/workshops")
    public ResponseEntity<WorkShopOutDto> addWorkShop(@RequestBody WorkShopInDto workShopInDto) {
        logger.info("BEGIN addWorkShop");
        WorkShopOutDto newWorkShop = workShopService.add(workShopInDto);
        logger.info("END addWorkShop");
        return new ResponseEntity<>(newWorkShop, HttpStatus.CREATED);
    }

    @PutMapping("/workshops/{workShopId}")
    public ResponseEntity<WorkShopOutDto> modifyWorkShop(@PathVariable long workShopId, @Valid @RequestBody WorkShopInDto workShopInDto)
            throws WorkShopNotFoundException {
        logger.info("BEGIN modifyWorkShop");
        WorkShopOutDto modifiedWorkShop = workShopService.modify(workShopId, workShopInDto);
        logger.info("END modifyWorkShop");
        return new ResponseEntity<>(modifiedWorkShop, HttpStatus.OK);
    }

    @DeleteMapping("/workshops/{workShopId}")
    public ResponseEntity<Void> removeWorkShop(@PathVariable long workShopId) throws WorkShopNotFoundException {
        logger.info("BEGIN removeWorkShop");
        workShopService.remove(workShopId);
        logger.info("END removeWorkShop");
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleWorkShopNotFoundException(WorkShopNotFoundException exception) {
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
