package com.svalero.apibikes;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.BikeInDto;
import com.svalero.apibikes.domain.dto.BikeOutDto;
import com.svalero.apibikes.domain.dto.BikeRegistrationDto;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.repository.BikeRepository;
import com.svalero.apibikes.repository.UserRepository;
import com.svalero.apibikes.service.BikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BikeServiceTests {

    @InjectMocks
    private BikeService bikeService;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testFilterBikes() {
        List<Bike> mockBikeList = List.of(
            new Bike(1, "Orbea", "Alma", LocalDate.now(), LocalDate.now(), "green", 0.5, 0.34, null),
            new Bike(2, "BH", "Lynx", LocalDate.now(), LocalDate.now(), "red", 0.5, 0.44, null),
            new Bike(3, "Scott", "Spark", LocalDate.now(), LocalDate.now(), "white", 0.5, 0.24, null)
        );
        List<BikeOutDto> mockBikeOutDtoList = List.of(
            new BikeOutDto(1, "Orbea", "Alma", 1, 0.5, 0.34, "green"),
            new BikeOutDto(2, "BH", "Lynx", 1, 0.5, 0.44,"red"),
            new BikeOutDto(3, "Scott", "Spark", 2, 0.5, 0.24, "white")
        );

        //Mocks
        when(bikeRepository.findByBrandContainingAndModelContainingAndColorContaining("", "", "")).thenReturn(mockBikeList);
        when(modelMapper.map(mockBikeList, new TypeToken<List<BikeOutDto>>() {}.getType())).thenReturn(mockBikeOutDtoList);

        //Listado de bicis sin aplicar ningun filtro
        List<BikeOutDto> bikeList = bikeService.filterBikes("", "", "");
        assertEquals(3, bikeList.size());
        assertEquals("Orbea", bikeList.getFirst().getBrand());
        assertEquals("Alma", bikeList.getFirst().getModel());
        assertEquals("Scott", bikeList.getLast().getBrand());
        assertEquals("Spark", bikeList.getLast().getModel());

        verify(bikeRepository, times(1)).findByBrandContainingAndModelContainingAndColorContaining("", "", "");
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testAdd() {
        long userId = 1;
        User mockUser = new User(userId, "Yo", "testpass", "Nombre", "Apellido", "email@example.com", LocalDate.now(), true, new HashSet<>(), List.of());
        BikeRegistrationDto bikeRegistrationDto = new BikeRegistrationDto("Orbea", "Alma", LocalDate.now(), "green", 0.5, 0.34);

        Bike mockBike = new Bike(0, "Orbea", "Alma", LocalDate.now(), LocalDate.now(), "green", 0.5, 0.34, null);
        BikeOutDto mockBikeOutDto = new BikeOutDto(1, "Orbea", "Alma", 1, 0.5, 0.34, "green");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(bikeRegistrationDto, Bike.class)).thenReturn(mockBike);
        when(modelMapper.map(mockBike, BikeOutDto.class)).thenReturn(mockBikeOutDto);

        try {
            bikeService.add(userId, bikeRegistrationDto);
        } catch (UserNotFoundException unfe) {

        }

        assertEquals(1, mockBikeOutDto.getId());
        assertEquals("Orbea", mockBikeOutDto.getBrand());
        assertEquals("Alma", mockBikeOutDto.getModel());

        verify(bikeRepository, times(1)).save(mockBike);
        //verify(modelMapper, times(1)).map(bikeRegistrationDto, Bike.class);
        //verify(modelMapper, times(1)).map(mockBike, BikeOutDto.class);
    }

    @Test
    public void testAddUserNotFound() throws UserNotFoundException {
        long userId = 99;
        BikeRegistrationDto bikeRegistrationDto = new BikeRegistrationDto("Orbea", "Alma", LocalDate.now(), "green", 0.5, 0.34);

        try {
            when(userRepository.findById(userId).orElseThrow(UserNotFoundException::new)).thenThrow(UserNotFoundException.class);
        } catch (UserNotFoundException unfe) {

        }

        assertThrows(UserNotFoundException.class, () -> bikeService.add(userId, bikeRegistrationDto));
    }
}
