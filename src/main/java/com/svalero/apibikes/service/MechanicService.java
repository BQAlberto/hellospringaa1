package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.Mechanic;
import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.MechanicInDto;
import com.svalero.apibikes.domain.dto.MechanicOutDto;
import com.svalero.apibikes.domain.dto.UserOutDto;
import com.svalero.apibikes.exception.MechanicNotFoundException;
import com.svalero.apibikes.repository.MechanicRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MechanicService {

    @Autowired
    private MechanicRepository mechanicRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<MechanicOutDto> filterMechanics(String name, String surname, String specialization) {

        List<Mechanic> mechanics = mechanicRepository
                .findByNameContainingAndSurnameContainingAndSpecializationContaining(
                        name, surname, specialization);

        return modelMapper.map(mechanics, new TypeToken<List<MechanicOutDto>>() {}.getType());
    }

    public Mechanic get(long id) throws MechanicNotFoundException {
        return mechanicRepository.findById(id)
                .orElseThrow(MechanicNotFoundException::new);
    }

    public MechanicOutDto add(MechanicInDto mechanicInDto) {
        Mechanic mechanic = modelMapper.map(mechanicInDto, Mechanic.class);
        Mechanic newMechanic = mechanicRepository.save(mechanic);
        return modelMapper.map(newMechanic, MechanicOutDto.class);
    }

    public MechanicOutDto modify(long mechanicId, MechanicInDto mechanicInDto) throws MechanicNotFoundException {
        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(MechanicNotFoundException::new);

        modelMapper.map(mechanicInDto, mechanic);
        mechanicRepository.save(mechanic);

        return modelMapper.map(mechanic, MechanicOutDto.class);
    }

    public void remove(long id) throws MechanicNotFoundException {
        mechanicRepository.findById(id)
                .orElseThrow(MechanicNotFoundException::new);
        mechanicRepository.deleteById(id);
    }
}
