package com.svalero.apibikes;

import com.svalero.apibikes.domain.*;
import com.svalero.apibikes.domain.dto.RepairOrderInDto;
import com.svalero.apibikes.domain.dto.RepairOrderOutDto;
import com.svalero.apibikes.exception.RepairOrderNotFoundException;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.repository.MechanicRepository;
import com.svalero.apibikes.repository.RepairOrderRepository;
import com.svalero.apibikes.repository.UserRepository;
import com.svalero.apibikes.repository.WorkShopRepository;
import com.svalero.apibikes.service.RepairOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepairOrderServiceTests {

    @InjectMocks
    private RepairOrderService repairOrderService;

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MechanicRepository mechanicRepository;

    @Mock
    private WorkShopRepository workShopRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFilterRepairOrdersWithoutFilters() {
        List<RepairOrder> mockRepairOrderList = List.of(
                new RepairOrder(1L, null, null, null, LocalDate.now(), 30.0, "Cambio de cadena"),
                new RepairOrder(2L, null, null, null, LocalDate.now(), 20.0, "Ajuste de frenos"),
                new RepairOrder(3L, null, null, null, LocalDate.now(), 50.0, "Revisión completa")
        );

        List<RepairOrderOutDto> mockRepairOrderOutDtoList = List.of(
                new RepairOrderOutDto(1L, 0L, 0L, 0L, LocalDate.now(), 30.0, "Cambio de cadena"),
                new RepairOrderOutDto(2L, 0L, 0L, 0L, LocalDate.now(), 20.0, "Ajuste de frenos"),
                new RepairOrderOutDto(3L, 0L, 0L, 0L, LocalDate.now(), 50.0, "Revisión completa")
        );

        // Mocks
        when(repairOrderRepository.findAll()).thenReturn(mockRepairOrderList);
        when(modelMapper.map(mockRepairOrderList, new TypeToken<List<RepairOrderOutDto>>() {}.getType()))
                .thenReturn(mockRepairOrderOutDtoList);

        // Llamada al servicio sin aplicar filtros
        List<RepairOrderOutDto> repairOrderList = repairOrderService.filterRepairOrders(null, null, null);

        assertEquals(3, repairOrderList.size());
        assertEquals("Cambio de cadena", repairOrderList.getFirst().getDescription());
        assertEquals("Revisión completa", repairOrderList.getLast().getDescription());

        verify(repairOrderRepository, times(1)).findAll();
    }

    @Test
    public void testFilterRepairOrdersByBikeId() {
        Long bikeId = 1L;

        List<RepairOrder> mockRepairOrderList = List.of(
                new RepairOrder(1L, new Bike(1, "brand", "Alma", LocalDate.now(), LocalDate.now(), "green", 0.5, 0.34, null), null, null, LocalDate.now(), 30.0, "Cambio de cadena")
        );

        List<RepairOrderOutDto> mockRepairOrderOutDtoList = List.of(
                new RepairOrderOutDto(1L, bikeId, 0L, 0L, LocalDate.now(), 30.0, "Cambio de cadena")
        );

        when(repairOrderRepository.findWithFilters(bikeId, null, null)).thenReturn(mockRepairOrderList);
        when(modelMapper.map(anyList(), eq(new TypeToken<List<RepairOrderOutDto>>() {}.getType())))
                .thenReturn(mockRepairOrderOutDtoList);

        List<RepairOrderOutDto> result = repairOrderService.filterRepairOrders(bikeId, null, null);

        assertEquals(1, result.size());
        assertEquals("Cambio de cadena", result.getFirst().getDescription());

        verify(repairOrderRepository, times(1)).findWithFilters(bikeId, null, null);
    }


    @Test
    public void testFilterRepairOrdersByMechanicId() {
        Long mechanicId = 2L;

        Mechanic mechanic = new Mechanic();
        mechanic.setId(mechanicId);

        List<RepairOrder> mockRepairOrderList = List.of(
                new RepairOrder(2L, null, mechanic, null, LocalDate.now(), 20.0, "Ajuste de frenos")
        );

        List<RepairOrderOutDto> mockRepairOrderOutDtoList = List.of(
                new RepairOrderOutDto(2L, 0L, mechanicId, 0L, LocalDate.now(), 20.0, "Ajuste de frenos")
        );

        when(repairOrderRepository.findWithFilters(null, mechanicId, null)).thenReturn(mockRepairOrderList);
        when(modelMapper.map(anyList(), eq(new TypeToken<List<RepairOrderOutDto>>() {}.getType())))
                .thenReturn(mockRepairOrderOutDtoList);

        List<RepairOrderOutDto> result = repairOrderService.filterRepairOrders(null, mechanicId, null);

        assertEquals(1, result.size());
        assertEquals("Ajuste de frenos", result.getFirst().getDescription());

        verify(repairOrderRepository, times(1)).findWithFilters(null, mechanicId, null);
    }

    @Test
    public void testFilterRepairOrdersByWorkShopId() {
        Long workShopId = 3L;
        List<RepairOrder> mockRepairOrderList = List.of(
                new RepairOrder(3L, null, null, null, LocalDate.now(), 50.0, "Revisión completa")
        );
        List<RepairOrderOutDto> mockRepairOrderOutDtoList = List.of(
                new RepairOrderOutDto(3L, 0L, 0L, 3L, LocalDate.now(), 50.0, "Revisión completa")
        );

        when(repairOrderRepository.findWithFilters(null, null, workShopId)).thenReturn(mockRepairOrderList);
        when(modelMapper.map(mockRepairOrderList, new TypeToken<List<RepairOrderOutDto>>() {}.getType()))
                .thenReturn(mockRepairOrderOutDtoList);

        List<RepairOrderOutDto> result = repairOrderService.filterRepairOrders(null, null, workShopId);

        assertEquals(1, result.size());
        assertEquals("Revisión completa", result.getFirst().getDescription());

        verify(repairOrderRepository, times(1)).findWithFilters(null, null, workShopId);
    }

    @Test
    public void testFilterRepairOrdersByAllFilters() {
        Long bikeId = 1L;
        Long mechanicId = 2L;
        Long workShopId = 3L;

        List<RepairOrder> mockRepairOrderList = List.of(
                new RepairOrder(4L,
                        new Bike(1, "brand", "Alma", LocalDate.now(), LocalDate.now(), "green", 0.5, 0.34, null),
                        new Mechanic(0L, "John", "Doe", "123456789", "Brakes", null),
                        new WorkShop(1, "BikeFix", "Calle Mayor 1", "976000000", "bikefix@example.com", List.of()),
                        LocalDate.now(), 45.0, "Cambio pastillas")
        );

        List<RepairOrderOutDto> mockRepairOrderOutDtoList = List.of(
                new RepairOrderOutDto(4L, bikeId, mechanicId, workShopId, LocalDate.now(), 45.0, "Cambio pastillas")
        );

        when(repairOrderRepository.findWithFilters(bikeId, mechanicId, workShopId)).thenReturn(mockRepairOrderList);
        when(modelMapper.map(anyList(), eq(new TypeToken<List<RepairOrderOutDto>>() {}.getType())))
                .thenReturn(mockRepairOrderOutDtoList);

        List<RepairOrderOutDto> result = repairOrderService.filterRepairOrders(bikeId, mechanicId, workShopId);

        assertEquals(1, result.size());
        assertEquals("Cambio pastillas", result.getFirst().getDescription());

        verify(repairOrderRepository, times(1)).findWithFilters(bikeId, mechanicId, workShopId);
    }

    @Test
    public void testGetRepairOrderOk() throws Exception {
        long repairOrderId = 1L;
        RepairOrder mockRepairOrder = new RepairOrder(
                repairOrderId, null, null, null, LocalDate.now(), 30.0, "Cambio de cadena"
        );

        when(repairOrderRepository.findById(repairOrderId)).thenReturn(Optional.of(mockRepairOrder));

        RepairOrder result = repairOrderService.get(repairOrderId);

        assertEquals("Cambio de cadena", result.getDescription());
        assertEquals(repairOrderId, result.getId());

        verify(repairOrderRepository, times(1)).findById(repairOrderId);
    }

    @Test
    public void testGetRepairOrderNotFound() {
        long repairOrderId = 99L;

        when(repairOrderRepository.findById(repairOrderId)).thenReturn(Optional.empty());

        assertThrows(
                RepairOrderNotFoundException.class,
                () -> repairOrderService.get(repairOrderId)
        );

        verify(repairOrderRepository, times(1)).findById(repairOrderId);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testAdd() throws Exception {
        long userId = 1L;
        long mechanicId = 2L;
        long workShopId = 3L;

        User mockUser = new User(userId, "Yo", "testpass", "Nombre", "Apellido", "email@example.com", LocalDate.now(), true, new HashSet<>(), List.of());
        Mechanic mockMechanic = new Mechanic(mechanicId, "Pepe", "Mecánico", "12345678A", "Zaragoza", List.of());
        WorkShop mockWorkShop = new WorkShop(workShopId, "Taller A", "Calle Julio 123", "Zaragoza", "pepe@pepe.es", List.of());

        RepairOrderInDto repairOrderInDto = new RepairOrderInDto(userId, mechanicId, workShopId, LocalDate.now(), 50.0, "Cambio de frenos");

        RepairOrder mockRepairOrder = new RepairOrder();
        mockRepairOrder.setId(1L); // importante: asignar ID al mock
        RepairOrderOutDto mockRepairOrderOutDto = new RepairOrderOutDto(1L, userId, mechanicId, workShopId, LocalDate.now(), 50.0, "Cambio de frenos");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mechanicRepository.findById(mechanicId)).thenReturn(Optional.of(mockMechanic));
        when(workShopRepository.findById(workShopId)).thenReturn(Optional.of(mockWorkShop));
        when(modelMapper.map(repairOrderInDto, RepairOrder.class)).thenReturn(mockRepairOrder);
        when(repairOrderRepository.save(any(RepairOrder.class))).thenReturn(mockRepairOrder); // <- FIX aquí
        when(modelMapper.map(mockRepairOrder, RepairOrderOutDto.class)).thenReturn(mockRepairOrderOutDto);

        RepairOrderOutDto result = repairOrderService.add(repairOrderInDto);

        assertEquals(1L, result.getId());
        assertEquals("Cambio de frenos", result.getDescription());

        verify(repairOrderRepository, times(1)).save(any(RepairOrder.class));
    }


    @Test
    public void testModifyRepairOrderOk() throws Exception {
        long repairOrderId = 1L;

        RepairOrderInDto repairOrderInDto = new RepairOrderInDto(1L, 2L, 3L, LocalDate.now(), 40.0, "Cambio de cadena");
        RepairOrder mockRepairOrder = new RepairOrder(
                repairOrderId, null, null, null, LocalDate.now(), 30.0, "Ajuste de frenos"
        );
        RepairOrderOutDto mockRepairOrderOutDto = new RepairOrderOutDto(
                repairOrderId, 1L, 2L, 3L, LocalDate.now(), 40.0, "Cambio de cadena"
        );

        when(repairOrderRepository.findById(repairOrderId)).thenReturn(Optional.of(mockRepairOrder));
        when(modelMapper.map(mockRepairOrder, RepairOrderOutDto.class)).thenReturn(mockRepairOrderOutDto);

        RepairOrderOutDto result = repairOrderService.modify(repairOrderId, repairOrderInDto);

        assertEquals(40.0, result.getCost());
        assertEquals("Cambio de cadena", result.getDescription());

        verify(repairOrderRepository, times(1)).findById(repairOrderId);
        verify(repairOrderRepository, times(1)).save(mockRepairOrder);
    }

    @Test
    public void testModifyRepairOrderNotFound() {
        long repairOrderId = 99L;
        RepairOrderInDto repairOrderInDto = new RepairOrderInDto(1L, 2L, 3L, LocalDate.now(), 40.0, "Cambio de cadena");

        when(repairOrderRepository.findById(repairOrderId)).thenReturn(Optional.empty());

        assertThrows(
                RepairOrderNotFoundException.class,
                () -> repairOrderService.modify(repairOrderId, repairOrderInDto)
        );

        verify(repairOrderRepository, times(1)).findById(repairOrderId);
    }

    @Test
    public void testRemoveRepairOrderOk() throws Exception {
        long repairOrderId = 1L;
        RepairOrder mockRepairOrder = new RepairOrder(
                repairOrderId, null, null, null, LocalDate.now(), 40.0, "Cambio de cadena"
        );

        when(repairOrderRepository.findById(repairOrderId)).thenReturn(Optional.of(mockRepairOrder));

        repairOrderService.remove(repairOrderId);

        verify(repairOrderRepository, times(1)).findById(repairOrderId);
        verify(repairOrderRepository, times(1)).deleteById(repairOrderId);
    }

    @Test
    public void testRemoveRepairOrderNotFound() {
        long repairOrderId = 99L;

        when(repairOrderRepository.findById(repairOrderId)).thenReturn(Optional.empty());

        assertThrows(
                RepairOrderNotFoundException.class,
                () -> repairOrderService.remove(repairOrderId)
        );

        verify(repairOrderRepository, times(1)).findById(repairOrderId);
    }

}
