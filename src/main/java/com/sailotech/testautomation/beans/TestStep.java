package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestStep {
    private String id;
    private String uuid;
    private String name;
    private String startTime;
    private String endTime;
    private String status;
    private String testCaseID;
}
