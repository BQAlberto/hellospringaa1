package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.*;
import com.svalero.apibikes.domain.dto.RepairOrderInDto;
import com.svalero.apibikes.domain.dto.RepairOrderOutDto;
import com.svalero.apibikes.domain.dto.UserOutDto;
import com.svalero.apibikes.exception.RepairOrderNotFoundException;
import com.svalero.apibikes.repository.RepairOrderRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairOrderService {

    @Autowired
    private RepairOrderRepository repairOrderRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<RepairOrderOutDto> filterRepairOrders(Long bikeId, Long mechanicId, Long workShopId) {
        List<RepairOrder> repairOrders;

        if (bikeId == null && mechanicId == null && workShopId == null) {
            // Sin filtros: devolver todas las órdenes
            repairOrders = repairOrderRepository.findAll();
        } else {
            // Filtrar con los valores proporcionados
            repairOrders = repairOrderRepository.findWithFilters(bikeId, mechanicId, workShopId);
        }

        return modelMapper.map(repairOrders, new TypeToken<List<RepairOrderOutDto>>() {}.getType());
    }

    public RepairOrder get(long id) throws RepairOrderNotFoundException {
        return repairOrderRepository.findById(id)
                .orElseThrow(RepairOrderNotFoundException::new);
    }

    public RepairOrderOutDto add(RepairOrderInDto repairOrderInDto) {
        // Crear una nueva instancia de RepairOrder
        RepairOrder repairOrder = new RepairOrder();

        // Asignar entidades a partir de los IDs
        repairOrder.setBike(new Bike(repairOrderInDto.getBikeId()));
        repairOrder.setMechanic(new Mechanic(repairOrderInDto.getMechanicId()));
        repairOrder.setWorkShop(new WorkShop(repairOrderInDto.getWorkShopId()));

        // Asignar otros campos
        repairOrder.setRepairDate(repairOrderInDto.getRepairDate());
        repairOrder.setCost(repairOrderInDto.getCost());
        repairOrder.setDescription(repairOrderInDto.getDescription());

        // Guardar en la base de datos
        RepairOrder newRepairOrder = repairOrderRepository.save(repairOrder);

        // Convertir a DTO de salida y devolver
        return modelMapper.map(newRepairOrder, RepairOrderOutDto.class);
    }

    public RepairOrderOutDto modify(long repairOrderId, RepairOrderInDto repairOrderInDto) throws RepairOrderNotFoundException {
        // Buscar la orden de reparación existente
        RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(RepairOrderNotFoundException::new);

        // Asignar entidades a partir de los IDs
        repairOrder.setBike(new Bike(repairOrderInDto.getBikeId())); // Solo asigna el ID
        repairOrder.setMechanic(new Mechanic(repairOrderInDto.getMechanicId())); // Solo asigna el ID
        repairOrder.setWorkShop(new WorkShop(repairOrderInDto.getWorkShopId())); // Solo asigna el ID

        // Asignar otros campos
        repairOrder.setRepairDate(repairOrderInDto.getRepairDate());
        repairOrder.setCost(repairOrderInDto.getCost());
        repairOrder.setDescription(repairOrderInDto.getDescription());

        // Guardar los cambios
        repairOrderRepository.save(repairOrder);

        // Convertir a DTO de salida y devolver
        return modelMapper.map(repairOrder, RepairOrderOutDto.class);
    }

    public void remove(long id) throws RepairOrderNotFoundException {
        repairOrderRepository.findById(id)
                .orElseThrow(RepairOrderNotFoundException::new);
        repairOrderRepository.deleteById(id);
    }
}
