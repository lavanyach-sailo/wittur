/**
 * 
 */
package com.sailotech.testautomation.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.epam.reportportal.listeners.ItemStatus;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.test.enums.IssueEnum;

/**
 * Request Builder Class
 * 
 * @author niranjan.nellutla
 *
 */
public class RequestBuilder extends TestBase {
	
	/**
	 * Method to build TestEnsure Request
	 * 
	 * @param bugTitle
	 * @param flag
	 * @param fileName
	 * @param priority
	 * @return
	 */
	public static TestEnsureRequest build(String bugTitle, boolean flag, String fileName, int priority) {
		// screen shot
		//String filePath = getScreenShot(fileName);
		String filePath = "";

		// adding files
		List<File> files = new ArrayList<File>();
		//files.add(new File(filePath));

		TestEnsureRequest request = new TestEnsureRequest(bugTitle, files, flag, priority, fileName, filePath);
		return request;
	}

	/**
	 * Method to build TestEnsure Request
	 * 
	 * @param bugTitle
	 * @param status
	 * @param fileName
	 * @param priority
	 * @return
	 */
	public static TestEnsureRequest build(String bugTitle, ItemStatus status, IssueEnum issueType, String fileName, int priority) {
		// screen shot
		//String filePath = getScreenShot(fileName);
		String filePath = "";

		// adding files
		List<File> files = new ArrayList<File>();
		//files.add(new File(filePath));

		TestEnsureRequest request = new TestEnsureRequest(bugTitle, files, status, issueType, priority, fileName, filePath);
		return request;
	}
	
	/**
	 * Method to build TestEnsure Request
	 * 
	 * @param bugTitle
	 * @param status
	 * @param fileName
	 * @param priority
	 * @return
	 */
	public static TestEnsureRequest manualBuild(String bugTitle, ItemStatus status, IssueEnum issueType, String fileName, int priority) {
		return new TestEnsureRequest(bugTitle, null, status, issueType, priority, fileName, null);
	}
}
