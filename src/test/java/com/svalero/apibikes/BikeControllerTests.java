package com.svalero.apibikes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apibikes.config.TestSecurityConfig;
import com.svalero.apibikes.controller.GlobalExceptionHandler;
import com.svalero.apibikes.controller.BikeController;
import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.dto.BikeInDto;
import com.svalero.apibikes.domain.dto.BikeOutDto;
import com.svalero.apibikes.domain.dto.BikeRegistrationDto;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.exception.BikeNotFoundException;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.repository.UserRepository;
import com.svalero.apibikes.security.BikesUserDetailsService;
import com.svalero.apibikes.service.BikeService;
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


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@WebMvcTest(BikeController.class)
@ActiveProfiles("test")
public class BikeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtEncoder jwtEncoder;

    @MockBean
    private BikeService bikeService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BikesUserDetailsService bikesUserDetailsService;

    @Test
    public void testFilterBikesWithoutParametersReturnOk() throws Exception {
        List<BikeOutDto> mockBikeOutDtoList = List.of(
                new BikeOutDto(1, "Orbea", "Alma", 1, 0.5, 0.34, "green"),
                new BikeOutDto(2, "BH", "Lynx", 1, 0.5, 0.44,"red"),
                new BikeOutDto(3, "Scott", "Spark", 2, 0.5, 0.24, "white")
        );

        when(bikeService.filterBikes("", "", "")).thenReturn(mockBikeOutDtoList);

        MvcResult response = mockMvc.perform(get("/bikes")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        List<BikeOutDto> bikeListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(bikeListResponse);
        assertEquals(3, bikeListResponse.size());
        assertEquals("Orbea", bikeListResponse.getFirst().getBrand());
        assertEquals("Alma", bikeListResponse.getFirst().getModel());
    }

    @Test
    public void testFilterBikesByBrandReturnOk() throws Exception {
        List<BikeOutDto> mockBikeOutDtoList = List.of(
                new BikeOutDto(1, "Orbea", "Alma", 1, 0.5, 0.34, "green"),
                new BikeOutDto(2, "Orbea", "Oiz", 1, 0.5, 0.44,"red")
        );

        when(bikeService.filterBikes("Orbea", "", "")).thenReturn(mockBikeOutDtoList);

        MvcResult response = mockMvc.perform(get("/bikes")
                        .queryParam("brand", "Orbea")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        List<BikeOutDto> bikeListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(bikeListResponse);
        assertEquals(2, bikeListResponse.size());
        assertEquals("Orbea", bikeListResponse.getFirst().getBrand());
        assertEquals("Alma", bikeListResponse.getFirst().getModel());
    }

    @Test
    public void testFilterBikesByModelReturnOk() throws Exception {
        List<BikeOutDto> mockBikes = List.of(
                new BikeOutDto(1, "Orbea", "Alma", 1, 1.0, 2.0, "green")
        );

        when(bikeService.filterBikes("", "Alma", "")).thenReturn(mockBikes);

        MvcResult response = mockMvc.perform(get("/bikes")
                        .queryParam("model", "Alma")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<BikeOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("Alma", result.getFirst().getModel());
    }

    @Test
    public void testFilterBikesByColorReturnOk() throws Exception {
        List<BikeOutDto> mockBikes = List.of(
                new BikeOutDto(1, "Orbea", "Alma", 1, 1.0, 2.0, "green")
        );

        when(bikeService.filterBikes("", "", "green")).thenReturn(mockBikes);

        MvcResult response = mockMvc.perform(get("/bikes")
                        .queryParam("color", "green")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<BikeOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("green", result.getFirst().getColor());
    }

    @Test
    public void testFilterBikesByAllFiltersReturnOk() throws Exception {
        List<BikeOutDto> mockBikes = List.of(
                new BikeOutDto(1, "Orbea", "Alma", 1, 1.0, 2.0, "green")
        );

        when(bikeService.filterBikes("Orbea", "Alma", "green")).thenReturn(mockBikes);

        MvcResult response = mockMvc.perform(get("/bikes")
                        .queryParam("brand", "Orbea")
                        .queryParam("model", "Alma")
                        .queryParam("color", "green")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<BikeOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("Orbea", result.getFirst().getBrand());
        assertEquals("Alma", result.getFirst().getModel());
        assertEquals("green", result.getFirst().getColor());
    }

    @Test
    public void testGetBikeReturnOk() throws Exception {
        Bike mockBike = new Bike(14, "Orbea", "Alma", LocalDate.now(), LocalDate.now(), "green", 1, 1, null);

        when(bikeService.get(14)).thenReturn(mockBike);

        MvcResult response = mockMvc.perform(get("/bikes/{bikeId}", "14")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        Bike bikeResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(bikeResponse);
        assertEquals("Orbea", bikeResponse.getBrand());
        assertEquals("Alma", bikeResponse.getModel());
    }

    @Test
    public void testGetBikeNotFound() throws Exception {
        when(bikeService.get(14)).thenThrow(new BikeNotFoundException());

        MvcResult response = mockMvc.perform(get("/bikes/{bikeId}", "14"))
                .andExpect(status().isNotFound())
                .andReturn();


        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getCode());
        assertEquals("The bike does not exist", errorResponse.getMessage());
    }

    @Test
    public void testAddBikeCreated() throws Exception {
        String brand = "Orbea";
        String model = "Alma";
        long userId = 1L;
        double latitude = 1.0;
        double longitude = 2.0;
        String color = "green";
        LocalDate releaseDate = LocalDate.now();

        BikeRegistrationDto bikeRegistrationDto = new BikeRegistrationDto(
                brand, model, releaseDate, color, latitude, longitude);

        BikeOutDto bikeOutDto = new BikeOutDto(
                1L, brand, model, userId, latitude, longitude, color);

        when(bikeService.add(userId, bikeRegistrationDto)).thenReturn(bikeOutDto);
        String requestBody = objectMapper.writeValueAsString(bikeRegistrationDto);

        MvcResult response = mockMvc.perform(post("/users/{userId}/bikes", userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value((int) userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brand").value(brand))
                .andExpect(MockMvcResultMatchers.jsonPath("$.model").value(model))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(color))
                .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(latitude))
                .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(longitude))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        BikeOutDto responseDto = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(responseDto);
        assertEquals(brand, responseDto.getBrand());
        assertEquals(model, responseDto.getModel());
    }


    @Test
    public void testAddBikeUserNotFound() throws Exception {
        String brand = "Orbea";
        String model = "Alma";
        long userId = 1;
        double latitude = 1.0;
        double longitude = 2.0;
        BikeOutDto bikeOutDto = new BikeOutDto(1, brand, model, userId, latitude, longitude, "green");
        BikeRegistrationDto bikeRegistrationDto = new BikeRegistrationDto(brand, model, LocalDate.now(),
                "green", latitude, longitude);

        when(bikeService.add(userId, bikeRegistrationDto)).thenThrow(new UserNotFoundException());

        String requestBody = objectMapper.writeValueAsString(bikeRegistrationDto);
        MvcResult response = mockMvc.perform(post("/users/{userId}/bikes", userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getCode());
        assertEquals("The user does not exist", errorResponse.getMessage());
    }

    @Test
    public void testAddBikeValidationError() throws Exception {
        String brand = null;
        String model = null;
        long userId = 1;
        double latitude = 1.0;
        double longitude = 2.0;
        BikeRegistrationDto bikeRegistrationDto = new BikeRegistrationDto(brand, model, LocalDate.now(),
                "green", latitude, longitude);

        String requestBody = objectMapper.writeValueAsString(bikeRegistrationDto);
        MvcResult response = mockMvc.perform(post("/users/{userId}/bikes", userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getCode());
        assertEquals("Bad request", errorResponse.getMessage());
        assertEquals("El campo brand es obligatorio", errorResponse.getErrorMessages().get("brand"));
        assertEquals("El campo model es obligatorio", errorResponse.getErrorMessages().get("model"));
    }

    @Test
    public void testModifyBikeOk() throws Exception {
        long bikeId = 1L;
        String newBrand = "Specialized";
        String newModel = "Epic";
        String newColor = "blue";

        BikeInDto bikeInDto = new BikeInDto(newBrand, newModel, LocalDate.now(), LocalDate.now(), newColor);
        BikeOutDto modifiedBike = new BikeOutDto(bikeId, newBrand, newModel, 1L, 0.5, 0.34, newColor);

        when(bikeService.modify(bikeId, bikeInDto)).thenReturn(modifiedBike);

        String requestBody = objectMapper.writeValueAsString(bikeInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/bikes/{bikeId}", bikeId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value((int) bikeId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.brand").value(newBrand))
                .andExpect(MockMvcResultMatchers.jsonPath("$.model").value(newModel))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(newColor))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        BikeOutDto responseDto = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(responseDto);
        assertEquals(newBrand, responseDto.getBrand());
        assertEquals(newModel, responseDto.getModel());
        assertEquals(newColor, responseDto.getColor());
    }

    @Test
    public void testModifyBikeNotFound() throws Exception {
        long bikeId = 99L;
        BikeInDto bikeInDto = new BikeInDto("Orbea", "Oiz", LocalDate.now(), LocalDate.now(), "blue");

        when(bikeService.modify(bikeId, bikeInDto)).thenThrow(new BikeNotFoundException());

        String requestBody = objectMapper.writeValueAsString(bikeInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/bikes/{bikeId}", bikeId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getCode());
        assertEquals("The bike does not exist", errorResponse.getMessage());
    }

    @Test
    public void testModifyBikeValidationError() throws Exception {
        long bikeId = 1L;
        BikeInDto bikeInDto = new BikeInDto(null, null, LocalDate.now(), LocalDate.now(), "blue"); // <-- Brand y Model nulos

        String requestBody = objectMapper.writeValueAsString(bikeInDto);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/bikes/{bikeId}", bikeId)
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
        assertEquals("El campo brand es obligatorio", errorResponse.getErrorMessages().get("brand"));
        assertEquals("El campo model es obligatorio", errorResponse.getErrorMessages().get("model"));
    }

    @Test
    public void testRemoveBikeOk() throws Exception {
        long bikeId = 1L;

        // No hace falta que el servicio devuelva nada, solo que no lance excepción
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/bikes/{bikeId}", bikeId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent()) // 204
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());
    }

    @Test
    public void testRemoveBikeNotFound() throws Exception {
        long bikeId = 99L;

        doThrow(new BikeNotFoundException()).when(bikeService).remove(bikeId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/bikes/{bikeId}", bikeId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound()) // 404
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getCode());
        assertEquals("The bike does not exist", errorResponse.getMessage());
    }

}
