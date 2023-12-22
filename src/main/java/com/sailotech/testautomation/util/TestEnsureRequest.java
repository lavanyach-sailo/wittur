/**
 * 
 */
package com.sailotech.testautomation.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.ITestResult;

import com.epam.reportportal.listeners.ItemStatus;
import com.sailotech.testautomation.test.enums.IssueEnum;

/**
 * @author niranjan.nellutla
 *
 */
public class TestEnsureRequest {
	private String bugTitle;
	private List<File> files= new ArrayList<>();
	private  ITestResult testResults=null;
	private int priority;
	private String fileName;
	private String filePath;
	private boolean flg;
	ItemStatus status;
	private String log;
	private IssueEnum issueType;
	
	public TestEnsureRequest(String log, String bugTitle, List<File> files, boolean flg, int priority, String fileName,
			String filePath) {
		super();
		this.bugTitle = bugTitle;
		this.files = files;
		
		this.priority = priority;
		this.fileName = fileName;
		this.filePath = filePath;
		this.flg=flg;
		this.log = log;
	}
	
	public TestEnsureRequest(String bugTitle, List<File> files, boolean flg, int priority, String fileName,
			String filePath) {
		super();
		this.bugTitle = bugTitle;
		this.files = files;
		
		this.priority = priority;
		this.fileName = fileName;
		this.filePath = filePath;
		this.flg=flg;
	}
	
	public TestEnsureRequest(String log, String bugTitle, List<File> files, ItemStatus status, IssueEnum issueType, int priority, String fileName,
			String filePath) {
		super();
		this.bugTitle = bugTitle;
		this.files = files;
		
		this.priority = priority;
		this.fileName = fileName;
		this.filePath = filePath;
		this.status=status;
		this.log = log;
		this.issueType = issueType;
	}
	
	public TestEnsureRequest(String bugTitle, List<File> files, ItemStatus status, IssueEnum issueType, int priority, String fileName,
			String filePath) {
		super();
		this.bugTitle = bugTitle;
		this.files = files;
		
		this.priority = priority;
		this.fileName = fileName;
		this.filePath = filePath;
		this.status=status;
		this.issueType = issueType;
	}

	public String getBugTitle() {
		return bugTitle;
	}
	public void setBugTitle(String bugTitle) {
		this.bugTitle = bugTitle;
	}
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}
	public ITestResult getTestResults() {
		return testResults;
	}
	public void setTestResults(ITestResult testResults) {
		this.testResults = testResults;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public boolean isFlg() {
		return flg;
	}
	public void setFlg(boolean flg) {
		this.flg = flg;
	}

	public boolean getFlg() {
		return this.flg;
	}

	public ItemStatus getItemStatus() {
		return status;
	}

	public void setItemStatus(ItemStatus status) {
		this.status = status;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public ItemStatus getStatus() {
		return status;
	}

	public void setStatus(ItemStatus status) {
		this.status = status;
	}

	public IssueEnum getIssueType() {
		return issueType;
	}

	public void setIssueType(IssueEnum issueType) {
		this.issueType = issueType;
	}

	@Override
	public String toString() {
		return "TestEnsureRequest [bugTitle=" + bugTitle + ", files=" + files + ", testResults=" + testResults
				+ ", priority=" + priority + ", fileName=" + fileName + ", filePath=" + filePath + ", flg=" + flg
				+ ", status=" + status + ", log=" + log + ", issueType=" + issueType + "]";
	}
}
