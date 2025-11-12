package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @Column(name = "author")
    private String authorName;

    @Column(name = "text")
    private String text;

    @Column(name = "created")
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}