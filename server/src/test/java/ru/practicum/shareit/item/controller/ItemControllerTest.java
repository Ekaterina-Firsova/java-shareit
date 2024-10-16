package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testCreateItem() throws Exception {
        long userId = 1L;
        ItemDto itemDto = Instancio.of(ItemDto.class)
                .create();

        Mockito.when(itemService.create(userId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        Mockito.verify(itemService).create(userId, itemDto);
    }

    @Test
    public void testUpdateItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ItemDto updatedItem = Instancio.of(ItemDto.class)
                .create();

        Mockito.when(itemService.update(userId, itemId, updatedItem)).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedItem)));

        Mockito.verify(itemService).update(userId, itemId, updatedItem);
    }

    @Test
    public void testGetAllItemsByUser() throws Exception {
        long userId = 1L;
        List<ItemDto> items = List.of(
                Instancio.of(ItemDto.class).create(),
                Instancio.of(ItemDto.class).create()
        );

        Mockito.when(itemService.getAllItemsByUser(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        Mockito.verify(itemService).getAllItemsByUser(userId);
    }

    @Test
    public void testGetItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDto = Instancio.of(ItemDto.class).create();

        Mockito.when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        Mockito.verify(itemService).getById(itemId, userId);
    }

    @Test
    public void testSearchItems() throws Exception {
        String searchText = "Item";
        List<ItemDto> items = List.of(
                Instancio.of(ItemDto.class).create(),
                Instancio.of(ItemDto.class).create()
        );

        Mockito.when(itemService.getText(searchText)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        Mockito.verify(itemService).getText(searchText);
    }

    @Test
    public void testCreateComment() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        CommentDto commentDto = Instancio.of(CommentDto.class).create();

        Mockito.when(itemService.createComment(itemId, commentDto, userId)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));

        Mockito.verify(itemService).createComment(itemId, commentDto, userId);
    }
}
