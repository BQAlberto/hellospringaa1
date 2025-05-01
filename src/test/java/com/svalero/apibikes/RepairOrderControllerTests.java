package com.svalero.apibikes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apibikes.config.TestSecurityConfig;
import com.svalero.apibikes.controller.GlobalExceptionHandler;
import com.svalero.apibikes.controller.RepairOrderController;
import com.svalero.apibikes.domain.RepairOrder;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.domain.dto.RepairOrderInDto;
import com.svalero.apibikes.domain.dto.RepairOrderOutDto;
import com.svalero.apibikes.exception.RepairOrderNotFoundException;
import com.svalero.apibikes.security.BikesUserDetailsService;
import com.svalero.apibikes.service.RepairOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@WebMvcTest(RepairOrderController.class)
@ActiveProfiles("test")
public class RepairOrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtEncoder jwtEncoder;

    @MockBean
    private RepairOrderService repairOrderService;

    @MockBean
    private BikesUserDetailsService bikesUserDetailsService;

    @Test
    public void testFilterRepairOrdersWithoutParametersReturnOk() throws Exception {
        List<RepairOrderOutDto> mockRepairOrders = List.of(
                new RepairOrderOutDto(1L, 6L, 7L, 5L, LocalDate.now(), 50.0, "Revision general"),
                new RepairOrderOutDto(2L, 3L, 4L, 1L, LocalDate.now(), 40.0, "Cambio cadena")
        );

        when(repairOrderService.filterRepairOrders(null, null, null)).thenReturn(mockRepairOrders);

        MvcResult response = mockMvc.perform(get("/repair-orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<RepairOrderOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Revision general", result.getFirst().getDescription());
    }

    @Test
    public void testFilterRepairOrdersByBikeReturnOk() throws Exception {
        List<RepairOrderOutDto> mockRepairOrders = List.of(
                new RepairOrderOutDto(3L, 6L, 3L, 1L, LocalDate.now(), 30.0, "Cambio cadena")
        );

        when(repairOrderService.filterRepairOrders(6L, null, null)).thenReturn(mockRepairOrders);

        MvcResult response = mockMvc.perform(get("/repair-orders")
                        .queryParam("bikeId", "6")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<RepairOrderOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals(6L, result.getFirst().getBikeId());
    }

    @Test
    public void testFilterRepairOrdersByMechanicReturnOk() throws Exception {
        List<RepairOrderOutDto> mockRepairOrders = List.of(
                new RepairOrderOutDto(4L, 5L, 9L, 3L, LocalDate.now(), 70.0, "Ajuste frenos")
        );

        when(repairOrderService.filterRepairOrders(null, 9L, null)).thenReturn(mockRepairOrders);

        MvcResult response = mockMvc.perform(get("/repair-orders")
                        .queryParam("mechanicId", "9")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<RepairOrderOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals(9L, result.getFirst().getMechanicId());
    }

    @Test
    public void testFilterRepairOrdersByWorkShopReturnOk() throws Exception {
        List<RepairOrderOutDto> mockRepairOrders = List.of(
                new RepairOrderOutDto(5L, 2L, 4L, 10L, LocalDate.now(), 45.0, "Revisión de transmision")
        );

        when(repairOrderService.filterRepairOrders(null, null, 10L)).thenReturn(mockRepairOrders);

        MvcResult response = mockMvc.perform(get("/repair-orders")
                        .queryParam("workShopId", "10")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<RepairOrderOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().getWorkShopId());
    }

    @Test
    public void testFilterRepairOrdersByAllParametersReturnOk() throws Exception {
        List<RepairOrderOutDto> mockRepairOrders = List.of(
                new RepairOrderOutDto(6L, 8L, 7L, 2L, LocalDate.now(), 55.0, "Cambio pastillas")
        );

        when(repairOrderService.filterRepairOrders(8L, 7L, 2L)).thenReturn(mockRepairOrders);

        MvcResult response = mockMvc.perform(get("/repair-orders")
                        .queryParam("bikeId", "8")
                        .queryParam("mechanicId", "7")
                        .queryParam("workShopId", "2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<RepairOrderOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals(8L, result.getFirst().getBikeId());
        assertEquals(7L, result.getFirst().getMechanicId());
        assertEquals(2L, result.getFirst().getWorkShopId());
    }

    @Test
    public void testGetRepairOrderByIdOk() throws Exception {
        RepairOrder repairOrder = new RepairOrder(1L, null, null, null, LocalDate.now(), 60.0, "Cambio cadena");

        when(repairOrderService.get(1L)).thenReturn(repairOrder);

        MvcResult response = mockMvc.perform(get("/repair-orders/{repairOrderId}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        RepairOrder result = objectMapper.readValue(response.getResponse().getContentAsString(), RepairOrder.class);
        assertEquals(60.0, result.getCost());
        assertEquals("Cambio cadena", result.getDescription());
    }

    @Test
    public void testGetRepairOrderByIdNotFound() throws Exception {
        when(repairOrderService.get(99L)).thenThrow(new RepairOrderNotFoundException());

        MvcResult response = mockMvc.perform(get("/repair-orders/{repairOrderId}", 99L))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals(404, error.getCode());
    }

    @Test
    public void testAddRepairOrderCreated() throws Exception {
        RepairOrderInDto inDto = new RepairOrderInDto(1L, 2L, 3L, LocalDate.now(), 70.0, "Cambio frenos");
        RepairOrderOutDto outDto = new RepairOrderOutDto(10L, 1L, 2L, 3L, inDto.getRepairDate(), inDto.getCost(), inDto.getDescription());

        when(repairOrderService.add(inDto)).thenReturn(outDto);

        String json = objectMapper.writeValueAsString(inDto);

        MvcResult response = mockMvc.perform(post("/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.bikeId").value(1L))
                .andExpect(jsonPath("$.description").value("Cambio frenos"))
                .andReturn();

        RepairOrderOutDto result = objectMapper.readValue(response.getResponse().getContentAsString(), RepairOrderOutDto.class);
        assertEquals("Cambio frenos", result.getDescription());
    }

    @Test
    public void testAddRepairOrderValidationError() throws Exception {
        RepairOrderInDto inDto = new RepairOrderInDto();

        String json = objectMapper.writeValueAsString(inDto);

        MvcResult response = mockMvc.perform(post("/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);

        assertEquals(400, error.getCode());
        assertEquals("Bad request", error.getMessage());
        assertTrue(error.getErrorMessages().containsKey("repairDate"));
    }

    @Test
    public void testModifyRepairOrderOk() throws Exception {
        RepairOrderInDto inDto = new RepairOrderInDto(1L, 2L, 3L, LocalDate.now(), 80.0, "Ajuste cambio");
        RepairOrderOutDto outDto = new RepairOrderOutDto(20L, 1L, 2L, 3L, inDto.getRepairDate(), inDto.getCost(), inDto.getDescription());

        when(repairOrderService.modify(20L, inDto)).thenReturn(outDto);

        String json = objectMapper.writeValueAsString(inDto);

        MvcResult response = mockMvc.perform(put("/repair-orders/{repairOrderId}", 20L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.cost").value(80.0))
                .andReturn();

        RepairOrderOutDto result = objectMapper.readValue(response.getResponse().getContentAsString(), RepairOrderOutDto.class);
        assertEquals("Ajuste cambio", result.getDescription());
    }

    @Test
    public void testModifyRepairOrderNotFound() throws Exception {
        RepairOrderInDto inDto = new RepairOrderInDto(1L, 2L, 3L, LocalDate.now(), 80.0, "Ajuste cambio");

        when(repairOrderService.modify(99L, inDto)).thenThrow(new RepairOrderNotFoundException());

        String json = objectMapper.writeValueAsString(inDto);

        MvcResult response = mockMvc.perform(put("/repair-orders/{repairOrderId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals(404, error.getCode());
    }

    @Test
    public void testModifyRepairOrderValidationError() throws Exception {
        RepairOrderInDto inDto = new RepairOrderInDto(); // Todos los campos nulos

        String json = objectMapper.writeValueAsString(inDto);

        MvcResult response = mockMvc.perform(put("/repair-orders/{repairOrderId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);

        assertEquals(400, error.getCode());
        assertEquals("Bad request", error.getMessage());
        assertTrue(error.getErrorMessages().containsKey("repairDate"));
    }


    @Test
    public void testRemoveRepairOrderOk() throws Exception {
        MvcResult response = mockMvc.perform(delete("/repair-orders/{repairOrderId}", 1L))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());
    }

    @Test
    public void testRemoveRepairOrderNotFound() throws Exception {
        doThrow(new RepairOrderNotFoundException()).when(repairOrderService).remove(99L);

        MvcResult response = mockMvc.perform(delete("/repair-orders/{repairOrderId}", 99L))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals(404, error.getCode());
    }
}
