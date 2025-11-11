package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceUnitTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemService itemService;
    BookingService bookingService;

    User user;
    User booker;
    Item item;
    Booking booking;

    @BeforeEach
    void before() {
        user = User.builder()
                .id(3L)
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Donkey")
                .email("shrekIsLife@gmail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("Shrexy pants")
                .description("No words are needed")
                .available(true)
                .build();

        booking = Booking.builder()
                .id(4L)
                .item(item)
                .booker(booker)
                .status(StatusBook.WAITING)
                .startDate(LocalDateTime.now().minusSeconds(10))
                .endDate(LocalDateTime.now().plusHours(1))
                .build();

        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void testChangeStatus() {

        Mockito.when(bookingRepository.findById(4L))
                .thenReturn(Optional.of(booking));

        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        ResponseBookingDto resp = bookingService.changeBookStatus(3, 4, true);


        assertThat(resp.getStatus(), is(StatusBook.APPROVED));
    }
}
