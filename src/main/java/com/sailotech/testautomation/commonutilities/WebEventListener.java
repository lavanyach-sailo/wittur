package com.sailotech.testautomation.commonutilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.sailotech.testautomation.accelarators.TestBase;



public class WebEventListener extends TestBase implements WebDriverEventListener {
	
	private static final String ROOT = System.getProperty("user.dir");
	private static final String SEPARATOR = File.separator;

	public void beforeNavigateTo(String url, WebDriver driver) {
		log.info("Before navigating to: '" + url + "'");
	}

	public void afterNavigateTo(String url, WebDriver driver) {
		log.info("Navigated to:'" + url + "'");
	}

	public void beforeChangeValueOf(WebElement element, WebDriver driver) {
		log.info("Value of the:" + element.toString() + " before any changes made");
	}

	public void afterChangeValueOf(WebElement element, WebDriver driver) {
		log.info("Element value changed to: " + element.toString());
	}

	public void beforeClickOn(WebElement element, WebDriver driver) {
		log.info("Trying to click on: " + element.toString());
	}

	public void afterClickOn(WebElement element, WebDriver driver) {
		log.info("Clicked on: " + element.toString());
	}

	public void beforeNavigateBack(WebDriver driver) {
		log.info("Navigating back to previous page");
	}

	public void afterNavigateBack(WebDriver driver) {
		log.info("Navigated back to previous page");
	}

	public void beforeNavigateForward(WebDriver driver) {
		log.info("Navigating forward to next page");
	}

	public void afterNavigateForward(WebDriver driver) {
		log.info("Navigated forward to next page");
	}

