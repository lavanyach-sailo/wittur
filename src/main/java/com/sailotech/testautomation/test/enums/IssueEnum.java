package com.sailotech.testautomation.test.enums;

public enum IssueEnum {
	TO_INVESTIAGE("ti001"),
	AUTOMATION_BUG("ab001"),
	PRODUCTION_BUG("pb001"),
	NO_DEFECT("nd001"),
	SYSTEM_ISSUE("si001");
	
	public final String issueType;

    private IssueEnum(String issueType) {
        this.issueType = issueType;
    }
    
}
