package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final  UserRepository userRepository;
    private final  ItemRepository itemRepository;
    private final  ItemService itemService;
    Comparator<Booking> comparator = (o1, o2) -> {
        if (o1.getStartDate().isAfter(o2.getStartDate())) {
            return 3;
        } else if (o1.getStartDate().isBefore(o2.getStartDate())) {
            return -3;
        } else {
            return 0;
        }
    };

    @Override
    public ResponseBookingDto bookItem(long userId, BookingDto requestBookingDto) {
        Booking booking = prepareAndMakeBookingPOJO(userId, requestBookingDto);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id '" + userId + "' не найден");
        }

        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Предмет не доступен для бронирования");
        }

        validated(booking);

        booking.setStatus(StatusBook.WAITING);

        return prepareAndMakeBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto changeBookStatus(long ownerId, long bookingId, boolean approved) {
        StatusBook  status = approved ? StatusBook.APPROVED : StatusBook.REJECTED;
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id '" + bookingId + "' не найдена"));

        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ConditionsNotMatchException("Только владелец может изменять статус брони");
        }

        booking.setStatus(status);

        return prepareAndMakeBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto getBooking(long bookingId) {
        return prepareAndMakeBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id '" + bookingId + "' не найдена")));
    }

    @Override
    public List<ResponseBookingDto> getUserBookings(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id '" + userId + "' не найден");
        }

        return getBookingsByState(userId, state, "user").stream()
                .sorted(comparator)
                .map(this::prepareAndMakeBookingDto)
                .toList();
    }

    @Override
    public List<ResponseBookingDto> getOwnerBookings(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id '" + ownerId + "' не найден");
        }

        return getBookingsByState(ownerId, state, "owner").stream()
                .sorted(comparator)
                .map(this::prepareAndMakeBookingDto)
                .toList();
    }

    @Override
    public ResponseBookingDto prepareAndMakeBookingDto(Booking booking) {
        ItemDto itemDto = itemService.prepareAndMakeItemDto(booking.getItem(), false);

        return BookingMapper.mapToDTO(itemDto, booking);
    }

    @Override
    public Booking prepareAndMakeBookingPOJO(long userId, BookingDto requestBookingDto) {
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id '" + userId + "' не найден"));

        Item item = itemRepository.findById(requestBookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с id '" + requestBookingDto.getItemId() + "' не найден"));

        return BookingMapper.mapToBooking(booker, item, requestBookingDto);
    }

    private void validated(Booking booking){
        if (booking.getEndDate().isBefore(booking.getStartDate())) {
            throw new BadRequestException("Окончание бронирования не может быть раньше начала бронирования");
        }

        if (booking.getEndDate().equals(booking.getStartDate())) {
            throw new BadRequestException("Окончание бронирования не может быть в момент начала бронирования");
        }

        if (booking.getEndDate().isBefore(Instant.now())) {
            throw new BadRequestException("Окончание бронирования не может быть в прошлом");
        }

        if (booking.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("Начало бронирования не может быть в прошлом");
        }
    }

    private List<Booking> getBookingsByState(Long id, String state, String ownerOrUser){
        Instant now = Instant.now();
        Map<String, Function<Long, List<Booking>>> userBookingsMap = Map.of(
                "current", userId -> bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(id, now, now),
                "past", userId -> bookingRepository.findByBookerIdAndEndDateBefore(id, now),
                "future", userId -> bookingRepository.findByBookerIdAndStartDateAfter(id, now),
                "waiting", userId -> bookingRepository
                        .findByBookerIdAndStatusContaining(id, "WAITING"),
                "rejected", userId -> bookingRepository
                        .findByBookerIdAndStatusContaining(id, "REJECTED")
        );

        Map<String, Function<Long, List<Booking>>> ownerBookingsMap = Map.of(
                "current", ownerId -> bookingRepository
                        .findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(id, now, now),
                "past", ownerId -> bookingRepository.findByItemOwnerIdAndEndDateBefore(id, now),
                "future", ownerId -> bookingRepository.findByItemOwnerIdAndStartDateAfter(id, now),
                "waiting", ownerId -> bookingRepository
                        .findByItemOwnerIdAndStatusContaining(id, "WAITING"),
                "rejected", ownerId -> bookingRepository
                        .findByItemOwnerIdAndStatusContaining(id, "REJECTED")
        );

        Map<String, Function<Long, List<Booking>>> selectedMap =
                ownerOrUser.equals("user") ? userBookingsMap : ownerBookingsMap;

        return selectedMap.getOrDefault(state,
                ownerOrUser.equals("user") ? bookingRepository::findByBookerId : bookingRepository::findByItemOwnerId
        ).apply(id);
    }

}
