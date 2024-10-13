package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    private UserClient userClient;

    @Test
    void testGetUser() throws Exception {
        long userId = 1L;
        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userClient).getById(userId);
    }

    @Test
    void testCreate() throws Exception {
        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .ignore(field("id"))
                .create();

        when(userClient.create(userDto)).thenReturn(ResponseEntity.ok(userDto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).create(userDto);
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

         when(userClient.getAll()).thenReturn(responseEntity);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(users.get(0).getName()))
                .andExpect(jsonPath("$[1].name").value(users.get(1).getName()));

        verify(userClient).getAll();
    }

    @Test
    void testUpdateUser() throws Exception {
        long userId = 1L;

        UserDto updatedUserDto = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(updatedUserDto);

         when(userClient.patch(userId, updatedUserDto)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedUserDto))) // Преобразуем DTO в JSON
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated.email@example.com"));

        verify(userClient).patch(userId, updatedUserDto);
    }
}
