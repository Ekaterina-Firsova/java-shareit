package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.CommentDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void testCreateItem() throws Exception {
        Long userId = 1L;

        ItemDto itemDto = Instancio.of(ItemDto.class)
                .create();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(itemDto);

        when(itemClient.post(itemDto, userId)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemClient).post(itemDto, userId);
    }

    @Test
    void testUpdateItem() throws Exception {
        Long userId = 1L;

        ItemDto updatedItemDto = Instancio.of(ItemDto.class)
                .create();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(updatedItemDto);

        when(itemClient.patch(userId, updatedItemDto)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.patch("/items/{id}", updatedItemDto.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(updatedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(updatedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(updatedItemDto.getAvailable()));

        verify(itemClient).patch(userId, updatedItemDto);
    }

    @Test
    void testGetAllItems() throws Exception {

        Long userId = 1L;

        List<ItemDto> items = List.of(
                Instancio.of(ItemDto.class).create(),
                Instancio.of(ItemDto.class).create()
        );

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(items);

        when(itemClient.getAll(userId)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(items.get(0).getName()))
                .andExpect(jsonPath("$[0].available").value(items.get(0).getAvailable()))
                .andExpect(jsonPath("$[0].description").value(items.get(0).getDescription()))
                .andExpect(jsonPath("$[1].name").value(items.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(items.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(items.get(1).getAvailable()));

        verify(itemClient).getAll(userId);
    }

    @Test
    void testGetItemById() throws Exception {
        Long userId = 1L;

        ItemDto itemDto = Instancio.of(ItemDto.class)
                .create();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(itemDto);

        when(itemClient.getById(itemDto.getId(), userId)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemClient).getById(itemDto.getId(), userId);
    }

    @Test
    void testSearchItems() throws Exception {
        // Поисковый текст
        String searchText = "example";

        List<ItemDto> items = List.of(
                Instancio.of(ItemDto.class).create(),
                Instancio.of(ItemDto.class).create()
        );

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(items);

        when(itemClient.getText(searchText)).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(items.get(0).getName()))
                .andExpect(jsonPath("$[0].available").value(items.get(0).getAvailable()))
                .andExpect(jsonPath("$[0].description").value(items.get(0).getDescription()))
                .andExpect(jsonPath("$[1].name").value(items.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(items.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(items.get(1).getAvailable()));

        verify(itemClient).getText(searchText);
    }

    @Test
    void testCreateComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(commentDto);

        when(itemClient.createComment(eq(itemId), eq(commentDto), eq(userId))).thenReturn(responseEntity);

        mvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Great item!"));

        verify(itemClient).createComment(eq(itemId), eq(commentDto), eq(userId));
    }

}