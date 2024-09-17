package dev.nilptr.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargeCreditCardService {
    private CompletableFuture<Map<String, Double>> findAmountCharged(Double totalWithTax) {
        System.out.println("findAmountCharged called with totalWithTax = " + totalWithTax);
        return CompletableFuture.supplyAsync(() -> {
            log.info("Async task started for totalWithTax = " + totalWithTax);
            try {
                // Simulating activity
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Async task completed for totalWithTax = " + totalWithTax);

            return Map.of("amountCharged", totalWithTax);
        });
    }

    public Mono<Map<String, Double>> findAmountChargedReactive(Double totalWithTax) {
        return Mono.fromFuture(findAmountCharged(totalWithTax));
    }
}
