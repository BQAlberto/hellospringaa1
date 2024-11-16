package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.BikeInDto;
import com.svalero.apibikes.domain.dto.BikeOutDto;
import com.svalero.apibikes.exception.BikeNotFoundException;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.repository.BikeRepository;
import com.svalero.apibikes.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BikeService {

    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<BikeOutDto> getAll(String brand, String model) {
        List<Bike> bikeList;

        if (brand.isEmpty() && model.isEmpty()) {
            bikeList = bikeRepository.findAll();
        } else if (brand.isEmpty()) {
            bikeList = bikeRepository.findByModel(model);
        } else if (model.isEmpty()) {
            bikeList = bikeRepository.findByBrand(brand);
        } else {
            bikeList = bikeRepository.findByBrandAndModel(brand, model);
        }

        return modelMapper.map(bikeList, new TypeToken<List<BikeOutDto>>() {}.getType());
    }

    public Bike get(long id) throws BikeNotFoundException {
        return bikeRepository.findById(id)
                .orElseThrow(BikeNotFoundException::new);
    }

    public BikeOutDto add(long userId, BikeInDto bikeInDto) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Bike bike = modelMapper.map(bikeInDto, Bike.class);
        bike.setRegistrationDate(LocalDate.now());
        bike.setUser(user);
        Bike newBike = bikeRepository.save(bike);

        return modelMapper.map(newBike, BikeOutDto.class);
    }

    public BikeOutDto modify(long bikeId, BikeInDto bikeInDto) throws BikeNotFoundException {
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(BikeNotFoundException::new);

        modelMapper.map(bikeInDto, bike);
        bikeRepository.save(bike);

        return modelMapper.map(bike, BikeOutDto.class);
    }

    public void remove(long id) throws BikeNotFoundException {
        bikeRepository.findById(id)
                .orElseThrow(BikeNotFoundException::new);
        bikeRepository.deleteById(id);
    }
}
