package ru.practicum.shareit.service;

import java.util.List;

public interface CrudService<T> {

  T create(Long userId, T t);

  T getById(Long id);

 }
