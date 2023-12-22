package com.sailotech.testautomation.test.parameterization.common.validateactions;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.epam.reportportal.listeners.ItemStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.constants.ProcessConstants;
import com.sailotech.testautomation.test.enums.IssueEnum;
import com.sailotech.testautomation.test.parameterization.common.navactions.NavigationActions;
import com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants;
import com.sailotech.testautomation.util.RequestBuilder;
import com.sailotech.testautomation.util.TestEnsureRequest;

public class ValidateActionImpl extends TestBase {

	public static TestEnsureRequest isDisplayed(JsonNode validateActionValueNode, String file, String testCaseStepId)
			throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processValidateNode(validateActionValueNode, ProcessConstants.IS_DISPAYED, file,
				testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest isElementPresent(JsonNode validateActionValueNode, String file,
			String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processValidateNode(validateActionValueNode, ProcessConstants.IS_ELEMENT_PRESENT, file,
				testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest verifyStrings(JsonNode validateActionValueNode, String file, String testCaseStepId)
			throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processValidateNode(validateActionValueNode, ProcessConstants.VRIFYSTRINGS, file,
				testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest isElementNotPresent(JsonNode validateActionValueNode, String file,
			String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processValidateNode(validateActionValueNode, ProcessConstants.IS_ELEMENT_NOT_PRESENT, file,
				testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest verifyText(JsonNode validateActionValueNode, String file, String testCaseStepId)
			throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processValidateNode(validateActionValueNode, ProcessConstants.VERIFYTEXT, file,
				testCaseStepId);
		return testEnsureRequest;
	}

	/*
	 * public static void compareListItems(JsonNode validateActionValueNode, String
	 * file) throws Throwable {
	 * 
	 * System.out
	 * .println("Executing validate action processCompareListItems for JsonNode\n" +
	 * validateActionValueNode); String finalXpath = ""; List<String> expectedList =
	 * null; boolean status = false; List<String> difference = null; if
	 * (validateActionValueNode.isArray()) {
	 * 
	 * for (JsonNode validateActionValueJson : validateActionValueNode) { String
	 * validationActionXpath = validateActionValueJson.fields().next().getKey();
	 * JsonNode validationActionXpathValue =
	 * validateActionValueJson.fields().next().getValue(); log.info(
	 * "validateActionValueJson:::\n" + validationActionXpath + "---" +
	 * validationActionXpathValue);
	 * 
	 * if (validationActionXpath.equalsIgnoreCase("expectedList")) { expectedList =
	 * getExpectedStringList(validationActionXpathValue); } else if
	 * (validationActionXpathValue.fields().next().getKey().contains("xpathparams"))
	 * { finalXpath = getXPathValueFromProperty(validationActionXpath); finalXpath =
	 * getFinalXPathFromXPathParams(validationActionXpathValue, finalXpath); } else
	 * { log.info("called unimplemented method"); }
	 * 
	 * } } log.info("Calling final xpath to get actual list :: " +
	 * finalXpath + " ::::: expectedList :: " + expectedList.toString());
	 * 
	 * List<WebElement> webElementList = getListofWebelements(By.xpath(finalXpath));
	 * List<String> actualList = new ArrayList<String>(); // Thread.sleep(2000); for
	 * (int i = 0; i < webElementList.size(); i++) {
	 * actualList.add(webElementList.get(i).getText()); }
	 * log.info("actuals-->" + actualList); // Thread.sleep(2000);
	 * log.info("status-->" + status); assertTrue(status); }
	 */
	public static TestEnsureRequest compareListItems(JsonNode validateActionValueNode, String file) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		String objects = getJsonFieldValue(validateActionValueNode, "objects");
		String[] objectArr = objects.split(",");
		log.info("objectArr[0]->" + objArr.get(objectArr[0]) + "objectArr[1]->" + obj.get(objectArr[1]));
		boolean status = compareCost(obj.get(objectArr[1]), objArr.get(objectArr[0]));
		if (status)
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		else
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, IssueEnum.PRODUCTION_BUG,
					"fileName", 1);
		return testEnsureRequest;

	}
	public static TestEnsureRequest compareList(JsonNode validateActionValueNode, String file) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		String objects = getJsonFieldValue(validateActionValueNode, "objects");
		String[] objectArr = objects.split(",");
		log.info("objectArr[0]->" + objArr.get(objectArr[0]) + "objectArr[1]->" + objArr.get(objectArr[1]));
		boolean status = compareCostList(objArr.get(objectArr[0]), objArr.get(objectArr[1]));
		if (status)
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		else
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, IssueEnum.PRODUCTION_BUG,
					"fileName", 1);
		return testEnsureRequest;

	}

	public static boolean compareCost(Map<String, String> map, List list) {
		int index = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (!value.equals(list.get(index)))
				return false;
			index++;

		}
		log.info("New Object List->" + objList.toString() + " new object->" + obj.toString());
		return true;
	}

