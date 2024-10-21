package ru.practicum.shareit.user.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate_ValidUser_ShouldReturnUserDto() {
        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .create();

        User user = UserMapper.mapToUser(userDto);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void testCreate_InvalidUserName_ShouldThrowException() {
        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .set(field("name"), "")
                .create();

        assertThrows(InvalidDataException.class, () -> {
            userService.create(userDto);
        });

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testCreate_InvalidUserEmail_ShouldThrowException() {
        UserDto userDto = Instancio.of(UserDto.class)
                .create();

        assertThrows(InvalidDataException.class, () -> {
            userService.create(userDto);
        });

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testCreate_BlankUserEmail_ShouldThrowException() {
        UserDto userDto = Instancio.of(UserDto.class)
                .set(field("email"), "")
                .create();

        assertThrows(InvalidDataException.class, () -> {
            userService.create(userDto);
        });

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testUpdate_ValidInput_ShouldUpdateUser() {
        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .create();

        User existingUser = User.builder()
                .id(userDto.getId())
                .name("Old Name")
                .email("old.email@example.com")
                .build();

        Mockito.when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(userDto.getId(), userDto);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        Mockito.verify(userRepository).findById(userDto.getId());
        Mockito.verify(userRepository).save(existingUser);
    }

    @Test
    public void testUpdate_UserNotFound_ShouldThrowNotFoundException() {
        long userId = 1L;
        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.update(userId, userDto);
        });

        assertEquals("User not found with id: " + userId, exception.getMessage());
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testUpdate_NullName_ShouldNotUpdateName() {
        UserDto userDto = Instancio.of(UserDto.class)
                .set(field("email"), "new.email@example.com")
                .set(field("name"), "")
                .create();

        User existingUser = User.builder()
                .id(userDto.getId())
                .name("Old Name")
                .email("old.email@example.com")
                .build();

        Mockito.when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(userDto.getId(), userDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName()); // Имя не должно измениться
        assertEquals("new.email@example.com", result.getEmail());

        Mockito.verify(userRepository).findById(userDto.getId());
        Mockito.verify(userRepository).save(existingUser);
    }

    @Test
    public void testUpdate_NullEmail_ShouldNotUpdateEmail() {
        UserDto userDto = Instancio.of(UserDto.class)
                .set(field("email"), "")
                .create();
        User existingUser = User.builder()
                .id(userDto.getId())
                .name("Old Name")
                .email("old.email@example.com")
                .build();

        Mockito.when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(userDto.getId(), userDto);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals("old.email@example.com", result.getEmail());

        Mockito.verify(userRepository).findById(userDto.getId());
        Mockito.verify(userRepository).save(existingUser);
    }

    @Test
    public void testGetAll_ShouldReturnListOfUsers() {
        List<User> users = List.of(
                Instancio.of(User.class)
                        .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                        .create(),
                Instancio.of(User.class)
                        .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                        .create()
        );

        Mockito.when(userRepository.findAll()).thenReturn(users);

        Collection<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        UserDto firstUser = result.stream().findFirst().orElse(null);
        assertNotNull(firstUser);
        assertEquals(users.get(0).getName(), firstUser.getName());
        assertEquals(users.get(0).getEmail(), firstUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetAll_ShouldReturnEmptyListIfNoUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetById_ShouldReturnUserDto_WhenUserExists() {
        User user = User.builder()
                .id(1L)
                .name("User One")
                .email("user1@example.com")
                .build();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    public void testGetById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        Long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getById(userId);
        });
        assertEquals("User not found with id: " + userId, exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    public void testDelete_ShouldCallRepositoryDeleteById() {
        long userId = 1L;

        userService.delete(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }
}