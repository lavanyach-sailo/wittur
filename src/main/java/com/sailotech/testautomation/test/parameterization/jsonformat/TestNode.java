package com.sailotech.testautomation.test.parameterization.jsonformat;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Test Node structure:
 * 
 * <pre>
 * {@code
 * 		"testNode":{
 *		"testCaseID":"",
 *		"preReq":
 *			{ "dependsOn":["testCaseID"] }, 
 *		"testCaseSteps": 
 *			[
 * 				{"navActionNode"}, 
 * 				{"validate":["validationNode"]} 
 * 			] 
 * 		}
 * }
 * </pre>
 * 
 */
@JsonDeserialize(using = TestNodeDeserializer.class)
public class TestNode {
	public String _id;
	public String testCaseID;
	public String testCaseTitle;
	public String testCaseDescription;
	public JsonNode preReq;
	public List<JsonNode> testCaseSteps;
	public String dependsOn;
	public List<String> tags;

	public TestNode(String _id, String testCaseID, String testCaseTitle, String testCaseDescription, JsonNode preReq,
			List<JsonNode> testCaseSteps, String dependsOn, List<String> tags) {
		super();
		this._id = _id;
		this.testCaseID = testCaseID;
		this.testCaseTitle = testCaseTitle;
		this.testCaseDescription = testCaseDescription;
		this.preReq = preReq;
		this.testCaseSteps = testCaseSteps;
		this.dependsOn = dependsOn;
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "TestNode [_id=" + _id + "testCaseID=" + testCaseID + ", testCaseTitle=" + testCaseTitle
				+ ", testCaseDescription="
				+ testCaseDescription + ", preReq=" + preReq + ", testCaseSteps=" + testCaseSteps + ", dependsOn="
				+ dependsOn + ", tags=" + tags + "]";
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * @return the testCaseID
	 */
	public String getTestCaseID() {
		return testCaseID;
	}

	/**
	 * @return the testCaseDescription
	 */
	public String getTestCaseDescription() {
		return testCaseDescription;
	}

	/**
	 * @return the testCaseTitle
	 */
	public String getTestCaseTitle() {
		return testCaseTitle;
	}

	/**
	 * @return the preReq
	 */
	public JsonNode getPreReq() {
		return preReq;
	}

	/**
	 * @return the testCaseSteps
	 */
	public List<JsonNode> getTestCaseSteps() {
		return testCaseSteps;
	}

	public String getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(String dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
