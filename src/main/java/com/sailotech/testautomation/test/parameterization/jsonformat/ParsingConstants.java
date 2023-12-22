package com.sailotech.testautomation.test.parameterization.jsonformat;

import gherkin.deps.com.google.gson.JsonParseException;

public class ParsingConstants {
	// Constants which are the standard keys defined for the json structure.
	public static final String TEST_NODE_DEF = "testNodeDef";
	public static final String TEST_NODE_DEF_NAME = "testNodeDefName";
	public static final String DEF_NAME = "defName";
	public static final String TEST_NODE = "testNode";
	public static final String TEST_CASES = "testCases";
	public static final String TEST_CASE_STEPS = "testCaseSteps";
	public static final String VALIDATE = "validate";
	public static final String LOOP = "loop";
	public static final String MANUAL = "manual";
	public static final String TEST_DATA = "testData";
	public static final String _ID = "_id";
	public static final String TEST_CASE_ID = "testCaseID";
	public static final String TEST_CASE_TITLE = "testCaseTitle";
	public static final String TEST_CASE_DESCRIPTION = "testCaseDescription";
	public static final String DEPENDS_ON = "dependsOn";
	public static final String TAGS = "tags";
	public static final String PRE_REQUISITE = "preReq";
	public static final String SLEEP = "sleep";
	public static final String KEYBOARDEVENT = "keyBoardEvent";

	// Helper constants needed for tracking while Test execution/ json nodes
	// parsing. i.e. Only internal code.
	private static final String TEST_NODE_SEQUENCE_POSTFIX = "_SEQ";

	public static String getNextTestCodeSequenceString(int sequence, String testNodeType) {
		String postfix = "_" + TEST_NODE + TEST_NODE_SEQUENCE_POSTFIX;
		if (sequence > 0) {
			return sequence + postfix;
		}

		throw new JsonParseException(postfix + "'s, prefix value should be >0");
	}
}
