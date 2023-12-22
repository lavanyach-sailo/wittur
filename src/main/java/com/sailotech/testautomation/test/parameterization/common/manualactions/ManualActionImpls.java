package com.sailotech.testautomation.test.parameterization.common.manualactions;

import com.epam.reportportal.listeners.ItemStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.util.RequestBuilder;
import com.sailotech.testautomation.util.TestEnsureRequest;

public class ManualActionImpls extends TestBase {

	public static TestEnsureRequest manual(JsonNode manualActionParams, String file, String testCaseStepId)
			throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = RequestBuilder.manualBuild("bugTitle", ItemStatus.INFO, null, "fileName", 1);
		TestBase.manualTestCase = true;
		String expected = "Expected : " + getJsonFieldValue(manualActionParams, "expected");
		// StatusUpdater.updateStatus(runID, testCaseStepId, "UNTESTED");
		return testEnsureRequest;
	}
}
