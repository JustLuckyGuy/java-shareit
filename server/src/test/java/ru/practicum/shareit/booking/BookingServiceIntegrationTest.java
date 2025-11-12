package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService service;


    private User user;
    private User booker;
    private User anotherUser;
    private Item item;
    private Item unavailableItem;
    private Booking booking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking currentBooking;

    @BeforeEach
    void before() {
        user = userRepository.save(User.builder()
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("Donkey")
                .email("shrekIsLife@gmail.com")
                .build());

        anotherUser = userRepository.save(User.builder()
                .name("Fiona")
                .email("fiona@gmail.com")
                .build());

        item = itemRepository.save(Item.builder()
                .owner(user)
                .name("Shrexy pants")
                .description("No words are needed")
                .available(true)
                .build());

        unavailableItem = itemRepository.save(Item.builder()
                .owner(user)
                .name("Broken item")
                .description("Not available for booking")
                .available(false)
                .build());

        booking = repository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .build());

        futureBooking = repository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build());

        waitingBooking = repository.save(Booking.builder()
                .item(item)
                .booker(anotherUser)
                .status(StatusBook.WAITING)
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(4))
                .build());


        currentBooking = repository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().minusHours(1))
                .endDate(LocalDateTime.now().plusHours(1))
                .build());
    }

    @Test
    void testBookItemSuccess() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(11))
                .build();

        ResponseBookingDto result = service.bookItem(booker.getId(), bookingDto);

        assertNotNull(result);
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
        assertThat(result.getStatus(), is(StatusBook.WAITING));
        assertThat(result.getStart(), is(bookingDto.getStart()));
        assertThat(result.getEnd(), is(bookingDto.getEnd()));
    }

    @Test
    void testBookItemUnavailableItem() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(unavailableItem.getId())
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(11))
                .build();

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.bookItem(booker.getId(), bookingDto));

        assertThat(exception.getMessage(), containsString("не доступен для бронирования"));
    }

    @Test
    void testBookItemUserNotFound() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(11))
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.bookItem(999L, bookingDto));

        assertThat(exception.getMessage(), containsString("Пользователь с id '999' не найден"));
    }

    @Test
    void testBookItemItemNotFound() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(999L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(11))
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.bookItem(booker.getId(), bookingDto));

        assertThat(exception.getMessage(), containsString("Предмет с id '999' не найден"));
    }


    @Test
    void testGetOwnerBooking() {

        List<ResponseBookingDto> resp = service.getOwnerBookings(user.getId(), "past");
        List<ResponseBookingDto> emptyResp = service.getUserBookings(user.getId(), "future");

        assertThat(resp.isEmpty(), is(false));
        assertThat(resp.getFirst().getItem().getName(), is(item.getName()));
        assertThat(emptyResp, empty());
    }

    @Test
    void testGetUserBooking() {

        List<ResponseBookingDto> resp = service.getUserBookings(booker.getId(), "past");
        List<ResponseBookingDto> futureResp = service.getUserBookings(booker.getId(), "future");

        assertThat(resp.isEmpty(), is(false));
        assertThat(resp.getFirst().getItem().getName(), is(item.getName()));
        assertThat(futureResp.isEmpty(), is(false));
        assertThat(futureResp.getFirst().getItem().getName(), is(item.getName()));
    }

    @Test
    void testChangeBookStatusApproveSuccess() {
        ResponseBookingDto result = service.changeBookStatus(user.getId(), waitingBooking.getId(), true);

        assertNotNull(result);
        assertThat(result.getStatus(), is(StatusBook.APPROVED));
        assertThat(result.getId(), is(waitingBooking.getId()));
    }

    @Test
    void testChangeBookStatusRejectSuccess() {
        ResponseBookingDto result = service.changeBookStatus(user.getId(), waitingBooking.getId(), false);

        assertNotNull(result);
        assertThat(result.getStatus(), is(StatusBook.REJECTED));
        assertThat(result.getId(), is(waitingBooking.getId()));
    }

    @Test
    void testChangeBookStatusBookingNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.changeBookStatus(user.getId(), 999L, true));

        assertThat(exception.getMessage(), containsString("Бронь с id '999' не найдена"));
    }

    @Test
    void testChangeBookStatusNotOwner() {
        ConditionsNotMatchException exception = assertThrows(ConditionsNotMatchException.class,
                () -> service.changeBookStatus(anotherUser.getId(), waitingBooking.getId(), true));

        assertThat(exception.getMessage(), containsString("Только владелец может изменять статус брони"));
    }

    @Test
    void testChangeBookStatusUserNotFound() {
        User tempUser = userRepository.save(User.builder()
                .name("Temp User")
                .email("temp@email.com")
                .build());

        Booking bookingWithTempUser = repository.save(Booking.builder()
                .item(item)
                .booker(tempUser)
                .status(StatusBook.WAITING)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build());

        userRepository.delete(tempUser);

        ConditionsNotMatchException exception = assertThrows(ConditionsNotMatchException.class,
                () -> service.changeBookStatus(user.getId(), bookingWithTempUser.getId(), true));

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }


    @Test
    void testGetBookingSuccess() {
        ResponseBookingDto result = service.getBooking(booking.getId());

        assertNotNull(result);
        assertThat(result.getId(), is(booking.getId()));
        assertThat(result.getBooker().getName(), is(booker.getName()));
        assertThat(result.getItem().getName(), is(item.getName()));
        assertThat(result.getStart(), is(booking.getStartDate()));
        assertThat(result.getEnd(), is(booking.getEndDate()));
    }

    @Test
    void testGetBookingNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getBooking(999L));

        assertThat(exception.getMessage(), containsString("Бронь с id '999' не найдена"));
    }

    @Test
    void testGetUserBookingsAll() {
        List<ResponseBookingDto> result = service.getUserBookings(booker.getId(), "all");

        assertNotNull(result);
        assertThat(result.size(), is(3));
        assertThat(result.getFirst().getId(), is(futureBooking.getId()));
    }

    @Test
    void testGetUserBookingsCurrent() {
        List<ResponseBookingDto> result = service.getUserBookings(booker.getId(), "current");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(currentBooking.getId()));
    }

    @Test
    void testGetUserBookingsPast() {
        List<ResponseBookingDto> result = service.getUserBookings(booker.getId(), "past");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(booking.getId()));
    }

    @Test
    void testGetUserBookingsFuture() {
        List<ResponseBookingDto> result = service.getUserBookings(booker.getId(), "future");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(futureBooking.getId()));
    }


    @Test
    void testGetUserBookingsUserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getUserBookings(999L, "all"));

        assertThat(exception.getMessage(), containsString("Пользователь с id 999 не найден"));
    }

    @Test
    void testGetUserBookingsInvalidState() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.getUserBookings(booker.getId(), "invalid_state"));

        assertThat(exception.getMessage(), containsString("Не верно введенный статус"));
    }

    @Test
    void testGetOwnerBookingsAll() {
        List<ResponseBookingDto> result = service.getOwnerBookings(user.getId(), "all");

        assertNotNull(result);
        assertThat(result.size(), is(4));
    }

    @Test
    void testGetOwnerBookingsCurrent() {
        List<ResponseBookingDto> result = service.getOwnerBookings(user.getId(), "current");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(currentBooking.getId()));
    }

    @Test
    void testGetOwnerBookingsPast() {
        List<ResponseBookingDto> result = service.getOwnerBookings(user.getId(), "past");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(booking.getId()));
    }

    @Test
    void testGetOwnerBookingsUserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getOwnerBookings(999L, "all"));

        assertThat(exception.getMessage(), containsString("Пользователь с id '999' не найден"));
    }

    @Test
    void testGetOwnerBookingsInvalidState() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.getOwnerBookings(user.getId(), "invalid_state"));

        assertThat(exception.getMessage(), containsString("Не верно введенный статус"));
    }

    @Test
    void testGetOwnerBookingsNoBookings() {
        User userWithoutItems = userRepository.save(User.builder()
                .name("No Items User")
                .email("noitems@email.com")
                .build());

        List<ResponseBookingDto> result = service.getOwnerBookings(userWithoutItems.getId(), "all");

        assertNotNull(result);
        assertThat(result, empty());
    }

    @Test
    void testBookItemBookOwnItem() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(11))
                .build();

        ResponseBookingDto result = service.bookItem(user.getId(), bookingDto);

        assertNotNull(result);
        assertThat(result.getStatus(), is(StatusBook.WAITING));
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(user.getId()));
    }

    @Test
    void testChangeBookStatusAlreadyApproved() {
        ResponseBookingDto result = service.changeBookStatus(user.getId(), booking.getId(), true);

        assertThat(result.getStatus(), is(StatusBook.APPROVED));
        assertThat(result.getId(), is(booking.getId()));
    }


    @Test
    void testBookingTimeValidationStartEqualsEnd() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(sameTime)
                .end(sameTime)
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> service.bookItem(booker.getId(), bookingDto));
    }

    @Test
    void testNewBookingHasWaitingStatus() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(20))
                .end(LocalDateTime.now().plusDays(21))
                .build();

        ResponseBookingDto result = service.bookItem(booker.getId(), bookingDto);

        assertThat(result.getStatus(), is(StatusBook.WAITING));
    }

    @Test
    void testGetUserBookingsEmptyResults() {
        User newUser = userRepository.save(User.builder()
                .name("New User")
                .email("newuser@email.com")
                .build());

        List<ResponseBookingDto> result = service.getUserBookings(newUser.getId(), "all");

        assertNotNull(result);
        assertThat(result, empty());
    }


}
