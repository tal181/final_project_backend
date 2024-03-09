package com.dq.project.rest.domain;

import lombok.Data;

@Data
public class DataSetValidationRequest {
    String strategy;
    String dataSetPath;
    String schemaConfig;
}
