package com.sailotech.testautomation.test.parameterization.jsonformat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sailotech.testautomation.exceptions.jsonparsing.JsonAutomationFormatIssue;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Deserializes {@link TestNode} from the "testNode" structure in json.
 * 
 */
public class TestNodeDeserializer extends StdDeserializer<TestNode> {

	/**
	 * Generated Serial version UID.
	 */
	private static final long serialVersionUID = 5720594187839805048L;

	public TestNodeDeserializer() {
		this(null);
	}

	public TestNodeDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public TestNode deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		node = node.isArray() ? node.get(0) : node;
		String _id = parse_id(node);
		String testCaseId = parseTestCaseID(node);
		String testCaseDescription = parseTestCaseDescription(node);
		String testCaseTitle = parseTestCaseTitle(node);
		String dependsOn = parseDependsOn(node);
		List<String> tags = parseTags(node);
		JsonNode preReqNode = node.get(ParsingConstants.PRE_REQUISITE);
		List<JsonNode> testCaseSteps = StreamSupport
				.stream(node.get(ParsingConstants.TEST_CASE_STEPS).spliterator(), false).collect(Collectors.toList());
		return new TestNode(_id, testCaseId, testCaseTitle, testCaseDescription, preReqNode, testCaseSteps, dependsOn,
				tags);
	}

	/**
	 * Parses testCaseID from the {@link TestNode} jsonNode
	 * 
	 * @param jsonNode
	 * @return
	 */
	private String parse_id(JsonNode jsonNode) {
		JsonNode _id = jsonNode.get(ParsingConstants._ID);
		if (_id == null) {
			throw new JsonAutomationFormatIssue("TestCaseID field missing in testNode json");
		}
		if (_id.textValue() == null) {
			throw new JsonAutomationFormatIssue("TestCaseID value should be a valid String");
		}
		return _id.textValue();
	}

	/**
	 * Parses testCaseID from the {@link TestNode} jsonNode
	 * 
	 * @param jsonNode
	 * @return
	 */
	private String parseTestCaseID(JsonNode jsonNode) {
		JsonNode testCaseId = jsonNode.get(ParsingConstants.TEST_CASE_ID);
		if (testCaseId == null) {
			throw new JsonAutomationFormatIssue("TestCaseID field missing in testNode json");
		}
		if (testCaseId.textValue() == null) {
			throw new JsonAutomationFormatIssue("TestCaseID value should be a valid String");
		}
		return testCaseId.textValue();
	}

	/**
	 * Parses testCaseDescription from the {@link TestNode} jsonNode
	 * 
	 * @param jsonNode
	 * @return
	 */
	private String parseTestCaseDescription(JsonNode jsonNode) {
		JsonNode testCaseDescription = jsonNode.get(ParsingConstants.TEST_CASE_DESCRIPTION);
		if (testCaseDescription == null) {
			// throw new JsonAutomationFormatIssue("TestCaseDescription field missing in
			// testNode json");
			return "TestCaseDescription";
		}
		if (testCaseDescription.textValue() == null) {
			throw new JsonAutomationFormatIssue("TestCaseDescription value should be a valid String");
		}
		return testCaseDescription.textValue();
	}

	/**
	 * Parses testCaseDescription from the {@link TestNode} jsonNode
	 * 
	 * @param jsonNode
	 * @return
	 */
	private String parseTestCaseTitle(JsonNode jsonNode) {
		JsonNode testCaseTitle = jsonNode.get(ParsingConstants.TEST_CASE_TITLE);
		if (testCaseTitle == null) {
			// throw new JsonAutomationFormatIssue("TestCaseDescription field missing in
			// testNode json");
			return "TestCaseTitle";
		}
		if (testCaseTitle.textValue() == null) {
			throw new JsonAutomationFormatIssue("TestCaseTitle value should be a valid String");
		}
		return testCaseTitle.textValue();
	}

	/**
	 * @param jsonNode
	 * @return
	 */
	private String parseDependsOn(JsonNode jsonNode) {
		JsonNode dependsOn = jsonNode.get(ParsingConstants.DEPENDS_ON);
		if (dependsOn == null) {
			throw new JsonAutomationFormatIssue("DependsOn field missing in testNode json");
		}
		if (dependsOn.textValue() == null) {
			throw new JsonAutomationFormatIssue("DependsOn value should be a valid String");
		}
		return dependsOn.textValue();
	}

	/**
	 * @param jsonNode
	 * @return
	 */
	private List<String> parseTags(JsonNode jsonNode) {
		JsonNode tagsNode = jsonNode.get(ParsingConstants.TAGS);
		List<String> tags = new ArrayList<String>();
		if (tagsNode != null) {
			// throw new JsonAutomationFormatIssue("TestCaseID field missing in testNode
			// json");
			tags = Arrays.asList(tagsNode.toString().replace("[", "").replace("]", "").replace("\"", "").split(","));
		}
		/*
		 * if (tags.size() == 0) { throw new
		 * JsonAutomationFormatIssue("Tags value should be a valid Array"); }
		 */
		return tags;
	}
}
