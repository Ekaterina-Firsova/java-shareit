package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
//    @Query("""
//        SELECT DISTINCT ir
//        FROM ItemRequest ir
//        LEFT JOIN FETCH ir.items items
//        WHERE ir.requester.id = :userId
//        ORDER BY ir.created DESC
//        """)
//    List<ItemRequest> getAllRequestsByUser(@Param("userId") Long userId);

//    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id = :userId ORDER BY ir.created DESC")
//    List<ItemRequest> getAllRequestsByUser(@Param("userId") Long userId);

    List<ItemRequest> findAllByRequester_IdOrderByCreatedDesc(Long userId);


    List<ItemRequest> findAllByOrderByCreatedDesc();

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.id = ?1")
    List<ItemRequest> findRequestWithItems(Long requestId);

}
