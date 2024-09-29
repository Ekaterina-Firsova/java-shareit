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


import java.time.LocalDate;
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

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(itemDto.getRequest())
                .build();

        return ItemMapper.mapToItemDto(itemRepository.save(item), List.of());
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
            return ItemMapper.mapToItemDto(itemRepository.save(item), comments);
        }
        throw new NotFoundException("The user with ID = " + userId + " is not the owner");
    }


    @Override
    public List<ItemDto> getAll() {
        List<Item> items = itemRepository.findAll(); // Извлекаем все элементы

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = getComments(item.getId()); // Получаем комментарии для каждого элемента с информацией о пользователе
                    return ItemMapper.mapToItemDto(item, comments); // Создаем ItemDto с комментариями
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found")); // Извлекаем элемент

        List<CommentDto> comments = getComments(item.getId()); // Получаем комментарии с информацией о пользователе

        return ItemMapper.mapToItemDto(item, comments); // Создаем ItemDto с комментариями
    }

    @Override
    public List<ItemDto> getAllFromUser(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId); // Извлекаем элементы владельца

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = getComments(item.getId()); // Извлекаем комментарии с авторами для каждого элемента
                    return ItemMapper.mapToItemDto(item, comments); // Составляем ItemDto с комментариями
                })
                .toList();
    }

    @Override
    public List<ItemDto> getText(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        List<Item> items = itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);

//        return items.stream()
//                .map(item -> {
//                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()) // Получаем комментарии для каждого элемента
//                            .stream()
//                            .map(CommentMapper::mapToCommentDto)
//                            .toList();
//                    return ItemMapper.mapToItemDto(item, comments); // Создаем ItemDto с комментариями
//                })
//                .toList();
        return items.stream()
                .map(item -> {
                    // Получаем комментарии для каждого элемента
                    List<CommentDto> comments = getComments(item.getId());
                    return ItemMapper.mapToItemDto(item, comments); // Создаем ItemDto с комментариями
                })
                .toList();
    }


    @Override
    public CommentDto createComment(Long itemId, CommentDto commentDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);

        LocalDate today = LocalDate.now();

        boolean hasActiveBooking = bookings.stream()
                .anyMatch(booking -> {
                    LocalDate startDate = booking.getStart().toLocalDate();
                    LocalDate endDate = booking.getEnd().toLocalDate();
                    // Проверка, что аренда была завершена до текущего дня
                    return startDate.isBefore(today) && endDate.isBefore(today) && !startDate.isEqual(endDate);
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
                            .orElseThrow(() -> new NotFoundException("User not found")); // Получаем пользователя по ID автора
                    return CommentMapper.mapToCommentDto(comment, author); // Передаем найденного пользователя в маппер
                })
                .collect(Collectors.toList());
    }

}
