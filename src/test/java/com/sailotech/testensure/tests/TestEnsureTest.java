package com.sailotech.testensure.tests;

import static com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants.DEF_NAME;
import static com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants.TEST_DATA;
import static com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants.TEST_NODE;
import static com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants.TEST_NODE_DEF;
import static com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants.TEST_NODE_DEF_NAME;
import static com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants.getNextTestCodeSequenceString;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.epam.reportportal.listeners.ItemStatus;
import com.epam.reportportal.listeners.ListenerParameters;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.beans.JobResponse;
import com.sailotech.testautomation.beans.ModuleResponse;
import com.sailotech.testautomation.beans.Release;
import com.sailotech.testautomation.beans.ReleaseResponse;
import com.sailotech.testautomation.beans.UserResponse;
import com.sailotech.testautomation.commonutilities.CustomException;
import com.sailotech.testautomation.commonutilities.ScreenRecording;
import com.sailotech.testautomation.database.MongoDBUtil;
import com.sailotech.testautomation.database.StatusUpdater;
import com.sailotech.testautomation.test.enums.IssueEnum;
import com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants;
import com.sailotech.testautomation.test.parameterization.jsonformat.SortTestNodesByName;
import com.sailotech.testautomation.test.parameterization.jsonformat.TestNode;
import com.sailotech.testautomation.test.rputils.ReportPortalUtil;
import com.sailotech.testautomation.util.RequestBuilder;
import com.sailotech.testautomation.util.TestEnsureRequest;
import com.sailotech.testautomation.util.TimerContext;

import io.reactivex.Maybe;

public class TestEnsureTest extends TestBase {

	ScreenRecording screenRecord;
	private boolean recordingFlag = false;
	static Logger log = Logger.getLogger(TestEnsureTest.class.getName());
	static Map<String, ItemStatus> testEnsureRequestStatusMap = new HashMap<String, ItemStatus>();
	static String currentProject = null;
	// Release release = null;
	String jsons = null;
	String jsonDelimeter = null;
	String fileNameDelimeter = null;
	TimerContext timerContext = new TimerContext();;
	String launchName = null;

	String tags = null;
	Maybe<String> projectRs = null;
	Maybe<String> suiteRs = null;

	private static ObjectMapper objectMapper = new ObjectMapper();

	public TestEnsureTest() {
		super();
		log.info("********* no constructor invoked **********");
	}

	public TestEnsureTest(String tags, String jsons, String jsonDelimeter, String fileNameDelimeter) {
		super();
		log.info("********* jsons constructor invoked **********");
		this.jsons = jsons;
		this.tags = tags;
		this.jsonDelimeter = jsonDelimeter;
		this.fileNameDelimeter = fileNameDelimeter;
	}

	public TestEnsureTest(Release release) {
		super();
		log.info("********* release constructor invoked **********");
		TestBase.release = release;
		log.info("Release" + TestBase.release);
	}

	@BeforeMethod
	public void setUp(ITestResult tr) {
		tr.setAttribute("class", getClass().getName());
		tr.setAttribute("toString", tr.toString());
	}

	@BeforeClass
	public void screenRecord() throws Exception {
		System.setProperty("logfile", TestEnsureTest.class.getSimpleName());
		PropertyConfigurator.configure("log4j.properties");
		setHeadlessOption();
		if (Boolean.parseBoolean((String) prop.get("enableScreenRecording"))) {
			screenRecord = new ScreenRecording();
			screenRecord.startRecording();
			recordingFlag = true;
		}
		log.info("TestEnsureTestTest tags" + tags);
		// String launchName = null;
		log.info("release :: " + release);
		/*
		 * if (release != null) launchName = release.getReleaseName(); final
		 * ListenerParameters STANDARD_PARAMETERS = standardParameters(launchName,
		 * tags); launchName = ReportPortalUtil.createLaunch(STANDARD_PARAMETERS);
		 * testResults.setLaunchName(launchName);
		 */
	}

	private static RestTemplate restTemplate = new RestTemplateBuilder().rootUri("http://localhost:5000").build();

