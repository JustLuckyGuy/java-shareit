package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private Item unavailableItem;

    @BeforeEach
    void before() {
        user = User.builder()
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();
        user2 = User.builder()
                .name("Fiona")
                .email("fiona@gmail.com")
                .build();

        item = Item.builder()
                .owner(user)
                .name("Shrexy pants")
                .description("No words are needed")
                .available(true)
                .build();

        item2 = Item.builder()
                .owner(user)
                .name("carbonara")
                .description("Yummy")
                .available(true)
                .build();
        unavailableItem = Item.builder()
                .owner(user)
                .name("Broken item")
                .description("Not available")
                .available(false)
                .build();
    }

    @Test
    void testGetItem() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);

        ItemDto resp = itemService.itemById(item.getId());

        assertThat(resp.getId(), is(item.getId()));
        assertThat(resp.getName(), is(item.getName()));
    }

    @Test
    void testGetItemNotFound() {
        userRepository.save(user);

        assertThrows(NotFoundException.class, () -> itemService.itemById(999L));
    }

    @Test
    void testGetUserItems() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);

        List<ItemDto> resp = itemService.itemsOfUser(user.getId());

        assertThat(resp.getFirst().getName(), is(item.getName()));
        assertThat(resp.getLast().getName(), is(item2.getName()));
    }

    @Test
    void testGetUserItemsEmpty() {
        userRepository.save(user);

        List<ItemDto> resp = itemService.itemsOfUser(user.getId());

        assertThat(resp, empty());
    }

    @Test
    void testItemSearch() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);

        List<ItemDto> resp = itemService.searchItem("Shrexy");

        assertThat(resp.size(), is(1));
        assertThat(resp.getFirst().getDescription(), is(item.getDescription()));
    }

    @Test
    void testItemSearchEmptyText() {
        userRepository.save(user);
        itemRepository.save(item);

        List<ItemDto> resp = itemService.searchItem("");

        assertThat(resp, empty());
    }

    @Test
    void testItemSearchBlankText() {
        userRepository.save(user);
        itemRepository.save(item);

        List<ItemDto> resp = itemService.searchItem("   ");

        assertThat(resp, empty());
    }

    @Test
    void testItemSearchUnavailable() {
        userRepository.save(user);
        itemRepository.save(unavailableItem);

        List<ItemDto> resp = itemService.searchItem("Broken");

        assertThat(resp, empty());
    }

    @Test
    void testCreateItem() {
        userRepository.save(user);
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        ItemDto created = itemService.createItem(user.getId(), itemDto);

        assertThat(created.getId(), notNullValue());
        assertThat(created.getName(), is("New Item"));
        assertThat(created.getDescription(), is("New Description"));
        assertThat(created.getAvailable(), is(true));
    }

    @Test
    void testCreateItemUserNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        assertThrows(NotFoundException.class, () -> itemService.createItem(999L, itemDto));
    }

    @Test
    void testUpdateItem() {
        userRepository.save(user);
        itemRepository.save(item);

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto updated = itemService.updateItem(user.getId(), item.getId(), updateDto);

        assertThat(updated.getName(), is("Updated Name"));
        assertThat(updated.getDescription(), is("Updated Description"));
        assertThat(updated.getAvailable(), is(false));
    }

    @Test
    void testUpdateItemPartial() {
        userRepository.save(user);
        itemRepository.save(item);

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name Only")
                .build();

        ItemDto updated = itemService.updateItem(user.getId(), item.getId(), updateDto);

        assertThat(updated.getName(), is("Updated Name Only"));
        assertThat(updated.getDescription(), is(item.getDescription()));
        assertThat(updated.getAvailable(), is(true));
    }

    @Test
    void testUpdateItemNotOwner() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .build();

        assertThrows(ConditionsNotMatchException.class,
                () -> itemService.updateItem(user2.getId(), item.getId(), updateDto));
    }


    @Test
    void testDeleteItem() {
        userRepository.save(user);
        itemRepository.save(item);

        itemService.deleteItem(user.getId(), item.getId());

        assertThrows(NotFoundException.class, () -> itemService.itemById(item.getId()));
    }

    @Test
    void testDeleteItemNotOwner() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);

        assertThrows(ConditionsNotMatchException.class,
                () -> itemService.deleteItem(user2.getId(), item.getId()));
    }

    @Test
    void testAddComment() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);


        Booking booking = Booking.builder()
                .item(item)
                .booker(user2)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .status(StatusBook.APPROVED)
                .build();
        bookingRepository.save(booking);

        CommentDTO commentDto = CommentDTO.builder()
                .text("Great item!")
                .build();

        CommentDTO createdComment = itemService.addComment(user2.getId(), item.getId(), commentDto);

        assertThat(createdComment.getId(), notNullValue());
        assertThat(createdComment.getText(), is("Great item!"));
        assertThat(createdComment.getAuthorName(), is(user2.getName()));
        assertThat(createdComment.getCreated(), notNullValue());
    }

    @Test
    void testAddCommentWithoutBooking() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);

        CommentDTO commentDto = CommentDTO.builder()
                .text("Great item!")
                .build();

        assertThrows(BadRequestException.class,
                () -> itemService.addComment(user2.getId(), item.getId(), commentDto));
    }


    @Test
    void testGetAllItems() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);

        List<ItemDto> allItems = itemService.allItems();

        assertThat(allItems, hasSize(2));
        assertThat(allItems.stream().map(ItemDto::getName).toList(),
                containsInAnyOrder("Shrexy pants", "carbonara"));
    }

    @Test
    void testGetAllItemsEmpty() {
        List<ItemDto> allItems = itemService.allItems();

        assertThat(allItems, empty());
    }
}
