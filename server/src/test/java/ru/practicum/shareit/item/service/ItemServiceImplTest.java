package ru.practicum.shareit.item.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateItem_ValidInput_ShouldSaveItem() {
        Long userId = 1L;
        Long requestId = 2L;
        User user = Instancio.of(User.class)
                .set(field(User::getId), userId)
                .create();
        ItemRequest itemRequest = Instancio.of(ItemRequest.class)
                .set(field(ItemRequest::getId), requestId)
                .create();
        ItemDto itemDto = Instancio.of(ItemDto.class)
                .set(field(ItemDto::getRequestId), requestId)
                .create();
        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .request(itemRequest)
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(userId, itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(requestId);
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));
    }

    @Test
    public void testCreateItem_EmptyName_ShouldThrowInvalidDataException() {
        Long userId = 1L;
        ItemDto itemDto = Instancio.of(ItemDto.class)
                .ignore(field(ItemDto::getName))
                .create();

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            itemService.create(userId, itemDto);
        });

        assertEquals("Name cannot be empty", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    public void testCreateItem_EmptyDescription_ShouldThrowInvalidDataException() {
        Long userId = 1L;
        ItemDto itemDto = Instancio.of(ItemDto.class)
                .ignore(field(ItemDto::getDescription))
                .create();

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            itemService.create(userId, itemDto);
        });

        assertEquals("Description cannot be empty", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    public void testCreateItem_UserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        ItemDto itemDto = Instancio.of(ItemDto.class)
                .create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.create(userId, itemDto);
        });

        assertEquals("User is not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    public void testCreateItem_RequestNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 2L;
        User user = Instancio.of(User.class)
                .set(field(User::getId), userId)
                .create();
        ItemDto itemDto = Instancio.of(ItemDto.class)
                .set(field(ItemDto::getRequestId), requestId)
                .create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.create(userId, itemDto);
        });

        assertEquals("Request is not found", exception.getMessage());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(requestId);
    }

    @Test
    public void testUpdateItem_ValidInput_ShouldUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        Long authorId = 2L;

        Item originalItem = Instancio.of(Item.class)
                .set(field(Item::getId), itemId)
                .set(field(Item::getOwner), Instancio.of(User.class).set(field(User::getId), userId).create())
                .create();

        ItemDto updatedItemDto = Instancio.of(ItemDto.class)
                .set(field(ItemDto::getName), "Updated Item Name")
                .set(field(ItemDto::getDescription), "Updated Description")
                .set(field(ItemDto::getAvailable), true)
                .create();

        Comment comment = Instancio.of(Comment.class)
                .set(field(Comment::getAuthorId), authorId)
                .create();
        User author = Instancio.of(User.class)
                .set(field(User::getId), authorId)
                .create();
        CommentDto commentDto = CommentMapper.mapToCommentDto(comment, author);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(originalItem));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));
        Mockito.when(userRepository.findById(authorId)).thenReturn(Optional.of(author));

        ItemDto result = itemService.update(userId, itemId, updatedItemDto);

        assertNotNull(result);
        assertEquals("Updated Item Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.getAvailable());

        assertEquals(1, result.getComments().size());
        assertEquals(commentDto.getId(), result.getComments().get(0).getId());
        assertEquals(commentDto.getText(), result.getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.getComments().get(0).getAuthorName());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));

        Mockito.verify(commentRepository, Mockito.times(1)).findByItemId(itemId);
        Mockito.verify(userRepository, Mockito.times(1)).findById(authorId);
    }

    @Test
    public void testUpdateItem_UserNotOwner_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        Long otherUserId = 2L;

        // Создаем элемент с другим владельцем
        Item originalItem = Instancio.of(Item.class)
                .set(field(Item::getId), itemId)
                .set(field(Item::getOwner), Instancio.of(User.class).set(field(User::getId), otherUserId).create())
                .create();

        ItemDto updatedItemDto = Instancio.of(ItemDto.class).create();

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(originalItem));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.update(userId, itemId, updatedItemDto);
        });

        assertEquals("The user with ID = " + userId + " is not the owner", exception.getMessage());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));

        Mockito.verify(commentRepository, Mockito.never()).findByItemId(itemId);
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    public void testGetItemById_Owner_ShouldReturnItemWithBookings() {
        Long itemId = 1L;
        Long userId = 1L;
        Long authorId = 2L;

        Item item = Instancio.of(Item.class)
                .set(field(Item::getId), itemId)
                .set(field(Item::getOwner), Instancio.of(User.class).set(field(User::getId), userId).create())
                .create();

        Comment comment = Instancio.of(Comment.class)
                .set(field(Comment::getAuthorId), authorId)
                .create();
        User author = Instancio.of(User.class)
                .set(field(User::getId), authorId)
                .create();
        CommentDto commentDto = CommentMapper.mapToCommentDto(comment, author);

        LocalDateTime lastBookingDate = LocalDateTime.now().minusDays(2);
        LocalDateTime nextBookingDate = LocalDateTime.now().plusDays(2);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Mockito.when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));
        Mockito.when(userRepository.findById(authorId)).thenReturn(Optional.of(author));

        Mockito.when(bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(Mockito.eq(itemId), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(Instancio.of(Booking.class).set(field(Booking::getEnd), lastBookingDate).create()));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(Mockito.eq(itemId), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(Instancio.of(Booking.class).set(field(Booking::getStart), nextBookingDate).create()));

        ItemDto result = itemService.getById(itemId, userId);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());

        assertEquals(1, result.getComments().size());
        assertEquals(commentDto.getText(), result.getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.getComments().get(0).getAuthorName());

        assertEquals(lastBookingDate, result.getLastBooking());
        assertEquals(nextBookingDate, result.getNextBooking());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verify(commentRepository, Mockito.times(1)).findByItemId(itemId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findFirstByItemIdAndEndBeforeOrderByEndDesc(Mockito.eq(itemId), Mockito.any(LocalDateTime.class));
        Mockito.verify(bookingRepository, Mockito.times(1)).findFirstByItemIdAndStartAfterOrderByStartAsc(Mockito.eq(itemId), Mockito.any(LocalDateTime.class));
    }

    @Test
    public void testGetItemById_NotOwner_ShouldReturnItemWithoutBookings() {
        Long itemId = 1L;
        Long userId = 2L; // Пользователь не владелец
        Long ownerId = 1L;
        Long authorId = 3L;

        Item item = Instancio.of(Item.class)
                .set(field(Item::getId), itemId)
                .set(field(Item::getOwner), Instancio.of(User.class).set(field(User::getId), ownerId).create())
                .create();

        Comment comment = Instancio.of(Comment.class)
                .set(field(Comment::getAuthorId), authorId)
                .create();
        User author = Instancio.of(User.class)
                .set(field(User::getId), authorId)
                .create();
        CommentDto commentDto = CommentMapper.mapToCommentDto(comment, author);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Mockito.when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));
        Mockito.when(userRepository.findById(authorId)).thenReturn(Optional.of(author));

        ItemDto result = itemService.getById(itemId, userId);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());

        assertEquals(1, result.getComments().size());
        assertEquals(commentDto.getText(), result.getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.getComments().get(0).getAuthorName());

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verify(commentRepository, Mockito.times(1)).findByItemId(itemId);
        Mockito.verify(bookingRepository, Mockito.never()).findFirstByItemIdAndEndBeforeOrderByEndDesc(Mockito.anyLong(), Mockito.any(LocalDateTime.class));
        Mockito.verify(bookingRepository, Mockito.never()).findFirstByItemIdAndStartAfterOrderByStartAsc(Mockito.anyLong(), Mockito.any(LocalDateTime.class));
    }

    @Test
    public void testGetText_EmptyText_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.getText("");

        assertTrue(result.isEmpty());

        result = itemService.getText(null);
        assertTrue(result.isEmpty());

        Mockito.verify(itemRepository, Mockito.never()).findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testGetText_ValidText_ShouldReturnMatchingItems() {
        String searchText = "test";
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        Long authorId = 3L;

        Item item1 = Instancio.of(Item.class)
                .set(field(Item::getId), itemId1)
                .set(field(Item::getName), "Test Item 1")
                .create();

        Item item2 = Instancio.of(Item.class)
                .set(field(Item::getId), itemId2)
                .set(field(Item::getDescription), "Description with test keyword")
                .create();

        Comment comment1 = Instancio.of(Comment.class)
                .set(field(Comment::getAuthorId), authorId)
                .create();
        CommentDto commentDto1 = CommentMapper.mapToCommentDto(comment1, Instancio.of(User.class).set(field(User::getId), authorId).create());

        Comment comment2 = Instancio.of(Comment.class)
                .set(field(Comment::getAuthorId), authorId)
                .create();
        CommentDto commentDto2 = CommentMapper.mapToCommentDto(comment2, Instancio.of(User.class).set(field(User::getId), authorId).create());

        Mockito.when(itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText))
                .thenReturn(List.of(item1, item2));

        Mockito.when(commentRepository.findByItemId(itemId1)).thenReturn(List.of(comment1));
        Mockito.when(commentRepository.findByItemId(itemId2)).thenReturn(List.of(comment2));

        Mockito.when(userRepository.findById(authorId)).thenReturn(Optional.of(Instancio.of(User.class).set(field(User::getId), authorId).create()));

        List<ItemDto> result = itemService.getText(searchText);

        assertNotNull(result);
        assertEquals(2, result.size());

        ItemDto itemDto1 = result.get(0);
        assertEquals(item1.getName(), itemDto1.getName());
        assertEquals(item1.getDescription(), itemDto1.getDescription());
        assertEquals(1, itemDto1.getComments().size());
        assertEquals(commentDto1.getText(), itemDto1.getComments().get(0).getText());

        ItemDto itemDto2 = result.get(1);
        assertEquals(item2.getName(), itemDto2.getName());
        assertEquals(item2.getDescription(), itemDto2.getDescription());
        assertEquals(1, itemDto2.getComments().size());
        assertEquals(commentDto2.getText(), itemDto2.getComments().get(0).getText());

        Mockito.verify(itemRepository, Mockito.times(1)).findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText);
        Mockito.verify(commentRepository, Mockito.times(1)).findByItemId(itemId1);
        Mockito.verify(commentRepository, Mockito.times(1)).findByItemId(itemId2);
    }

    @Test
    public void testCreateComment_UserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = Instancio.of(CommentDto.class).create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.createComment(itemId, commentDto, userId);
        });

        assertEquals("User not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    public void testCreateComment_ItemNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = Instancio.of(CommentDto.class).create();
        User user = Instancio.of(User.class).set(field(User::getId), userId).create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.createComment(itemId, commentDto, userId);
        });

        assertEquals("Item not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
    }

    @Test
    public void testCreateComment_NoValidBooking_ShouldThrowInvalidDataException() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = Instancio.of(CommentDto.class).create();
        User user = Instancio.of(User.class).set(field(User::getId), userId).create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(Instancio.of(Item.class).create()));
        Mockito.when(bookingRepository.findByItemIdAndBookerId(itemId, userId)).thenReturn(List.of());

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            itemService.createComment(itemId, commentDto, userId);
        });

        assertEquals("User did not rent this item or the rental period has not ended", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findByItemIdAndBookerId(itemId, userId);
    }

    @Test
    public void testCreateComment_ValidBooking_ShouldSaveComment() {
        Long userId = 1L;
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = Instancio.of(CommentDto.class).create();
        User user = Instancio.of(User.class).set(field(User::getId), userId).create();
        Booking booking = Instancio.of(Booking.class)
                .set(field(Booking::getStart), now.minusDays(5))
                .set(field(Booking::getEnd), now.minusDays(2))
                .create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(Instancio.of(Item.class).create()));
        Mockito.when(bookingRepository.findByItemIdAndBookerId(itemId, userId)).thenReturn(List.of(booking));

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .itemId(itemId)
                .authorId(userId)
                .created(LocalDateTime.now())
                .build();

        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(itemId, commentDto, userId);

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findByItemIdAndBookerId(itemId, userId);
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));
    }


}