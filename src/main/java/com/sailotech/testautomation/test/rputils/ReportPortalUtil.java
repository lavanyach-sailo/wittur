package com.sailotech.testautomation.test.rputils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.epam.reportportal.listeners.ItemStatus;
import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.ReportPortal;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import com.epam.ta.reportportal.ws.model.issue.Issue;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.beans.JobUpdateRequest;
import com.sailotech.testautomation.database.StatusUpdater;
import com.sailotech.testautomation.test.enums.IssueEnum;
import com.sailotech.testautomation.util.RestAPIUtil;
import com.sailotech.testautomation.util.TestEnsureRequest;

import io.reactivex.Maybe;

public class ReportPortalUtil extends TestBase {

	static ReportPortal reportPortal = null;
	private static Launch launch;
	private static String launchUUID = null;
	static String launchName = null;
	static Maybe<String> suiteRs = null;
	static Maybe<String> testRs = null;
	static boolean updateReportPortal = Boolean.parseBoolean((String) prop.get("updateReportPortal"));
	public static boolean rerun = false;
	public static String endpoint = null;
	public static String bearerToken = null;
	public static String projectName = null;

	public static ListenerParameters standardParameters() {
		ListenerParameters result = new ListenerParameters();
		result.setRerun(false);
		result.setBaseUrl(reportPortalParams.getProperty("rp.endpoint"));
		result.setClientJoin(Boolean.valueOf(reportPortalParams.getProperty("rp.client.join")));
		result.setBatchLogsSize(Integer.parseInt(reportPortalParams.getProperty("rp.batch.size.logs")));
		result.setLaunchName(reportPortalParams.getProperty("rp.launch") + generateUniqueId());
		result.setProjectName(reportPortalParams.getProperty("rp.project"));
		result.setEnable(Boolean.valueOf(reportPortalParams.getProperty("rp.enable")));
		// result.setApiKey(reportPortalParams.getProperty("rp.uuid"));
		return result;
	}

