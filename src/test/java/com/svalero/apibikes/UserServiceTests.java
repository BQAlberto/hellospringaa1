package com.svalero.apibikes;

import com.svalero.apibikes.domain.User;
import com.svalero.apibikes.domain.dto.UserInDto;
import com.svalero.apibikes.domain.dto.UserOutDto;
import com.svalero.apibikes.repository.UserRepository;
import com.svalero.apibikes.service.UserService;
import org.h2.engine.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFilterUsersWithoutFilters() {
        List<User> mockUserList = List.of(
                new User(1L, "johndoe", "password123", "John", "Doe", "john@example.com",
                        LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>())
        );
        List<UserOutDto> mockUserOutDtoList = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.of(1990, 1, 1))
        );

        when(userRepository.findByNameContainingAndSurnameContainingAndEmailContaining("", "", ""))
                .thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType()))
                .thenReturn(mockUserOutDtoList);

        List<UserOutDto> result = userService.filterUsers("", "", "");

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());

        verify(userRepository, times(1))
                .findByNameContainingAndSurnameContainingAndEmailContaining("", "", "");
    }


    @Test
    public void testFilterUsersByName() {
        List<User> mockUserList = List.of(
                new User(1L, "johndoe", "password123", "John", "Doe", "john@example.com",
                        LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>())
        );
        List<UserOutDto> mockUserOutDtoList = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.of(1990, 1, 1))
        );

        when(userRepository.findByNameContainingAndSurnameContainingAndEmailContaining("John", "", ""))
                .thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType()))
                .thenReturn(mockUserOutDtoList);

        List<UserOutDto> result = userService.filterUsers("John", "", "");

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());

        verify(userRepository, times(1))
                .findByNameContainingAndSurnameContainingAndEmailContaining("John", "", "");
    }

    @Test
    public void testFilterUsersBySurname() {
        List<User> mockUserList = List.of(
                new User(1L, "johndoe", "password123", "John", "Doe", "john@example.com",
                        LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>())
        );
        List<UserOutDto> mockUserOutDtoList = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.of(1990, 1, 1))
        );

        when(userRepository.findByNameContainingAndSurnameContainingAndEmailContaining("", "Doe", ""))
                .thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType()))
                .thenReturn(mockUserOutDtoList);

        List<UserOutDto> result = userService.filterUsers("", "Doe", "");

        assertEquals(1, result.size());
        assertEquals("Doe", result.getFirst().getSurname());

        verify(userRepository, times(1))
                .findByNameContainingAndSurnameContainingAndEmailContaining("", "Doe", "");
    }

    @Test
    public void testFilterUsersByEmail() {
        List<User> mockUserList = List.of(
                new User(1L, "johndoe", "password123", "John", "Doe", "john@example.com",
                        LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>())
        );
        List<UserOutDto> mockUserOutDtoList = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.of(1990, 1, 1))
        );

        when(userRepository.findByNameContainingAndSurnameContainingAndEmailContaining("", "", "john@example.com"))
                .thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType()))
                .thenReturn(mockUserOutDtoList);

        List<UserOutDto> result = userService.filterUsers("", "", "john@example.com");

        assertEquals(1, result.size());
        assertEquals("john@example.com", result.getFirst().getEmail());

        verify(userRepository, times(1))
                .findByNameContainingAndSurnameContainingAndEmailContaining("", "", "john@example.com");
    }

    @Test
    public void testFilterUsersByAllFilters() {
        List<User> mockUserList = List.of(
                new User(1L, "johndoe", "password123", "John", "Doe", "john@example.com",
                        LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>())
        );
        List<UserOutDto> mockUserOutDtoList = List.of(
                new UserOutDto(1L, "johndoe", "John", "Doe", "john@example.com", LocalDate.of(1990, 1, 1))
        );

        when(userRepository.findByNameContainingAndSurnameContainingAndEmailContaining("John", "Doe", "john@example.com"))
                .thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType()))
                .thenReturn(mockUserOutDtoList);

        List<UserOutDto> result = userService.filterUsers("John", "Doe", "john@example.com");

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());
        assertEquals("Doe", result.getFirst().getSurname());
        assertEquals("john@example.com", result.getFirst().getEmail());

        verify(userRepository, times(1))
                .findByNameContainingAndSurnameContainingAndEmailContaining("John", "Doe", "john@example.com");
    }

    @Test
    public void testGetUserOk() throws Exception {
        long userId = 1L;
        User mockUser = new User(userId, "johndoe", "password123", "John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = userService.get(userId);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("John", result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserNotFound() {
        long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                com.svalero.apibikes.exception.UserNotFoundException.class,
                () -> userService.get(userId)
        );

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testAddUserOk() {
        UserInDto userInDto = new UserInDto("johndoe", "password123", "John", "Doe", "john@example.com");
        User savedUser = new User(1L, "johndoe", "encodedPassword", "John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>());

        when(passwordEncoder.encode(userInDto.getPassword())).thenReturn("encodedPassword");

        UserOutDto result = userService.add(userInDto);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals("john@example.com", result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testModifyUserOk() throws Exception {
        long userId = 1L;
        UserInDto userInDto = new UserInDto("johndoe", "newpassword", "John", "Smith", "johnsmith@example.com");
        User existingUser = new User(userId, "johndoe", "password123", "John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>());
        UserOutDto modifiedUserOutDto = new UserOutDto(userId, "johndoe", "John", "Smith", "johnsmith@example.com", LocalDate.of(1990,1,1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(modelMapper).map(userInDto, existingUser);
        when(modelMapper.map(existingUser, UserOutDto.class)).thenReturn(modifiedUserOutDto);

        UserOutDto result = userService.modify(userId, userInDto);

        assertNotNull(result);
        assertEquals("Smith", result.getSurname());
        assertEquals("johnsmith@example.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testModifyUserNotFound() {
        long userId = 99L;
        UserInDto userInDto = new UserInDto("johndoe", "password123", "John", "Doe", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                com.svalero.apibikes.exception.UserNotFoundException.class,
                () -> userService.modify(userId, userInDto)
        );

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testRemoveUserOk() throws Exception {
        long userId = 1L;
        User mockUser = new User(userId, "johndoe", "password123", "John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), true, new HashSet<>(), new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.remove(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testRemoveUserNotFound() {
        long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                com.svalero.apibikes.exception.UserNotFoundException.class,
                () -> userService.remove(userId)
        );

        verify(userRepository, times(1)).findById(userId);
    }
}
