package ru.practicum.shareit.item;

import lombok.AccessLevel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceIntegrationTest {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final ItemService itemService;

    User user;
    Item item;
    Item item2;

    @BeforeEach
    void before() {
        user = User.builder()
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
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
    void testGetUserItems() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);

        List<ItemDto> resp = itemService.itemsOfUser(user.getId());

        assertThat(resp.getFirst().getName(), is(item.getName()));
        assertThat(resp.getLast().getName(), is(item2.getName()));
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
}
