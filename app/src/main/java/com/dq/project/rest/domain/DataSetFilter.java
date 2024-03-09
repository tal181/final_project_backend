package com.dq.project.rest.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.spark.sql.types.StructType;

@AllArgsConstructor(staticName = "of")
@Data
public class DataSetFilter {
    private StructType structType;
    private String fqn;
    private String filter;
}
