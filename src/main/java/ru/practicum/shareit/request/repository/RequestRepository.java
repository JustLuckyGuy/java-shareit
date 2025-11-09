package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query
    List<ItemRequest> findByUserIdOrderByCreatedDateDesc(long userId);

    @Query
    List<ItemRequest> findByUserIdNotOrderByCreatedDateDesc(long userId);
}
