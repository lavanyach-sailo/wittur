package com.sailotech.testautomation.database;

import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.beans.JobUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StatusUpdater {

    private static RestTemplate restTemplate = new RestTemplateBuilder().rootUri("http://localhost:5000").build();

    public static void saveJobURL(String runID, String url, String videoUrl) {
        try {
            HttpEntity<JobUpdateRequest> entity = new HttpEntity<>(JobUpdateRequest.builder()
                    .reportPortalUrl(url).ltVideoUrl(videoUrl).build());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    String.format("/api/releases/jobUpdate/%s?bypass=true", runID),
                    HttpMethod.PATCH, entity, String.class, Map.of());
            log.info(
                    "Successfully updated the report portal url to {} for run id {}, response code {}, response body {}",
                    url, runID, response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.error("Failed updating the test case id {}, url {}, error: {}", runID, url, e.getMessage());
        }
    }

    public static void updateStatus(String runID, String testCaseStepID, String status, String moduleId,
            String testCaseId, JobUpdateRequest jobUpdateRequest) {
        try {

            Map<String, Object> requestBody = new HashMap<>();
            // jobUpdateRequest.setScreenshot(null);
            requestBody.put("deleteFile", TestBase.deleteScreenshotFile);
            requestBody.put("moduleId", moduleId);
            requestBody.put("testCaseId", testCaseId);
            requestBody.put("testNodeId", TestBase.testNodesMap.get(testCaseId));
            if (jobUpdateRequest != null)
                requestBody.put("images", jobUpdateRequest.getScreenshot());
            if (jobUpdateRequest != null)
                requestBody.put("log", jobUpdateRequest.getLog());

            if (TestBase.jobStartTime != null)
                requestBody.put("jobExecutionStart", String.valueOf(TestBase.jobStartTime));

            if (TestBase.startTime != null && testCaseStepID != null)
                requestBody.put("executionStart", String.valueOf(TestBase.startTime));

            if (TestBase.endTime != null && testCaseStepID != null)
                requestBody.put("executionEnd", String.valueOf(TestBase.endTime));

            if (TestBase.durationSeconds != 0 && testCaseStepID != null)
                requestBody.put("executionDuration", TestBase.durationSeconds);
            
            if(TestBase.moduleDurationSec != 0)
            	requestBody.put("moduleDuration", TestBase.moduleDurationSec);
            
            if(TestBase.defectType != null)
            	requestBody.put("defectType", TestBase.defectType);
            
            if(TestBase.jobDurationSec != 0)
            	requestBody.put("jobDuration", TestBase.jobDurationSec);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);

            ResponseEntity<String> response = restTemplate.exchange(
                    String.format("/api/releases/jobUpdate/%s/%s/%s?bypass=true", runID, testCaseStepID, status),
                    HttpMethod.PATCH, requestEntity, String.class, Map.of());
            log.info("Successfully updated the test case id {}, run id {}, response code {}, response body {}",
                    testCaseStepID, runID, response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed updating the test case id {}, run id {}, error: {}", testCaseStepID, runID,
                    e.getMessage());
        }
    }
}
