
package com.dq.project.rest.controllers;

import com.dq.project.rest.domain.SchemaColumn;
import com.dq.project.rest.domain.SchemaRequest;
import com.dq.project.rest.domain.StatusResult;
import com.dq.project.rest.errors.ServiceError;
import com.dq.project.rest.services.DataSetValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "api/status")
@Tag(name = "Get status", description = "Get status")
public class StatusController {

    @Resource
    private DataSetValidationService validationService;

    @RequestMapping(value = "/{tid}", method = RequestMethod.GET)
    @Operation(description = "Get status by tid ", summary = "Get status")
    public StatusResult getStatus(@PathVariable @Parameter(description = "The tid", required = true) String tid) {


        StatusResult status = validationService.getStatus(tid);
        return status;
    }

    @RequestMapping( method = RequestMethod.POST)
    @Operation(description = "Create tid ", summary = "Create tid")
    public String createTid() {
        String status = validationService.createTid();
        return status;
    }
}
