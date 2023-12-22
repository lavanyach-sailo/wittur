package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Module {
	private String moduleID;
	private String moduleName;
	private String moduleDesc;
	private String jsonFile;
	private String testCaseFile;
	private String testDataFile;
	private Map<String, String> testData;
	private String priority; 
	private String userStoryFile;
	private List<String> tags;
	private String jsonPath;
	private String jsonText;
	private String version;
	private int totalTestCases;
	private int totalTestSteps;
}