	public static String createLaunch(ListenerParameters STANDARD_PARAMETERS) throws InterruptedException {
		if (updateReportPortal && !rerun) {
			try {
				reportPortal = ReportPortal.builder().withParameters(STANDARD_PARAMETERS).build();
				String uuid = UUID.randomUUID().toString(); // Generates random UUID
				log.info("STANDARD_PARAMETERS => " + STANDARD_PARAMETERS);
				launch = reportPortal.newLaunch(standardLaunchRequest(STANDARD_PARAMETERS, uuid));
				Maybe<String> launchUuid = launch.start();
				launchName = launch.getParameters().getLaunchName();
				log.info("Launch ID: " + launchUuid + "::" + launchName + "::launch");
				launchUUID = uuid;
				Thread.sleep(2000);
				// String launch = reportPortalParams.getProperty("rp.launch");
				RestAPIUtil.postLaunchDataByUUid(endpoint, bearerToken, projectName, uuid);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return launchName;
	}

	/**
	 * Generates a unique ID shorter than UUID based on current time in milliseconds
	 * and thread ID.
	 *
	 * @return a unique ID string
	 */
	public static String generateUniqueId() {
		return System.currentTimeMillis() + "-" + Thread.currentThread().getId() + "-"
				+ ThreadLocalRandom.current().nextInt(9999);
	}

	public static FinishTestItemRQ positiveFinishRequest(String testCaseID, Maybe<String> item) {
		FinishTestItemRQ rq = null;
		if (updateReportPortal && !rerun) {
			rq = new FinishTestItemRQ();
			rq.setEndTime(Calendar.getInstance().getTime());
			rq.setStatus("PASSED");
			Set<ItemAttributesRQ> items = new HashSet<ItemAttributesRQ>();
			ItemAttributesRQ itemAttributesRQ = new ItemAttributesRQ();
			itemAttributesRQ.setKey("status");
			if (manualTestCase) {
				itemAttributesRQ.setValue(String.valueOf("MANUAL"));
			} else if (testCaseFailed != true) {
				itemAttributesRQ.setValue(String.valueOf(ItemStatus.PASSED));
			} else {
				itemAttributesRQ.setValue(String.valueOf(ItemStatus.FAILED));
				testSuiteFailed = true;
			}
			items.add(itemAttributesRQ);
			rq.setAttributes(items);
			launch.finishTestItem(item, rq);
		} else if (rerun && rerunMap.size() != 0) {
			try {
				String id = rerunMap.get(testCaseID).keySet().iterator().next();
				if (testCaseFailed != true)
					RestAPIUtil.updateTestItem(endpoint, bearerToken, projectName, id, ItemStatus.PASSED);
				else
					RestAPIUtil.updateTestItem(endpoint, bearerToken, projectName, id, ItemStatus.FAILED);
			} catch (Exception e) {
			}
		}
		return rq;
	}

	public static FinishTestItemRQ manualFinishRequest(String testCaseID, Maybe<String> item) {
		FinishTestItemRQ rq = null;
		if (updateReportPortal && !rerun) {
			rq = new FinishTestItemRQ();
			rq.setEndTime(Calendar.getInstance().getTime());
			rq.setStatus(ItemStatus.INFO.toString());
			Set<ItemAttributesRQ> items = new HashSet<ItemAttributesRQ>();
			ItemAttributesRQ itemAttributesRQ = new ItemAttributesRQ();
			itemAttributesRQ.setKey("status");
			itemAttributesRQ.setValue(String.valueOf(ItemStatus.INFO));
			items.add(itemAttributesRQ);
			rq.setAttributes(items);
			launch.finishTestItem(item, rq);
		} else if (rerun && rerunMap.size() != 0) {
			try {
				String id = rerunMap.get(testCaseID).keySet().iterator().next();
				RestAPIUtil.updateTestItem(endpoint, bearerToken, projectName, id, ItemStatus.INFO);
			} catch (Exception e) {
			}
		}
		return rq;
	}

	public static FinishTestItemRQ finishRequest(Maybe<String> item, ItemStatus status, String issueType) {
		FinishTestItemRQ rq = null;
		if (updateReportPortal && !rerun) {
			rq = new FinishTestItemRQ();
			rq.setEndTime(Calendar.getInstance().getTime());
			rq.setStatus(String.valueOf(status));
			Set<ItemAttributesRQ> items = new HashSet<ItemAttributesRQ>();
			ItemAttributesRQ itemAttributesRQ = new ItemAttributesRQ();
			itemAttributesRQ.setKey("status");
			itemAttributesRQ.setValue(String.valueOf(status));
			items.add(itemAttributesRQ);
			rq.setAttributes(items);
			/*
			 * Issue issue = new Issue(); issue.setIssueType(issueType); rq.setIssue(issue);
			 */
			log.info("finishRequest" + rq.toString());
			launch.finishTestItem(item, rq);
		}
		return rq;
	}

	public static FinishTestItemRQ finishItemRequest(Maybe<String> item, ItemStatus status, IssueEnum issueType) {
		FinishTestItemRQ rq = null;
		if (updateReportPortal && !rerun) {
			rq = new FinishTestItemRQ();
			rq.setEndTime(Calendar.getInstance().getTime());
			log.info("Updating Item Request status to :: " + String.valueOf(status));
			rq.setStatus(String.valueOf(status));
			Set<ItemAttributesRQ> items = new HashSet<ItemAttributesRQ>();
			ItemAttributesRQ itemAttributesRQ = new ItemAttributesRQ();
			itemAttributesRQ.setKey("status");
			itemAttributesRQ.setValue(String.valueOf(status));
			items.add(itemAttributesRQ);
			rq.setAttributes(items);
			Issue issue = new Issue();
			issue.setIssueType(issueType.issueType);
			rq.setIssue(issue);
			log.info("finishItemRequest key atriutes" + rq.getAttributes().toString());
			launch.finishTestItem(item, rq);
		}
		return rq;
	}

	public static void addTestStep(TestEnsureRequest testEnsureRequest, String testStepDescription,
			String testCaseStepName, String moduleId, String testCaseId, String testCaseStepId) {
		if (updateReportPortal && !rerun) {
			try {
				launch.getStepReporter().sendStep(testEnsureRequest.getItemStatus(),
						(testStepDescription != null) ? testCaseStepName + ": " + testStepDescription
								: testCaseStepName);
				JobUpdateRequest imageFiles = addLogsAndScreenhots("fileName");
				// log.info("imageFiles length" + imageFiles.size());
				StatusUpdater.updateStatus(runID, testCaseStepId, testEnsureRequest.getItemStatus().toString(),
						moduleId, testCaseId, imageFiles);
				launch.getStepReporter().finishPreviousStep();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				JobUpdateRequest imageFiles = addLogsAndScreenhots("fileName");
				// log.info("imageFiles length" + imageFiles.size());
				StatusUpdater.updateStatus(runID, testCaseStepId, testEnsureRequest.getItemStatus().toString(),
						moduleId, testCaseId, imageFiles);
			} catch (Exception e) {
			}
		}
	}

	public static void addTestStep(TestEnsureRequest testEnsureRequest, String testStepDescription,
			String testCaseStepName, String content, String moduleId, String testCaseId, String testCaseStepId) {
		if (updateReportPortal && !rerun) {
			try {
				launch.getStepReporter().sendStep(testEnsureRequest.getItemStatus(),
						(testStepDescription != null) ? testCaseStepName + ": " + testStepDescription
								: testCaseStepName);
				ReportPortal.emitLog(content, "INFO", Calendar.getInstance().getTime());
				StatusUpdater.updateStatus(runID, testCaseStepId, "UNTESTED", moduleId, testCaseId,
						new JobUpdateRequest());
				launch.getStepReporter().finishPreviousStep();
			} catch (Exception e) {
			}
		}
	}

	public static void addSkippedTestStep(String testCaseStepName) {
		if (updateReportPortal && !rerun) {
			try {
				launch.getStepReporter().sendStep(ItemStatus.SKIPPED, testCaseStepName);
				ReportPortal.emitLog("Test Step skipped due to previous test step Failure", "INFO",
						Calendar.getInstance().getTime());
				launch.getStepReporter().finishPreviousStep();
			} catch (Exception e) {
			}
		}
	}

	public static FinishExecutionRQ finishExecutionRequest(String launchName) {
		FinishExecutionRQ rq = null;
		try {
			if (updateReportPortal && !rerun) {
				rq = new FinishExecutionRQ();
				rq.setEndTime(Calendar.getInstance().getTime());
				if (testSuiteFailed != true)
					rq.setStatus(String.valueOf(ItemStatus.PASSED));
				else
					rq.setStatus(String.valueOf(ItemStatus.FAILED));
				Set<ItemAttributesRQ> items = new HashSet<ItemAttributesRQ>();
				ItemAttributesRQ itemAttributesRQ = new ItemAttributesRQ();
				itemAttributesRQ.setKey("build");
				itemAttributesRQ.setValue(launchName);
				items.add(itemAttributesRQ);
				rq.setAttributes(items);
				log.info("finishItemRequest key atriutes" + rq.getAttributes().toString());
				log.info("FinishExecutionRQ" + rq.toString());
				// rq.setStatus(isLaunchFailed.get() ? Statuses.FAILED : Statuses.PASSED);
				launch.finish(rq);
				log.info("Rerun 1");
			}
		} catch (Exception e) {
		}
		return rq;
	}

	public static StartLaunchRQ standardLaunchRequest(final ListenerParameters params, String uuid) {
		StartLaunchRQ rq = new StartLaunchRQ();
		rq.setRerun(false);
		rq.setName(params.getLaunchName());
		rq.setStartTime(Calendar.getInstance().getTime());
		rq.setAttributes(params.getAttributes());
		rq.setMode(params.getLaunchRunningMode());
		rq.setRerun(params.isRerun());
		rq.setStartTime(Calendar.getInstance().getTime());
		rq.setUuid(uuid);
		return rq;
	}

	public static StartTestItemRQ standardStartSuiteRequest() {
		StartTestItemRQ rq = new StartTestItemRQ();
		rq.setStartTime(Calendar.getInstance().getTime());
		String id = generateUniqueId();
		rq.setName("Suite_" + id);
		rq.setDescription("Suite description");
		rq.setUniqueId(id);
		rq.setType("SUITE");
		return rq;
	}

	public static Maybe<String> standardStartSuiteRequest(String suiteName, String description) {
		if (updateReportPortal && !rerun) {
			StartTestItemRQ rq = new StartTestItemRQ();
			rq.setStartTime(Calendar.getInstance().getTime());
			String id = generateUniqueId();
			rq.setName(suiteName);
			rq.setDescription(description);
			rq.setUniqueId(id);
			rq.setType("SUITE");
			suiteRs = launch.startTestItem(rq);
			log.info("suiteRS::" + suiteRs.toString() + "::" + suiteRs);
			log.info("Rerun 2");
		}
		return suiteRs;
	}

	public static Maybe<String> standardStartSuiteRequest(Maybe<String> item, String suiteName, String description) {
		if (updateReportPortal && !rerun) {
			StartTestItemRQ rq = new StartTestItemRQ();
			rq.setStartTime(Calendar.getInstance().getTime());
			String id = generateUniqueId();
			rq.setName(suiteName);
			rq.setDescription(description);
			rq.setUniqueId(id);
			rq.setType("SUITE");
			suiteRs = launch.startTestItem(item, rq);
			log.info("suiteRS::" + suiteRs.toString() + "::" + suiteRs);
			log.info("Rerun 2");
		}
		return suiteRs;
	}

	public static StartTestItemRQ standardStartTestRequest() {
		StartTestItemRQ rq = new StartTestItemRQ();
		rq.setStartTime(Calendar.getInstance().getTime());
		String id = generateUniqueId();
		rq.setName("Test_" + id);
		rq.setDescription("Test description");
		rq.setUniqueId(id);
		rq.setType("TEST");
		return rq;
	}

	public static Maybe<String> standardStartTestRequest(Maybe<String> item, String testCaseID, String name,
			String description, String type) {
		if (updateReportPortal && !rerun) {
			StartTestItemRQ rq = new StartTestItemRQ();
			rq.setStartTime(Calendar.getInstance().getTime());
			log.info("rq.start" + rq.getStartTime());
			log.info(rq.toString());
			String id = generateUniqueId();
			rq.setTestCaseId(testCaseID);
			rq.setName(name);
			rq.setDescription(description);
			rq.setUniqueId(id);
			rq.setType(type);
			log.info("standardStartTestRequest");
			log.info(rq.toString());
			testRs = launch.startTestItem(item, rq);
			log.info("Rerun 3");
		}
		return testRs;
	}

	public static StartTestItemRQ standardStartStepRequest() {
		StartTestItemRQ rq = new StartTestItemRQ();
		rq.setStartTime(Calendar.getInstance().getTime());
		String id = generateUniqueId();
		rq.setName("Step_" + id);
		rq.setDescription("Test step description");
		rq.setUniqueId(id);
		rq.setType("STEP");
		return rq;
	}

	public static void updateReportPortalData() {
		try {
			// Thread.sleep(1000);
			if (launchID != null && !launchID.equals(""))
				RestAPIUtil.updateLaunchDataByID(endpoint, bearerToken, projectName, launchID);
			else
				RestAPIUtil.updateLaunchDataByUUID(endpoint, bearerToken, projectName, launchUUID);
			log.info("Rerun 4");
		} catch (Exception e) {
			log.info("Rerun 5");
		}
	}

	public static void updateTestResultsData() {
		try {
			// Thread.sleep(2000);
			RestAPIUtil.postLaunchDataByUUid(endpoint, bearerToken, projectName, launchUUID, launchUUID);
			log.info("Rerun 4");
		} catch (Exception e) {
			log.info("Rerun 5");
		}
	}

}