	public static boolean compareCostList(List list1, List list2) {

		for (int index = 0; index < list1.size(); index++) {
			if (!list1.get(index).equals(list2.get(index)))
				return false;

		}
		log.info("New Object List->" + objList.toString() + " new object->" + obj.toString());
		return true;
	}

	public static TestEnsureRequest compareString(JsonNode validateActionValueNode, String file) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {
			String actual = variables.get(getJsonFieldValue(validateActionValueNode, "actual"));
			String expected = variables.get(getJsonFieldValue(validateActionValueNode, "expected"));
			log.info("Actual" + actual + "Expected " + expected);
			if (actual.equals(expected)) {
				log.info("Strings are same");
				testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
			} else {
				testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, IssueEnum.PRODUCTION_BUG,
						"fileName", 1);
			}
		} catch (Exception e) {
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, IssueEnum.AUTOMATION_BUG,
					"fileName", 1);
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}
		return testEnsureRequest;

	}

	public static TestEnsureRequest processValidateNode(JsonNode validateActionValueNode, String processType,
			String file, String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		String expectedValue = "";
		String fileName = null;
		String bugTitle = null;
		boolean testStepStatus = false;
		int testPriority = 2;
		String finalXpath = null;
		JsonNode actionNode = null;
		JsonNode ationValue = null;
		String action = null;
		boolean skipUpdate = false;
		try {
			System.out
					.println("Executing validate action " + processType + " for JsonNode\n" + validateActionValueNode);

			if (validateActionValueNode.isArray()) {

				for (JsonNode validateActionValueJson : validateActionValueNode) {
					String validationActionXpath = validateActionValueJson.fields().next().getKey();
					JsonNode validationActionXpathValue = validateActionValueJson.fields().next().getValue();

					log.info("\nvalidateActionValueJson" + validateActionValueJson + "\n");
					log.info("validateActionValueJson:::\n" + validationActionXpath + "---"
							+ validationActionXpathValue);

					log.info("TestBase.testStepStatus++" + TestBase.testStepStatus);
					if (validationActionXpath.contains("then") && TestBase.testStepStatus) {
						/*
						 * navActionNode = validationActionXpathValue; navAction =
						 * navActionNode.fields().next().getKey(); navActionValue =
						 * navActionNode.fields().next().getValue(); log.info("Element found");
						 * log.info("navAction::"+navAction+"::navActionNode::"+navActionNode);
						 */
						JsonNode validationactionThenArray = validationActionXpathValue;
						for (JsonNode validationactionThenActionValue : validationactionThenArray) {

							actionNode = validationactionThenActionValue;
							action = actionNode.fields().next().getKey();
							ationValue = actionNode.fields().next().getValue();

							if (action.equals(ParsingConstants.VALIDATE)) {
								actionNode = ationValue.get(0);
								action = actionNode.fields().next().getKey();
								ationValue = actionNode.fields().next().getValue();
								ValidateAction.delegateValidateAction(action, ationValue, file, testCaseStepId);
							} else {
								// Process navAction Node.
								NavigationActions.delegateNavAction(action, ationValue, file, testCaseStepId);
							}
						}
						// NavigationActions.delegateNavAction(navAction, navActionValue, file);
						TestBase.testStepStatus = true;
						testCaseFailed = false;
						skipUpdate = true;
					} else if (validationActionXpath.contains("else") && !TestBase.testStepStatus) {
						/*
						 * navActionNode = validationActionXpathValue; navAction =
						 * navActionNode.fields().next().getKey(); navActionValue =
						 * navActionNode.fields().next().getValue(); log.info("Element found");
						 * log.info("navAction::"+navAction+"::navActionNode::"+navActionNode);
						 */
						JsonNode validationactionElseArray = validationActionXpathValue;
						for (JsonNode validationactionElseActionValue : validationactionElseArray) {
							actionNode = validationactionElseActionValue;
							action = actionNode.fields().next().getKey();
							ationValue = actionNode.fields().next().getValue();

							if (action.equals(ParsingConstants.VALIDATE)) {
								actionNode = ationValue.get(0);
								action = actionNode.fields().next().getKey();
								ationValue = actionNode.fields().next().getValue();
								ValidateAction.delegateValidateAction(action, ationValue, file, testCaseStepId);
							} else {
								// Process navAction Node.
								NavigationActions.delegateNavAction(action, ationValue, file, testCaseStepId);
							}
						}
						// NavigationActions.delegateNavAction(navAction, navActionValue, file);
						TestBase.testStepStatus = true;
						testCaseFailed = false;
						skipUpdate = true;
					} else if (!validationActionXpath.contains("else") && !validationActionXpath.contains("then")) {
						finalXpath = getXPathValueFromProperty(validationActionXpath);
						String keyValue = getXPathKeyValueFromJSON(validateActionValueJson, validationActionXpath);
						log.info("keyValue:::\n" + validationActionXpath);
						fileName = getJsonFieldValue(validateActionValueJson, "fileName");
						bugTitle = getJsonFieldValue(validateActionValueJson, "bugTitle");
						if (!validationActionXpathValue.asText().trim().isEmpty()) {
							finalXpath = generateDynamicXpath(finalXpath, keyValue);
							expectedValue = validationActionXpathValue.asText();
						} else if (validationActionXpath.equalsIgnoreCase("getXpath")) {
							log.info(validationActionXpathValue);
							finalXpath = getJsonFinalXpath(validationActionXpathValue, true);
							expectedValue = validationActionXpathValue.asText();
						} else if (validationActionXpathValue.fields().next().getKey().contains("xpathparams")) {
							if (validationActionXpathValue.fields().next().getValue().isArray()) {

								List<String> array = new ArrayList<String>();
								for (int i = 0; i < validationActionXpathValue.fields().next().getValue().size(); i++) {

									JsonNode jsonNode = validationActionXpathValue.fields().next().getValue().get(i);
									array.add(jsonNode.asText());
									expectedValue = jsonNode.asText();
								}
								Object[] objArr = array.toArray();
								finalXpath = generateDynamicXpath(finalXpath, file, objArr);
							}
							fileName = getJsonFieldValue(validateActionValueNode, "fileName");
							bugTitle = getJsonFieldValue(validateActionValueNode, "bugTitle");
						}
						try {
							// Thread.sleep(5000);
							try {
								if (finalXpath != null) {
									if (processType.equals(ProcessConstants.IS_ELEMENT_NOT_PRESENT)) {
										log.info("waituntitdisappear");
										// wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(finalXpath)));
										// waitUntilElementDisappear(finalXpath, By.xpath(finalXpath), get_timout, 5,
										// driver);
									} else {
										log.info("waituntilvisible");
										waitUntilVisible(finalXpath, By.xpath(finalXpath), get_timout, 5);
										// wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(finalXpath)));
									}
								}

							} catch (Exception e) {
								String methodName = new Object() {
								}.getClass().getEnclosingMethod().getName();
								log.info("waituntilvisible::" + methodName + "::" + testEnsureRequest
										+ ":: teststepstatus::" + testStepStatus + ":: testcasefailed::"
										+ testCaseFailed);
								e.printStackTrace();
							}

							log.info("expectedValue:::" + expectedValue);
							log.info("processType::" + processType);

							String actual = variables.get(getJsonFieldValue(validateActionValueNode, "actual"));
							String expected = variables.get(getJsonFieldValue(validateActionValueNode, "expected"));

							switch (processType) {
							case ProcessConstants.IS_DISPAYED:
								testStepStatus = isDisplayed(By.xpath(finalXpath), validationActionXpathValue.asText());
								break;
							case ProcessConstants.IS_ELEMENT_PRESENT:
								testStepStatus = isElementPresent(By.xpath(finalXpath),
										validationActionXpathValue.asText());
								break;
							case ProcessConstants.IS_ELEMENT_NOT_PRESENT:
								testStepStatus = isElementNotPresent(By.xpath(finalXpath),
										validationActionXpathValue.asText());
								break;
							case ProcessConstants.IS_SELECTED:
								testStepStatus = isSelected(By.xpath(finalXpath), validationActionXpathValue.asText());
								break;
							case ProcessConstants.IS_NOT_SELECTED:
								testStepStatus = isNotSelected(By.xpath(finalXpath),
										validationActionXpathValue.asText());
								break;
							case ProcessConstants.VERIFYTEXT:
								testStepStatus = verifyAssertion(By.xpath(finalXpath),
										validationActionXpathValue.asText());
								break;
							case ProcessConstants.VRIFYSTRINGS:
								testStepStatus = verifyTwoStrings(actual, expected);
								break;

							}

							log.info("validateActionValueJson:::\n" + validationActionXpath + "---"
									+ validationActionXpathValue);
							log.info("Verification done :: testStepStatus :: " + testStepStatus);
							log.info("fileName::" + fileName);
							log.info("bugTitle::" + bugTitle);
							if (testStepStatus) {
								testPriority = 1;
							}
						} catch (Exception e) {
							testEnsureRequest = RequestBuilder.build("validationActionXpath", ItemStatus.FAILED,
									IssueEnum.TO_INVESTIAGE, "fileName", 2);
							//StatusUpdater.updateStatus(runID, testCaseStepId, "FAILED");
							String methodName = new Object() {
							}.getClass().getEnclosingMethod().getName();
							log.info("validation::" + methodName + "::" + testEnsureRequest
									+ ":: teststepstatus::" + testStepStatus + ":: testcasefailed::" + testCaseFailed);
							e.printStackTrace();
						}
						try {

							if (getJsonFieldValue(validateActionValueNode, "continueTestNodes") != null)
								continueTestNodes = Boolean
										.valueOf(getJsonFieldValue(validateActionValueNode, "continueTestNodes"));
						} catch (Exception e) {
							testEnsureRequest = RequestBuilder.build("validationActionXpath", ItemStatus.FAILED,
									IssueEnum.TO_INVESTIAGE, "fileName", 2);
							//StatusUpdater.updateStatus(runID, testCaseStepId, "FAILED");
							String methodName = new Object() {
							}.getClass().getEnclosingMethod().getName();
							log.info("continuetestnodes::" + methodName + "::" + testEnsureRequest
									+ ":: teststepstatus::" + testStepStatus + ":: testcasefailed::" + testCaseFailed);
							e.printStackTrace();
						}
						if (!skipUpdate && !validationActionXpath.equals("then")
								&& !validationActionXpath.equals("else")) {
							log.info("if testStepStatus" + testStepStatus);
							TestBase.testStepStatus = testStepStatus;
							if (!testStepStatus) {
								if (noOfRetries < retryCount) {
									noOfRetries++;
									log.info("********** Failed to process ValidateAction Node *** Retrying "
											+ noOfRetries + " time ***********");
									processValidateNode(validateActionValueNode, processType, file, testCaseStepId);
								}
								TestBase.testCaseFailed = true;
							}
							/*
							 * log.info("****************************************");
							 * log.info("testStepStatus :: " + testStepStatus);
							 * log.info("testCaseFailed :: " + testCaseFailed);
							 * log.info("testSuiteFailed :: " + testSuiteFailed); log.info("skipUpdate :: "
							 * + skipUpdate);
							 * log.info("testStepStatus || !skipUpdate && !testCaseFailed :: " +
							 * (testStepStatus || !skipUpdate && !testCaseFailed));
							 * log.info("testStepStatus && !skipUpdate && testCaseFailed :: " +
							 * (testStepStatus && !skipUpdate && testCaseFailed));
							 * log.info("!skipUpdate :: " + !skipUpdate);
							 * log.info("****************************************");
							 */
							if (testStepStatus || !skipUpdate && !testCaseFailed) {
								testEnsureRequest = RequestBuilder.build(validationActionXpath, ItemStatus.PASSED, null,
										fileName, testPriority);
								//StatusUpdater.updateStatus(runID, testCaseStepId, "PASSED");
							} else if (testStepStatus && !skipUpdate && testCaseFailed) {
								testEnsureRequest = RequestBuilder.build(validationActionXpath, ItemStatus.FAILED,
										IssueEnum.TO_INVESTIAGE, fileName, testPriority);
								//StatusUpdater.updateStatus(runID, testCaseStepId, "FAILED");
							} else if (!skipUpdate) {
								testEnsureRequest = RequestBuilder.build(validationActionXpath, ItemStatus.FAILED,
										IssueEnum.PRODUCTION_BUG, fileName, testPriority);
								//StatusUpdater.updateStatus(runID, testCaseStepId, "FAILED");
							}
						} else {
							testEnsureRequest = null;
						}
					}
				}
				if (skipUpdate) {
					TestBase.testStepStatus = true;
					testCaseFailed = false;
					testEnsureRequest = RequestBuilder.build("", ItemStatus.PASSED, null, fileName, testPriority);
					//StatusUpdater.updateStatus(runID, testCaseStepId, "PASSED");
				}
			}
		} catch (Exception e) {
			if (noOfRetries < retryCount) {
				noOfRetries++;
				log.info("********** Failed to process ValidateAction Node *** Retrying " + noOfRetries
						+ " time ***********");
				e.printStackTrace();
				processValidateNode(validateActionValueNode, processType, file, testCaseStepId);
			}
			testEnsureRequest = RequestBuilder.build("validationActionXpath", ItemStatus.FAILED,
					IssueEnum.AUTOMATION_BUG, "fileName", 2);
			//StatusUpdater.updateStatus(runID, testCaseStepId, "FAILED");
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			TestBase.testStepStatus = false;
			TestBase.testCaseFailed = true;
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}
		/*
		 * if(!testStepStatus && testEnsureRequest!= null &&
		 * testEnsureRequest.getItemStatus() != ItemStatus.FAILED) { testEnsureRequest =
		 * RequestBuilder.build("validationActionXpath", ItemStatus.FAILED,
		 * IssueEnum.TO_INVESTIAGE, "fileName", 2); }
		 */
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*
		 * if(!testStepStatus) { testEnsureRequest.setStatus(ItemStatus.FAILED);
		 * testEnsureRequest.setItemStatus(ItemStatus.FAILED);
		 * testEnsureRequest.setIssueType(IssueEnum.TO_INVESTIAGE); }
		 */
		log.info("Final values :: " + methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
				+ ":: testcasefailed::" + testCaseFailed);
		return testEnsureRequest;
	}
}
