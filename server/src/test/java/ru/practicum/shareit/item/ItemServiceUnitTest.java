package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceIml;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceUnitTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    RequestRepository requestRepository;
    Booking finishedBooking;
    ItemService service;

    User user;
    Item item;

    @BeforeEach
    void before() {

        user = User.builder()
                .id(3L)
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        item = Item.builder()
                .id(3L)
                .owner(user)
                .name("Shrexy pants")
                .description("No words are needed")
                .available(true)
                .build();


        finishedBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1)) // завершено
                .build();


        service = new ItemServiceIml(userRepository, itemRepository, commentRepository, bookingRepository,
                requestRepository);

    }

    @Test
    void testDelete() {
        Mockito.when(itemRepository.existsByIdAndOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);

        service.deleteItem(user.getId(), item.getId());

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(item.getId());
    }


    @Test
    void testPostComment() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findByItemIdAndBookerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Arrays.asList(finishedBooking));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    if (comment.getId() == null) {
                        comment.setId(1L);
                    }
                    return comment;
                });

        CommentDTO comment = CommentDTO.builder().text("Danila it's amazing").build();

        CommentDTO resp = service.addComment(user.getId(), item.getId(), comment);

        assertThat(resp.getText(), is(comment.getText()));
        assertThat(resp.getAuthorName(), is(user.getName()));
    }
}