	public void onException(Throwable error, WebDriver driver) {
		log.info("Exception occured: " + error);
		try {
			TestUtil.takeScreenshotAtEndOfTest();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void beforeFindBy(By by, WebElement element, WebDriver driver) {
		log.info("Trying to find Element By : " + by.toString());


		WebElement elem = driver.findElement(by);
		// draw a border around the found element
		if (driver instanceof JavascriptExecutor) {
			((JavascriptExecutor) driver).executeScript("arguments[0].style.border='4px solid yellow'", elem);
			//((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style', arguments[1]);", elem, "color: yellow; border: 2px solid yellow;");
		}
	
	}

	public void afterFindBy(By by, WebElement element, WebDriver driver) {
		log.info("Found Element By : " + by.toString());
	}

	/*
	 * non overridden methods of WebListener class
	 */
	public void beforeScript(String script, WebDriver driver) {
	}

	public void afterScript(String script, WebDriver driver) {
	}

	public void beforeAlertAccept(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	public void afterAlertAccept(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	public void afterAlertDismiss(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	public void beforeAlertDismiss(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	public void beforeNavigateRefresh(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	public void afterNavigateRefresh(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
		// TODO Auto-generated method stub

	}

	public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
		// TODO Auto-generated method stub

	}

	public <X> void afterGetScreenshotAs(OutputType<X> arg0, X arg1) {
		// TODO Auto-generated method stub
		
	}

	public void afterGetText(WebElement arg0, WebDriver arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	public void afterSwitchToWindow(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	public <X> void beforeGetScreenshotAs(OutputType<X> arg0) {
		// TODO Auto-generated method stub
		
	}

	public void beforeGetText(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	public void beforeSwitchToWindow(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public synchronized void onStart(ITestContext context) {
		log.info("----------------------START onStart---------------");
		super.onStart(context);
		String testSuite = context.getSuite().getName();
		log.info("[Test Suite START (Thread=" + Thread.currentThread().getId() + ")] " + testSuite);
		log.info("----------------------END onStart---------------");
	}

	@Override
	public synchronized void onFinish(ITestContext context) {
		log.info("----------------------START onFinish---------------");
		super.onFinish(context);
		log.info("[Test Suite Finish (Thread=" + Thread.currentThread().getId() + ")] " + context.getSuite().getName());
		log.info("----------------------END onFinish---------------");
	}

	@Override
	public synchronized void onTestStart(ITestResult result) {
		log.info("----------------------START onTestStart---------------");
		super.onTestStart(result);
		String testClass = result.getTestClass().getName().replaceAll(".+\\.", "");
		String testMethod = result.getMethod().getMethodName();
		String[] testGroups = result.getMethod().getGroups();
		String testStart = "[TEST START (Thread=" + Thread.currentThread().getId() + ")] " + testClass + " - " + testMethod;
		log.info(testStart);
		log.info("----------------------END onTestStart---------------");	
	}

	@Override
	public synchronized void onTestSuccess(ITestResult tr) {
		log.info("----------------------START onTestSuccess---------------");
		super.onTestSuccess(tr);
		String testName = tr.getTestClass().getName().replaceAll(".+\\.", "") + " - " + tr.getMethod().getMethodName();
		String testPass = "[***TEST PASS (Thread=" + Thread.currentThread().getId() + ")***] " + testName;
		log.info(testPass);
		log.info("----------------------END onTestSuccess---------------");
	}

	@Override
	public synchronized void onTestFailure(ITestResult tr) {
		log.info("----------------------START onTestFailure---------------");
		super.onTestFailure(tr);
		Object currentClass = tr.getInstance();
		WebDriver driver = null ;
		try{
			driver= ((TestBase) currentClass).driver;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		//Timestamp the screen shot
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy-HH-mm-ss");
		Calendar calendar = Calendar.getInstance();
		String timeStamp = simpleDateFormat.format(calendar.getTime());
		String baseDir = ROOT + SEPARATOR + "results" + SEPARATOR + "test-output";
		try {
			File scrFile;
			scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String fileName = tr.getMethod().getMethodName() + "_" + timeStamp + "_TestFAILED.png";
			File destination = new File(baseDir + SEPARATOR + fileName);
			FileUtils.copyFile(scrFile, destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String errHeader = " [***TEST FAIL (Thread=" + Thread.currentThread().getId() + ")***] " + tr.getTestClass().getName().replaceAll(".+\\.", "") + " - " + tr.getMethod().getMethodName();
		String errMsg = errHeader + "\r\n" + tr.getThrowable().getMessage() + "\r\n\t" +Arrays.toString(tr.getThrowable().getStackTrace()).replaceAll(",", "\r\n\t");
		log.error(errMsg);
		log.info("----------------------END onTestFailure---------------");
	}

	@Override
	public synchronized void onTestSkipped(ITestResult tr) {
		try {
			log.info("----------------------START TEST SKIPPED---------------");
			super.onTestSkipped(tr);
			log.info("----------------------TEST skip Reason-------------------"+tr.getThrowable().getMessage());
			String skipErr = " [TEST SKIP (Thread=" + Thread.currentThread().getId() + ")] " + tr.getTestClass().getName().replaceAll(".+\\.", "") + " - " + tr.getMethod().getMethodName();
			log.info("------------------Test Skipped as : "+skipErr+"------------------------");
			log.info(skipErr);
			log.info("----------------------TEST SKIP REASON---------------"+skipErr);
			log.info("----------------------END TEST SKIPPED---------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void onConfigurationFailure(ITestResult tr) {
		log.info("----------------------START onConfigurationFailure---------------");
		super.onConfigurationFailure(tr);
		log.info("----------------------Configuration Failure REASON-------------------"+tr.getThrowable().getMessage());
		String testClass = tr.getTestClass().getName().replaceAll(".+\\.", "");
		String testMethod = tr.getMethod().getMethodName();
		Object currentClass = tr.getInstance();
		String errHeader = " [TEST CONFIG FAIL (Thread=" + Thread.currentThread().getId() + ")] " + testClass + " - " + testMethod;
		WebDriver driver = null;
		//Timestamp the screen shot
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy-HH-mm-ss");
		Calendar calendar = Calendar.getInstance();
		String timeStamp = simpleDateFormat.format(calendar.getTime());
		String baseDir = ROOT + SEPARATOR + "results" + SEPARATOR + "test-output";
		File scrFile=null;
		try{
			driver = ((TestBase) currentClass).driver;
			if(driver!=null)
			{
				scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		String fileName = testMethod + "_ConfigFail_" + timeStamp + ".png";
		File destination = new File(baseDir + SEPARATOR + fileName);
		// adding screenshots to log
		try {
			FileUtils.copyFile(scrFile, destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String err = errHeader + "\r\n" + tr.getThrowable().getMessage() + "\r\n\t" +
				Arrays.toString(tr.getThrowable().getStackTrace()).replaceAll(",", "\r\n\t");
		log.error(err);
		log.info("----------------------END onConfigurationFailure---------------");
	}

	@Override
	public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

}