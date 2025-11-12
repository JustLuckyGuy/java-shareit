package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
class ItemDtoTest {
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldSerializeAndDeserializeBookingShortDto() throws Exception {
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(bookingShortDto);
        BookingShortDto deserialized = objectMapper.readValue(json, BookingShortDto.class);

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getBookerId()).isEqualTo(2L);
        assertThat(deserialized.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(deserialized.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 2, 10, 0));
    }

    @Test
    void shouldHandleNullFieldsInBookingShortDto() throws Exception {
        String json = "{\"id\":1}";

        BookingShortDto bookingShortDto = objectMapper.readValue(json, BookingShortDto.class);

        assertThat(bookingShortDto.getId()).isEqualTo(1L);
        assertThat(bookingShortDto.getBookerId()).isNull();
        assertThat(bookingShortDto.getStart()).isNull();
        assertThat(bookingShortDto.getEnd()).isNull();
    }

    @Test
    void shouldSerializeBookingShortDtoWithAllFields() throws Exception {
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(bookingShortDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"bookerId\":2");
        assertThat(json).contains("\"start\":\"2023-10-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2023-10-02T10:00:00\"");
    }


    @Test
    void shouldSerializeAndDeserializeCommentDTO() throws Exception {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1L)
                .authorName("John Doe")
                .text("Great item!")
                .created("2023-10-01T10:00:00")
                .build();

        String json = objectMapper.writeValueAsString(commentDTO);
        CommentDTO deserialized = objectMapper.readValue(json, CommentDTO.class);

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getAuthorName()).isEqualTo("John Doe");
        assertThat(deserialized.getText()).isEqualTo("Great item!");
        assertThat(deserialized.getCreated()).isEqualTo("2023-10-01T10:00:00");
    }

    @Test
    void shouldHandleNullFieldsInCommentDTO() throws Exception {
        String json = "{\"id\":1,\"text\":\"Test comment\"}";

        CommentDTO commentDTO = objectMapper.readValue(json, CommentDTO.class);

        assertThat(commentDTO.getId()).isEqualTo(1L);
        assertThat(commentDTO.getText()).isEqualTo("Test comment");
        assertThat(commentDTO.getAuthorName()).isNull();
        assertThat(commentDTO.getCreated()).isNull();
    }

    @Test
    void shouldSerializeCommentDTOWithAllFields() throws Exception {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1L)
                .authorName("John Doe")
                .text("Great item!")
                .created("2023-10-01T10:00:00")
                .build();

        String json = objectMapper.writeValueAsString(commentDTO);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"created\":\"2023-10-01T10:00:00\"");
    }

    @Test
    void shouldDeserializeCommentDTOWithOnlyRequiredFields() throws Exception {
        String json = "{\"text\":\"Required text\"}";

        CommentDTO commentDTO = objectMapper.readValue(json, CommentDTO.class);

        assertThat(commentDTO.getText()).isEqualTo("Required text");
        assertThat(commentDTO.getId()).isEqualTo(0L);
        assertThat(commentDTO.getAuthorName()).isNull();
        assertThat(commentDTO.getCreated()).isNull();
    }


    @Test
    void shouldSerializeAndDeserializeItemDto() throws Exception {
        BookingShortDto nextBooking = BookingShortDto.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        BookingShortDto lastBooking = BookingShortDto.builder()
                .id(3L)
                .bookerId(4L)
                .start(LocalDateTime.of(2023, 10, 3, 10, 0))
                .end(LocalDateTime.of(2023, 10, 4, 10, 0))
                .build();

        CommentDTO comment = CommentDTO.builder()
                .id(1L)
                .authorName("John Doe")
                .text("Great item!")
                .created("2023-10-01T10:00:00")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .request(5L)
                .nextBooking(nextBooking)
                .lastBooking(lastBooking)
                .comments(List.of(comment))
                .requestId(6L)
                .build();

        String json = objectMapper.writeValueAsString(itemDto);
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getName()).isEqualTo("Test Item");
        assertThat(deserialized.getDescription()).isEqualTo("Test Description");
        assertThat(deserialized.getAvailable()).isTrue();
        assertThat(deserialized.getRequest()).isEqualTo(5L);
        assertThat(deserialized.getRequestId()).isEqualTo(6L);


        assertThat(deserialized.getNextBooking()).isNotNull();
        assertThat(deserialized.getNextBooking().getId()).isEqualTo(1L);
        assertThat(deserialized.getLastBooking()).isNotNull();
        assertThat(deserialized.getLastBooking().getId()).isEqualTo(3L);
        assertThat(deserialized.getComments()).hasSize(1);
        assertThat(deserialized.getComments().get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    void shouldHandleNullFieldsInItemDto() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test Item");
        assertThat(itemDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequest()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void shouldSerializeItemDtoWithAllFields() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .request(5L)
                .requestId(6L)
                .build();

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"description\":\"Test Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"request\":5");
        assertThat(json).contains("\"requestId\":6");
    }

    @Test
    void shouldDeserializeItemDtoWithNestedObjects() throws Exception {
        String json = """
                {
                    "id": 1,
                    "name": "Test Item",
                    "description": "Test Description",
                    "available": true,
                    "request": 5,
                    "nextBooking": {
                        "id": 1,
                        "bookerId": 2,
                        "start": "2023-10-01T10:00:00",
                        "end": "2023-10-02T10:00:00"
                    },
                    "lastBooking": {
                        "id": 3,
                        "bookerId": 4,
                        "start": "2023-10-03T10:00:00",
                        "end": "2023-10-04T10:00:00"
                    },
                    "comments": [
                        {
                            "id": 1,
                            "authorName": "John Doe",
                            "text": "Great item!",
                            "created": "2023-10-01T10:00:00"
                        }
                    ],
                    "requestId": 6
                }
                """;

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test Item");
        assertThat(itemDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequest()).isEqualTo(5L);
        assertThat(itemDto.getRequestId()).isEqualTo(6L);


        assertThat(itemDto.getNextBooking()).isNotNull();
        assertThat(itemDto.getNextBooking().getId()).isEqualTo(1L);
        assertThat(itemDto.getNextBooking().getBookerId()).isEqualTo(2L);
        assertThat(itemDto.getLastBooking()).isNotNull();
        assertThat(itemDto.getLastBooking().getId()).isEqualTo(3L);
        assertThat(itemDto.getLastBooking().getBookerId()).isEqualTo(4L);
        assertThat(itemDto.getComments()).hasSize(1);
        assertThat(itemDto.getComments().get(0).getAuthorName()).isEqualTo("John Doe");
        assertThat(itemDto.getComments().get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    void shouldHandleEmptyCommentsListInItemDto() throws Exception {
        String json = """
                {
                    "id": 1,
                    "name": "Test Item",
                    "description": "Test Description",
                    "available": true,
                    "comments": []
                }
                """;

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test Item");
        assertThat(itemDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getComments()).isEmpty();
    }

    @Test
    void shouldHandleNullBooleanAvailableInItemDto() throws Exception {
        String json = """
                {
                    "id": 1,
                    "name": "Test Item",
                    "description": "Test Description"
                }
                """;

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test Item");
        assertThat(itemDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemDto.getAvailable()).isNull();
    }

    @Test
    void shouldHandleLongNameInItemDto() throws Exception {
        String longName = "A".repeat(100);
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(longName)
                .description("Test Description")
                .available(true)
                .build();

        String json = objectMapper.writeValueAsString(itemDto);
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertThat(deserialized.getName()).isEqualTo(longName);
        assertThat(deserialized.getName().length()).isEqualTo(100);
    }
}
