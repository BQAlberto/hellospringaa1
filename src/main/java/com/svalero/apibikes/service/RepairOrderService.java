package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.Mechanic;
import com.svalero.apibikes.domain.RepairOrder;
import com.svalero.apibikes.domain.WorkShop;
import com.svalero.apibikes.domain.dto.RepairOrderInDto;
import com.svalero.apibikes.domain.dto.RepairOrderOutDto;
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

    public List<RepairOrderOutDto> getAll() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAll();
        return modelMapper.map(repairOrderList, new TypeToken<List<RepairOrderOutDto>>() {}.getType());
    }

    public RepairOrder get(long id) throws RepairOrderNotFoundException {
        return repairOrderRepository.findById(id)
                .orElseThrow(RepairOrderNotFoundException::new);
    }

    public RepairOrderOutDto add(RepairOrderInDto repairOrderInDto) {
        // Crear una nueva instancia de RepairOrder
        RepairOrder repairOrder = new RepairOrder();

        // Asignar entidades a partir de los IDs
        repairOrder.setBike(new Bike(repairOrderInDto.getBikeId())); // Solo asigna el ID
        repairOrder.setMechanic(new Mechanic(repairOrderInDto.getMechanicId())); // Solo asigna el ID
        repairOrder.setWorkShop(new WorkShop(repairOrderInDto.getWorkShopId())); // Solo asigna el ID

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
