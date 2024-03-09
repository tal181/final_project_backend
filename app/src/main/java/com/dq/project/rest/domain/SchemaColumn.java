package com.dq.project.rest.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchemaColumn {
    String columnName;
    String type;
}
