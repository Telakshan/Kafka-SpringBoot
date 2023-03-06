package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import com.learnkafka.producer.LibraryEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class LibraryEventsController {
    @Autowired
    LibraryEventProducer libraryEventProducer;
    @GetMapping("/status/check")
    public String status(){
        return "working...";
    }
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        //libraryEvent.setLibraryEventId(1 + (int)(Math.random() * ((10000 - 1) + 1)));

        //invoke the kafka producer asynchronously
       // libraryEventProducer.sendLibraryEvent(libraryEvent);

        //invoke the kafka producer synchronously
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

//    @PutMapping
//    public ResponseEntity<LibraryEvent> updateLibraryEvent(){
//
//    }
}
