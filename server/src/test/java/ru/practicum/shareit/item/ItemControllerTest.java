package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDto responseItemDto;
    private CommentDTO commentDto;
    private CommentDTO responseCommentDto;
    private final Long userId = 1L;
    private final Long itemId = 1L;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        responseItemDto = ItemDto.builder()
                .id(itemId)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        commentDto = CommentDTO.builder()
                .text("Test comment")
                .build();

        responseCommentDto = CommentDTO.builder()
                .id(1L)
                .text("Test comment")
                .authorName("Test User")
                .created(String.valueOf(LocalDateTime.now()))
                .build();
    }

    @Test
    void allUsersShouldReturnAllItems() throws Exception {
        List<ItemDto> items = List.of(responseItemDto);

        Mockito.when(itemService.allItems())
                .thenReturn(items);

        mockMvc.perform(get("/items/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemId.intValue())))
                .andExpect(jsonPath("$[0].name", is("Test Item")));

        Mockito.verify(itemService).allItems();
    }

    @Test
    void getItemShouldReturnItem() throws Exception {
        Mockito.when(itemService.itemById(itemId))
                .thenReturn(responseItemDto);

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is("Test Item")));

        Mockito.verify(itemService).itemById(itemId);
    }

    @Test
    void getUserItemsShouldReturnUserItems() throws Exception {
        List<ItemDto> items = List.of(responseItemDto);

        Mockito.when(itemService.itemsOfUser(userId))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemId.intValue())));

        Mockito.verify(itemService).itemsOfUser(userId);
    }

    @Test
    void itemSearchShouldReturnSearchResults() throws Exception {
        List<ItemDto> items = List.of(responseItemDto);

        Mockito.when(itemService.searchItem("test"))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Item")));

        Mockito.verify(itemService).searchItem("test");
    }

    @Test
    void itemSearchWithEmptyTextShouldReturnEmptyList() throws Exception {
        Mockito.when(itemService.searchItem(""))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        Mockito.verify(itemService).searchItem("");
    }

    @Test
    void saveItemShouldCreateItem() throws Exception {
        Mockito.when(itemService.createItem(Mockito.eq(userId), Mockito.any(ItemDto.class)))
                .thenReturn(responseItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is("Test Item")));

        Mockito.verify(itemService).createItem(Mockito.eq(userId), Mockito.any(ItemDto.class));
    }

    @Test
    void updateItemShouldUpdateItem() throws Exception {
        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        Mockito.when(itemService.updateItem(Mockito.eq(userId), Mockito.eq(itemId), Mockito.any(ItemDto.class)))
                .thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is("Updated Item")));

        Mockito.verify(itemService).updateItem(Mockito.eq(userId), Mockito.eq(itemId), Mockito.any(ItemDto.class));
    }

    @Test
    void removeItemShouldDeleteItem() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(userId, itemId);

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        Mockito.verify(itemService).deleteItem(userId, itemId);
    }

    @Test
    void addCommentShouldCreateComment() throws Exception {
        Mockito.when(itemService.addComment(Mockito.eq(userId), Mockito.eq(itemId), Mockito.any(CommentDTO.class)))
                .thenReturn(responseCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Test comment")))
                .andExpect(jsonPath("$.authorName", is("Test User")));

        Mockito.verify(itemService).addComment(Mockito.eq(userId), Mockito.eq(itemId), Mockito.any(CommentDTO.class));
    }

    @Test
    void saveItemWithMissingUserIdShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemWithPartialDataShouldWork() throws Exception {
        ItemDto partialUpdate = ItemDto.builder()
                .name("Only Name Updated")
                .build();

        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Only Name Updated")
                .description("Test Description")
                .available(true)
                .build();

        Mockito.when(itemService.updateItem(Mockito.eq(userId), Mockito.eq(itemId), Mockito.any(ItemDto.class)))
                .thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Only Name Updated")))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

}
