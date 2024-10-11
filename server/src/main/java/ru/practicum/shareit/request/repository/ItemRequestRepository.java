package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester_IdOrderByCreatedDesc(Long userId);

    List<ItemRequest> findAllByOrderByCreatedDesc();

//    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.id = ?1")
//    List<ItemRequest> findRequestWithItems(Long requestId);

}