	@Test(description = "Here we can login to TestEnsureTest by providing credentials")
	public void TC01_Login() throws Throwable {
		List<TestEnsureRequest> testEnsureRequests = new ArrayList<TestEnsureRequest>();
		try {
			// System.setProperty("job.id", "651bf05ab3319951440681d7");
			String jobID = System.getProperty("job.id");
			Assert.hasText(jobID, "JOB ID is required");
			log.info("*****Login*****");

			objectMapper.setSerializationInclusion(Include.NON_NULL);

			JobResponse response = restTemplate
					.getForEntity(String.format("/api/releases/jobJson/%s?bypass=true", jobID), JobResponse.class)
					.getBody();

			log.info("JOB RESPONSE => " + objectMapper.writeValueAsString(response));

			String userId = response.getCreatedBy();
			log.info("userId => " + userId);

			ReleaseResponse releaseResponse = restTemplate
					.getForEntity(String.format("/api/releases/Release/%s?bypass=true", response.getReleaseID()),
							ReleaseResponse.class)
					.getBody();

			log.info("RELEASE RESPONSE => " + objectMapper.writeValueAsString(releaseResponse));
			UserResponse userResponse = null;
			try {
				userResponse = restTemplate
						.getForEntity(String.format("/api/users/User/%s?bypass=true", response.getCreatedBy()),
								UserResponse.class)
						.getBody();
				log.info("USER RESPONSE => " + objectMapper.writeValueAsString(userResponse));
			} catch (Exception e) {
			}

			String rpAccessToken = userResponse != null ? userResponse.getCompany().getRpAccessToken() : null;
			String rpUsername = userResponse != null ? userResponse.getCompany().getRpUsername() : null;
			log.info("rpAccessToken => " + rpAccessToken);
			log.info("rpUsername => " + rpUsername);
			TestBase.rpUsername = rpUsername;

			runID = response.get_id();

			launchName = releaseResponse.getReleaseName().replaceAll(" ", "_");
			try {
				final ListenerParameters STANDARD_PARAMETERS = standardParameters(launchName, tags, rpAccessToken);
				launchName = ReportPortalUtil.createLaunch(STANDARD_PARAMETERS);
			} catch (Exception e) {
			}

			testResults.setLaunchName(launchName);
			TestBase.launchName = launchName;

			String projectName = null, fileName = null, json = null;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			// startTime = dtf.format(now);
			if (response != null) {
				jobStartTime = LocalDateTime.now();
				List<ModuleResponse> modules = response.getModules();
				for (ModuleResponse module : modules) {
					System.gc();
					moduleStartTime = LocalDateTime.now();
					initBrowser(fileName);
					json = MongoDBUtil.getModuleJson(module);
					jsonsInputMap = module.getTestPlaceholders();
					TestEnsureTestParameterExecution(json, fileName);
					closeBrowser();
					moduleEndTime = LocalDateTime.now();
					Duration moduleDuration = Duration.between(moduleStartTime, moduleEndTime);
					moduleDurationSec = dateFormatUtil.getDurationEpoch(moduleDuration);
					StatusUpdater.updateStatus(runID, testCaseStepId, testCaseFailed ? "FAILED" : "PASSED", moduleId,
							testCaseId, null);
				}
				jobEndTime = LocalDateTime.now();
				Duration jobDuration = Duration.between(jobStartTime, jobEndTime);
				jobDurationSec = dateFormatUtil.getDurationEpoch(jobDuration);
				log.info("jobDurationSeconds " + jobDurationSec + " " + jobDuration.getSeconds());
				StatusUpdater.updateStatus(runID, testCaseStepId, testCaseFailed ? "FAILED" : "PASSED", moduleId,
						testCaseId, null);

				/*
				 * TestBase.currentProject = release.getProjects().get(0).getProjectName(); //
				 * projectRs = //
				 * ReportPortalUtil.standardStartSuiteRequest(TestBase.currentProject, "Project
				 * // Description"); //
				 * log.info("currentproject====>"+currentProject); TestBase.release =
				 * this.release; List<Project> projects = release.getProjects(); for (Project
				 * project : projects) { projectName = project.getProjectName();
				 * 
				 * if(!TestBase.currentProject.equals(projectName)) {
				 * log.info("closing "+TestBase.currentProject);
				 * ReportPortalUtil.positiveFinishRequest(null, projectRs);
				 * TestBase.currentProject = projectName; projectRs =
				 * ReportPortalUtil.standardStartSuiteRequest(TestBase.currentProject,
				 * "Project Description");
				 * log.info("starting "+TestBase.currentProject); }
				 * 
				 * projectRs =
				 * ReportPortalUtil.standardStartSuiteRequest(project.getProjectName(),
				 * "Project Description"); List<Module> modules = project.getModules(); for
				 * (Module module : modules) { currentModule = module.getModuleName();
				 * log.info("module --> " + module); fileName =
				 * module.getModuleName(); initBrowser(fileName); tags = String.join(",",
				 * module.getTags()); TestBase.testData = module.getTestData(); json =
				 * JsonReader.readFileAsString(module.getJsonPath()); //
				 * TestEnsureTestParameterExecution(json, fileName); closeBrowser();
				 * sendEmail(); } // ReportPortalUtil.positiveFinishRequest(null, projectRs); }
				 */
				ReportPortalUtil.positiveFinishRequest(null, projectRs);
			} else {
				String[] jsonArray = jsons.split(jsonDelimeter);
				for (String jsonString : jsonArray) {
					fileName = jsonString.split(fileNameDelimeter)[0];
					json = jsonString.split(fileNameDelimeter)[1];
					initBrowser(fileName);
					// TestEnsureTestParameterExecution(json, fileName);
					closeBrowser();
					sendEmail();
				}
			}
			// now = LocalDateTime.now();
			// endTime = dtf.format(now);
			log.info("launch execution completed");
		} catch (Exception e) {
			log.info(e);
			suiteRs = ReportPortalUtil.standardStartSuiteRequest("System Issue", "System Issue has occured");
			Maybe<String> testRs = ReportPortalUtil.standardStartTestRequest(suiteRs, "System Issue", "System Issue",
					"System Issue Description", "STEP");
			log.error("Unable to run jsons due to system failure / browser issue");
			addLogs();
			ReportPortalUtil.finishItemRequest(testRs, ItemStatus.SKIPPED, IssueEnum.SYSTEM_ISSUE);
			ReportPortalUtil.finishRequest(suiteRs, ItemStatus.STOPPED, IssueEnum.SYSTEM_ISSUE.issueType);
			e.printStackTrace();
			testEnsureRequests.add(RequestBuilder.build("Cannot Login to TestEnsureTest Application", ItemStatus.FAILED,
					IssueEnum.TO_INVESTIAGE, "Login_", 2));
			testSuiteFailed = true;
			throw new CustomException("Cannot Execute Test Cases");
		} finally {
			log.debug("Inside finally method");
		}
		// ReportPortalUtil.updateReportPortalData();
	}

