package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
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
            List<CommentDto> comments = commentRepository.findByItemId(itemId)
                    .stream()
                    .map(CommentMapper::mapToCommentDto)
                    .collect(Collectors.toList());

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
                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()) // Извлекаем комментарии для каждого элемента
                            .stream()
                            .map(CommentMapper::mapToCommentDto)
                            .toList();
                    return ItemMapper.mapToItemDto(item, comments); // Составляем ItemDto с комментариями
                })
                .toList();
    }

    @Override
    public ItemDto getById(Long id) {
        List<CommentDto> comments = commentRepository.findByItemId(id)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.mapToItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found")), comments);
    }

    @Override
    public List<ItemDto> getAllFromUser(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId); // Извлекаем элементы владельца

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()) // Извлекаем комментарии для каждого элемента
                            .stream()
                            .map(CommentMapper::mapToCommentDto)
                            .toList();
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

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()) // Получаем комментарии для каждого элемента
                            .stream()
                            .map(CommentMapper::mapToCommentDto)
                            .toList();
                    return ItemMapper.mapToItemDto(item, comments); // Создаем ItemDto с комментариями
                })
                .toList();
    }


    @Override
    public ItemDto createComment(Long itemId, CommentDto commentDto, Long userId) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        List<CommentDto> comments = getComments(itemId);

         return ItemMapper.mapToItemDto(item, comments);
    }

    protected List<CommentDto> getComments(Long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }
}
