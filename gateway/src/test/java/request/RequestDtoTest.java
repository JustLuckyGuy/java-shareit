package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
class RequestDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeAndDeserializeItemRequestDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill for home renovation")
                .items(List.of(itemDto))
                .created(LocalDateTime.of(2023, 10, 1, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto deserialized = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getDescription()).isEqualTo("Need a drill for home renovation");
        assertThat(deserialized.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(deserialized.getItems()).hasSize(1);
        assertThat(deserialized.getItems().get(0).getName()).isEqualTo("Test Item");
        assertThat(deserialized.getItems().get(0).getDescription()).isEqualTo("Test Description");
    }

    @Test
    void shouldSerializeItemRequestDtoWithAllFields() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill for home renovation")
                .created(LocalDateTime.of(2023, 10, 1, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill for home renovation\"");
        assertThat(json).contains("\"created\":\"2023-10-01T10:00:00\"");
    }

    @Test
    void shouldDeserializeItemRequestDtoFromJson() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Need a drill for home renovation",
                    "created": "2023-10-01T10:00:00"
                }
                """;

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Need a drill for home renovation");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(itemRequestDto.getItems()).isNull();
    }

    @Test
    void shouldHandleNullFieldsInItemRequestDto() throws Exception {
        String json = "{\"description\":\"Test description\"}";

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getDescription()).isEqualTo("Test description");
        assertThat(itemRequestDto.getId()).isNull();
        assertThat(itemRequestDto.getItems()).isNull();
        assertThat(itemRequestDto.getCreated()).isNull();
    }

    @Test
    void shouldDeserializeItemRequestDtoWithItemsList() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Need a drill for home renovation",
                    "created": "2023-10-01T10:00:00",
                    "items": [
                        {
                            "id": 1,
                            "name": "Drill",
                            "description": "Powerful drill",
                            "available": true
                        },
                        {
                            "id": 2,
                            "name": "Hammer",
                            "description": "Heavy duty hammer",
                            "available": false
                        }
                    ]
                }
                """;

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Need a drill for home renovation");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(itemRequestDto.getItems()).hasSize(2);
        assertThat(itemRequestDto.getItems().get(0).getName()).isEqualTo("Drill");
        assertThat(itemRequestDto.getItems().get(0).getAvailable()).isTrue();
        assertThat(itemRequestDto.getItems().get(1).getName()).isEqualTo("Hammer");
        assertThat(itemRequestDto.getItems().get(1).getAvailable()).isFalse();
    }

    @Test
    void shouldHandleEmptyItemsList() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Need a drill for home renovation",
                    "created": "2023-10-01T10:00:00",
                    "items": []
                }
                """;

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Need a drill for home renovation");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(itemRequestDto.getItems()).isEmpty();
    }

    @Test
    void shouldHandleLongDescription() throws Exception {
        String longDescription = "A".repeat(1000);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(longDescription)
                .created(LocalDateTime.of(2023, 10, 1, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto deserialized = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(deserialized.getDescription()).isEqualTo(longDescription);
        assertThat(deserialized.getDescription().length()).isEqualTo(1000);
    }

    @Test
    void shouldHandleSpecialCharactersInDescription() throws Exception {
        String descriptionWithSpecialChars = "Need item with special chars: !@#$%^&*()_+{}[]|:;<>,.?/~`";
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(descriptionWithSpecialChars)
                .created(LocalDateTime.of(2023, 10, 1, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto deserialized = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(deserialized.getDescription()).isEqualTo(descriptionWithSpecialChars);
    }

    @Test
    void shouldHandleNullCreatedDate() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Test description"
                }
                """;

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Test description");
        assertThat(itemRequestDto.getCreated()).isNull();
    }

    @Test
    void shouldHandleFutureCreatedDate() throws Exception {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Future request")
                .created(futureDate)
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto deserialized = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(deserialized.getCreated()).isEqualTo(futureDate);
    }

    @Test
    void shouldHandlePastCreatedDate() throws Exception {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Past request")
                .created(pastDate)
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto deserialized = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(deserialized.getCreated()).isEqualTo(pastDate);
    }

    @Test
    void shouldSerializeWithoutId() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("New request without ID")
                .created(LocalDateTime.of(2023, 10, 1, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto deserialized = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(deserialized.getId()).isNull();
        assertThat(deserialized.getDescription()).isEqualTo("New request without ID");
        assertThat(deserialized.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
    }

    @Test
    void shouldHandleComplexNestedItems() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Complex request with nested items",
                    "created": "2023-10-01T10:00:00",
                    "items": [
                        {
                            "id": 1,
                            "name": "Item 1",
                            "description": "Description 1",
                            "available": true,
                            "request": 1,
                            "nextBooking": {
                                "id": 10,
                                "bookerId": 100,
                                "start": "2023-10-02T10:00:00",
                                "end": "2023-10-03T10:00:00"
                            },
                            "lastBooking": {
                                "id": 11,
                                "bookerId": 101,
                                "start": "2023-09-25T10:00:00",
                                "end": "2023-09-26T10:00:00"
                            },
                            "comments": [
                                {
                                    "id": 1,
                                    "authorName": "User1",
                                    "text": "Great item!",
                                    "created": "2023-09-20T10:00:00"
                                }
                            ],
                            "requestId": 1
                        }
                    ]
                }
                """;

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Complex request with nested items");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(itemRequestDto.getItems()).hasSize(1);

        ItemDto nestedItem = itemRequestDto.getItems().get(0);
        assertThat(nestedItem.getName()).isEqualTo("Item 1");
        assertThat(nestedItem.getAvailable()).isTrue();
        assertThat(nestedItem.getNextBooking()).isNotNull();
        assertThat(nestedItem.getNextBooking().getId()).isEqualTo(10L);
        assertThat(nestedItem.getLastBooking()).isNotNull();
        assertThat(nestedItem.getLastBooking().getId()).isEqualTo(11L);
        assertThat(nestedItem.getComments()).hasSize(1);
        assertThat(nestedItem.getComments().get(0).getAuthorName()).isEqualTo("User1");
    }
}
