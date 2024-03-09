package com.dq.project.rest.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationResponse {

    ValidationResult sql;
    ValidationResult deequ;
    ValidationResult dqdf;
}
