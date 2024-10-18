package ru.practicum.shareit.request.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRequest_UserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = Instancio.of(ItemRequestDto.class).create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.create(userId, itemRequestDto);
        });

        assertEquals("User not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testCreateRequest_ValidInput_ShouldSaveRequest() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto itemRequestDto = Instancio.of(ItemRequestDto.class).create();
        User user = Instancio.of(User.class).set(field(User::getId), userId).create();

        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(user)
                .created(now)
                .build();

        ItemRequest savedItemRequest = ItemRequest.builder()
                .id(1L)
                .description(itemRequestDto.getDescription())
                .requester(user)
                .created(now)
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(savedItemRequest);

        ItemRequestDto result = itemRequestService.create(userId, itemRequestDto);

        assertNotNull(result);
        assertEquals(savedItemRequest.getDescription(), result.getDescription());
        assertEquals(savedItemRequest.getRequester().getId(), result.getRequester().getId());

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void testGetAllRequestsByUser_NoRequests_ShouldReturnEmptyList() {
        Long userId = 1L;

        Mockito.when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllRequestsByUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequester_IdOrderByCreatedDesc(userId);
    }

    @Test
    public void testGetAllRequestsByUser_WithRequests_ShouldReturnRequestList() {
        Long userId = 1L;

        ItemRequest request1 = Instancio.of(ItemRequest.class)
                .set(field(ItemRequest::getId), 1L)
                .create();
        ItemRequest request2 = Instancio.of(ItemRequest.class)
                .set(field(ItemRequest::getId), 2L)
                .create();

        List<ItemRequest> itemRequests = List.of(request1, request2);

        Mockito.when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId))
                .thenReturn(itemRequests);

        List<ItemRequestDto> result = itemRequestService.getAllRequestsByUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(request1.getId(), result.get(0).getId());
        assertEquals(request2.getId(), result.get(1).getId());

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequester_IdOrderByCreatedDesc(userId);
    }

    @Test
    public void testGetAll_NoRequests_ShouldReturnEmptyList() {
        Long userId = 1L;

        Mockito.when(itemRequestRepository.findAllByOrderByCreatedDesc())
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAll(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByOrderByCreatedDesc();
    }

    @Test
    public void testGetAll_WithRequests_ShouldReturnRequestList() {
        Long userId = 1L;

        ItemRequest request1 = Instancio.of(ItemRequest.class)
                .set(field(ItemRequest::getId), 1L)
                .create();
        ItemRequest request2 = Instancio.of(ItemRequest.class)
                .set(field(ItemRequest::getId), 2L)
                .create();

        List<ItemRequest> itemRequests = List.of(request1, request2);

        Mockito.when(itemRequestRepository.findAllByOrderByCreatedDesc())
                .thenReturn(itemRequests);

        List<ItemRequestDto> result = itemRequestService.getAll(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(request1.getId(), result.get(0).getId());
        assertEquals(request2.getId(), result.get(1).getId());

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByOrderByCreatedDesc();
    }

    @Test
    public void testGetById_RequestExists_ShouldReturnRequestDto() {
        Long requestId = 1L;

        ItemRequest itemRequest = Instancio.of(ItemRequest.class)
                .set(field(ItemRequest::getId), requestId)
                .create();

        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getById(requestId);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(requestId);
    }

    @Test
    public void testGetById_RequestNotFound_ShouldThrowNotFoundException() {
        Long requestId = 1L;

        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getById(requestId);
        });

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(requestId);
    }

}