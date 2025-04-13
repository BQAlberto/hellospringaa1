package com.svalero.apibikes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apibikes.controller.BikeController;
import com.svalero.apibikes.domain.Bike;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BikeController.class)
public class BikeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/bikes")
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

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/bikes")
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

    //TODO testFilterBikeslByModelReturnOK
    //TODO testFilterBikesByBrandAndModelReturnOK

    @Test
    public void testGetBikeReturnOk() throws Exception {
        Bike mockBike = new Bike(14, "Orbea", "Alma", LocalDate.now(), LocalDate.now(), "green", 1, 1, null);

        when(bikeService.get(14)).thenReturn(mockBike);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/bikes/{bikeId}", "14")
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

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/bikes/{bikeId}", "14"))
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
        long userId = 1;
        double latitude = 1.0;
        double longitude = 2.0;
        BikeOutDto bikeOutDto = new BikeOutDto(1, brand, model, userId, latitude, longitude, "green");
        BikeRegistrationDto bikeRegistrationDto = new BikeRegistrationDto(brand, model, LocalDate.now(),
                "green", latitude, longitude);

        when(bikeService.add(userId, bikeRegistrationDto)).thenReturn(bikeOutDto);
        String requestBody = objectMapper.writeValueAsString(bikeRegistrationDto);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/bikes", userId)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").exists())
                //Se deberian añadir todos los campos no solo estos dos
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        BikeOutDto responseBikeOutDto = objectMapper.readValue(jsonResponse, new TypeReference<>(){});


        assertNotNull(responseBikeOutDto);
        assertEquals(brand, responseBikeOutDto.getBrand());
        assertEquals(model, responseBikeOutDto.getModel());
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
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/bikes", userId)
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
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/bikes", userId)
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
}
