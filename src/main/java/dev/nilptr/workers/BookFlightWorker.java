package dev.nilptr.workers;

import dev.nilptr.services.TravelService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookFlightWorker {
    private final TravelService travelService;

    @JobWorker(type = "bookFlight", autoComplete = false)
    public void handleBookFlight(JobClient client, ActivatedJob job, @Variable(name = "timeslot") String timeslot) {
        log.info("Received timeslot: " + timeslot);
        travelService.findTimeSlotReactive().flatMap(flightNumber -> Mono.fromCompletionStage(client.newCompleteCommand(job.getKey()).variables(Map.of("flightNumber", flightNumber)).send())).doOnError(throwable -> {
            log.error("Failed to complete job: " + throwable.getMessage());
            client.newFailCommand(job.getKey()).retries(job.getRetries() - 1).errorMessage(throwable.getMessage()).send().exceptionally(failThrowable -> {
                log.error("Failed to fail job: " + failThrowable.getMessage());
                return null;
            });
        }).subscribe();
    }
}
