package com.sailotech.testautomation.util;

import java.util.HashMap;

/*
 * Timer context built during the test case execution in the test automation context.
 */
public class TimerContext {
	private static HashMap<String, TimingDetails> testCaseIDToTimingDetailsmap = new HashMap();

	private static class TimingDetails {
		private Long aquillaAPICallTiming;
		private Long testCaseUINavigationAndValidationTiming;

		public TimingDetails setAquillaAPICallTiming(Long timeInMillis) {
			aquillaAPICallTiming = timeInMillis;
			return this;
		}

		public TimingDetails setTestCaseUIStepsTiming(Long timeInMillis) {
			testCaseUINavigationAndValidationTiming = timeInMillis;
			return this;
		}

		static TimingDetails builder() {
			return new TimingDetails();
		}

		@Override
		public String toString() {
			return "TimingDetails [aquillaAPICallTiming=" + aquillaAPICallTiming
					+ ", testCaseUINavigationAndValidationTiming=" + testCaseUINavigationAndValidationTiming + "]\n";
		}
	}

	public void addTestNodeTiming(String testCaseID, Long timeInMillis) {
		if (!existingEntry(testCaseID)) {
			testCaseIDToTimingDetailsmap.put(testCaseID, TimingDetails.builder().setAquillaAPICallTiming(timeInMillis));
		} else {
			testCaseIDToTimingDetailsmap.put(testCaseID,
					TimingDetails.builder().setAquillaAPICallTiming(timeInMillis).setTestCaseUIStepsTiming(
							testCaseIDToTimingDetailsmap.get(testCaseID).testCaseUINavigationAndValidationTiming));
		}

	}

	public void addTestNodeUITiming(String testCaseID, Long timeInMillis) {
		if (!existingEntry(testCaseID)) {
			testCaseIDToTimingDetailsmap.put(testCaseID,
					TimingDetails.builder().setTestCaseUIStepsTiming(timeInMillis));
		}
	}

	private boolean existingEntry(String testCaseID) {
		return testCaseIDToTimingDetailsmap.containsKey(testCaseID);
	}

	@Override
	public String toString() {
		return "TimerContext \n: "+testCaseIDToTimingDetailsmap;
	}	
	
}
