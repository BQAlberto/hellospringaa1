package com.svalero.apibikes;

import com.svalero.apibikes.domain.WorkShop;
import com.svalero.apibikes.domain.dto.WorkShopInDto;
import com.svalero.apibikes.domain.dto.WorkShopOutDto;
import com.svalero.apibikes.exception.WorkShopNotFoundException;
import com.svalero.apibikes.repository.WorkShopRepository;
import com.svalero.apibikes.service.WorkShopService;
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
public class WorkShopServiceTests {

    @InjectMocks
    private WorkShopService workShopService;

    @Mock
    private WorkShopRepository workShopRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFilterWorkShopsWithoutFiltersOk() {
        List<WorkShop> mockWorkShopList = List.of(
                new WorkShop(1L, "Genérico", "Dirección Genérica", "600123123", "mail@g.com", List.of())
        );
        List<WorkShopOutDto> mockWorkShopOutDtoList = List.of(
                new WorkShopOutDto(1L, "Genérico", "Dirección Genérica", "600123123", "mail@g.com")
        );

        when(workShopRepository.findByNameContainingAndAddressContainingAndEmailContaining("", "", ""))
                .thenReturn(mockWorkShopList);
        when(modelMapper.map(mockWorkShopList, new TypeToken<List<WorkShopOutDto>>() {}.getType()))
                .thenReturn(mockWorkShopOutDtoList);

        List<WorkShopOutDto> result = workShopService.filterWorkshops("", "", "");

        assertEquals(1, result.size());
        assertEquals("Genérico", result.getFirst().getName());
    }

    @Test
    public void testFilterWorkShopsByNameOk() {
        List<WorkShop> mockWorkShopList = List.of(
                new WorkShop(1L, "BikeFix", "Calle A", "600000000", "email@example.com", List.of())
        );
        List<WorkShopOutDto> mockWorkShopOutDtoList = List.of(
                new WorkShopOutDto(1L, "BikeFix", "Calle A", "600000000", "email@example.com")
        );

        when(workShopRepository.findByNameContainingAndAddressContainingAndEmailContaining("BikeFix", "", ""))
                .thenReturn(mockWorkShopList);
        when(modelMapper.map(mockWorkShopList, new TypeToken<List<WorkShopOutDto>>() {}.getType()))
                .thenReturn(mockWorkShopOutDtoList);

        List<WorkShopOutDto> result = workShopService.filterWorkshops("BikeFix", "", "");

        assertEquals(1, result.size());
        assertEquals("BikeFix", result.getFirst().getName());
    }

    @Test
    public void testFilterWorkShopsByAddressOk() {
        List<WorkShop> mockWorkShopList = List.of(
                new WorkShop(1L, "Taller", "Calle A", "600000000", "email@example.com", List.of())
        );
        List<WorkShopOutDto> mockWorkShopOutDtoList = List.of(
                new WorkShopOutDto(1L, "Taller", "Calle A", "600000000", "email@example.com")
        );

        when(workShopRepository.findByNameContainingAndAddressContainingAndEmailContaining("", "Calle A", ""))
                .thenReturn(mockWorkShopList);
        when(modelMapper.map(mockWorkShopList, new TypeToken<List<WorkShopOutDto>>() {}.getType()))
                .thenReturn(mockWorkShopOutDtoList);

        List<WorkShopOutDto> result = workShopService.filterWorkshops("", "Calle A", "");

        assertEquals(1, result.size());
        assertEquals("Calle A", result.getFirst().getAddress());
    }

    @Test
    public void testFilterWorkShopsByEmailOk() {
        List<WorkShop> mockWorkShopList = List.of(
                new WorkShop(1L, "Taller", "Dirección", "600000000", "mail@x.com", List.of())
        );
        List<WorkShopOutDto> mockWorkShopOutDtoList = List.of(
                new WorkShopOutDto(1L, "Taller", "Dirección", "600000000", "mail@x.com")
        );

        when(workShopRepository.findByNameContainingAndAddressContainingAndEmailContaining("", "", "mail@x.com"))
                .thenReturn(mockWorkShopList);
        when(modelMapper.map(mockWorkShopList, new TypeToken<List<WorkShopOutDto>>() {}.getType()))
                .thenReturn(mockWorkShopOutDtoList);

        List<WorkShopOutDto> result = workShopService.filterWorkshops("", "", "mail@x.com");

        assertEquals(1, result.size());
        assertEquals("mail@x.com", result.getFirst().getEmail());
    }

