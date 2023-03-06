package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducer eventProducer;

    @Test
    void sendLibraryEvent_Failure() throws JsonProcessingException, ExecutionException, InterruptedException {

        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Patrick Rothfuss")
                .bookName("Name of the wind")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Exception calling Kafka");
        });

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        assertThrows(Exception.class, () -> eventProducer.sendLibraryEvent_Approach2(libraryEvent).get());

    }

}
