package ru.practicum.shareit.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface CrudService<T> {

  T create(Long userId, T t);

  //T update(Long userId, T t);

  List<T> getAll();

  T getById(Long id);

  //void delete(long id);
}
