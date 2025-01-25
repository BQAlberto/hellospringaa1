package com.svalero.apibikes.service;

import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.WorkShop;
import com.svalero.apibikes.domain.dto.UserOutDto;
import com.svalero.apibikes.domain.dto.WorkShopInDto;
import com.svalero.apibikes.domain.dto.WorkShopOutDto;
import com.svalero.apibikes.exception.WorkShopNotFoundException;
import com.svalero.apibikes.repository.WorkShopRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkShopService {

    @Autowired
    private WorkShopRepository workShopRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<WorkShopOutDto> filterWorkshops(String name, String address, String email) {

        List<WorkShop> workshops = workShopRepository
                .findByNameContainingAndAddressContainingAndEmailContaining(
                        name, address, email);

        return modelMapper.map(workshops, new TypeToken<List<WorkShopOutDto>>() {}.getType());
    }

    public WorkShop get(long id) throws WorkShopNotFoundException {
        return workShopRepository.findById(id)
                .orElseThrow(WorkShopNotFoundException::new);
    }

    public WorkShopOutDto add(WorkShopInDto workShopInDto) {
        WorkShop workShop = modelMapper.map(workShopInDto, WorkShop.class);
        WorkShop newWorkShop = workShopRepository.save(workShop);
        return modelMapper.map(newWorkShop, WorkShopOutDto.class);
    }

    public WorkShopOutDto modify(long workShopId, WorkShopInDto workShopInDto) throws WorkShopNotFoundException {
        WorkShop workShop = workShopRepository.findById(workShopId)
                .orElseThrow(WorkShopNotFoundException::new);

        modelMapper.map(workShopInDto, workShop);
        workShopRepository.save(workShop);

        return modelMapper.map(workShop, WorkShopOutDto.class);
    }

    public void remove(long id) throws WorkShopNotFoundException {
        workShopRepository.findById(id)
                .orElseThrow(WorkShopNotFoundException::new);
        workShopRepository.deleteById(id);
    }
}
