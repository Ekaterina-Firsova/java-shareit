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
        //List<ItemRequest> itemRequest = itemRequestRepository.findRequestWithItems(userId);

        return itemRequest
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }

//    private ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
//        // Преобразование списка Item в список ItemOwnerDto
//        List<ItemOwnerDto> itemOwnerDtos = itemRequest.getItems().stream()
//                .map(item -> ItemOwnerDto.builder()
//                        .id(item.getId())
//                        .name(item.getName())
//                        .owner(User.builder()
//                                .id(item.getOwner().getId())
//                                .name(item.getOwner().getName())
//                                .build()) // Преобразование owner в UserDto
//                        .build())
//                .collect(Collectors.toList());
//
//        return ItemRequestDto.builder()
//                .id(itemRequest.getId())
//                .description(itemRequest.getDescription())
//                .requester(User.builder() // Преобразование requester в UserDto
//                        .id(itemRequest.getRequester().getId())
//                        .name(itemRequest.getRequester().getName())
//                        .build())
//                .created(itemRequest.getCreated())
//                .items(itemOwnerDtos) // Здесь массив items заполняется
//                .build();
//    }

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
                .orElseThrow());
    }



}
