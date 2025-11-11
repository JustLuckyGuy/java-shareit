package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceIntegrationTest {
    final BookingRepository repository;
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingService service;


    User user;
    User booker;
    Item item;
    Booking booking;

    @BeforeEach
    void before() {
        user = User.builder()
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        booker = User.builder()
                .name("Donkey")
                .email("shrekIsLife@gmail.com")
                .build();

        item = Item.builder()
                .owner(user)
                .name("Shrexy pants")
                .description("No words are needed")
                .available(true)
                .build();

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().minusSeconds(10))
                .endDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetBooking() {
        userRepository.save(user);
        userRepository.save(booker);
        itemRepository.save(item);
        repository.save(booking);

        ResponseBookingDto bookingResp = service.getBooking(booking.getId());

        assertThat(bookingResp.getBooker().getName(), is(booker.getName()));
        assertThat(bookingResp.getItem().getName(), is(item.getName()));
        assertThat(bookingResp.getStart(), is(booking.getStartDate()));
    }

    @Test
    void testGetOwnerBooking() {
        userRepository.save(user);
        userRepository.save(booker);
        itemRepository.save(item);
        repository.save(booking);

        List<ResponseBookingDto> resp = service.getOwnerBookings(user.getId(), "past");
        List<ResponseBookingDto> emptyResp = service.getUserBookings(user.getId(), "future");

        assertThat(resp.isEmpty(), is(false));
        assertThat(resp.getFirst().getItem().getName(), is(item.getName()));
        assertThat(emptyResp, empty());
    }

    @Test
    void testGetUserBooking() {
        userRepository.save(user);
        userRepository.save(booker);
        itemRepository.save(item);
        repository.save(booking);

        List<ResponseBookingDto> resp = service.getUserBookings(booker.getId(), "past");
        List<ResponseBookingDto> emptyResp = service.getUserBookings(booker.getId(), "future");

        assertThat(resp.isEmpty(), is(false));
        assertThat(resp.getFirst().getItem().getName(), is(item.getName()));
        assertThat(emptyResp, empty());
    }
}
