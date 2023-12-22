package com.sailotech.testautomation.test.parameterization.common.loopactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.epam.reportportal.listeners.ItemStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.test.parameterization.jsonformat.TestNode;
import com.sailotech.testautomation.util.TestEnsureRequest;

public class LoopActionImpls extends TestBase {
	
	static Map<String, ItemStatus> testEnsureRequestStatusMap = new HashMap<String, ItemStatus>();
	
	public static TestEnsureRequest execute(JsonNode navActionParams, String file, String testCaseStepId) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		String from = getJsonFieldValue(navActionParams, "from");
		String to = getJsonFieldValue(navActionParams, "to");
		String testnode = getJsonFieldValue(navActionParams, "testNode");
		String object = getJsonFieldValue(navActionParams, "object");
		String varName = getJsonFieldValue(navActionParams, "varName");
		
		boolean start = false;
		boolean stop = false;
		
		List<TestNode> tempTestNodes = new ArrayList<>();
		
		for (TestNode testNode : testNodes) {
			
			if(testnode != null && !testnode.equals("")) {
				if(testNode.getTestCaseID().equals(testnode)) {
					tempTestNodes.add(testNode);
				}
			} else {
				if (testNode.getTestCaseID().equals(from)) {
					start = true;
				}
				if(start && !stop) {
					log.info("testNode " + testNode.getTestCaseID());
					tempTestNodes.add(testNode);
				}
				if(testNode.getTestCaseID().equals(to)) {
					stop = true;
				}
			}
		}
		log.info("objects.get(object).length "+objects.get(object).length);
		log.info("varName "+variables.get(varName));
		if (Integer.parseInt(variables.get(varName)) < objects.get(object).length)
			executeFinalTestNodes(tempTestNodes, file);
		
		return testAssureRequest;
	}
	
	public static TestEnsureRequest loop(JsonNode navActionParams, String file) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		String from = getJsonFieldValue(navActionParams, "from");
		String to = getJsonFieldValue(navActionParams, "to");
		String object = getJsonFieldValue(navActionParams, "object");
		String varName = getJsonFieldValue(navActionParams, "varName");
		String varFile = getJsonFieldValue(navActionParams, "varFile");
		// String variable = getJsonFieldValue(navActionParams, "variable");

		boolean start = false;
		boolean stop = false;

		List<TestNode> tempTestNodes = new ArrayList<>();
		log.info("testNodes-->" + testNodes);
        log.info("Object " +object);
		log.info("starting testNodes process " + testNodes.size());
		for (TestNode testNode : testNodes) {

			if (testNode.getTestCaseID().equals(from)) {
				start = true;
			}
			if (start && !stop) {
				log.info("testNode " + testNode.getTestCaseID());
				tempTestNodes.add(testNode);
			}
			if (testNode.getTestCaseID().equals(to)) {
				stop = true;
			}
		}
		log.info("Vriables in loop" + variables.toString());
		log.info("starting testNodes process end " + testNodes.size());
		//log.info("varFile Size" +fileVar.get(varFile).size());
		//log.info("varFile " +fileVar.get(varFile).toString());
		//log.info("object " +variables.get(object).toString());
		//log.info("varName Size" + Integer.parseInt(variables.get(varName).toString()));
		//log.info("varName " + variables.get(varName));
		try {
		//log.info("If Condition fileVar " + (fileVar != null && Integer.parseInt(variables.get(varName)) < fileVar.get(varFile).size()));
		//log.info("If Condition object " + (object != null && Integer.parseInt(variables.get(varName)) < objects.get(object).length));
		/*
		 * boolean condition = (object != null &&
		 * Integer.parseInt(variables.get(varName)) < objects.get(object).length) ||
		 * (fileVar != null && Integer.parseInt(variables.get(varName)) <
		 * fileVar.get(varFile).size());
		 */
		//log.info("If Condition " +condition);
		if ((object != null && Integer.parseInt(variables.get(varName)) < objects.get(object).length)
				|| (fileVar != null && Integer.parseInt(variables.get(varName)) < fileVar.get(varFile).size())) {
			log.info("Temp Test Nodes-->" + tempTestNodes);
			executeFinalTestNodes(tempTestNodes, file);
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		/*
		 * if ( variable != null && Integer.parseInt(variables.get(varName)) <
		 * Integer.parseInt(variables.get(variable)))
		 * executeFinalTestNodes(tempTestNodes, file);
		 */

		return testAssureRequest;
	}

}
