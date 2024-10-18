package ru.practicum.shareit.service;

public interface CrudService<T> {

  T create(Long userId, T t);

  T getById(Long id);

 }
