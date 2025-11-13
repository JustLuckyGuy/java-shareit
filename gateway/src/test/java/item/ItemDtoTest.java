package item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class ItemDtoTest {
    @Autowired
    private JacksonTester<BookingShortDto> bookingShortDtoJson;

    @Autowired
    private JacksonTester<CommentDTO> commentDtoJson;

    @Autowired
    private JacksonTester<ItemDto> itemDtoJson;

    @Test
    void shouldSerializeAndDeserializeBookingShortDto() throws Exception {
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        JsonContent<BookingShortDto> result = bookingShortDtoJson.write(bookingShortDto);
        BookingShortDto deserialized = bookingShortDtoJson.parseObject(result.getJson());

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getBookerId()).isEqualTo(2L);
        assertThat(deserialized.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(deserialized.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 2, 10, 0));
    }

    @Test
    void shouldHandleNullFieldsInBookingShortDto() throws Exception {
        String content = "{\"id\":1}";

        BookingShortDto bookingShortDto = bookingShortDtoJson.parseObject(content);

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

        JsonContent<BookingShortDto> result = bookingShortDtoJson.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-02T10:00:00");
    }

    @Test
    void shouldSerializeAndDeserializeCommentDTO() throws Exception {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1L)
                .authorName("John Doe")
                .text("Great item!")
                .created("2023-10-01T10:00:00")
                .build();

        JsonContent<CommentDTO> result = commentDtoJson.write(commentDTO);
        CommentDTO deserialized = commentDtoJson.parseObject(result.getJson());

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getAuthorName()).isEqualTo("John Doe");
        assertThat(deserialized.getText()).isEqualTo("Great item!");
        assertThat(deserialized.getCreated()).isEqualTo("2023-10-01T10:00:00");
    }

    @Test
    void shouldHandleNullFieldsInCommentDTO() throws Exception {
        String content = "{\"id\":1,\"text\":\"Test comment\"}";

        CommentDTO commentDTO = commentDtoJson.parseObject(content);

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

        JsonContent<CommentDTO> result = commentDtoJson.write(commentDTO);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-01T10:00:00");
    }

    @Test
    void shouldDeserializeCommentDTOWithOnlyRequiredFields() throws Exception {
        String content = "{\"text\":\"Required text\"}";

        CommentDTO commentDTO = commentDtoJson.parseObject(content);

        assertThat(commentDTO.getText()).isEqualTo("Required text");
        assertThat(commentDTO.getId()).isZero();
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

        JsonContent<ItemDto> result = itemDtoJson.write(itemDto);
        ItemDto deserialized = itemDtoJson.parseObject(result.getJson());

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
        assertThat(deserialized.getComments().getFirst().getText()).isEqualTo("Great item!");
    }

    @Test
    void shouldHandleNullFieldsInItemDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true}";

        ItemDto itemDto = itemDtoJson.parseObject(content);

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

        JsonContent<ItemDto> result = itemDtoJson.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(6);
    }

    @Test
    void shouldDeserializeItemDtoWithNestedObjects() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"request\":5,\"nextBooking\":{\"id\":1,\"bookerId\":2,\"start\":\"2023-10-01T10:00:00\",\"end\":\"2023-10-02T10:00:00\"},\"lastBooking\":{\"id\":3,\"bookerId\":4,\"start\":\"2023-10-03T10:00:00\",\"end\":\"2023-10-04T10:00:00\"},\"comments\":[{\"id\":1,\"authorName\":\"John Doe\",\"text\":\"Great item!\",\"created\":\"2023-10-01T10:00:00\"}],\"requestId\":6}";

        ItemDto itemDto = itemDtoJson.parseObject(content);

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
        assertThat(itemDto.getComments().getFirst().getAuthorName()).isEqualTo("John Doe");
        assertThat(itemDto.getComments().getFirst().getText()).isEqualTo("Great item!");
    }

    @Test
    void shouldHandleEmptyCommentsListInItemDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"comments\":[]}";

        ItemDto itemDto = itemDtoJson.parseObject(content);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test Item");
        assertThat(itemDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getComments()).isEmpty();
    }

    @Test
    void shouldHandleNullBooleanAvailableInItemDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\"}";

        ItemDto itemDto = itemDtoJson.parseObject(content);

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

        JsonContent<ItemDto> result = itemDtoJson.write(itemDto);
        ItemDto deserialized = itemDtoJson.parseObject(result.getJson());

        assertThat(deserialized.getName()).isEqualTo(longName);
        assertThat(deserialized.getName()).hasSize(100);
    }
}
