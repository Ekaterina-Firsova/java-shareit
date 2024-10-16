package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        itemRequestDto.setRequester(user);
        itemRequestDto.setCreated(LocalDateTime.now());

        return ItemRequestMapper.mapToItemRequestDto(
                itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(itemRequestDto))
        );
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUser(Long userId) {
        //получаем все запросы, сделанные пользователем userId
        List<ItemRequest> itemRequest = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId);

        return itemRequest
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        return itemRequestRepository.findAllByOrderByCreatedDesc()
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long id) {
        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request not found")));
    }

}
