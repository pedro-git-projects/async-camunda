package dev.nilptr.workers;

import dev.nilptr.services.ChargeCreditCardService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChargeCreditCardWorker {
    private final ChargeCreditCardService chargeCreditCardService;

    @JobWorker(type = "chargeCreditCard", autoComplete = false)
    public void handleChargeCreditCard(JobClient client, ActivatedJob job, @Variable(name = "totalWithTax") Double totalWithTax) {
        log.info("Received variable " + totalWithTax);
        chargeCreditCardService.findAmountChargedReactive(totalWithTax)
                .flatMap(resultMap ->
                        Mono.fromCompletionStage(client.newCompleteCommand(job.getKey())
                                .variables(resultMap)
                                .send())
                )
                .doOnError(throwable -> {
                    log.error("Failed to complete job: " + throwable.getMessage());
                    client.newFailCommand(job.getKey())
                            .retries(job.getRetries() - 1)
                            .errorMessage(throwable.getMessage())
                            .send()
                            .exceptionally(failThrowable -> {
                                log.error("Failed to fail job: " + failThrowable.getMessage());
                                return null;
                            });
                })
                .subscribe();
    }

}