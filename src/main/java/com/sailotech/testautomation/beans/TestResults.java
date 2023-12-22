package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestResults {
    private String launchName;
    private String rpID;
    private String launchUUID;
    private String jenkinsJobID;
    private String jenkinsJobName;
    private String rpproject;
    private String number;
    private String jsonFiles;
    private String tags;
    private String launchedBy;
    private String startTime;
    private String dateTime;
    private String status;
    private String userID;
    private String projectID;
    private String approximateDuration;
    private String endTime;
    private String failed;
    private String lastModified;
    private String passed;
    private String skipped;
    private String total;
    private List<TestProject> testProjects;
}


