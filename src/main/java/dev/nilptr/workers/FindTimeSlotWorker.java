package dev.nilptr.workers;

import dev.nilptr.services.TravelService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindTimeSlotWorker {
    private final TravelService travelService;

    @JobWorker(type = "findTimeSlot", autoComplete = false)
    public void handleFindTimeSlotReactive(JobClient client, ActivatedJob job) {
        log.info("starting findTimeSlot job");
        travelService.findTimeSlotReactive().flatMap(timeslotString -> Mono.fromCompletionStage(client.newCompleteCommand(job.getKey()).variables(Map.of("timeslot", timeslotString)).send())).doOnError(throwable -> {
            log.error("Failed to complete job: " + throwable.getMessage());
            client.newFailCommand(job.getKey()).retries(job.getRetries() - 1).errorMessage(throwable.getMessage()).send().exceptionally(failThrowable -> {
                log.error("Failed to fail job: " + failThrowable.getMessage());
                return null;
            });
        }).subscribe();
    }
}
