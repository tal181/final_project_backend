package com.dq.project.rest.controllers;

import com.dq.project.rest.domain.DataSetValidationRequest;
import com.dq.project.rest.domain.ValidationResponse;
import com.dq.project.rest.errors.ServiceError;
import com.dq.project.rest.services.DataSetValidationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "api/strategy/run")
@Tag(name = "Run strategy", description = "Run strategy")
public class DataSetValidationController {

    @Resource
    private DataSetValidationService validationService;

    @RequestMapping(method = RequestMethod.POST)
    public ValidationResponse validate(@RequestBody @Valid @Parameter(required = true)
                                           DataSetValidationRequest request) throws ServiceError {


        ValidationResponse queryValidationResult = validationService.validate(request);
        return queryValidationResult;
    }
}
