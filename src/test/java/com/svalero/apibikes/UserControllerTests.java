package com.svalero.apibikes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apibikes.config.TestSecurityConfig;
import com.svalero.apibikes.controller.GlobalExceptionHandler;
import com.svalero.apibikes.controller.UserController;
import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.ErrorResponse;
import com.svalero.apibikes.domain.dto.UserInDto;
import com.svalero.apibikes.domain.dto.UserOutDto;
import com.svalero.apibikes.exception.UserNotFoundException;
import com.svalero.apibikes.security.BikesUserDetailsService;
import com.svalero.apibikes.service.UserService;
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
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private BikesUserDetailsService bikesUserDetailsService;

    @MockBean
    private JwtEncoder jwtEncoder;

    @Test
    public void testFilterUsersWithoutParametersReturnOk() throws Exception {
        List<UserOutDto> mockUsers = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.now())
        );

        when(userService.filterUsers("", "", "")).thenReturn(mockUsers);

        MvcResult response = mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<UserOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("johndoe", result.getFirst().getUsername());
    }

    @Test
    public void testFilterUsersByNameReturnOk() throws Exception {
        List<UserOutDto> mockUsers = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.now())
        );
        when(userService.filterUsers("John", "", "")).thenReturn(mockUsers);

        MvcResult response = mockMvc.perform(get("/users")
                        .param("name", "John")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<UserOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());
    }

    @Test
    public void testFilterUsersBySurnameReturnOk() throws Exception {
        List<UserOutDto> mockUsers = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.now())
        );
        when(userService.filterUsers("", "Doe", "")).thenReturn(mockUsers);

        MvcResult response = mockMvc.perform(get("/users")
                        .param("surname", "Doe")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<UserOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("Doe", result.getFirst().getSurname());
    }

    @Test
    public void testFilterUsersByEmailReturnOk() throws Exception {
        List<UserOutDto> mockUsers = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.now())
        );
        when(userService.filterUsers("", "", "john@example.com")).thenReturn(mockUsers);

        MvcResult response = mockMvc.perform(get("/users")
                        .param("email", "john@example.com")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<UserOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("john@example.com", result.getFirst().getEmail());
    }

    @Test
    public void testFilterUsersByAllFiltersReturnOk() throws Exception {
        List<UserOutDto> mockUsers = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.now())
        );
        when(userService.filterUsers("John", "Doe", "john@example.com")).thenReturn(mockUsers);

        MvcResult response = mockMvc.perform(get("/users")
                        .param("name", "John")
                        .param("surname", "Doe")
                        .param("email", "john@example.com")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        List<UserOutDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());
        assertEquals("Doe", result.getFirst().getSurname());
        assertEquals("john@example.com", result.getFirst().getEmail());
    }

    @Test
    public void testGetUserReturnOk() throws Exception {
        User mockUser = new User(1L, "johndoe", "password", "John", "Doe", "john@example.com", LocalDate.now(), true, new HashSet<>(), List.of());
        when(userService.get(1L)).thenReturn(mockUser);

        MvcResult response = mockMvc.perform(get("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        User result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals("johndoe", result.getUsername());
    }

    @Test
    public void testGetUserNotFound() throws Exception {
        when(userService.get(1L)).thenThrow(new UserNotFoundException());

        MvcResult response = mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(404, error.getCode());
    }

    @Test
    public void testAddUserCreated() throws Exception {
        UserInDto userInDto = new UserInDto("johndoe", "password", "John", "Doe", "john@example.com");
        UserOutDto userOutDto = new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.now());

        when(userService.add(userInDto)).thenReturn(userOutDto);

        String requestBody = objectMapper.writeValueAsString(userInDto);
        MvcResult response = mockMvc.perform(post("/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andReturn();

        UserOutDto result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals("johndoe", result.getUsername());
    }

    @Test
    public void testAddUserValidationError() throws Exception {
        UserInDto invalidUser = new UserInDto(null, null, null, null, null);
        String requestBody = objectMapper.writeValueAsString(invalidUser);

        MvcResult response = mockMvc.perform(post("/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(400, error.getCode());
    }

    @Test
    public void testModifyUserOk() throws Exception {
        UserInDto userInDto = new UserInDto("johndoe", "password", "John", "Smith", "johnsmith@example.com");
        UserOutDto modifiedUser = new UserOutDto(1L, "johndoe", "John", "Smith", "johnsmith@example.com", LocalDate.now());

        when(userService.modify(1L, userInDto)).thenReturn(modifiedUser);

        String requestBody = objectMapper.writeValueAsString(userInDto);

        MvcResult response = mockMvc.perform(put("/users/{userId}", 1L)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        UserOutDto result = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals("Smith", result.getSurname());
    }

    @Test
    public void testModifyUserNotFound() throws Exception {
        UserInDto userInDto = new UserInDto("johndoe", "password", "John", "Smith", "johnsmith@example.com");

        when(userService.modify(1L, userInDto)).thenThrow(new UserNotFoundException());

        String requestBody = objectMapper.writeValueAsString(userInDto);

        MvcResult response = mockMvc.perform(put("/users/{userId}", 1L)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(404, error.getCode());
    }

    @Test
    public void testModifyUserValidationError() throws Exception {
        UserInDto invalidUser = new UserInDto(null, null, null, null, null);
        String requestBody = objectMapper.writeValueAsString(invalidUser);

        MvcResult response = mockMvc.perform(put("/users/{userId}", 1L)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(400, error.getCode());
    }

    @Test
    public void testRemoveUserOk() throws Exception {
        MvcResult response = mockMvc.perform(delete("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());
    }

    @Test
    public void testRemoveUserNotFound() throws Exception {
        doThrow(new UserNotFoundException()).when(userService).remove(1L);

        MvcResult response = mockMvc.perform(delete("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(404, error.getCode());
    }
}

