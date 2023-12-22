package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder({ "testCaseID", "_id", "testCaseTitle", "testCaseDescription", "dependsOn", "tags",
		"testCaseSteps" })
public class TestNodeSingularResponse {
	private String _id;
	private String testCaseID;
	private String testCaseTitle;
	private String testCaseDescription;
	private String dependsOn;
	private List<Object> testCaseSteps;
	private List<String> tags;
}
