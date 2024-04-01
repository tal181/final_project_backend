
package com.dq.project.rest.services;

//import com.dq.project.model.DataSetValidation;
//import com.dq.project.model.ValidationResult;
import com.dq.project.rest.domain.*;

import java.util.List;

public interface DataSetValidationService {

    /**
     * Validate a dataset filter
     *
     * @param dataSetValidation represented as model with the dataset details
     * @return whether the dataset is valid, and if not - the reason
     */
    ValidationResponse validate(DataSetValidationRequest dataSetValidation);

    List<SchemaColumn> getSchema(SchemaRequest schemaRequest);

    StatusResult getStatus(String tid);

    String createTid();
    
}
