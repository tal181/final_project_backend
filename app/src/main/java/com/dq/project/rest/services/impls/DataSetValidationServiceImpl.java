
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSetValidationServiceImpl implements DataSetValidationService {
    Map<String, StatusResult> tidToStatus = new HashMap<>();

    @Override
    public List<SchemaColumn> getSchema(SchemaRequest schemaRequest) {

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
    public StatusResult getStatus(String tid) {
        log.debug("status is " + tidToStatus.get(tid));
        return tidToStatus.get(tid);
    }

    @Override
    public String createTid() {
        String uuid = UUID.randomUUID().toString();
        StatusResult result = StatusResult.builder().sql("Running").deequ("PENDING").dqdf("PENDING").build();
        tidToStatus.put(uuid, result);
        return uuid;
    }

    @Override
    public ValidationResponse validate(DataSetValidationRequest dataSetValidation) {
        String tid = dataSetValidation.getTid();
        try {
            log.debug("data set validation model is " + dataSetValidation);

            StatusResult statusResult = tidToStatus.get(tid);
            statusResult.setSql("Running");
            tidToStatus.put(tid, statusResult);

            ValidationResult sql = createStrategyResponse(dataSetValidation, "SQL");
            sql.getChecks().add(addMissingCheck());
            Collections.sort(sql.getChecks(), Comparator.comparing(JavaCheckResult::getCheckName));
            statusResult.setSql("Finished");
            tidToStatus.put(tid, statusResult);

            statusResult.setDqdf("Running");
            tidToStatus.put(tid, statusResult);
            ValidationResult dqdf = createStrategyResponse(dataSetValidation, "DQDF");
            Collections.sort(dqdf.getChecks(), Comparator.comparing(JavaCheckResult::getCheckName));
            statusResult.setDqdf("Finished");
            tidToStatus.put(tid, statusResult);

            statusResult.setDeequ("Running");
            tidToStatus.put(tid, statusResult);
            ValidationResult deequ = createStrategyResponse(dataSetValidation, "Deequ");
            Collections.sort(deequ.getChecks(), Comparator.comparing(JavaCheckResult::getCheckName));
            statusResult.setDeequ("Finished");
            tidToStatus.put(tid, statusResult);

            return ValidationResponse.builder().sql(sql).deequ(deequ).dqdf(dqdf).build();
        } catch (UnSupportedDataTypeException e) {
            log.error("error " + e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("error " + e.getMessage());

            // return ValidationResult.failed(e.getMessage());
            return null;
        }
    }


    private JavaCheckResult addMissingCheck() {
        return JavaCheckResult.builder().checkName("No Duplicate column names").value("N/A").build();
    }

    private ValidationResult createStrategyResponse(DataSetValidationRequest dataSetValidation, String strategy) {
        LocalDateTime from = LocalDateTime.now();

        dataSetValidation.setStrategy(strategy);
        List<String> list = buildArgs(dataSetValidation);

        Seq array = JavaConversions.asScalaBuffer(list).toSeq();

        CommandLineArgs args = new CommandLineArgs(array);

        Seq checks = App.run(args, dataSetValidation.getStrategy());
        LocalDateTime to = LocalDateTime.now();

        List<CheckResult> javaChecks = scala.collection.JavaConverters.seqAsJavaList(checks);

        List<JavaCheckResult> javaChecksRes = javaChecks.stream().map(check -> JavaCheckResult.builder().checkName(check.checkName()).value(Boolean.toString(check.status())).build()).collect(Collectors.toList());

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