	public void TestEnsureTestParameterExecution(String json, String fileName) throws Throwable {
		try {
			// File jsonFile = new File("resources/ISAM_Data.json");
			// File jsonFile = new File("resources/NOL_NAR_TabOverview.json");
			// <mod-end>

			// ****Parse the json data to build TestNodeDef - TestNodeData, TestNode Objects
			HashMap<String, JsonNode> testNodeDefsMap = new HashMap<String, JsonNode>();
			LinkedHashMap<String, JsonNode> testNodeAndDataMap = new LinkedHashMap<String, JsonNode>();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);
			// Step1) Fetch "testCases" json-value node.
			String suiteName = (root.get("suiteName") != null && !root.get("suiteName").asText().equals(""))
					? root.get("suiteName").asText()
					: fileName;
			if (release != null) {
				suiteName = fileName;
			}
			TestBase.moduleId = (root.get("_id") != null && !root.get("_id").asText().equals(""))
					? root.get("_id").asText()
					: null;
			String suiteDescription = (root.get("suiteDescription") != null
					&& !root.get("suiteDescription").asText().equals("")) ? root.get("suiteDescription").asText()
							: fileName + " Suite";
			log.info("suiteName::" + suiteName + "::suiteDesc::" + suiteDescription);
			suiteRs = ReportPortalUtil.standardStartSuiteRequest(projectRs, suiteName, suiteDescription);
			TestBase.suiteRs = suiteRs;
			ArrayNode arrayNode = (ArrayNode) root.get("testNodes");
			int testNodeSequence = 1;
			// Step2) Iterate over each of the JsonNodes to determine the test execution
			// structures. i.e. 1)"testNode",2)"testNodeDef",3)"testData"
			for (JsonNode arrayElement : arrayNode) {
				Iterator<String> iterator = arrayElement.fieldNames();
				String fieldName = iterator.next();
				log.info("fieldName" + fieldName);
				String testNodeId = arrayElement.get("_id").asText();
				String testCaseId = arrayElement.get(fieldName).get(0).get("_id").asText();
				testNodesMap.put(testCaseId, testNodeId);

				// Step3) testNodeDef objects.
				if (fieldName.equals(TEST_NODE_DEF)) {
					JsonNode testNodeDef = arrayElement.get(TEST_NODE_DEF);
					String defName = testNodeDef.get(DEF_NAME).asText();
					// testNodeDef should have a defName, otherwise throw exception
					if (defName.isEmpty()) {
						throw new IllegalArgumentException("testNodeDef: doesn't have defName attribute******"
								+ testNodeDef.toPrettyString() + "\n******");
					} else {
						testNodeDefsMap.put(defName, testNodeDef);
					}
				} else if (fieldName.equals(TEST_DATA)) { // Step4) testData objects.
					JsonNode testNodeData = arrayElement.get(TEST_DATA);
					String testNodeDefName = testNodeData.get(TEST_NODE_DEF_NAME).asText();
					// testNodeData should have a mapping testNodeDefName, otherwise throw
					// exception
					if (testNodeDefName.isEmpty()) {
						throw new IllegalArgumentException("testNodeData: doesn't have testNodeDefname attribute******"
								+ testNodeData.toPrettyString() + "\n******");
					} else {
						testNodeAndDataMap.put(getNextTestCodeSequenceString(testNodeSequence, TEST_DATA),
								testNodeData);
						testNodeSequence++;
					}
				} else if (fieldName.equals(TEST_NODE)) { // Step5) testNode Objects
					JsonNode testNode = arrayElement;
					testNodeAndDataMap.put(getNextTestCodeSequenceString(testNodeSequence, TEST_DATA), testNode);
					testNodeSequence++;
				}
			}
			// log.debug("Printing the Test constructs parsed"+ testNodeAndDataMap);
			// for(Entry<String, JsonNode> jsonNodeEntry:testNodeAndDataMap.entrySet()) {
			// log.debug(jsonNodeEntry.getKey()+"\n"+jsonNodeEntry.getValue().toPrettyString());
			// }
			Set<String> issueLocatorsFound = verifyLocators(testNodeAndDataMap, fileName);
			log.info("No of issueLocatorsFound  : " + issueLocatorsFound.size());
			if (issueLocatorsFound.size() == 0) {
				log.info("************************ SUCCESS ****************************");
				executeTestNodes(testNodeAndDataMap, fileName);
			} else {
				log.info("******************** LOCATOR NOT FOUND IN PROPERTIES FILE ********************");
				for (String issueLocator : issueLocatorsFound) {
					log.info(issueLocator);
				}
				log.info("******************************************************************************");
			}
			// executeTestNodes(testNodeAndDataMap, fileName);
			ReportPortalUtil.positiveFinishRequest(null, suiteRs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Set<String> verifyLocators(LinkedHashMap<String, JsonNode> testNodeAndDataMap, String file)
			throws Throwable {
		Set<String> locators = new HashSet<String>();
		List<TestNode> testNodes = new ArrayList<TestNode>();
		log.debug(":::::::::::::Creating TestNodes:::::::::::::");
		for (JsonNode testNodeJson : testNodeAndDataMap.values()) {
			String testNodeJsonText = testNodeJson.get(TEST_NODE).toString();
			TestNode testNode = buildTestNode(testNodeJsonText);
			log.debug(testNode);
			testNodes.add(testNode);
		}

		log.debug(":::::::::::::Verifing Locators:::::::::::::");
		log.info(testNodes);
		for (TestNode testNode : testNodes) {
			List<JsonNode> testCaseSteps = testNode.getTestCaseSteps();
			for (JsonNode testCaseStep : testCaseSteps) {
				JsonNode locatorJsonNode = null;
				String locatorProp = null;
				String locator = null;
				if (testCaseStep.get(ParsingConstants.VALIDATE) != null) {
					JsonNode validateJsonNode = testCaseStep.get(ParsingConstants.VALIDATE).get(0);
					Iterator<String> fieldsIterator = validateJsonNode.fieldNames();
					String validateFieldName = fieldsIterator.next();
					locatorJsonNode = validateJsonNode.get(validateFieldName).get(0);
				} else if (testCaseStep.get(ParsingConstants.MANUAL) == null) {
					Iterator<String> fieldsIterator = testCaseStep.fieldNames();
					String fieldName = fieldsIterator.next();
					if (!fieldName.equals(ParsingConstants.SLEEP)
							&& !fieldName.equals(ParsingConstants.KEYBOARDEVENT)) {
						JsonNode testStepNode = testCaseStep.get(fieldName);
						locatorJsonNode = testStepNode.get(0);
					}
				}
				try {
					if (locatorJsonNode != null) {
						Iterator<String> fieldsIterator = locatorJsonNode.fieldNames();
						locator = fieldsIterator.next();
						locatorProp = locatorsProp.getProperty(locator);
						if (locatorProp == null) {
							locators.add(locator);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return locators;
	}

	// 2) Process TestNodeDatas based to TestNodeDefs and TestNodes
	public void executeTestNodes(LinkedHashMap<String, JsonNode> testNodeAndDataMap, String file) throws Throwable {
		List<TestNode> testNodes = new ArrayList<TestNode>();
		log.debug(":::::::::::::Creating TestNodes:::::::::::::");
		for (JsonNode testNodeJson : testNodeAndDataMap.values()) {
			String testNodeJsonText = testNodeJson.get(TEST_NODE).toString();
			TestNode testNode = buildTestNode(testNodeJsonText);
			log.debug(testNode);
			testNodes.add(testNode);
		}

		log.debug(":::::::::::::Processing TestNodes:::::::::::::" + testNodes);

		boolean parallelExecute = false;
		if (parallelExecute) {
			executeFinalTestNodesInParallel(testNodes);
		} else {
			validateAAndSortTestNodes(testNodes);
			if (tags != null && !tags.equals("")) {
				testNodes = filterTestNodesByTagName(testNodes);
				TestBase.testNodes = testNodes;
			}
			for (TestNode testnode : testNodes) {
				// log.info("TestNode " +testnode.getTestCaseID());
				// log.info("TestNode " +testnode.toString());
			}
			if (testNodes.size() != 0)
				executeFinalTestNodes(testNodes, file);
		}
	}

	private List<TestNode> filterTestNodesByTagName(List<TestNode> testNodes) {
		LinkedHashSet<TestNode> filteredNodes = new LinkedHashSet<TestNode>();
		Iterator<TestNode> iterator = testNodes.iterator();
		while (iterator.hasNext()) {
			TestNode testNode = iterator.next();
			List<String> jsonTags = testNode.getTags();
			try {
				for (String jsonTag : jsonTags) {
					String[] jsonTagArray = tags.split(",");
					for (int index = 0; index < jsonTagArray.length; index++) {
						if (jsonTagArray[index].toLowerCase().equals(jsonTag.toLowerCase())) {
							filteredNodes.add(testNode);
						}
					}
				}
			} catch (Exception e) {
				log.info("Tags field is not defined");
			}
		}
		log.info(":::::::::: Filtered TestNodes ::::::::::::::::");
		log.info(filteredNodes.size());
		if (launchID != null && !launchID.equals("")) {
			LinkedHashSet<TestNode> rerunFilteredNodes = new LinkedHashSet<TestNode>();
			Set<String> rerunTestCaseIds = rerunMap.keySet();
			for (TestNode testNode : filteredNodes) {
				if (rerunTestCaseIds.contains(testNode.testCaseID)) {
					rerunFilteredNodes.add(testNode);
					while (testNode.dependsOn != null && !testNode.dependsOn.equals("")) {
						for (TestNode filteredNode : filteredNodes) {
							if (filteredNode.testCaseID.equals(testNode.dependsOn)) {
								rerunFilteredNodes.add(filteredNode);
								testNode = filteredNode;
							}
						}
					}
				}
			}
			log.info(":::::::::: Rerun Filtered TestNodes ::::::::::::::::");
			log.info(rerunFilteredNodes.size());
			List<TestNode> tempList = new ArrayList<TestNode>(rerunFilteredNodes);
			Collections.sort(tempList, new SortTestNodesByName());
			return (tempList.size() != 0) ? tempList : new ArrayList<TestNode>();
		}
		return (filteredNodes.size() == 0) ? testNodes : new ArrayList<TestNode>(filteredNodes);
	}

	/*
	 * private void executeFinalTestNodes(List<TestNode> testNodes, String file)
	 * throws Throwable { List<TestEnsureRequest> testEnsureRequests = null; String
	 * firstTestCaseID = testNodes.get(0).getTestCaseID();
	 * log.debug("firstTestCaseID::" + firstTestCaseID); boolean skipTestCase =
	 * false; IssueEnum issueType = null; TestBase.testNodes = testNodes; for
	 * (TestNode testNode : testNodes) { manualTestCase = false; long startTime =
	 * System.currentTimeMillis();
	 * log.info("Start time before executing UI context of testCase:" +
	 * testNode.getTestCaseID() + "is::" + startTime);
	 * 
	 * Maybe<String> testRs = ReportPortalUtil.standardStartTestRequest(suiteRs,
	 * testNode.getTestCaseID(), testNode.getTestCaseID() + "_" +
	 * testNode.getTestCaseTitle(), testNode.getTestCaseDescription(), "STEP");
	 * testEnsureRequests = new ArrayList<TestEnsureRequest>(); // Step1: Get all
	 * the testCaseSteps List<JsonNode> testCaseSteps = testNode.getTestCaseSteps();
	 * String testCaseId = testNode.getTestCaseID(); ItemStatus dependsOn =
	 * testEnsureRequestStatusMap.get(testNode.getDependsOn());
	 * log.info("+++++TEstcaseID++++" + testCaseId); log.info("+++++DependsON++++++"
	 * + testNode.getDependsOn()); log.info("+++++DependsON Status++++++" +
	 * dependsOn); log.debug("continueTestNodes" + continueTestNodes); if
	 * ((dependsOn != null && dependsOn != ItemStatus.FAILED) ||
	 * testCaseId.equals(firstTestCaseID) ||
	 * testNode.getDependsOn().equals(ParsingConstants.MANUAL) || continueTestNodes)
	 * { // Step2: Process testCaseSteps log.debug("testStepStatus" +
	 * testStepStatus); log.debug("continueTestNodes" + continueTestNodes);
	 * log.debug("testCaseFailed" + testCaseFailed); testStepStatus = true;
	 * continueTestNodes = false; testCaseFailed = false; for (JsonNode testCaseStep
	 * : testCaseSteps) { System.out .println("testStepStatus::" + testStepStatus +
	 * "::continueTestNodes::" + continueTestNodes); PrintWriter pw = new
	 * PrintWriter( System.getProperty("user.dir") +
	 * envRelativePath("\\logs\\") + "TestEnsureTest.log"); pw.close(); String
	 * testCaseStepName = testCaseStep.fields().next().getKey().toString(); JsonNode
	 * testStepFileds = testCaseStep.fields().next().getValue(); String
	 * testStepDescription = getJsonFieldValue(testStepFileds,
	 * "testStepDescription"); if (testStepDescription == null) {
	 * log.info("testCaseStep.get(ParsingConstants.VALIDATE)" +
	 * testCaseStep.get(ParsingConstants.VALIDATE)); testStepDescription =
	 * getJsonFieldValue(testCaseStep.get(ParsingConstants.VALIDATE),
	 * "testStepDescription"); } String content = getJsonFieldValue(testStepFileds,
	 * "expected"); // Maybe<String> stepRs =
	 * ReportPortalUtil.standardStartTestRequest(testRs, // testCaseStepName,
	 * (testStepDescription!= null)? testStepDescription: //
	 * "testStepDescription comes here", "STEP"); if (testStepStatus ||
	 * continueTestNodes || testNode.getDependsOn().equals(ParsingConstants.MANUAL))
	 * { // log = Logger.getLogger(TestBase.class); testEnsureRequests =
	 * processTestCaseStep(testCaseStep, file); if (testEnsureRequests != null &&
	 * testEnsureRequests.size() != 0) { List<TestEnsureRequest>
	 * tempTestEnsureRequests = new ArrayList<>(); for (TestEnsureRequest
	 * testEnsureRequest : testEnsureRequests) { if (testEnsureRequest != null)
	 * tempTestEnsureRequests.add(testEnsureRequest); }
	 * log.info("+++++++++++++++++++++++++++++++++++++++++++");
	 * log.info("testEnsureRequests"+ testEnsureRequests.toString());
	 * log.info("tempTestEnsureRequests"+tempTestEnsureRequests.toString()
	 * ); log.info("+++++++++++++++++++++++++++++++++++++++++++");
	 * testEnsureRequests = tempTestEnsureRequests; //
	 * addLogsAndScreenhots("fileName"); //
	 * addVideo("C:\\Users\\gowtham.murikipudi\\Videos\\sample.mp4"); //
	 * log.info("testensurerequests"+testEnsureRequests.toString()); for
	 * (TestEnsureRequest testEnsureRequest : testEnsureRequests) {
	 * log.info("$$$$$$$$$$$ updating item status $$$$$$$$$$$$$$$");
	 * log.info(testEnsureRequest.toString()); if
	 * (testEnsureRequest.getItemStatus() != null) { //
	 * ReportPortalUtil.finishItemRequest(stepRs, //
	 * testEnsureRequest.getItemStatus());
	 * if(testNode.getDependsOn().equals(ParsingConstants.MANUAL)) {
	 * ReportPortalUtil.addTestStep(testEnsureRequest, testStepDescription,
	 * testCaseStepName, content); } else
	 * ReportPortalUtil.addTestStep(testEnsureRequest, testStepDescription,
	 * testCaseStepName); issueType = testEnsureRequest.getIssueType();
	 * if(testEnsureRequest.getItemStatus().equals(ItemStatus.PASSED)) {
	 * testStepStatus = true; testCaseFailed = false; } else { testStepStatus =
	 * false; testCaseFailed = true; } } System.out.
	 * println("$$$$$$$$$$$ updating item status finished $$$$$$$$$$$$$$$"); } }
	 * ReportPortalUtil.updateReportPortalData(); } else { //
	 * ReportPortal.emitLog("Test Step skipped due to previous test step Failure",
	 * // "INFO", Calendar.getInstance().getTime()); //
	 * ReportPortalUtil.finishItemRequest(stepRs, ItemStatus.SKIPPED);
	 * ReportPortalUtil.addSkippedTestStep(testCaseStepName); } } } else {
	 * log.info("skip statement");
	 * ReportPortal.emitLog("Test Case skipped due to dependant test case Failure",
	 * "INFO", Calendar.getInstance().getTime());
	 * ReportPortalUtil.finishItemRequest(testRs, ItemStatus.SKIPPED,
	 * IssueEnum.NO_DEFECT); skipTestCase = true; }
	 * 
	 * long endTime = System.currentTimeMillis();
	 * 
	 * long duration = (endTime - startTime); // Total execution time in milli
	 * seconds log.info("Time take for executing UI context of testCase:" +
	 * testNode.getTestCaseID() + "is::" + duration);
	 * 
	 * timerContext.addTestNodeUITiming(testNode.getTestCaseID(), duration);
	 * 
	 * startTime = System.currentTimeMillis();
	 * 
	 * log.info("****************************************");
	 * log.info("testStepStatus :: " + testStepStatus);
	 * log.info("testCaseFailed :: " + testCaseFailed);
	 * log.info("testSuiteFailed :: " + testSuiteFailed);
	 * log.info("****************************************");
	 * 
	 * testEnsureRequestStatusMap.put(testCaseId, (testStepStatus) ?
	 * ItemStatus.PASSED : ItemStatus.FAILED);
	 * 
	 * log.info("skipTestCase testfailedstatus::" + testCaseFailed);
	 * log.info("testStepStatus ::" + testStepStatus);
	 * log.info("manualTestCase ::" + manualTestCase); if (manualTestCase)
	 * { log.info(testNode.getTestCaseID() + " => manual test case");
	 * ReportPortalUtil.manualFinishRequest(testNode.getTestCaseID(), testRs); }
	 * else if (!skipTestCase && testStepStatus && !testCaseFailed) {
	 * ReportPortalUtil.positiveFinishRequest(testNode.getTestCaseID(), testRs); }
	 * else if (!skipTestCase && !testStepStatus && testCaseFailed) { if(issueType
	 * == null) issueType = IssueEnum.TO_INVESTIAGE;
	 * ReportPortalUtil.finishItemRequest(testRs, ItemStatus.FAILED, issueType);
	 * testSuiteFailed = true; } endTime = System.currentTimeMillis();
	 * 
	 * duration = (endTime - startTime); // Total execution time in milli seconds
	 * timerContext.addTestNodeTiming(testNode.getTestCaseID(), duration); }
	 * 
	 * // Print the entire timings for this execution.
	 * 
	 * log.info("**** Printing the timing context for this execution\n" +
	 * timerContext + "/ ");
	 * 
	 * }
	 */

	/**
	 * Processes a single testCaseStep from the {@link TestNode}
	 * 
	 * @param testCaseStep
	 * @return
	 * @throws Throwable
	 */
	/*
	 * private List<TestEnsureRequest> processTestCaseStep(JsonNode testCaseStep,
	 * String file) throws Throwable { //
	 * log.debug("Processing testCaseStep::::::::\n" + //
	 * testCaseStep.toPrettyString()); // Process TestCaseStep
	 * List<TestEnsureRequest> testEnsureRequests = null; Iterator<String>
	 * fieldsIterator = testCaseStep.fieldNames(); if (!fieldsIterator.hasNext()) {
	 * log.
	 * debug("Empty TestCaseStep, or invalid testCaseStep Node, hence skipping this node:\n"
	 * + testCaseStep.toPrettyString()); } else { String fieldName =
	 * fieldsIterator.next(); // Validate if there is only a single field. report
	 * bad format otherwise. if (fieldsIterator.hasNext()) { throw new
	 * JsonAutomationFormatIssue("Multiple fields not allowed in a navAction/validate JsonNode :\n"
	 * + testCaseStep.toPrettyString()); } // Process validate node. if
	 * (fieldName.equals(ParsingConstants.VALIDATE)) { testEnsureRequests =
	 * processValidationNode(testCaseStep.get(ParsingConstants.VALIDATE), file); }
	 * else if (fieldName.equals(ParsingConstants.LOOP)) { testEnsureRequests =
	 * processLoopingActionNode(testCaseStep.get(ParsingConstants.LOOP), file); }
	 * else if (fieldName.equals(ParsingConstants.MANUAL)) { testEnsureRequests =
	 * processManulActionNode(testCaseStep, file); }else { // Process navAction
	 * Node. testEnsureRequests = processNavigationActionNode(testCaseStep, file); }
	 * } return testEnsureRequests; }
	 */

	/*
	 * @param testCaseStep
	 * 
	 * @throws Throwable
	 */
	/*
	 * private List<TestEnsureRequest> processNavigationActionNode(JsonNode
	 * navActionNode, String file) throws Throwable { // Process navActionNode
	 * List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
	 * TestEnsureRequest testEnsureRequest = null;
	 * log.debug("Processing navActionNode:::::\n" +
	 * navActionNode.toPrettyString()); String navAction =
	 * navActionNode.fields().next().getKey(); JsonNode navActionValue =
	 * navActionNode.fields().next().getValue(); testEnsureRequest =
	 * NavigationActions.delegateNavAction(navAction, navActionValue, file);
	 * testEnsureRequests.add(testEnsureRequest); return testEnsureRequests;
	 * 
	 * }
	 */

	/*
	 * @param testCaseStep
	 * 
	 * @throws Throwable
	 */
	/*
	 * private List<TestEnsureRequest> processLoopingActionNode(JsonNode
	 * navActionNode, String file) throws Throwable { // Process navActionNode
	 * List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
	 * TestEnsureRequest testEnsureRequest = null;
	 * log.debug("Processing loopActionNode:::::\n" +
	 * navActionNode.toPrettyString()); String loopAction =
	 * navActionNode.fields().next().getKey(); JsonNode loopActionValue =
	 * navActionNode.fields().next().getValue(); testEnsureRequest =
	 * LoopingActions.delegateLoopAction(loopAction, loopActionValue, file);
	 * testEnsureRequests.add(testEnsureRequest); return testEnsureRequests;
	 * 
	 * }
	 */

	/*
	 * @param testCaseStep
	 * 
	 * @throws Throwable
	 */
	/*
	 * private List<TestEnsureRequest> processManulActionNode(JsonNode
	 * manualActionNode, String file) throws Throwable { // Process navActionNode
	 * List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
	 * TestEnsureRequest testEnsureRequest = null;
	 * log.debug("Processing navActionNode:::::\n" +
	 * manualActionNode.toPrettyString()); String manualAction =
	 * manualActionNode.fields().next().getKey(); JsonNode manualActionValue =
	 * manualActionNode.fields().next().getValue(); testEnsureRequest =
	 * ManualActions.delegateManualAction(manualAction, manualActionValue, file);
	 * testEnsureRequests.add(testEnsureRequest); return testEnsureRequests;
	 * 
	 * }
	 */

	/**
	 * <pre>
	 * { "validate": 
			[ 
			{ "isElementPresent":
					[
						{"fn_configurable_products":{"xpathparams":[""], "fileName" : "FN_Configurable_Products", "bugTitle" : "Unable to navigate to FN Configure Products"}}									
					]
			},
			{ "idDisplayed":
					[
						{"fn_configurable_products":{"xpathparams":[""], "fileName" : "FN_Configurable_Products", "bugTitle" : "Unable to navigate to FN Configure Products"}}									
					]
			}
			]
		}
	 * </pre>
	 * 
	 * @param listJsonNode
	 * @return
	 * @throws Throwable
	 */
	/*
	 * private List<TestEnsureRequest> processValidationNode(JsonNode listJsonNode,
	 * String file) throws Throwable { // Process validateActionNode
	 * List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
	 * TestEnsureRequest testEnsureRequest = null; ObjectMapper mapper = new
	 * ObjectMapper(); log.debug("Processing validateActionNode:::::\n" +
	 * listJsonNode.toPrettyString()); try { for (JsonNode jsonNodeData :
	 * listJsonNode) { log.info("jsonNodeData" + jsonNodeData); String
	 * validateAction = jsonNodeData.fields().next().getKey(); JsonNode
	 * validateActionValue = jsonNodeData.fields().next().getValue();
	 * log.info("validateActionValue" + validateActionValue);
	 * log.info("validateActionValue" + validateActionValue.size());
	 * 
	 *//**
		 * <pre>
		 * { "isElementPresent":
				[
					{"fn_configurable_products":{"xpathparams":[""], "fileName" : "FN_Configurable_Products", "bugTitle" : "Unable to navigate to FN Configure Products"}}									
				]
			},
		 * </pre>
		 *//*
			 * // Expand and iterate over the validation mode in case of mutiple validations
			 * for (JsonNode jsonNode : validateActionValue) {
			 * log.info("jsonNode1" + jsonNode); String tempString = "[" +
			 * jsonNode + "]"; jsonNode = mapper.readTree(tempString);
			 * log.info("tempJsonNode" + jsonNode); testEnsureRequest =
			 * ValidateAction.delegateValidateAction(validateAction, jsonNode, file);
			 * testEnsureRequests.add(testEnsureRequest); } } } catch (Exception e) { //
			 * e.printStackTrace(); } return testEnsureRequests; }
			 */

	private static void validateAAndSortTestNodes(List<TestNode> testNodes) {
		// dependencies to be executed.
	}

	private static void executeFinalTestNodesInParallel(List<TestNode> testNodes) {
	}

	private TestNode buildTestNode(String testNodeJsonText) throws JsonMappingException, JsonProcessingException {
		return new ObjectMapper().readValue(testNodeJsonText, TestNode.class);
	}

	public static void processTestNode(TestNode testNode) {
		// Step1) Process PreReq Node.
		// JsonNode preReqNode;
		// processPreReqNode(testNode.getPreReq());
		// Step2) Process Validations Node.
	}

	@AfterTest(alwaysRun = true)
	public void stopRecording() throws Exception {
		if (recordingFlag) {
			screenRecord.stopRecording();
		}

		System.out.print("After Class Method Executed");
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		try {
			log.debug("Teardown executed");
			ReportPortalUtil.finishExecutionRequest(launchName);
			ReportPortalUtil.updateReportPortalData();
			ReportPortalUtil.updateTestResultsData();
			// closeBrowser();
		} catch (Exception e) {
		}

	}
}