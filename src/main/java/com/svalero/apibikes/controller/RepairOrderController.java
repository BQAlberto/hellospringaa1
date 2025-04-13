package com.svalero.apibikes.controller;

import com.svalero.apibikes.domain.RepairOrder;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.domain.dto.RepairOrderInDto;
import com.svalero.apibikes.domain.dto.RepairOrderOutDto;
import com.svalero.apibikes.exception.RepairOrderNotFoundException;
import com.svalero.apibikes.service.RepairOrderService;
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
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;

    private final Logger logger = LoggerFactory.getLogger(RepairOrderController.class);

    @GetMapping("/repair-orders")
    public List<RepairOrderOutDto> filterRepairOrders(
            @RequestParam(required = false) Long bikeId,
            @RequestParam(required = false) Long mechanicId,
            @RequestParam(required = false) Long workShopId) {

        logger.info("Filtrando órdenes de reparación con parámetros: bikeId={}, mechanicId={}, workShopId={}",
                bikeId, mechanicId, workShopId);

        return repairOrderService.filterRepairOrders(bikeId, mechanicId, workShopId);
    }

    @GetMapping("/repair-orders/{repairOrderId}")
    public ResponseEntity<RepairOrder> getRepairOrder(@PathVariable long repairOrderId) throws RepairOrderNotFoundException {
        logger.info("BEGIN getRepairOrder");
        RepairOrder repairOrder = repairOrderService.get(repairOrderId);
        logger.info("END getRepairOrder");
        return new ResponseEntity<>(repairOrder, HttpStatus.OK);
    }

    @PostMapping("/repair-orders")
    public ResponseEntity<RepairOrderOutDto> addRepairOrder(@RequestBody RepairOrderInDto repairOrderInDto) {
        logger.info("BEGIN addRepairOrder");
        RepairOrderOutDto newRepairOrder = repairOrderService.add(repairOrderInDto);
        logger.info("END addRepairOrder");
        return new ResponseEntity<>(newRepairOrder, HttpStatus.CREATED);
    }

    @PutMapping("/repair-orders/{repairOrderId}")
    public ResponseEntity<RepairOrderOutDto> modifyRepairOrder(@PathVariable long repairOrderId, @Valid @RequestBody RepairOrderInDto repairOrderInDto)
            throws RepairOrderNotFoundException {
        logger.info("BEGIN modifyRepairOrder");
        RepairOrderOutDto modifiedRepairOrder = repairOrderService.modify(repairOrderId, repairOrderInDto);
        logger.info("END modifyRepairOrder");
        return new ResponseEntity<>(modifiedRepairOrder, HttpStatus.OK);
    }

    @DeleteMapping("/repair-orders/{repairOrderId}")
    public ResponseEntity<Void> removeRepairOrder(@PathVariable long repairOrderId) throws RepairOrderNotFoundException {
        logger.info("BEGIN removeRepairOrder");
        repairOrderService.remove(repairOrderId);
        logger.info("END removeRepairOrder");
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleRepairOrderNotFoundException(RepairOrderNotFoundException exception) {
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
