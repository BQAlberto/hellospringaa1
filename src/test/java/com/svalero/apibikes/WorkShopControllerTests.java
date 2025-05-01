package com.svalero.apibikes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apibikes.config.TestSecurityConfig;
import com.svalero.apibikes.controller.GlobalExceptionHandler;
import com.svalero.apibikes.controller.WorkShopController;
import com.svalero.apibikes.domain.WorkShop;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.domain.dto.WorkShopInDto;
import com.svalero.apibikes.domain.dto.WorkShopOutDto;
import com.svalero.apibikes.exception.WorkShopNotFoundException;
import com.svalero.apibikes.security.BikesUserDetailsService;
import com.svalero.apibikes.service.WorkShopService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@WebMvcTest(WorkShopController.class)
@ActiveProfiles("test")
public class WorkShopControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtEncoder jwtEncoder;

    @MockBean
    private WorkShopService workShopService;

    @MockBean
    private BikesUserDetailsService bikesUserDetailsService;

    @Test
    public void testFilterWorkShopsWithoutParametersReturnOk() throws Exception {
        List<WorkShopOutDto> mockWorkShops = List.of(
                new WorkShopOutDto(1L, "BikeFix", "Av. Madrid", "976123456", "bikefix@mail.com"),
                new WorkShopOutDto(2L, "FixAndGo", "Calle Mayor", "976654321", "fixgo@mail.com")
        );

        when(workShopService.filterWorkshops("", "", "")).thenReturn(mockWorkShops);

        MvcResult response = mockMvc.perform(get("/workshops")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkShopOutDto> result = objectMapper.readValue(
                response.getResponse().getContentAsString(), new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BikeFix", result.getFirst().getName());
    }

    @Test
    public void testFilterWorkShopsByNameReturnOk() throws Exception {
        List<WorkShopOutDto> mockWorkShops = List.of(
                new WorkShopOutDto(1L, "BikeFix", "Av. Madrid", "976123456", "bikefix@mail.com")
        );

        when(workShopService.filterWorkshops("BikeFix", "", "")).thenReturn(mockWorkShops);

        MvcResult response = mockMvc.perform(get("/workshops")
                        .queryParam("name", "BikeFix")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkShopOutDto> result = objectMapper.readValue(
                response.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(1, result.size());
        assertEquals("BikeFix", result.getFirst().getName());
    }

    @Test
    public void testFilterWorkShopsByAddressReturnOk() throws Exception {
        List<WorkShopOutDto> mockWorkShops = List.of(
                new WorkShopOutDto(1L, "FixAndGo", "Calle Mayor", "976123456", "fixgo@mail.com")
        );

        when(workShopService.filterWorkshops("", "Calle Mayor", "")).thenReturn(mockWorkShops);

        MvcResult response = mockMvc.perform(get("/workshops")
                        .queryParam("address", "Calle Mayor")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkShopOutDto> result = objectMapper.readValue(
                response.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(1, result.size());
        assertEquals("Calle Mayor", result.getFirst().getAddress());
    }

    @Test
    public void testFilterWorkShopsByEmailReturnOk() throws Exception {
        List<WorkShopOutDto> mockWorkShops = List.of(
                new WorkShopOutDto(1L, "TallerX", "Zona Centro", "976111111", "tallerx@mail.com")
        );

        when(workShopService.filterWorkshops("", "", "tallerx@mail.com")).thenReturn(mockWorkShops);

        MvcResult response = mockMvc.perform(get("/workshops")
                        .queryParam("email", "tallerx@mail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkShopOutDto> result = objectMapper.readValue(
                response.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(1, result.size());
        assertEquals("tallerx@mail.com", result.getFirst().getEmail());
    }

    @Test
    public void testFilterWorkShopsByAllFiltersReturnOk() throws Exception {
        List<WorkShopOutDto> mockWorkShops = List.of(
                new WorkShopOutDto(1L, "FixZone", "Centro", "976888888", "fixzone@mail.com")
        );

        when(workShopService.filterWorkshops("FixZone", "Centro", "fixzone@mail.com")).thenReturn(mockWorkShops);

        MvcResult response = mockMvc.perform(get("/workshops")
                        .queryParam("name", "FixZone")
                        .queryParam("address", "Centro")
                        .queryParam("email", "fixzone@mail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkShopOutDto> result = objectMapper.readValue(
                response.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(1, result.size());
        assertEquals("FixZone", result.getFirst().getName());
        assertEquals("Centro", result.getFirst().getAddress());
        assertEquals("fixzone@mail.com", result.getFirst().getEmail());
    }

    @Test
    public void testGetWorkShopByIdReturnOk() throws Exception {
        long workShopId = 1L;
        WorkShop mockWorkShop = new WorkShop(workShopId, "BikeFix", "Av. Madrid", "976123456", "bikefix@mail.com", List.of());

        when(workShopService.get(workShopId)).thenReturn(mockWorkShop);

        MvcResult response = mockMvc.perform(get("/workshops/{id}", workShopId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = response.getResponse().getContentAsString();
        assertTrue(json.contains("BikeFix"));
        assertTrue(json.contains("Av. Madrid"));
        assertTrue(json.contains("976123456"));
        assertTrue(json.contains("bikefix@mail.com"));
    }

    @Test
    public void testGetWorkShopByIdReturnNotFound() throws Exception {
        long workShopId = 99L;

        when(workShopService.get(workShopId)).thenThrow(new com.svalero.apibikes.exception.WorkShopNotFoundException());

        mockMvc.perform(get("/workshops/{id}", workShopId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddWorkShopCreated() throws Exception {
        WorkShopInDto workShopInDto = new WorkShopInDto("BikeFix", "Av. Madrid", "976123456", "bikefix@mail.com");
        WorkShopOutDto workShopOutDto = new WorkShopOutDto(1L, "BikeFix", "Av. Madrid", "976123456", "bikefix@mail.com");

        when(workShopService.add(workShopInDto)).thenReturn(workShopOutDto);

        String requestBody = objectMapper.writeValueAsString(workShopInDto);
        MvcResult response = mockMvc.perform(post("/workshops")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andReturn();

        WorkShopOutDto result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals("BikeFix", result.getName());
        assertEquals("Av. Madrid", result.getAddress());
    }

    @Test
    public void testAddWorkShopValidationError() throws Exception {
        WorkShopInDto invalidWorkShop = new WorkShopInDto(null, null, null, null);
        String requestBody = objectMapper.writeValueAsString(invalidWorkShop);

        MvcResult response = mockMvc.perform(post("/workshops")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(400, error.getCode());
    }

    @Test
    public void testModifyWorkShopOk() throws Exception {
        long workShopId = 1L;
        WorkShopInDto inDto = new WorkShopInDto("BikeFix", "Nueva direccion", "976000000", "newemail@mail.com");
        WorkShopOutDto outDto = new WorkShopOutDto(workShopId, "BikeFix", "Nueva direccion", "976000000", "newemail@mail.com");

        when(workShopService.modify(workShopId, inDto)).thenReturn(outDto);

        String requestBody = objectMapper.writeValueAsString(inDto);

        MvcResult response = mockMvc.perform(put("/workshops/{id}", workShopId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        WorkShopOutDto result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals("Nueva direccion", result.getAddress());
    }

    @Test
    public void testModifyWorkShopValidationError() throws Exception {
        long workShopId = 1L;
        WorkShopInDto invalidDto = new WorkShopInDto(null, null, null, null);

        String requestBody = objectMapper.writeValueAsString(invalidDto);

        MvcResult response = mockMvc.perform(put("/workshops/{id}", workShopId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(400, error.getCode());
    }

    @Test
    public void testModifyWorkShopNotFound() throws Exception {
        long workShopId = 99L;
        WorkShopInDto dto = new WorkShopInDto("NoExiste", "Direccion", "976000000", "inexistente@mail.com");

        when(workShopService.modify(eq(workShopId), any(WorkShopInDto.class)))
                .thenThrow(new WorkShopNotFoundException());

        String requestBody = objectMapper.writeValueAsString(dto);

        MvcResult response = mockMvc.perform(put("/workshops/{id}", workShopId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(404, error.getCode());
    }

    @Test
    public void testRemoveWorkShopOk() throws Exception {
        long workShopId = 1L;

        mockMvc.perform(delete("/workshops/{id}", workShopId))
                .andExpect(status().isNoContent());

        verify(workShopService, times(1)).remove(workShopId);
    }

    @Test
    public void testRemoveWorkShopNotFound() throws Exception {
        long workShopId = 99L;

        doThrow(new WorkShopNotFoundException()).when(workShopService).remove(workShopId);

        MvcResult response = mockMvc.perform(delete("/workshops/{id}", workShopId))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(404, error.getCode());
    }


}
