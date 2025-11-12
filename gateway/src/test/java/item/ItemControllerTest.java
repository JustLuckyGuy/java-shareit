package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient client;
    private ItemDto dto;
    private ItemDto dto2;
    private CommentDTO comment;

    @BeforeEach
    void before() {
        dto = ItemDto.builder()
                .name("barby doll")
                .description("it is what it is")
                .available(true)
                .build();

        dto2 = ItemDto.builder()
                .name("ken doll")
                .description("bruh")
                .available(false)
                .build();

        comment = CommentDTO.builder()
                .authorName("Mafiosy")
                .text("carbonara is better")
                .build();
    }

    @Test
    void testPost() throws Exception {
        Mockito.when(client.postItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(post("/items")
                .header("X-Sharer-User-Id", 313)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
        ).andExpect(jsonPath("name", is(dto.getName())));

        Mockito.verify(client, Mockito.times(1))
                .postItem(313, dto);
    }

    @Test
    void testUpdate() throws Exception {
        Mockito.when(client.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(dto2));

        mvc.perform(patch("/items/12")
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("name", is(dto2.getName())))
                .andExpect(jsonPath("description", is(dto2.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .updateItem(3, 12, dto2);
    }

    @Test
    void testGetItem() throws Exception {
        Mockito.when(client.getItem(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(get("/items/2")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("name", is(dto.getName())))
                .andExpect(jsonPath("description", is(dto.getDescription())))
                .andExpect(jsonPath("available", is(dto.getAvailable())));

        Mockito.verify(client, Mockito.times(1))
                .getItem(2);
    }

    @Test
    void testGetUserItems() throws Exception {
        Mockito.when(client.getUserItems(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(Arrays.asList(dto, dto2)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("[0]name", is(dto.getName())))
                .andExpect(jsonPath("[0]description", is(dto.getDescription())))
                .andExpect(jsonPath("[1]name", is(dto2.getName())));

        Mockito.verify(client, Mockito.times(1))
                .getUserItems(3);
    }

    @Test
    void testItemSearch() throws Exception {
        String text = dto.getDescription();

        Mockito.when(client.itemSearch(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(dto2)));

        mvc.perform(get("/items/search?text=" + text)
                        .header("X-Sharer-User-Id", 47)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("[0]name", is(dto2.getName())))
                .andExpect(jsonPath("[0]description", is(dto2.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .itemSearch(text);
    }

    @Test
    void testDeleteItem() throws Exception {
        Mockito.when(client.deleteItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/items/97")
                .header("X-Sharer-User-Id", 3221)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        );

        Mockito.verify(client, Mockito.times(1))
                .deleteItem(3221, 97);
    }

    @Test
    void testAddComment() throws Exception {
        Mockito.when(client.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDTO.class)))
                .thenReturn(ResponseEntity.ok(comment));

        mvc.perform(post("/items/32/comment")
                        .header("X-Sharer-User-Id", 333)
                        .content(mapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("text", is(comment.getText())));

        Mockito.verify(client, Mockito.times(1))
                .addComment(333, 32, comment);
    }

    @Test
    void testPostWithoutUserIdHeader() throws Exception {
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWithoutUserIdHeader() throws Exception {
        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserItemsWithoutUserIdHeader() throws Exception {
        mvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteWithoutUserIdHeader() throws Exception {
        mvc.perform(delete("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddCommentWithoutUserIdHeader() throws Exception {
        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithInvalidItemDto() throws Exception {
        ItemDto invalidDto = ItemDto.builder()
                .description("description")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithEmptyName() throws Exception {
        ItemDto invalidDto = ItemDto.builder()
                .name("")
                .description("description")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithNullAvailable() throws Exception {
        ItemDto invalidDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(null)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithLongName() throws Exception {
        String longName = "a".repeat(101);
        ItemDto invalidDto = ItemDto.builder()
                .name(longName)
                .description("description")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddCommentWithEmptyText() throws Exception {
        CommentDTO invalidComment = CommentDTO.builder()
                .authorName("Author")
                .text("")
                .build();

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddCommentWithBlankText() throws Exception {
        CommentDTO invalidComment = CommentDTO.builder()
                .authorName("Author")
                .text("   ")
                .build();

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetItemWithZeroId() throws Exception {
        Mockito.when(client.getItem(0L))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(get("/items/0"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateWithZeroIds() throws Exception {
        Mockito.when(client.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/items/0")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testItemSearchWithEmptyText() throws Exception {
        Mockito.when(client.itemSearch(""))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/items/search?text=")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void testItemSearchWithSpaceText() throws Exception {
        Mockito.when(client.itemSearch("   "))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/items/search?text=   ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void testGetUserItemsWithEmptyList() throws Exception {
        Mockito.when(client.getUserItems(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void testItemSearchWithSpecialCharacters() throws Exception {
        String specialText = "test&search?param=value";
        Mockito.when(client.itemSearch(specialText))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(dto)));

        mvc.perform(get("/items/search?text=" + specialText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateOnlyName() throws Exception {
        ItemDto updateDto = dto;
        updateDto.setName("Updated Name");

        ItemDto expectedDto = ItemDto.builder()
                .name("Updated Name")
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();

        Mockito.when(client.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(expectedDto));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Updated Name")));
    }

    @Test
    void testUpdateOnlyDescription() throws Exception {
        ItemDto updateDto = dto;
        updateDto.setDescription("Updated Description");

        ItemDto expectedDto = ItemDto.builder()
                .name(dto.getName())
                .description("Updated Description")
                .available(dto.getAvailable())
                .build();

        Mockito.when(client.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(expectedDto));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", is("Updated Description")));
    }

    @Test
    void testUpdateWithPartialData() throws Exception {

        ItemDto nameOnlyUpdate = ItemDto.builder()
                .name("Updated Name")
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();

        Mockito.when(client.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nameOnlyUpdate)))
                .andExpect(status().isOk());

        ItemDto descOnlyUpdate = ItemDto.builder()
                .name(dto.getName())
                .description("Updated Desc")
                .available(dto.getAvailable())
                .build();

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(descOnlyUpdate)))
                .andExpect(status().isOk());

        ItemDto availOnlyUpdate = ItemDto.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(false)
                .build();

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(availOnlyUpdate)))
                .andExpect(status().isOk());
    }
}
