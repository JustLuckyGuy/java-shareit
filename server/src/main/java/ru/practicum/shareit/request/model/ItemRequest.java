package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ElementCollection
    @CollectionTable(name="requests_items", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "item_id")
    private Set<Long> items;

    @Builder.Default
    @Column(name = "creation_date")
    private LocalDateTime createdDate = LocalDateTime.now();
}
