package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;


    @Override
    public ResponseBookingDto bookItem(long userId, BookingDto requestBookingDto) {
        Booking booking = prepareAndMakeBookingPOJO(userId, requestBookingDto);

        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Предмет не доступен для бронирования");
        }

        booking.setStatus(StatusBook.WAITING);
        return prepareAndMakeBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto changeBookStatus(long ownerId, long bookingId, boolean approved) {
        StatusBook status = approved ? StatusBook.APPROVED : StatusBook.REJECTED;
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id '" + bookingId + "' не найдена"));

        if (!userRepository.existsById(booking.getBooker().getId()))
            throw new ConditionsNotMatchException("Пользователь не найден");

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
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        return getBookingsByState(userId, state, "user").stream()
                .map(this::prepareAndMakeBookingDto)
                .toList();
    }

    @Override
    public List<ResponseBookingDto> getOwnerBookings(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id '" + ownerId + "' не найден");
        }

        return getBookingsByState(ownerId, state, "owner").stream()
                .map(this::prepareAndMakeBookingDto)
                .toList();
    }


    private ResponseBookingDto prepareAndMakeBookingDto(Booking booking) {
        ItemDto itemDto = itemService.itemById(booking.getItem().getId());

        return BookingMapper.mapToDTO(itemDto, booking);
    }


    private Booking prepareAndMakeBookingPOJO(long userId, BookingDto requestBookingDto) {
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id '" + userId + "' не найден"));

        Item item = itemRepository.findById(requestBookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с id '" + requestBookingDto.getItemId() + "' не найден"));

        return BookingMapper.mapToBooking(booker, item, requestBookingDto);
    }


    private List<Booking> getBookingsByState(Long id, String state, String ownerOrUser) {
        Instant now = Instant.now();
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "startDate");

        if ("all".equalsIgnoreCase(state)) {
            return ownerOrUser.equals("user")
                    ? bookingRepository.findByBookerId(id, newestFirst)
                    : bookingRepository.findByItemOwnerId(id);
        }

        return switch (state.toLowerCase()) {
            case "current" -> ownerOrUser.equals("user")
                    ? bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(id, now, now, newestFirst)
                    : bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(id, now, now, newestFirst);

            case "past" -> ownerOrUser.equals("user")
                    ? bookingRepository.findByBookerIdAndEndDateBefore(id, now, newestFirst)
                    : bookingRepository.findByItemOwnerIdAndEndDateBefore(id, now, newestFirst);

            case "future" -> ownerOrUser.equals("user")
                    ? bookingRepository.findByBookerIdAndStartDateAfter(id, now, newestFirst)
                    : bookingRepository.findByItemOwnerIdAndStartDateAfter(id, now, newestFirst);

            case "waiting" -> ownerOrUser.equals("user")
                    ? bookingRepository.findByBookerIdAndStatusContaining(id, "WAITING", newestFirst)
                    : bookingRepository.findByItemOwnerIdAndStatusContaining(id, "WAITING", newestFirst);

            case "rejected" -> ownerOrUser.equals("user")
                    ? bookingRepository.findByBookerIdAndStatusContaining(id, "REJECTED", newestFirst)
                    : bookingRepository.findByItemOwnerIdAndStatusContaining(id, "REJECTED", newestFirst);

            default -> ownerOrUser.equals("user")
                    ? bookingRepository.findByBookerId(id, newestFirst)
                    : bookingRepository.findByItemOwnerId(id);
        };
    }

}
