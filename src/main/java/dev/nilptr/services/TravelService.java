package dev.nilptr.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class TravelService {
    private CompletableFuture<String> findTimeSlot() {
        return CompletableFuture.supplyAsync(() -> "2024-01-15T10:00:00");
    }

    public Mono<String> findTimeSlotReactive() {
        return Mono.fromFuture(findTimeSlot());
    }

    private CompletableFuture<String> bookFlight() {
        log.info("bookFlight called");
        byte[] arr = new byte[10];
        new Random().nextBytes(arr);
        String flightNumber = new String(arr, StandardCharsets.UTF_8);
        log.info("Flight number: " + flightNumber);
        return CompletableFuture.supplyAsync(() -> flightNumber);
    }

    public Mono<String> bookFlightReactive() {
        return Mono.fromFuture(bookFlight());
    }
}
