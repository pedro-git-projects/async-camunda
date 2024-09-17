package dev.nilptr.controllers;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/process")
public class ProcessInstanceController {
    private final ZeebeClient zeebeClient;

    public ProcessInstanceController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @PostMapping("/start/{processId}")
    public Mono<Object> startProcessInstance(@PathVariable String processId) {
        return Mono.create(sink -> {
                    ZeebeFuture<ProcessInstanceEvent> zeebeFuture = zeebeClient
                            .newCreateInstanceCommand()
                            .bpmnProcessId(processId)
                            .latestVersion()
                            .send();

                    zeebeFuture.handle((processInstanceEvent, throwable) -> {
                        if (throwable != null) {
                            sink.error(throwable);
                        } else {
                            sink.success("Process instance started successfully with key: "
                                    + processInstanceEvent.getProcessInstanceKey());
                        }
                        return null;
                    });
                })
                .onErrorResume(e ->
                        Mono.just("Failed to start process instance: " + e.getMessage())
                );
    }

}
