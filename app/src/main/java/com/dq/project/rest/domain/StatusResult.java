package com.dq.project.rest.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatusResult {
    String deequ;
    String sql;
    String dqdf;
}
