package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class LibraryEventProducer {
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;
    String topic = "library-events";

    @Autowired
    ObjectMapper objectMapper;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.sendDefault(key, value);

        //onSuccess
        completableFuture.thenApply( result-> {
            handleSuccess(key, value,result);
            return result;
        });

        //onFailure
        completableFuture.exceptionally( throwable -> {
            handleFailure(key, value, throwable);
            return  null;
        });

    }

    public void sendLibraryEvent_Approach2(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord =  buildProducerRecord(key, value, topic);

        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.send(producerRecord);

        //onSuccess
        completableFuture.thenApply( result-> {
            handleSuccess(key, value,result);
            return result;
        });

        //onFailure
        completableFuture.exceptionally( throwable -> {
            handleFailure(key, value, throwable);
            return  null;
        });

    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {

        List<Header> recordHeaders = List.of(new RecordHeader("events-source", "scanner".getBytes()));

        return new ProducerRecord<>(topic, null, key, value, recordHeaders);

    }

    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer, String> sendResult = null;

        try {
            sendResult = kafkaTemplate.sendDefault(key, value).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            log.error("ExecutionException | InterruptedException sending the message. Exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception sending the message. Exception: {}", e.getMessage());
            throw e;
        }

        return sendResult;
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message sent successfully for the key: {}; value: {}; partition: {}", key, value, result.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the message. Exception: {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable e) {
            log.error("Error in handleFailure: {}", e.getMessage());
        }
    }

}
