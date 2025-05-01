package com.svalero.apibikes;

import com.svalero.apibikes.domain.Mechanic;
import com.svalero.apibikes.domain.dto.MechanicInDto;
import com.svalero.apibikes.domain.dto.MechanicOutDto;
import com.svalero.apibikes.repository.MechanicRepository;
import com.svalero.apibikes.service.MechanicService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MechanicServiceTests {

    @InjectMocks
    MechanicService mechanicService;

    @Mock
    private MechanicRepository mechanicRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFilterMechanicsWithoutFilters() {
        List<Mechanic> mockMechanicList = List.of(
                new Mechanic(1, "John", "Doe", "123456789", "Brakes", null),
                new Mechanic(2, "Jane", "Smith", "987654321", "Suspension", null)
        );
        List<MechanicOutDto> mockMechanicOutDtoList = List.of(
                new MechanicOutDto(1, "John", "Doe", "123456789", "Brakes"),
                new MechanicOutDto(2, "Jane", "Smith", "987654321", "Suspension")
        );

        when(mechanicRepository.findByNameContainingAndSurnameContainingAndSpecializationContaining("", "", ""))
                .thenReturn(mockMechanicList);
        when(modelMapper.map(mockMechanicList, new TypeToken<List<MechanicOutDto>>() {}.getType()))
                .thenReturn(mockMechanicOutDtoList);

        List<MechanicOutDto> result = mechanicService.filterMechanics("", "", "");

        assertEquals(2, result.size());
        assertEquals("John", result.getFirst().getName());
        verify(mechanicRepository, times(1))
                .findByNameContainingAndSurnameContainingAndSpecializationContaining("", "", "");
    }

    @Test
    public void testFilterMechanicsByName() {
        List<Mechanic> mockMechanicList = List.of(
                new Mechanic(1, "John", "Doe", "123456789", "Brakes", null)
        );
        List<MechanicOutDto> mockMechanicOutDtoList = List.of(
                new MechanicOutDto(1, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicRepository.findByNameContainingAndSurnameContainingAndSpecializationContaining("John", "", ""))
                .thenReturn(mockMechanicList);
        when(modelMapper.map(mockMechanicList, new TypeToken<List<MechanicOutDto>>() {}.getType()))
                .thenReturn(mockMechanicOutDtoList);

        List<MechanicOutDto> result = mechanicService.filterMechanics("John", "", "");

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());
    }

    @Test
    public void testFilterMechanicsBySurname() {
        List<Mechanic> mockMechanicList = List.of(
                new Mechanic(1, "John", "Doe", "123456789", "Brakes", null)
        );
        List<MechanicOutDto> mockMechanicOutDtoList = List.of(
                new MechanicOutDto(1, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicRepository.findByNameContainingAndSurnameContainingAndSpecializationContaining("", "Doe", ""))
                .thenReturn(mockMechanicList);
        when(modelMapper.map(mockMechanicList, new TypeToken<List<MechanicOutDto>>() {}.getType()))
                .thenReturn(mockMechanicOutDtoList);

        List<MechanicOutDto> result = mechanicService.filterMechanics("", "Doe", "");

        assertEquals(1, result.size());
        assertEquals("Doe", result.getFirst().getSurname());
    }

    @Test
    public void testFilterMechanicsBySpecialization() {
        List<Mechanic> mockMechanicList = List.of(
                new Mechanic(1, "John", "Doe", "123456789", "Brakes", null)
        );
        List<MechanicOutDto> mockMechanicOutDtoList = List.of(
                new MechanicOutDto(1, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicRepository.findByNameContainingAndSurnameContainingAndSpecializationContaining("", "", "Brakes"))
                .thenReturn(mockMechanicList);
        when(modelMapper.map(mockMechanicList, new TypeToken<List<MechanicOutDto>>() {}.getType()))
                .thenReturn(mockMechanicOutDtoList);

        List<MechanicOutDto> result = mechanicService.filterMechanics("", "", "Brakes");

        assertEquals(1, result.size());
        assertEquals("Brakes", result.getFirst().getSpecialization());
    }

    @Test
    public void testFilterMechanicsByAllFilters() {
        List<Mechanic> mockMechanicList = List.of(
                new Mechanic(1, "John", "Doe", "123456789", "Brakes", null)
        );
        List<MechanicOutDto> mockMechanicOutDtoList = List.of(
                new MechanicOutDto(1, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicRepository.findByNameContainingAndSurnameContainingAndSpecializationContaining("John", "Doe", "Brakes"))
                .thenReturn(mockMechanicList);
        when(modelMapper.map(mockMechanicList, new TypeToken<List<MechanicOutDto>>() {}.getType()))
                .thenReturn(mockMechanicOutDtoList);

        List<MechanicOutDto> result = mechanicService.filterMechanics("John", "Doe", "Brakes");

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());
        assertEquals("Doe", result.getFirst().getSurname());
        assertEquals("Brakes", result.getFirst().getSpecialization());
    }

    @Test
    public void testGetMechanicOk() throws Exception {
        long mechanicId = 1L;
        Mechanic mockMechanic = new Mechanic(mechanicId, "John", "Doe", "123456789", "Brakes", null);

        when(mechanicRepository.findById(mechanicId)).thenReturn(Optional.of(mockMechanic));

        Mechanic result = mechanicService.get(mechanicId);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        verify(mechanicRepository, times(1)).findById(mechanicId);
    }

    @Test
    public void testGetMechanicNotFound() {
        long mechanicId = 99L;

        when(mechanicRepository.findById(mechanicId)).thenReturn(Optional.empty());

        assertThrows(
                com.svalero.apibikes.exception.MechanicNotFoundException.class,
                () -> mechanicService.get(mechanicId)
        );

        verify(mechanicRepository, times(1)).findById(mechanicId);
    }

    @Test
    public void testAddMechanicOk() throws Exception {
        MechanicInDto mechanicInDto = new MechanicInDto("John", "Doe", "123456789", "Brakes");
        Mechanic mechanicEntity = new Mechanic(0L, "John", "Doe", "123456789", "Brakes", null);
        Mechanic savedMechanic = new Mechanic(1L, "John", "Doe", "123456789", "Brakes", null);
        MechanicOutDto mechanicOutDto = new MechanicOutDto(1L, "John", "Doe", "123456789", "Brakes");

        when(modelMapper.map(mechanicInDto, Mechanic.class)).thenReturn(mechanicEntity);
        when(mechanicRepository.save(mechanicEntity)).thenReturn(savedMechanic);
        when(modelMapper.map(savedMechanic, MechanicOutDto.class)).thenReturn(mechanicOutDto);

        MechanicOutDto result = mechanicService.add(mechanicInDto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals("Brakes", result.getSpecialization());

        verify(mechanicRepository, times(1)).save(mechanicEntity);
    }

    @Test
    public void testModifyMechanicOk() throws Exception {
        long mechanicId = 1L;
        MechanicInDto mechanicInDto = new MechanicInDto("John", "Smith", "111222333", "Suspension");
        Mechanic existingMechanic = new Mechanic(mechanicId, "John", "Doe", "123456789", "Brakes", null);
        MechanicOutDto modifiedMechanicOutDto = new MechanicOutDto(mechanicId, "John", "Smith", "111222333", "Suspension");

        when(mechanicRepository.findById(mechanicId)).thenReturn(Optional.of(existingMechanic));
        doNothing().when(modelMapper).map(mechanicInDto, existingMechanic);
        when(modelMapper.map(existingMechanic, MechanicOutDto.class)).thenReturn(modifiedMechanicOutDto);

        MechanicOutDto result = mechanicService.modify(mechanicId, mechanicInDto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Smith", result.getSurname());
        assertEquals("Suspension", result.getSpecialization());

        verify(mechanicRepository, times(1)).findById(mechanicId);
        verify(mechanicRepository, times(1)).save(existingMechanic);
    }

    @Test
    public void testModifyMechanicNotFound() {
        long mechanicId = 99L;
        MechanicInDto mechanicInDto = new MechanicInDto("John", "Smith", "111222333", "Suspension");

        when(mechanicRepository.findById(mechanicId)).thenReturn(Optional.empty());

        assertThrows(
                com.svalero.apibikes.exception.MechanicNotFoundException.class,
                () -> mechanicService.modify(mechanicId, mechanicInDto)
        );

        verify(mechanicRepository, times(1)).findById(mechanicId);
    }

    @Test
    public void testRemoveMechanicOk() throws Exception {
        long mechanicId = 1L;
        Mechanic mockMechanic = new Mechanic(mechanicId, "John", "Doe", "123456789", "Brakes", null);

        when(mechanicRepository.findById(mechanicId)).thenReturn(Optional.of(mockMechanic));

        mechanicService.remove(mechanicId);

        verify(mechanicRepository, times(1)).findById(mechanicId);
        verify(mechanicRepository, times(1)).deleteById(mechanicId);
    }

    @Test
    public void testRemoveMechanicNotFound() {
        long mechanicId = 99L;

        when(mechanicRepository.findById(mechanicId)).thenReturn(Optional.empty());

        assertThrows(
                com.svalero.apibikes.exception.MechanicNotFoundException.class,
                () -> mechanicService.remove(mechanicId)
        );

        verify(mechanicRepository, times(1)).findById(mechanicId);
    }

}
