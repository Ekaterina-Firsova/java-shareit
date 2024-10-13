package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void testCreateItemRequest() throws Exception {
        Long userId = 1L;

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Ищу дрель")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(itemRequestDto);

        when(itemRequestClient.post(eq(itemRequestDto), eq(userId))).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Ищу дрель"));

        verify(itemRequestClient).post(eq(itemRequestDto), eq(userId));
    }

    @Test
    void testGetUserRequests() throws Exception {
        Long userId = 1L;

        List<ItemRequestDto> itemRequests = List.of(
                ItemRequestDto.builder().description("Нужен рубанок").build(),
                ItemRequestDto.builder().description("срочно нужен молоток").build()
        );

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(itemRequests);

        when(itemRequestClient.getAllByUser(userId)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("Нужен рубанок"))
                .andExpect(jsonPath("$[1].description").value("срочно нужен молоток"));

        verify(itemRequestClient).getAllByUser(userId);
    }

    @Test
    void testGetAllItemRequests() throws Exception {
        Long userId = 1L;

        List<ItemRequestDto> itemRequests = List.of(
                ItemRequestDto.builder().description("Мышь беспроводная").build(),
                ItemRequestDto.builder().description("Чемодан").build()
        );

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(itemRequests);

        when(itemRequestClient.getAll(userId)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("Мышь беспроводная"))
                .andExpect(jsonPath("$[1].description").value("Чемодан"));

        verify(itemRequestClient).getAll(userId);
    }

    @Test
    void testGetUserRequestById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Стремянка")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(itemRequestDto);

        when(itemRequestClient.getById(requestId, userId)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Стремянка"));

        verify(itemRequestClient).getById(requestId, userId);
    }
}