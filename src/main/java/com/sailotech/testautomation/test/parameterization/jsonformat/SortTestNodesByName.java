package com.sailotech.testautomation.test.parameterization.jsonformat;

import java.util.Comparator;

public class SortTestNodesByName implements Comparator<TestNode> {
	// Used for sorting in ascending order of
	// name
	@Override
	public int compare(TestNode testNode1, TestNode testNode2) {
		return testNode1.testCaseID.compareTo(testNode2.testCaseID);
	}
}