    @Test
    public void testFilterWorkShopsByAllFiltersOk() {
        List<WorkShop> mockWorkShopList = List.of(
                new WorkShop(1L, "BikeFix", "Calle Mayor 1", "976000000", "bikefix@example.com", List.of())
        );
        List<WorkShopOutDto> mockWorkShopOutDtoList = List.of(
                new WorkShopOutDto(1L, "BikeFix", "Calle Mayor 1", "976000000", "bikefix@example.com")
        );

        when(workShopRepository.findByNameContainingAndAddressContainingAndEmailContaining("BikeFix", "Mayor", "bikefix"))
                .thenReturn(mockWorkShopList);
        when(modelMapper.map(mockWorkShopList, new TypeToken<List<WorkShopOutDto>>() {}.getType()))
                .thenReturn(mockWorkShopOutDtoList);

        List<WorkShopOutDto> result = workShopService.filterWorkshops("BikeFix", "Mayor", "bikefix");

        assertEquals(1, result.size());
        assertEquals("BikeFix", result.getFirst().getName());
        verify(workShopRepository, times(1))
                .findByNameContainingAndAddressContainingAndEmailContaining("BikeFix", "Mayor", "bikefix");
    }

    @Test
    public void testGetWorkShopOk() throws Exception {
        WorkShop mockWorkShop = new WorkShop(1L, "FixIt", "Av. Madrid", "123456789", "fixit@example.com", List.of());

        when(workShopRepository.findById(1L)).thenReturn(Optional.of(mockWorkShop));

        WorkShop result = workShopService.get(1L);

        assertNotNull(result);
        assertEquals("FixIt", result.getName());
        verify(workShopRepository).findById(1L);
    }

    @Test
    public void testGetWorkShopNotFound() {
        long workShopId = 1L;
        when(workShopRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(WorkShopNotFoundException.class, () -> workShopService.get(1L));

        verify(workShopRepository).findById(1L);
    }

    @Test
    public void testAddWorkShopOk() {

        WorkShopInDto inputDto = new WorkShopInDto("BikeFix", "Calle Mayor", "976000000", "bikefix@example.com");
        WorkShop mappedEntity = new WorkShop(1L, "BikeFix", "Calle Mayor", "976000000", "bikefix@example.com", List.of());
        WorkShop savedEntity = new WorkShop(1L, "BikeFix", "Calle Mayor", "976000000", "bikefix@example.com", List.of());
        WorkShopOutDto expectedOutput = new WorkShopOutDto(1L, "BikeFix", "Calle Mayor", "976000000", "bikefix@example.com");

        when(modelMapper.map(inputDto, WorkShop.class)).thenReturn(mappedEntity);
        when(workShopRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, WorkShopOutDto.class)).thenReturn(expectedOutput);

        WorkShopOutDto result = workShopService.add(inputDto);

        assertEquals(expectedOutput.getName(), result.getName());
        assertEquals(expectedOutput.getEmail(), result.getEmail());

        verify(workShopRepository).save(mappedEntity);
    }

    @Test
    public void testModifyWorkShopOk() throws Exception {
        long workShopId = 1L;
        WorkShopInDto inputDto = new WorkShopInDto("NuevoNombre", "NuevaDirección", "987654321", "nuevo@mail.com");
        WorkShop existingWorkShop = new WorkShop(1L, "ViejoNombre", "ViejaDirección", "123456789", "viejo@mail.com", List.of());
        WorkShopOutDto expectedOutput = new WorkShopOutDto(1L, "NuevoNombre", "NuevaDirección", "987654321", "nuevo@mail.com");

        when(workShopRepository.findById(1L)).thenReturn(Optional.of(existingWorkShop));
        doNothing().when(modelMapper).map(inputDto, existingWorkShop);
        when(modelMapper.map(existingWorkShop, WorkShopOutDto.class)).thenReturn(expectedOutput);

        WorkShopOutDto result = workShopService.modify(1L, inputDto);

        assertEquals("NuevoNombre", result.getName());
        verify(workShopRepository).save(existingWorkShop);
    }

    @Test
    public void testModifyWorkShopNotFound() {
        long workShopId = 99L;
        WorkShopInDto inputDto = new WorkShopInDto("X", "Y", "Z", "mail");
        when(workShopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WorkShopNotFoundException.class, () -> workShopService.modify(99L, inputDto));
        verify(workShopRepository).findById(99L);
    }

    @Test
    public void testRemoveWorkShopOk() throws Exception {
        long workShopId = 1L;
        WorkShop mockWorkShop = new WorkShop(1L, "Nombre", "Dirección", "600000000", "mail@mail.com", List.of());
        when(workShopRepository.findById(1L)).thenReturn(Optional.of(mockWorkShop));

        workShopService.remove(1L);

        verify(workShopRepository).deleteById(1L);
    }

    @Test
    public void testRemoveWorkShopNotFound() {
        long workShopId = 99L;
        when(workShopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WorkShopNotFoundException.class, () -> workShopService.remove(99L));
        verify(workShopRepository).findById(99L);
    }

}
