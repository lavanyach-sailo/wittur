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
public class TestCase {
    private String id;
    private String uuid;
    private String name;
    private String description;
    private String startTime;
    private String endTime;
    private String status;
    private String parent;
    private String testCaseId;
    private String launchID;
    private List<TestStep> testSteps;
}
