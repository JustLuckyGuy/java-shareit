package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    private BookingService bookingService;

    private User user;
    private User booker;
    private Item item;
    private Item unavailableItem;
    private Booking booking;
    private Booking futureBooking;
    private BookingDto bookingDto;

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

        unavailableItem = Item.builder()
                .id(5L)
                .owner(user)
                .name("Broken item")
                .description("Not available")
                .available(false)
                .build();

        booking = Booking.builder()
                .id(4L)
                .item(item)
                .booker(booker)
                .status(StatusBook.WAITING)
                .startDate(LocalDateTime.now().minusSeconds(10))
                .endDate(LocalDateTime.now().plusHours(1))
                .build();

        futureBooking = Booking.builder()
                .id(6L)
                .item(item)
                .booker(booker)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();


        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void testChangeStatusApproveSuccess() {
        Mockito.when(bookingRepository.findById(4L))
                .thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseBookingDto resp = bookingService.changeBookStatus(3, 4, true);

        assertThat(resp.getStatus(), is(StatusBook.APPROVED));
        Mockito.verify(bookingRepository).save(booking);
    }

    @Test
    void testChangeStatusRejectSuccess() {
        Mockito.when(bookingRepository.findById(4L))
                .thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseBookingDto resp = bookingService.changeBookStatus(3, 4, false);

        assertThat(resp.getStatus(), is(StatusBook.REJECTED));
    }

    @Test
    void testChangeStatusBookingNotFound() {
        Mockito.when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.changeBookStatus(3, 999, true));

        assertThat(exception.getMessage(), containsString("Бронь с id '999' не найдена"));
    }

    @Test
    void testChangeStatusUserNotFound() {
        Mockito.when(bookingRepository.findById(4L))
                .thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(false);

        ConditionsNotMatchException exception = assertThrows(ConditionsNotMatchException.class,
                () -> bookingService.changeBookStatus(3, 4, true));

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    @Test
    void testChangeStatusNotOwner() {
        Mockito.when(bookingRepository.findById(4L))
                .thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);

        ConditionsNotMatchException exception = assertThrows(ConditionsNotMatchException.class,
                () -> bookingService.changeBookStatus(999, 4, true));

        assertThat(exception.getMessage(), containsString("Только владелец может изменять статус брони"));
    }

    @Test
    void testBookItemSuccess() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking savedBooking = invocation.getArgument(0);
                    savedBooking.setId(10L);
                    return savedBooking;
                });

        ResponseBookingDto result = bookingService.bookItem(2, bookingDto);

        assertNotNull(result);
        assertThat(result.getStatus(), is(StatusBook.WAITING));
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    void testBookItemUserNotFound() {
        Mockito.when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.bookItem(999, bookingDto));

        assertThat(exception.getMessage(), containsString("Пользователь с id '999' не найден"));
    }

    @Test
    void testBookItemItemNotFound() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.bookItem(2, BookingDto.builder().itemId(999L).build()));

        assertThat(exception.getMessage(), containsString("Предмет с id '999' не найден"));
    }

    @Test
    void testBookItemUnavailableItem() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(5L))
                .thenReturn(Optional.of(unavailableItem));

        BookingDto unavailableBookingDto = BookingDto.builder()
                .itemId(5L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.bookItem(2, unavailableBookingDto));

        assertThat(exception.getMessage(), containsString("Предмет не доступен для бронирования"));
    }

    @Test
    void testGetBookingSuccess() {
        Mockito.when(bookingRepository.findById(4L))
                .thenReturn(Optional.of(booking));

        ResponseBookingDto result = bookingService.getBooking(4);

        assertNotNull(result);
        assertThat(result.getId(), is(booking.getId()));
        assertThat(result.getStatus(), is(booking.getStatus()));
        assertThat(result.getItem().getId(), is(item.getId()));
    }

    @Test
    void testGetBookingNotFound() {
        Mockito.when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(999));

        assertThat(exception.getMessage(), containsString("Бронь с id '999' не найдена"));
    }

    @Test
    void testGetUserBookingsUserNotFound() {
        Mockito.when(userRepository.existsById(999L))
                .thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getUserBookings(999L, "all"));

        assertThat(exception.getMessage(), containsString("Пользователь с id 999 не найден"));
    }

    @Test
    void testGetOwnerBookingsUserNotFound() {
        Mockito.when(userRepository.existsById(999L))
                .thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerBookings(999L, "all"));

        assertThat(exception.getMessage(), containsString("Пользователь с id '999' не найден"));
    }

    @Test
    void testGetUserBookingsInvalidState() {
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.getUserBookings(2L, "invalid_state"));

        assertThat(exception.getMessage(), containsString("Не верно введенный статус"));
    }

    @Test
    void testGetOwnerBookingsInvalidState() {
        Mockito.when(userRepository.existsById(3L))
                .thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.getOwnerBookings(3L, "invalid_state"));

        assertThat(exception.getMessage(), containsString("Не верно введенный статус"));
    }

    @Test
    void testGetUserBookingsAll() {
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking, futureBooking));

        List<ResponseBookingDto> result = bookingService.getUserBookings(2L, "all");

        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(booking.getId()));
        assertThat(result.get(1).getId(), is(futureBooking.getId()));
    }

    @Test
    void testGetUserBookingsFuture() {
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStartDateAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(futureBooking));

        List<ResponseBookingDto> result = bookingService.getUserBookings(2L, "future");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(futureBooking.getId()));
    }

    @Test
    void testGetOwnerBookingsAll() {
        Mockito.when(userRepository.existsById(3L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerId(3L))
                .thenReturn(List.of(booking, futureBooking));

        List<ResponseBookingDto> result = bookingService.getOwnerBookings(3L, "all");

        assertNotNull(result);
        assertThat(result.size(), is(2));
    }

    @Test
    void testGetOwnerBookingsFuture() {
        Mockito.when(userRepository.existsById(3L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartDateAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(futureBooking));

        List<ResponseBookingDto> result = bookingService.getOwnerBookings(3L, "future");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(futureBooking.getId()));
    }

    @Test
    void testGetUserBookingsWaiting() {
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStatusContaining(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = bookingService.getUserBookings(2L, "waiting");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getStatus(), is(StatusBook.WAITING));
    }

    @Test
    void testGetUserBookingsCurrent() {
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = bookingService.getUserBookings(2L, "current");

        assertNotNull(result);
        assertThat(result.size(), is(1));
    }

    @Test
    void testGetUserBookingsPast() {
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndEndDateBefore(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = bookingService.getUserBookings(2L, "past");

        assertNotNull(result);
        assertThat(result.size(), is(1));
    }

    @Test
    void testGetOwnerBookingsWaiting() {
        Mockito.when(userRepository.existsById(3L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatusContaining(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = bookingService.getOwnerBookings(3L, "waiting");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getStatus(), is(StatusBook.WAITING));
    }

    @Test
    void testGetOwnerBookingsRejected() {
        Booking rejectedBooking = Booking.builder()
                .id(7L)
                .item(item)
                .booker(booker)
                .status(StatusBook.REJECTED)
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(4))
                .build();

        Mockito.when(userRepository.existsById(3L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatusContaining(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(rejectedBooking));

        List<ResponseBookingDto> result = bookingService.getOwnerBookings(3L, "rejected");

        assertNotNull(result);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getStatus(), is(StatusBook.REJECTED));
    }


    @Test
    void testBookItemVerifyRepositoryCalls() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bookingService.bookItem(2, bookingDto);

        Mockito.verify(userRepository).findById(2L);
        Mockito.verify(itemRepository).findById(1L);
        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    void testGetUserBookingsEmptyList() {
        Mockito.when(userRepository.existsById(2L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of());

        List<ResponseBookingDto> result = bookingService.getUserBookings(2L, "all");

        assertNotNull(result);
        assertThat(result, empty());
    }

    @Test
    void testGetOwnerBookingsEmptyList() {
        Mockito.when(userRepository.existsById(3L))
                .thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerId(3L))
                .thenReturn(List.of());

        List<ResponseBookingDto> result = bookingService.getOwnerBookings(3L, "all");

        assertNotNull(result);
        assertThat(result, empty());
    }
}
