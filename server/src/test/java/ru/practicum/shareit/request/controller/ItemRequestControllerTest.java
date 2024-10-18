package ru.practicum.shareit.request.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRequestServiceImpl itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper.registerModule(new JavaTimeModule()); //для корректного разбора LocalDateTime
    }

    @Test
    public void testCreateRequest() throws Exception {
        long userId = 1L;
        ItemRequestDto requestDto = Instancio.of(ItemRequestDto.class).create();

        Mockito.when(itemRequestService.create(userId, requestDto)).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));

        Mockito.verify(itemRequestService).create(userId, requestDto);
    }

    @Test
    public void testGetUserRequests() throws Exception {
        long userId = 1L;
        List<ItemRequestDto> requests = List.of(
                Instancio.of(ItemRequestDto.class).create(),
                Instancio.of(ItemRequestDto.class).create());

        Mockito.when(itemRequestService.getAllRequestsByUser(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(itemRequestService).getAllRequestsByUser(userId);
    }

    @Test
    public void testGetAllRequests() throws Exception {
        long userId = 1L;
        List<ItemRequestDto> requests = List.of(
                Instancio.of(ItemRequestDto.class).create(),
                Instancio.of(ItemRequestDto.class).create());

        Mockito.when(itemRequestService.getAll(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(itemRequestService).getAll(userId);
    }

    @Test
    public void testGetRequestById() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestDto requestDto = Instancio.of(ItemRequestDto.class).create();

        Mockito.when(itemRequestService.getById(requestId)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));

        Mockito.verify(itemRequestService).getById(requestId);
    }
}