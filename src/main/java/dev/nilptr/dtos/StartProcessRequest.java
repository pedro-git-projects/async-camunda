package dev.nilptr.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class StartProcessRequest {
    private String processId;
    private Map<String, Object> variables;
}
