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
public class TestSuite {
	private String id;
	private String uuid;
	private String name;
	private String description;
	private String startTime;
	private String endTime;
	private String status;
	private String parent;
	private String total;
	private String passed;
	private String failed;
	private String skipped;
	private String launchId;
	private List<TestCase> testCases;
}
