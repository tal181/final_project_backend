
package com.dq.project.rest.controllers;

import com.dq.project.rest.domain.DataSetValidationRequest;
import com.dq.project.rest.domain.SchemaColumn;
import com.dq.project.rest.domain.SchemaRequest;
import com.dq.project.rest.domain.ValidationResponse;
import com.dq.project.rest.errors.ServiceError;
import com.dq.project.rest.services.DataSetValidationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "api/schema")
@Tag(name = "Get schema", description = "Get schema")
public class SchemaController {

    @Resource
    private DataSetValidationService validationService;

    @RequestMapping(method = RequestMethod.POST)
    public  List<SchemaColumn> validate(@RequestBody @Valid @Parameter(required = true)
                                   SchemaRequest request) throws ServiceError {


        List<SchemaColumn> schema = validationService.getSchema(request);
        return schema;
    }
}
