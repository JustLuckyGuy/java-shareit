package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Objects;
import java.util.function.Supplier;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private ItemClient itemClient;
    private static final String BASE_URL = "http://test-server";
    private static final String API_PREFIX = "/items";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL + API_PREFIX);
        when(builder.uriTemplateHandler(Mockito.any(UriTemplateHandler.class)))
                .thenReturn(builder);
        when(builder.requestFactory(Mockito.any(Supplier.class)))
                .thenReturn(builder);
        when(builder.build())
                .thenReturn(restTemplate);
        builder.uriTemplateHandler(uriBuilderFactory);

        itemClient = new ItemClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void postItemShouldCallPostWithItemDataAndUserIdHeader() {
        long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        itemClient.postItem(userId, itemDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == itemDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void updateItemShouldCallPatchWithItemIdAndUpdateData() {
        long userId = 1L;
        long itemId = 123L;
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        itemClient.updateItem(userId, itemId, updateDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == updateDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void updateItemWithPartialDataShouldCallPatchWithPartialUpdate() {
        long userId = 1L;
        long itemId = 123L;
        ItemDto nameOnlyUpdate = ItemDto.builder()
                .name("Updated Name Only")
                .build();

        itemClient.updateItem(userId, itemId, nameOnlyUpdate);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == nameOnlyUpdate &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getItemShouldCallGetWithItemIdInPath() {
        long itemId = 123L;

        itemClient.getItem(itemId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        !entity.getHeaders().containsKey(X_SHARER_USER_ID)),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getItemWithZeroIdShouldCallGetWithZeroIdPath() {
        long itemId = 0L;

        itemClient.getItem(itemId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/0"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        !entity.getHeaders().containsKey(X_SHARER_USER_ID)),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getUserItemsShouldCallGetWithEmptyPathAndUserIdHeader() {
        long userId = 1L;

        itemClient.getUserItems(userId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getUserItemsWithDifferentUsersShouldCallGetWithDifferentUserIds() {

        itemClient.getUserItems(1L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );


        itemClient.getUserItems(2L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "2")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void deleteItemShouldCallDeleteWithItemIdAndUserIdHeader() {
        long userId = 1L;
        long itemId = 123L;

        itemClient.deleteItem(userId, itemId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123"),
                Mockito.eq(HttpMethod.DELETE),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void addCommentShouldCallPostWithCommentPathAndData() {
        long userId = 1L;
        long itemId = 123L;
        CommentDTO commentDTO = CommentDTO.builder()
                .text("Great item!")
                .build();

        itemClient.addComment(userId, itemId, commentDTO);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123/comment"),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == commentDTO &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void addCommentWithFullCommentDataShouldCallPostWithCompleteComment() {
        long userId = 1L;
        long itemId = 123L;
        CommentDTO fullComment = CommentDTO.builder()
                .id(456L)
                .authorName("John Doe")
                .text("Excellent item, very useful!")
                .created("2023-10-01T10:00:00")
                .build();

        itemClient.addComment(userId, itemId, fullComment);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123/comment"),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == fullComment &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void postItemWithFullItemDataShouldCallPostWithCompleteItem() {
        long userId = 1L;
        ItemDto fullItem = ItemDto.builder()
                .id(123L)
                .name("Complete Test Item")
                .description("Very detailed description of the test item")
                .available(true)
                .request(456L)
                .requestId(789L)
                .build();

        itemClient.postItem(userId, fullItem);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == fullItem &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void updateItemWithDifferentItemIdsShouldCallPatchWithCorrectPaths() {
        long userId = 1L;


        ItemDto update1 = ItemDto.builder().name("Update 1").build();
        itemClient.updateItem(userId, 1L, update1);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/1"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );


        ItemDto update2 = ItemDto.builder().name("Update 2").build();
        itemClient.updateItem(userId, 999L, update2);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/999"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void deleteItemWithDifferentUsersAndItemsShouldCallDeleteWithCorrectParameters() {

        itemClient.deleteItem(1L, 1L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/1"),
                Mockito.eq(HttpMethod.DELETE),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );

        itemClient.deleteItem(2L, 2L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/2"),
                Mockito.eq(HttpMethod.DELETE),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "2")),
                Mockito.eq(Object.class)
        );
    }
}
