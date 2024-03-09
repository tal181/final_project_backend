package com.dq.project.rest.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationResult {
    List<JavaCheckResult> checks;
    Long duration;
}
