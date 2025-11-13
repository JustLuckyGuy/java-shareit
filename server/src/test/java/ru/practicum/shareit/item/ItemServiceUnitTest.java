package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConditionsNotMatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceIml;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    private ItemService service;

    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private Booking finishedBooking;
    private Booking futureBooking;

    @BeforeEach
    void before() {

        user = User.builder()
                .id(3L)
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Fiona")
                .email("fiona@gmail.com")
                .build();

        item = Item.builder()
                .id(3L)
                .owner(user)
                .name("Shrexy pants")
                .description("No words are needed")
                .available(true)
                .build();

        item2 = Item.builder()
                .id(2L)
                .owner(user)
                .name("carbonara")
                .description("Yummy pasta")
                .available(true)
                .build();

        finishedBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        futureBooking = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user2)
                .status(StatusBook.APPROVED)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();


        service = new ItemServiceIml(userRepository, itemRepository, commentRepository, bookingRepository,
                requestRepository);

    }

    @Test
    void testGetItemByIdNotFound() {
        Mockito.when(itemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.itemById(999L));
    }

    @Test
    void testCreateItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> {
                    Item itemToSave = invocation.getArgument(0);
                    itemToSave.setId(3L);
                    return itemToSave;
                });

        ItemDto result = service.createItem(1L, itemDto);

        assertThat(result.getName(), is("New Item"));
        assertThat(result.getDescription(), is("New Description"));
        Mockito.verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void testCreateItemWithRequest() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .items(new HashSet<>())
                .build();

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> {
                    Item itemToSave = invocation.getArgument(0);
                    itemToSave.setId(3L);
                    return itemToSave;
                });
        Mockito.when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));
        Mockito.when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(request);

        ItemDto result = service.createItem(1L, itemDto);

        assertThat(result.getName(), is("New Item"));
        Mockito.verify(requestRepository).findById(1L);
        Mockito.verify(requestRepository).save(request);
    }

    @Test
    void testCreateItemUserNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        Mockito.when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createItem(999L, itemDto));
    }

    @Test
    void testUpdateItem() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.existsByIdAndOwnerId(1L, 1L))
                .thenReturn(true);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        ItemDto result = service.updateItem(1L, 1L, updateDto);

        assertThat(result.getName(), is("Updated Name"));
        Mockito.verify(itemRepository).findById(1L);
    }


    @Test
    void testDeleteItem() {
        Mockito.when(itemRepository.existsByIdAndOwnerId(1L, 1L))
                .thenReturn(true);

        service.deleteItem(1L, 1L);

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void testDeleteItemNotOwner() {
        Mockito.when(itemRepository.existsByIdAndOwnerId(1L, 2L))
                .thenReturn(false);

        assertThrows(ConditionsNotMatchException.class, () -> service.deleteItem(2L, 1L));
    }

    @Test
    void testGetUserItems() {
        Mockito.when(userRepository.existsById(1L))
                .thenReturn(true);
        Mockito.when(itemRepository.findByOwnerId(1L))
                .thenReturn(Arrays.asList(item, item2));
        Mockito.when(bookingRepository.existsByItemId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(commentRepository.findByItemId(Mockito.anyLong()))
                .thenReturn(List.of());

        List<ItemDto> result = service.itemsOfUser(1L);

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getName(), is("Shrexy pants"));
        assertThat(result.get(1).getName(), is("carbonara"));
    }

    @Test
    void testSearchItem() {
        Mockito.when(itemRepository.findByNameContainingOrDescriptionContainingAndAvailableTrue("pasta"))
                .thenReturn(List.of(item2));

        List<ItemDto> result = service.searchItem("pasta");

        assertThat(result, hasSize(1));
        assertThat(result.getFirst().getName(), is("carbonara"));
    }

    @Test
    void testSearchItemEmptyText() {
        List<ItemDto> result = service.searchItem("");

        assertThat(result, empty());
    }

    @Test
    void testSearchItemBlankText() {
        List<ItemDto> result = service.searchItem("   ");

        assertThat(result, empty());
    }

    @Test
    void testGetAllItems() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(Arrays.asList(item, item2));
        Mockito.when(commentRepository.findByItemIn(Mockito.any(), Mockito.any()))
                .thenReturn(List.of());
        Mockito.when(bookingRepository.findApprovedForItems(Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        List<ItemDto> result = service.allItems();

        assertThat(result, hasSize(2));
    }

    @Test
    void testGetAllItemsEmpty() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(List.of());

        List<ItemDto> result = service.allItems();

        assertThat(result, empty());
    }

    @Test
    void testPostComment() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findByItemIdAndBookerId(1L, 2L))
                .thenReturn(Arrays.asList(finishedBooking));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    comment.setId(1L);
                    return comment;
                });

        CommentDTO comment = CommentDTO.builder().text("Danila it's amazing").build();

        CommentDTO resp = service.addComment(2L, 1L, comment);

        assertThat(resp.getText(), is(comment.getText()));
        assertThat(resp.getAuthorName(), is(user2.getName()));
    }

}
