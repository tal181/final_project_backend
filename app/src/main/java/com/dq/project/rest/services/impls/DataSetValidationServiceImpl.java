
package com.dq.project.rest.services.impls;

import com.dq.project.rest.domain.*;
import com.dq.project.rest.services.exceptions.UnSupportedDataTypeException;

import com.dq.project.rest.services.DataSetValidationService;
import com.project.App;
import com.project.checks.configuration.CommandLineArgs;
import com.project.checks.domain.CheckResult;
import com.project.checks.domain.Schema;
import com.project.checks.utils.SchemaParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.collection.JavaConverters;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSetValidationServiceImpl implements DataSetValidationService {
    @Override
    public List<SchemaColumn> getSchema(SchemaRequest schemaRequest){

        List<String> list = new ArrayList<>();
        list.add("--fileSchemaConfigPathArg");
        list.add(schemaRequest.getFilePath());

        Seq array = JavaConversions.asScalaBuffer(list).toSeq();

        CommandLineArgs args = new CommandLineArgs(array);


        Seq schema = SchemaParser.parseSchema(args);

        List<Schema> javaSchema = scala.collection.JavaConverters.seqAsJavaList(schema);

        return javaSchema.stream().map(column -> SchemaColumn.builder().columnName(column.column()).type(column.columnType()).build()).collect(Collectors.toList());

    }
    @Override
    public ValidationResponse validate(DataSetValidationRequest dataSetValidation) {
        try {
            log.debug("data set validation model is " + dataSetValidation);

            if(dataSetValidation.getStrategy().equals("all")) {
                ValidationResult sql = createStrategyResponse(dataSetValidation, "SQL");
                ValidationResult dqdf = createStrategyResponse(dataSetValidation, "DQDF");
                ValidationResult deequ = createStrategyResponse(dataSetValidation, "Deequ");

               return  ValidationResponse.builder().sql(sql).dqdf(dqdf).deequ(deequ).build();

            }
            else if(dataSetValidation.getStrategy().equals("SQL")){
                return ValidationResponse.builder().sql(createStrategyResponse(dataSetValidation, "SQL")).build();
            }
            else if(dataSetValidation.getStrategy().equals("DQDF")){
                return ValidationResponse.builder().dqdf(createStrategyResponse(dataSetValidation, "DQDF")).build();
            }
            else{
                return  ValidationResponse.builder().deequ(createStrategyResponse(dataSetValidation, "Deequ")).build();
            }

        } catch (UnSupportedDataTypeException e) {
            log.error("error " + e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("error " + e.getMessage());

            // return ValidationResult.failed(e.getMessage());
            return null;
        }
    }

    private ValidationResult createStrategyResponse(DataSetValidationRequest dataSetValidation, String strategy) {
        LocalDateTime from =  LocalDateTime.now();

        dataSetValidation.setStrategy(strategy);
        List<String> list = buildArgs(dataSetValidation);

        Seq array = JavaConversions.asScalaBuffer(list).toSeq();

        CommandLineArgs args = new CommandLineArgs(array);

        Seq checks = App.run(args, dataSetValidation.getStrategy());
        LocalDateTime to = LocalDateTime.now();

        List<CheckResult> javaChecks = scala.collection.JavaConverters.seqAsJavaList(checks);

        List<JavaCheckResult> javaChecksRes = javaChecks.stream().map(check -> JavaCheckResult.builder().checkName(check.checkName()).value(check.status()).build()).collect(Collectors.toList());

        Duration duration = Duration.between(from, to);

        long seconds = duration.toMillis() / 1000;
        log.info("duration is " + seconds + "seconds");

        return ValidationResult.builder().checks(javaChecksRes).duration(seconds).build();
    }

    private static List<String> buildArgs(DataSetValidationRequest dataSetValidation) {
        List<String> list = new ArrayList<>();
        list.add("--strategy");
        list.add(dataSetValidation.getStrategy());
        list.add("--dataSetPath");
        list.add(dataSetValidation.getDataSetPath());
        list.add("--fileSchemaConfigPathArg");
        list.add(dataSetValidation.getSchemaConfig());
        return list;
    }
}


