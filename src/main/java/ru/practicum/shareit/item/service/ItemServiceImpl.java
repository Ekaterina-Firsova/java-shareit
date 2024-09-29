package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new InvalidDataException("Name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new InvalidDataException("Description cannot be empty");
        }

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(itemDto.getRequest())
                .build();

        return ItemMapper.mapToItemDto(itemRepository.save(item), List.of(), null, null);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto updatedItem) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            List<CommentDto> comments = getComments(itemId);

            if (updatedItem.getAvailable() != null) {
                item.setAvailable(updatedItem.getAvailable());
            }
            if (updatedItem.getRequest() != null) {
                item.setRequest(updatedItem.getRequest());
            }
            if (updatedItem.getName() != null) {
                item.setName(updatedItem.getName());
            }
            if (updatedItem.getDescription() != null) {
                item.setDescription(updatedItem.getDescription());
            }
            return ItemMapper.mapToItemDto(itemRepository.save(item), comments, null, null);
        }
        throw new NotFoundException("The user with ID = " + userId + " is not the owner");
    }

    @Override
    public List<ItemDto> getAll() {
        List<Item> items = itemRepository.findAll();

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = getComments(item.getId());
                    return ItemMapper.mapToItemDto(item, comments, null, null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<CommentDto> comments = getComments(item.getId());

        return ItemMapper.mapToItemDto(item, comments, null, null); // Создаем ItemDto с комментариями
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<CommentDto> comments = getComments(item.getId());

        boolean isOwner = item.getOwner().getId().equals(userId);

        LocalDateTime lastBookingDate = null;
        LocalDateTime nextBookingDate = null;

        if (isOwner) {
            lastBookingDate = getLastBookingDate(item.getId());
            nextBookingDate = getNextBookingDate(item.getId());
        }

        return ItemMapper.mapToItemDto(item, comments, lastBookingDate, nextBookingDate);
    }

    private LocalDateTime getLastBookingDate(Long itemId) {
        return bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now())
                .map(Booking::getEnd)
                .orElse(null);
    }

    private LocalDateTime getNextBookingDate(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())
                .map(Booking::getStart)
                .orElse(null);
    }

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = getComments(item.getId());
                    return ItemMapper.mapToItemDto(item, comments, null, null);
                })
                .toList();
    }

    @Override
    public List<ItemDto> getText(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        List<Item> items = itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = getComments(item.getId());
                    return ItemMapper.mapToItemDto(item, comments, null, null);
                })
                .toList();
    }


    @Override
    public CommentDto createComment(Long itemId, CommentDto commentDto, Long userId) {
        LocalDateTime today = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);

        boolean hasActiveBooking = bookings.stream()
                .anyMatch(booking -> {
                    LocalDateTime startDate = booking.getStart();
                    LocalDateTime endDate = booking.getEnd();
                    return startDate.isBefore(today) && endDate.isBefore(today);
                });

        if (!hasActiveBooking) {
            throw new InvalidDataException("User did not rent this item or the rental period has not ended");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .itemId(itemId)
                .authorId(userId)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment, user);
    }

    protected List<CommentDto> getComments(Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);

        return comments.stream()
                .map(comment -> {
                    User author = userRepository.findById(comment.getAuthorId())
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    return CommentMapper.mapToCommentDto(comment, author);
                })
                .collect(Collectors.toList());
    }

}



