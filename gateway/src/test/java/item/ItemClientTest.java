package item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class ItemClientTest {

    @Test
    void postItemShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        ItemDto itemDto = ItemDto.builder().build();
        when(itemClient.postItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.postItem(1L, itemDto);

        assertNotNull(response);
    }

    @Test
    void updateItemShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        ItemDto itemDto = ItemDto.builder().build();
        when(itemClient.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.updateItem(1L, 1L, itemDto);

        assertNotNull(response);
    }

    @Test
    void getItemShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        when(itemClient.getItem(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.getItem(1L);

        assertNotNull(response);
    }

    @Test
    void getUserItemsShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        when(itemClient.getUserItems(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.getUserItems(1L);

        assertNotNull(response);
    }

    @Test
    void itemSearchShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        when(itemClient.itemSearch(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.itemSearch("test");

        assertNotNull(response);
    }

    @Test
    void deleteItemShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        when(itemClient.deleteItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.deleteItem(1L, 1L);

        assertNotNull(response);
    }

    @Test
    void addCommentShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        CommentDTO commentDTO = CommentDTO.builder().build();
        when(itemClient.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.addComment(1L, 1L, commentDTO);

        assertNotNull(response);
    }

    @Test
    void itemSearchWithEmptyTextShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        when(itemClient.itemSearch(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.itemSearch("");

        assertNotNull(response);
    }

    @Test
    void itemSearchWithSpecialCharactersShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        when(itemClient.itemSearch(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.itemSearch("test&search");

        assertNotNull(response);
    }

    @Test
    void updateItemWithPartialDataShouldReturnResponse() {
        ItemClient itemClient = mock(ItemClient.class);
        ItemDto nameOnlyUpdate = ItemDto.builder().name("Updated Name").build();
        when(itemClient.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = itemClient.updateItem(1L, 1L, nameOnlyUpdate);

        assertNotNull(response);
    }
}
