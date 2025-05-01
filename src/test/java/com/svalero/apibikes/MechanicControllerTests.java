package com.svalero.apibikes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apibikes.config.TestSecurityConfig;
import com.svalero.apibikes.controller.GlobalExceptionHandler;
import com.svalero.apibikes.controller.MechanicController;
import com.svalero.apibikes.domain.Mechanic;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.domain.dto.MechanicInDto;
import com.svalero.apibikes.domain.dto.MechanicOutDto;
import com.svalero.apibikes.exception.MechanicNotFoundException;
import com.svalero.apibikes.security.BikesUserDetailsService;
import com.svalero.apibikes.service.MechanicService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WebMvcTest(MechanicController.class)
@ActiveProfiles("test")
public class MechanicControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MechanicService mechanicService;

    @MockBean
    private BikesUserDetailsService bikesUserDetailsService;

    @MockBean
    private JwtEncoder jwtEncoder;


    @Test
    public void testFilterMechanicsWithoutFiltersOk() throws Exception {
        List<MechanicOutDto> mechanicOutDtoList = List.of(
                new MechanicOutDto(1L, "John", "Doe", "123456789", "Brakes"),
                new MechanicOutDto(2L, "Jane", "Smith", "987654321", "Suspension")
        );

        when(mechanicService.filterMechanics("", "", "")).thenReturn(mechanicOutDtoList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/mechanics")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        List<MechanicOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, responseList.size());
    }

    @Test
    public void testFilterMechanicsByNameOk() throws Exception {
        List<MechanicOutDto> mechanicOutDtoList = List.of(
                new MechanicOutDto(1L, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicService.filterMechanics("John", "", "")).thenReturn(mechanicOutDtoList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/mechanics")
                        .param("name", "John")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        List<MechanicOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, responseList.size());
        assertEquals("John", responseList.getFirst().getName());
    }

    @Test
    public void testFilterMechanicsBySurnameOk() throws Exception {
        List<MechanicOutDto> mechanicOutDtoList = List.of(
                new MechanicOutDto(1L, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicService.filterMechanics("", "Doe", "")).thenReturn(mechanicOutDtoList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/mechanics")
                        .param("surname", "Doe")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        List<MechanicOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, responseList.size());
        assertEquals("Doe", responseList.getFirst().getSurname());
    }

    @Test
    public void testFilterMechanicsBySpecializationOk() throws Exception {
        List<MechanicOutDto> mechanicOutDtoList = List.of(
                new MechanicOutDto(1L, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicService.filterMechanics("", "", "Brakes")).thenReturn(mechanicOutDtoList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/mechanics")
                        .param("specialization", "Brakes")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        List<MechanicOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, responseList.size());
        assertEquals("Brakes", responseList.getFirst().getSpecialization());
    }

    @Test
    public void testFilterMechanicsByAllFiltersOk() throws Exception {
        List<MechanicOutDto> mechanicOutDtoList = List.of(
                new MechanicOutDto(1L, "John", "Doe", "123456789", "Brakes")
        );

        when(mechanicService.filterMechanics("John", "Doe", "Brakes")).thenReturn(mechanicOutDtoList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/mechanics")
                        .param("name", "John")
                        .param("surname", "Doe")
                        .param("specialization", "Brakes")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        List<MechanicOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, responseList.size());
        assertEquals("John", responseList.getFirst().getName());
        assertEquals("Doe", responseList.getFirst().getSurname());
        assertEquals("Brakes", responseList.getFirst().getSpecialization());
    }

    @Test
    public void testGetMechanicOk() throws Exception {
        long mechanicId = 1L;
        MechanicOutDto mechanicOutDto = new MechanicOutDto(mechanicId, "John", "Doe", "123456789", "Brakes");

        when(mechanicService.get(mechanicId)).thenReturn(new Mechanic(mechanicId, "John", "Doe", "123456789", "Brakes", null));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/mechanics/{mechanicId}", mechanicId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Brakes"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        MechanicOutDto responseDto = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertEquals("John", responseDto.getName());
        assertEquals("Doe", responseDto.getSurname());
        assertEquals("Brakes", responseDto.getSpecialization());
    }

    @Test
    public void testGetMechanicNotFound() throws Exception {
        long mechanicId = 99L;

        when(mechanicService.get(mechanicId)).thenThrow(new MechanicNotFoundException());

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/mechanics/{mechanicId}", mechanicId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertEquals(404, errorResponse.getCode());
        assertEquals("The mechanic does not exist", errorResponse.getMessage());
    }

    @Test
    public void testAddMechanicCreated() throws Exception {
        MechanicInDto mechanicInDto = new MechanicInDto("John", "Doe", "123456789", "Brakes");
        MechanicOutDto mechanicOutDto = new MechanicOutDto(1L, "John", "Doe", "123456789", "Brakes");

        when(mechanicService.add(mechanicInDto)).thenReturn(mechanicOutDto);

        String requestBody = objectMapper.writeValueAsString(mechanicInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/mechanics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Brakes"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        MechanicOutDto responseDto = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(responseDto);
        assertEquals("John", responseDto.getName());
        assertEquals("Doe", responseDto.getSurname());
    }

    @Test
    public void testAddMechanicValidationError() throws Exception {
        MechanicInDto invalidMechanicInDto = new MechanicInDto(null, null, "123456789", "Brakes"); // Name y Surname nulos

        String requestBody = objectMapper.writeValueAsString(invalidMechanicInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/mechanics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getCode());
        assertEquals("Bad request", errorResponse.getMessage());
        assertTrue(errorResponse.getErrorMessages().containsKey("name"));
        assertTrue(errorResponse.getErrorMessages().containsKey("surname"));
    }

    @Test
    public void testModifyMechanicOk() throws Exception {
        long mechanicId = 1L;
        MechanicInDto mechanicInDto = new MechanicInDto("John", "Smith", "987654321", "Suspension");
        MechanicOutDto mechanicOutDto = new MechanicOutDto(mechanicId, "John", "Smith", "987654321", "Suspension");

        when(mechanicService.modify(mechanicId, mechanicInDto)).thenReturn(mechanicOutDto);

        String requestBody = objectMapper.writeValueAsString(mechanicInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/mechanics/{mechanicId}", mechanicId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value((int) mechanicId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value("Suspension"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        MechanicOutDto responseDto = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(responseDto);
        assertEquals("John", responseDto.getName());
        assertEquals("Smith", responseDto.getSurname());
        assertEquals("Suspension", responseDto.getSpecialization());
    }

    @Test
    public void testModifyMechanicNotFound() throws Exception {
        long mechanicId = 99L;
        MechanicInDto mechanicInDto = new MechanicInDto("John", "Smith", "987654321", "Suspension");

        when(mechanicService.modify(mechanicId, mechanicInDto)).thenThrow(new MechanicNotFoundException());

        String requestBody = objectMapper.writeValueAsString(mechanicInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/mechanics/{mechanicId}", mechanicId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertEquals(404, errorResponse.getCode());
        assertEquals("The mechanic does not exist", errorResponse.getMessage());
    }

    @Test
    public void testModifyMechanicValidationError() throws Exception {
        long mechanicId = 1L;
        MechanicInDto invalidMechanicInDto = new MechanicInDto(null, null, "987654321", "Suspension"); // Name y Surname nulos

        String requestBody = objectMapper.writeValueAsString(invalidMechanicInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/mechanics/{mechanicId}", mechanicId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getCode());
        assertEquals("Bad request", errorResponse.getMessage());
        assertTrue(errorResponse.getErrorMessages().containsKey("name"));
        assertTrue(errorResponse.getErrorMessages().containsKey("surname"));
    }

    @Test
    public void testRemoveMechanicOk() throws Exception {
        long mechanicId = 1L;

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/mechanics/{mechanicId}", mechanicId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());
    }

    @Test
    public void testRemoveMechanicNotFound() throws Exception {
        long mechanicId = 99L;

        doThrow(new MechanicNotFoundException()).when(mechanicService).remove(mechanicId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/mechanics/{mechanicId}", mechanicId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertEquals(404, errorResponse.getCode());
        assertEquals("The mechanic does not exist", errorResponse.getMessage());
    }

}
