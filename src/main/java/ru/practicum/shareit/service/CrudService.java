package ru.practicum.shareit.service;

import java.util.Collection;

public interface CrudService<T> {

  T create(T t);

  T update(Long id, T t);

  Collection<T> getAll();

  T getById(Long id);

  void delete(long id);
}
