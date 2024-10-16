package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mvc;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser_validEmail() throws Exception {
        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .ignore(field("id"))
                .create();

        when(userServiceImpl.create(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(userDto.getName()))
                .andExpect(jsonPath("email").value(userDto.getEmail()));

        verify(userServiceImpl).create(userDto);
    }

    @Test
    public void testCreateUser_invalidEmail_thenReturnedBadRequest() throws Exception {
        UserDto userDto = Instancio.of(UserDto.class)
                .ignore(field("id"))
                .create();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userServiceImpl, never()).create(userDto);
    }

    @Test
    void testGetById() throws Exception {
        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .create();

        when(userServiceImpl.getById(userDto.getId())).thenReturn(userDto);

        mvc.perform(get("/users/{id}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(userDto.getName()))
                .andExpect(jsonPath("email").value(userDto.getEmail()));

        verify(userServiceImpl).getById(userDto.getId());
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDto> users = List.of(
                Instancio.of(UserDto.class)
                        .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                        .create(),
                Instancio.of(UserDto.class)
                        .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                        .create()
        );

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(users);

        when(userServiceImpl.getAll()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(users.get(0).getName()))
                .andExpect(jsonPath("$[1].name").value(users.get(1).getName()));

        verify(userServiceImpl).getAll();
    }

    @Test
    void testUpdateUser() throws Exception {
        long userId = 1L;

        UserDto updatedUserDto = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        when(userServiceImpl.update(userId, updatedUserDto)).thenReturn(updatedUserDto);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated.email@example.com"));

        verify(userServiceImpl).update(userId, updatedUserDto);
    }

    @Test
    public void testDeleteUser() throws Exception {
        long userId = 1L;

        mvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userServiceImpl, times(1)).delete(userId);
    }

}