package com.sailotech.testautomation.test.parameterization.common.navactions;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.epam.reportportal.listeners.ItemStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.commonutilities.ReadExcel;
import com.sailotech.testautomation.commonutilities.TestUtil;
import com.sailotech.testautomation.constants.ProcessConstants;
import com.sailotech.testautomation.test.enums.IssueEnum;
import com.sailotech.testautomation.util.RequestBuilder;
import com.sailotech.testautomation.util.TestEnsureRequest;

public class NavActionImpls extends TestBase {

	public static TestEnsureRequest login(JsonNode navActionParams, String file, String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		String url = getJsonFieldValue(navActionParams, "url");
		String userName = getJsonFieldValue(navActionParams, "login_username");
		String password = getJsonFieldValue(navActionParams, "login_password");
		log.info("url" + url);
		log.info("userName" + userName);
		log.info("Password" + password);
		loginToInfor(url, userName, password);
		testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		return testEnsureRequest;
	}

	/**
	 * typeText action can have the following json Node.
	 * 
	 * <pre>
	 *  	{
	 *  		xpath:"textValueTobeTyped"
	 *  	}
	 * </pre>
	 * 
	 * @param navActionParams
	 * @throws Throwable
	 */
	public static TestEnsureRequest typeText(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.TYPE, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest clickElement(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.CLICKELEMENT, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest clickLastElement(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.CLICKLASTELEMENT, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest typeMeasurementValues(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.TYPEMEASUREMENTVALUES, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest clickVisibleElement(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.CLICKVISIBLEELEMENT, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest doubleClickElement(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.DOUBLECLICKELEMENT, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest typeTextWithEnter(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.TYPETEXTWITHENTER, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest typeTextWithTabKey(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.TYPETEXTWITHTABKEY, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest textClearInputs(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.TEXTCLEAR, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest zoomOut(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		zoomIn();
		return testEnsureRequest;
	}

	public static TestEnsureRequest savePDF(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {

		TestEnsureRequest testAssureRequest = null;
		String by = getJsonFieldValue(navActionParams, "by");
		String frame = getJsonFieldValue(navActionParams, "frame");
		String fileName = getJsonFieldValue(navActionParams, "fileName");
		String ignore = getJsonFieldValue(navActionParams, "ignore");
		String script = null;
		try {
			if (ignore == null)
				switch (by) {
				case "cssSelector":
					WebElement pdf = driver.findElement(By.cssSelector("a"));
					script = "arguments[0].setAttribute('download')"
							+ ((JavascriptExecutor) driver).executeScript(script, pdf);
					pdf.click();
					break;
				case "id":
					String parent = driver.getWindowHandle();
					String src = driver.findElement(By.id(frame)).getAttribute("src");
					log.info("nocode webelement " + src);

					if (!Boolean.parseBoolean((String) prop.getProperty("enableIncognitoNewTabJS"))) {
						driver.switchTo().newWindow(WindowType.TAB);
						driver.navigate().to(src);
					} else {
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("window.open('" + src + "','_blank');");
					}

					/*
					 * String selector =
					 * "return document.querySelector('body > settings-ui').shadowRoot.querySelector('#main').shadowRoot.querySelector('settings-basic-page').shadowRoot.querySelector('#basicPage > settings-section.expanded > settings-privacy-page').shadowRoot.querySelector('#pages > settings-subpage > div > settings-radio-group > settings-collapse-radio-button:nth-child(1)').shadowRoot.querySelector('#button')"
					 * ; driver.get("chrome://settings/content/pdfDocuments"); JavascriptExecutor js
					 * = (JavascriptExecutor) driver; WebElement webElement = (WebElement)
					 * js.executeScript(selector); log.info("selector webelement" +
					 * webElement); webElement.click();
					 */
					// driver.navigate().to(src);

					/*
					 * selector =
					 * "return document.querySelector('body > settings-ui').shadowRoot.querySelector('#main').shadowRoot.querySelector('settings-basic-page').shadowRoot.querySelector('#basicPage > settings-section.expanded > settings-privacy-page').shadowRoot.querySelector('#pages > settings-subpage > div > settings-radio-group > settings-collapse-radio-button:nth-child(2)').shadowRoot.querySelector('#button')"
					 * ; webElement = (WebElement) js.executeScript(selector);
					 * log.info("selector webelement" + webElement); webElement.click();
					 */
					Thread.sleep(1000);

					renameFile(fileName);
					// switchToPatentWindow(parent);

					// driver.switchTo().newWindow(WindowType.TAB);
					// driver.get("chrome://downloads/");
					// switchToPatentWindow(parent, true);
					break;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		testAssureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		return testAssureRequest;
	}

	public static TestEnsureRequest extractDataFromPDF(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {

		TestEnsureRequest testAssureRequest = null;
		String by = getJsonFieldValue(navActionParams, "by");
		String frame = getJsonFieldValue(navActionParams, "frame");
		String fileName = getJsonFieldValue(navActionParams, "fileName");
		int line = Integer.parseInt(getJsonFieldValue(navActionParams, "line"));
		int pos = Integer.parseInt(getJsonFieldValue(navActionParams, "pos"));
		String delimeter = getJsonFieldValue(navActionParams, "delimeter");
		String varName = getJsonFieldValue(navActionParams, "varName");
		String script = null;
		String src = null;
		String parent = driver.getWindowHandle();
		try {
			Thread.sleep(2000);
			switch (by) {
			case "cssSelector":
				WebElement pdf = driver.findElement(By.cssSelector("a"));
				script = "arguments[0].setAttribute('download')"
						+ ((JavascriptExecutor) driver).executeScript(script, pdf);
				pdf.click();
				break;
			case "class":
				List<WebElement> iframes = driver.findElements(By.className(frame));
				for (WebElement iframe : iframes) {
					src = iframe.getAttribute("src");
					log.info("src --> " + src);
				}
				if (src == null || src.isEmpty() || src.isBlank()) {
					String finalXpath = getJsonFinalXpath(navActionParams, false);
					log.info("finalXpath ---> " + finalXpath);
					WebElement element = driver.findElement(By.xpath(finalXpath));
					src = element.getAttribute("src");
					log.info("src xpath " + src);
				}
				log.info("nocode webelement " + src);
				break;
			}
			// driver.switchTo().newWindow(WindowType.TAB);
			// driver.navigate().to(src);
			try {
				for (File f : new File(downloadFilepath).listFiles())
					if (!f.isDirectory() && f.getName().contains("tmp"))
						f.delete();
			} catch (Exception e) {
			}

			log.info("downloadFilepath files deleted");

			if (!Boolean.parseBoolean((String) prop.getProperty("enableIncognitoNewTabJS"))) {
				driver.switchTo().newWindow(WindowType.TAB);
				driver.navigate().to(src);
			} else {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("window.open('" + src + "','_blank');");
			}

			String filePath = renameFile(fileName);
			Thread.sleep(2000);
			switchToPatentWindow(parent, true);

			log.info("filePath :: " + filePath);

			try {
				URL url = new URL("file:///" + filePath);
				InputStream is = url.openStream();
				BufferedInputStream fileParse = new BufferedInputStream(is);
				PDDocument document = PDDocument.load(fileParse);

				String pdfContent = new PDFTextStripper().getText(document);
				document.close();
				fileParse.close();
				is.close();
				log.info("pdfContent \n\n" + pdfContent);

				String lines[] = pdfContent.split("\n");
				log.info("lines " + lines.length);
				for (int i = 0; i < lines.length; i++) {
					if (line == i) {
						log.info("line " + i + " => " + lines[i]);
						String[] words = lines[i].split(delimeter);
						log.info("words :: " + words.length);
						log.info("word :: " + words[pos]);
						variables.put(varName, words[pos].trim().strip());
					}
				}
				log.info("$$$$$$$$$$$$$$ variables $$$$$$$$$$$$$$$");
				log.info("variables " + variables.toString());
				log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		testAssureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		return testAssureRequest;
	}

	public static void switchToPatentWindow(String parent, boolean switchFrame) {
		Set<String> s = driver.getWindowHandles();

		try {
			log.info("parent::" + parent);
			// Now iterate using Iterator
			Iterator<String> I1 = s.iterator();

			while (I1.hasNext()) {

				String child_window = I1.next();
				log.info(parent + " ---> " + child_window);
				try {
					if (!parent.equals(child_window)) {
						driver.switchTo().window(child_window);
						log.info("window title ----> " + driver.switchTo().window(child_window).getTitle());
						Thread.sleep(1000);
						driver.close();
					}
				} catch (Exception e) {
				}

				driver.switchTo().window(parent);
			}
			if (switchFrame) {
				log.info("switchFrame" + switchFrame);
				driver.switchTo().frame(driver.findElement(By.className("m-app-frame")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TestEnsureRequest zoom(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		String xpathKey = null, keyValue = null;
		if (!navActionParams.isArray()) {
			log.info("Executing Navaction typeTextWithEnter for JsonNode\n" + navActionParams);
			xpathKey = getXPathKeyFromJSON(navActionParams);
			keyValue = navActionParams.get(xpathKey).asText();
		} else if (navActionParams.isArray()) {
			keyValue = getJsonFieldValue(navActionParams, "value");
		}
		log.info("Zoom out Level" + keyValue);
		zoomOutByZoomLevel(keyValue);
		return testEnsureRequest;
	}

	public static TestEnsureRequest save(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		String varName = getJsonFieldValue(navActionParams, "varName");
		String assign = getJsonFieldValue(navActionParams, "assign");

		// variables.put(varName, String.valueOf(objectVar.size()));

		try {
//			log.info("assign"+assign);
//			log.info("Variables"+variables.toString());

			switch (assign)

			{
			case "0":
				log.info("case 0");
				variables.put(varName, String.valueOf("0"));
				break;
			case "inc":
				log.info("case inc");
				int counter = Integer.parseInt(variables.get(varName)) + 1;
				variables.put(varName, String.valueOf(counter));
				log.info("After Inc" + varName + "=" + counter);
				break;
			}

			log.info("Variables" + variables.toString());
		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}
		return testEnsureRequest;
	}

	public static TestEnsureRequest switchToNewTab(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		String url = getJsonFieldValue(navActionParams, "url");
		log.info("URL---->" + url);
		String parent = driver.getWindowHandle();
		try {
			if (!Boolean.parseBoolean((String) prop.getProperty("enableIncognitoNewTabJS"))) {
				driver.switchTo().newWindow(WindowType.TAB);
				driver.navigate().to(url);
				ArrayList<String> w = new ArrayList<String>(driver.getWindowHandles());
				driver.switchTo().window(w.get(1));
				log.info("Incognito Tab");
			} else {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("window.open('" + url + "','_blank');");
				ArrayList<String> w = new ArrayList<String>(driver.getWindowHandles());
				driver.switchTo().window(w.get(1));
				log.info("No Incognito Tab");
			}
			if (TestBase.EXECUTION_ENV.equals(TestBase.LINUX_ENV)) {
				driver.manage().window().setSize(new Dimension(1920, 1080));
			}
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		} catch (Exception e) {
			e.printStackTrace();
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, null, "fileName", 1);
		}
		return testEnsureRequest;
	}

	public static TestEnsureRequest closeTab(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		ArrayList<String> w = new ArrayList<String>(driver.getWindowHandles());
		// log.info("URL---->" +url);
		String parent = driver.getWindowHandle();
		try {
			if (!Boolean.parseBoolean((String) prop.getProperty("enableIncognitoNewTabJS"))) {
				driver.switchTo().window(w.get(1));
				driver.close();
				log.info("Incognito Tab");
			} else {
				driver.switchTo().window(w.get(1));
				driver.close();
				log.info("No Incognito Tab");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		driver.switchTo().window(parent);
		return testAssureRequest;
	}

	public static TestEnsureRequest refreshTab(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		String url = getJsonFieldValue(navActionParams, "url");
		log.info("URL---->" + url);
		try {
			if (!Boolean.parseBoolean((String) prop.getProperty("enableIncognitoNewTabJS"))) {
				driver.get(url);
				driver.navigate().refresh();
				log.info("Incognito Tab");
			} else {
				driver.get(url);
				driver.navigate().refresh();
				log.info("No Incognito Tab");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return testAssureRequest;
	}

	public static TestEnsureRequest frameSwitch(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		Thread.sleep(2000);
		try {
			log.info("Frame SWITCH\n" + navActionParams);
			String by = getJsonFieldValue(navActionParams, "by");
			String frame = getJsonFieldValue(navActionParams, "frame");
			String index = getJsonFieldValue(navActionParams, "index");
			// Thread.sleep(5000);
			switch (by) {
			case "id":
				wait.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
						.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(frame))));
				driver.switchTo().frame(driver.findElement(By.id(frame)));
				break;
			case "class":
				wait.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
						.until(ExpectedConditions.visibilityOf(driver.findElement(By.className(frame))));
				driver.switchTo().frame(driver.findElement(By.className(frame)));
				break;
			case "xpath":
				log.info("xpath ^^^^^^^^^^^ " + frame);
				wait.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
						.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(frame))));
				driver.switchTo().frame(driver.findElement(By.xpath(frame)));
				/*
				 * if (!frame.equals("xpath"))
				 * driver.switchTo().frame(driver.findElement(By.xpath(frame))); else { String
				 * frameXpath = getJsonFinalXpath(navActionParams, false);
				 * log.info("frame switch xpath->" + frameXpath);
				 * driver.switchTo().frame(driver.findElement(By.xpath(frameXpath)));
				 * driver.findElement(By.xpath(frameXpath)).click(); String text = (String)
				 * Toolkit.getDefaultToolkit().getSystemClipboard()
				 * .getData(DataFlavor.stringFlavor); log.info("frame text -> " +
				 * text); }
				 */
				break;
			case "index":
				driver.switchTo().frame(index);
				break;
			case "parent":
				driver.switchTo().parentFrame();
				break;
			case "default":
				driver.switchTo().defaultContent();
				break;
			}
			// Thread.sleep(2000);
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, null, "fileName", 1);
		}

		return testEnsureRequest;

	}

	public static TestEnsureRequest frameSwitchIndex(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {
			Thread.sleep(5000);
			log.info("Frame SWITCH\n" + navActionParams);
			int index = Integer.parseInt(getJsonFieldValue(navActionParams, "index"));

			String cssSelector = "(//iframe[@class=\"gwt-Frame\"])[last()]";

			waitUntilVisible("", By.xpath(cssSelector), get_timout, 5);
			// driver.switchTo().frame(index);

			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement webElement = (WebElement) js
					.executeScript("return document.querySelector(\"#session-history > div > iframe\")");
			log.info("webelement" + webElement);

			WebElement iframe = driver.findElement(By.xpath(cssSelector));

			// Switch to the frame
			driver.switchTo().frame(iframe);

			// driver.findElement(By.xpath("//*[@id=\"session-history\"]/div/iframe")).click();

			WebElement gmailLink = driver.findElement(By.xpath(cssSelector));
			int xOffset = gmailLink.getRect().getX();
			int yOffset = gmailLink.getRect().getY();
			Actions actionProvider = new Actions(driver);
			log.info("xOffset" + xOffset);
			log.info("yOffset" + yOffset);
			// Performs mouse move action onto the offset position
			actionProvider.moveByOffset(xOffset, yOffset).build().perform();
		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}
		return testEnsureRequest;
	}

	public static TestEnsureRequest windowSwitch(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		try {
			log.info("Executing Navaction typeTextWithEnter for JsonNode\n" + navActionParams);

			String xpathKey = getXPathKeyFromJSON(navActionParams);
			int value = Integer.parseInt(getXPathKeyValueFromJSON(navActionParams, xpathKey));
			log.info("Window Count :::" + navActionParams.get(xpathKey).asText());
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			// Thread.sleep(2000);
			driver.switchTo().window(tabs.get(value));
			// Thread.sleep(2000);
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, null, "fileName", 1);
		}
		return testEnsureRequest;
	}

	public static TestEnsureRequest readFileNames(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		String folderpath = prop.getProperty("folderpath");
		log.info("FolderPath " + folderpath);
//		String folderpath = getJsonFieldValue(navActionParams, "folderpath");
		String varFile = getJsonFieldValue(navActionParams, "varFile");
		// String object = getJsonFieldValue(navActionParams, "object");
		List<String> results = new ArrayList<String>();
		File[] filename = null;
		try {
			File[] files = new File(folderpath).listFiles();
			log.info("Length Of Files " + files.length);
			// If this pathname does not denote a directory, then listFiles() returns null.
			for (File file1 : files) {
				if (file1.isFile()) {
					results.add(file1.getName());
				}
			}
			fileVar.put(varFile, results);
		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}
		variables.put(folderpath, folderpath);
		log.info("results" + results);
		return testEnsureRequest;
	}

	public static TestEnsureRequest readFileNameInFolder(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		File f = null;
		String v = null;
		String varFile = getJsonFieldValue(navActionParams, "varFile");
		String varName = getJsonFieldValue(navActionParams, "varName");
		String varPO = getJsonFieldValue(navActionParams, "varPO");
		String attachFile = getJsonFieldValue(navActionParams, "attachFile");
		String varFilePath = getJsonFieldValue(navActionParams, "varFilePath");
//		String delimeter = getJsonFieldValue(navActionParams,"delimeter");
		String excelfilename = getJsonFieldValue(navActionParams, "excelfilename");
		String excelObj1 = getJsonFieldValue(navActionParams, "excelObj1");
		String excelObj2 = getJsonFieldValue(navActionParams, "excelObj2");
		String folderpath = prop.getProperty("folderpath");
		List<String> files = fileVar.get(varFile);
		List<String> filevalues = new ArrayList<String>();
		String varValue = variables.get(varName);
		try {
			int count = Integer.parseInt(varValue);
			String filenames = files.get(count).toString();
			String path = folderpath + "\\" + filenames;
//		    log.info("path--> "+path);
			variables.put(attachFile, filenames);
			variables.put(varFilePath, path);
//		    log.info("filenames2-->" + variables.get(attachFile));
			String[] filename = filenames.split(" PO ");
			// log.info("PO: "+filename[1]);
			String[] splitString = filename[1].split("[,_ ]");
			variables.put(varPO, splitString[0]);
			variables.put(varValue, String.valueOf(count));
			String counter = Integer.toString(count);
			filevalues.add(filenames);
			filevalues.add(splitString[0]);
			log.info("FileValues : " + filevalues);
//			log.info("Numbers-->" + splitString[0]);
			log.info("AttachFile-->" + variables.get(attachFile));
			log.info("Numbers-->" + variables.get(varPO));
			log.info("File Path-->" + variables.get(varFilePath));
			excelVar.put(varValue, filevalues);
		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}
		return testEnsureRequest;
	}

	public static TestEnsureRequest readSessionInFileName(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		String varFile = getJsonFieldValue(navActionParams, "varFile");
		String varName = getJsonFieldValue(navActionParams, "varName");
		String varModule = getJsonFieldValue(navActionParams, "varModule");
		String attachFile = getJsonFieldValue(navActionParams, "attachFile");
		String varFilePath = getJsonFieldValue(navActionParams, "varFilePath");
		String folderpath = prop.getProperty("folderpath");
		List<String> files = fileVar.get(varFile);
		List<String> filevalues = new ArrayList<String>();
		String varValue = variables.get(varName);
		try {
			int count = Integer.parseInt(varValue);
			String filenames = files.get(count).toString();
			String path = folderpath + "\\" + filenames;
//			    log.info("path--> "+path);
			variables.put(attachFile, filenames);
			variables.put(varFilePath, path);
//			    log.info("filenames2-->" + variables.get(attachFile));
			String[] filename = filenames.split("[,-]");
			log.info("FileNameLength: " + filename.length);
			if (filename.length <= 3) {
				String[] splitString = filename[2].split("[,-.]");
				variables.put(varModule, splitString[0].trim());
				variables.put(varValue, String.valueOf(count));
				String counter = Integer.toString(count);
				filevalues.add(filenames);
				filevalues.add(splitString[0]);
				log.info("FileValues : " + filevalues);
//					log.info("Numbers-->" + splitString[0]);
				log.info("AttachFile-->" + variables.get(attachFile));
				log.info("Module-->" + variables.get(varModule));
				log.info("File Path-->" + variables.get(varFilePath));
				excelVar.put(varValue, filevalues);

			} else {
				String[] splitString = filename[3].split("[,-.]");
				variables.put(varModule, splitString[0].trim());
				variables.put(varValue, String.valueOf(count));
				String counter = Integer.toString(count);
				filevalues.add(filenames);
				filevalues.add(splitString[0]);
				log.info("FileValues : " + filevalues);
//				log.info("Numbers-->" + splitString[0]);
				log.info("AttachFile-->" + variables.get(attachFile));
				log.info("Module-->" + variables.get(varModule));
				log.info("File Path-->" + variables.get(varFilePath));
				excelVar.put(varValue, filevalues);
			}
		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}

		return testEnsureRequest;
	}

	public static TestEnsureRequest mapModuleToInforModule(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		String varModule = getJsonFieldValue(navActionParams, "varModule");
		String inforModule = getJsonFieldValue(navActionParams, "inforModule");
		String moduleName = variables.get(varModule);
		log.info("ModuleName :" + moduleName);
		try {
			switch (moduleName) {
			case "Purchase":
				variables.put(inforModule, String.valueOf("Procurement"));
				log.info("Purchase " + variables.get(inforModule));
				break;
			case "Sales":
				variables.put(inforModule, String.valueOf("Sales"));
				log.info("Sales " + variables.get(inforModule));
				break;
			case "Warehousing":
				variables.put(inforModule, String.valueOf("Warehousing"));
				log.info("Warehousing " + variables.get(inforModule));
				break;
			case "Items":
				variables.put(inforModule, String.valueOf("Master Data"));
				log.info("Items " + variables.get(inforModule));
				break;
			case "Engineering Item":
				variables.put(inforModule, String.valueOf("Manufacturing"));
				log.info("Engineering Item " + variables.get(inforModule));
				break;
			case "Engineering Item Revision":
				variables.put(inforModule, String.valueOf("Manufacturing"));
				log.info("Engineering Item Revision " + variables.get(inforModule));
				break;
			case "Service":
				variables.put(inforModule, String.valueOf("Service"));
				log.info("Service " + variables.get(inforModule));
				break;

			}

		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}

		return testEnsureRequest;
	}

	public static TestEnsureRequest readDataFromExcel(JsonNode navActionParams, String file,String testCaseStepId)
			throws InvalidFormatException {
		TestEnsureRequest testEnsureRequest = null;
		Workbook workbook = null;
		String attachFile = variables.get(getJsonFieldValue(navActionParams, "attachFile"));
		String keys = getJsonFieldValue(navActionParams, "keys");
		String cellValues = getJsonFieldValue(navActionParams, "cellValues");
//		int cell = Integer.parseInt(cellValue);
		String folderpath = prop.getProperty("folderpath");
		log.info("FolderPath " + folderpath);
		log.info("AttachFile " + attachFile);
		// String filename = variables.get(attachFile);
		String excelPath = folderpath + "\\" + attachFile;
		log.info("ExcelPath " + excelPath);

		String[] keysArr = keys.split(",");
		String[] cellArr = cellValues.split(",");
		int[] cellValueArr = Arrays.stream(cellArr).mapToInt(Integer::parseInt).toArray();

		try {
			Map<String, String> companyData = ReadExcel.readMDMData(excelPath, keysArr, cellValueArr);
			variables.putAll(companyData);
			log.info("variables " + variables.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testEnsureRequest;
	}

	public static TestEnsureRequest roboFileUpload(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		String varFilePath = getJsonFieldValue(navActionParams, "varFilePath");
		String filepath = variables.get(varFilePath);
		log.info("FilePath :" + filepath);
		try {
			StringSelection s = new StringSelection(filepath);
			// Clipboard copy
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
			Robot r = new Robot();
			// pressing enter
			r.keyPress(KeyEvent.VK_ENTER);
			// releasing enter
			r.keyRelease(KeyEvent.VK_ENTER);
			// pressing ctrl+v
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_V);
			// releasing ctrl+v
			r.keyRelease(KeyEvent.VK_CONTROL);
			r.keyRelease(KeyEvent.VK_V);
			// pressing enter
			r.keyPress(KeyEvent.VK_ENTER);
			// releasing enter
			r.keyRelease(KeyEvent.VK_ENTER);

		} catch (Exception e) {
			String methodName = new Object() {
			}.getClass().getEnclosingMethod().getName();
			log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
					+ ":: testcasefailed::" + testCaseFailed);
			e.printStackTrace();
		}

		return testEnsureRequest;
	}

	public static TestEnsureRequest getText(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		testEnsureRequest = processNavActionNode(navActionParams, ProcessConstants.GETTEXT, file, testCaseStepId);
		return testEnsureRequest;
	}

	public static TestEnsureRequest clickPurchaseOrderAdvice(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		testAssureRequest = processNavActionNode(navActionParams, ProcessConstants.CLICKPURCHASEORDERADVICE, file, testCaseStepId);
		return testAssureRequest;
	}

	public static TestEnsureRequest getPeggingOrder(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		testAssureRequest = processNavActionNode(navActionParams, ProcessConstants.GETPEGGINGORDER, file, testCaseStepId);
		return testAssureRequest;
	}

	public static TestEnsureRequest clickPurchaseOrder(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		testAssureRequest = processNavActionNode(navActionParams, ProcessConstants.CLICKPURCHASEORDER, file, testCaseStepId);
		return testAssureRequest;
	}

	public static TestEnsureRequest clickShadowElement(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testAssureRequest = null;
		// testAssureRequest = processNavActionNode(navActionParams,
		// ProcessConstants.CLICKSHADOWELEMENT, file);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement webElement = (WebElement) js.executeScript(
				"return document.querySelector(\"#viewer\").shadowRoot.querySelector(\"#toolbar\").shadowRoot.querySelector(\"#downloads\").shadowRoot.querySelector(\"#download\").shadowRoot.querySelector(\"#icon\")");
		webElement.click();
		return testAssureRequest;
	}

	public static TestEnsureRequest splitText(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {
			log.info("Variables-->" + variables.get(getJsonFieldValue(navActionParams, "varName")));
			String splitString = variables.get(getJsonFieldValue(navActionParams, "varName"));
			log.info("Split String-->" + splitString);
			String delimeter = getJsonFieldValue(navActionParams, "delimeter");
			String[] pos = null;
			if (getJsonFieldValue(navActionParams, "pos") != null) {
				pos = getJsonFieldValue(navActionParams, "pos").split(",");
			}
			log.info("Delimeter-->" + delimeter);
			String[] splitValue = splitString.split(delimeter);
//			for(int i=0 ; i<= splitValue.length; i++) {
//				System.out.print("Split Values----->"+splitValue[i]);
//			}.
			String[] array = getJsonFieldValue(navActionParams, "variables").trim().split(",");
			if (pos != null && pos.length != 0) {
				for (int i = 0; i < pos.length; i++) {
					variables.put(array[i], splitValue[Integer.parseInt(pos[i])]);
				}
			} else if (splitValue.length >= 12) {
				log.info(",Split 2 Values----->" + splitValue[5] + "," + splitValue[12]);
				variables.put(array[0], splitValue[5]);
				variables.put(array[1], splitValue[12].replace(".", ""));
			} else {
				variables.put(array[0], splitValue[0].replace(",", ""));
				variables.put(array[1], splitValue[1].replace(",", ""));
			}
			log.info(",Split variable Values----->" + variables);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testEnsureRequest;

	}

//	public static TestEnsureRequest compareString(JsonNode navActionParams, String file) throws Throwable {
//		TestEnsureRequest testEnsureRequest = null;
//		try {
//			String actualString = variables.get(getJsonFieldValue(navActionParams, "actualString"));
//			String changedString = variables.get(getJsonFieldValue(navActionParams, "changedString"));
//			log.info("Actual Date: " +actualString + "Changed Date: " +changedString);
//			if(actualString.equals(changedString)) {
//				log.info("Strings are same");
//			}else {
//				log.info("Strings are not same");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return testEnsureRequest;
//
//
//
//	}
	public static TestEnsureRequest toArray(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {
			String varName = getJsonFieldValue(navActionParams, "varName");
			String object = getJsonFieldValue(navActionParams, "object");
			String[] values = jsonsInputMap.get(varName).split(",");
			objects.put(object, values);
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
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

	public static TestEnsureRequest addElementsToArray(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		log.info("Navactionparams is Array-->" + navActionParams.isArray());
		try {
			if (navActionParams.isArray()) {
				String varName = getJsonFieldValue(navActionParams, "varName");
				String object = getJsonFieldValue(navActionParams, "object");
				String finalXpath = getJsonFinalXpath(navActionParams, false);
				String alternate = getJsonFieldValue(navActionParams, "alternate");
				log.info("finalxapth-->" + finalXpath);
				log.info("object-->" + object);
				log.info("alternate-->" + alternate);
				String xpath = "(" + finalXpath + ")";
				ArrayList<String> arrayList = new ArrayList<String>();
				List<WebElement> amounts = driver.findElements(By.xpath(finalXpath));
				log.info("Amounts-->" + amounts.size());
				for (int i = 1; i <= amounts.size(); i++) {
					if (alternate.equals("yes")) {
						if (i % 2 != 0) {
							String amount = driver.findElement(By.xpath(xpath + "[" + i + "]")).getText();
							log.info("Amount-->" + amount);
							arrayList.add(amount);

						}
					} else {
						String amount = driver.findElement(By.xpath(xpath + "[" + i + "]")).getText();
						log.info("Get Text->" + amount);
						if (amount.isEmpty()) {
							driver.findElement(By.xpath(xpath + "[" + i + "]")).click(); // text copied to clipboard
							Thread.sleep(1000);
							Actions a = new Actions(driver);
							a.keyDown(Keys.CONTROL).sendKeys("C").keyUp(Keys.CONTROL).build().perform();
							amount = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
									.getData(DataFlavor.stringFlavor);
							log.info("clipboard action variableValue => " + amount);
						}

						log.info("Amount-->" + amount);
						arrayList.add(amount);
					}
				}
				log.info("ArrayList-->" + arrayList);
				objArr.put(object, arrayList);
				log.info("Objects-->" + objArr.get(object));
				testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
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

	public static TestEnsureRequest addElementsToList(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		log.info("Navactionparams is Array-->" + navActionParams.isArray());
		try {
			if (navActionParams.isArray()) {
				String varName = variables.get(getJsonFieldValue(navActionParams, "varName"));
				String lineItems = getJsonFieldValue(navActionParams, "lineItems");
				String object = getJsonFieldValue(navActionParams, "object");
				String finalXpath = getJsonFinalXpath(navActionParams, false);
				String[] lineItem = jsonsInputMap.get(lineItems).split(",");
				List<String> strList = new ArrayList<String>();
				log.info("finalxapth-->" + finalXpath);
				String value = driver.findElement(By.xpath(finalXpath)).getText();
				objList.put(lineItem[Integer.parseInt(varName)], value);
				obj.put(object, objList);
				log.info("VarName>" + varName + " Value>" + value + " Object>" + object + "lineItems->"
						+ lineItem[Integer.parseInt(varName)]);
				testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
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

	public static TestEnsureRequest calculateCostAmount(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {

			String varName = getJsonFieldValue(navActionParams, "varName");
			String object = getJsonFieldValue(navActionParams, "object");
			String objects = getJsonFieldValue(navActionParams, "objects");
			String total = getJsonFieldValue(navActionParams, "total");
			String[] strArr = jsonsInputMap.get(varName).split(",");
			if (object != null) {
				Map<String, String> map = obj.get(object);
				iterateUsingForEach(map, strArr, total);
				log.info("varName->" + varName + " object->" + object + " total->" + total + " strArr->"
						+ Arrays.toString(strArr) + " objectList->" + obj.get(object));

			} else if (objects != null) {
				String[] objArr1 = objects.split(",");
				List list1 = objArr.get(objArr1[0]);
				List list2 = objArr.get(objArr1[1]);
				log.info("list1->" + list1.toString() + "list2->" + list2.toString());
				iterateUsingList(list1, list2, total);
				log.info("varName->" + varName + " object->" + object + " total->" + total + " strArr->"
						+ Arrays.toString(strArr) + " objectList->" + obj.get(object));

			}

			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
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

	public static void iterateUsingForEach(Map<String, String> map, String[] strArr, String total) {

		int index = 0;
		Map<String, String> objList = new HashMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {

			String key = entry.getKey();
			String value = entry.getValue();
			float cost = Float.parseFloat(value);
			int qunatity = Integer.parseInt(strArr[index]);
			float totalCost = cost * qunatity;
			double round = Math.round(totalCost * 100.0) / 100.0;
			log.info("Key=" + key + ", Value=" + cost);
			log.info("Str[index]->" + qunatity);
			log.info("total->" + totalCost + " roundOff->" + round);
			objList.put(key, Double.toString(round));
			index++;

		}
		obj.put(total, objList);
		log.info("New Object List->" + objList.toString() + " new object->" + obj.toString());

	}

	public static void iterateUsingList(List list1, List list2, String total) {

		List<String> objList = new ArrayList<String>();
		for (int i = 0; i < list1.size(); i++) {

			float cost = Float.parseFloat(list1.get(i).toString());
			float qunatity = Float.parseFloat(list2.get(i).toString());
			float totalCost = cost * qunatity;
			double round = Math.round(totalCost * 100.0) / 100.0;
			DecimalFormat f = new DecimalFormat("##.00");
			// log.info("Decimal Format"+f.format(round));
			log.info("qunatity" + qunatity);
			log.info("total->" + totalCost + " Decimal Format->" + f.format(round));
			objList.add(f.format(round));

		}
		objArr.put(total, objList);
		// obj.put(total, objList);
		log.info("New Object List->" + objList.toString() + " new object->" + objArr.toString());

	}

	public static TestEnsureRequest readTaxData(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {

			String objects = getJsonFieldValue(navActionParams, "objects");

			if (objects != null) {
				String[] strList = objects.split(",");
				String amount = driver.findElement(By.xpath("//*[@id='cisli2145s000-cisli245.amti-n27']")).getText();
				String tax = driver.findElement(By.xpath("//*[@id='cisli2145s000-cisli245.txai-n30']")).getText();
				List<String> amountList = null;
				List<String> taxList = null;
				log.info("objArr->" + objArr + "strList[0]->" + strList[0] + "strList[1]->" + strList[1]);
				if (objArr.get(strList[0]) == null) {
					amountList = new ArrayList<String>();
					taxList = new ArrayList<String>();
				} else {
					amountList = objArr.get(strList[0]);
					taxList = objArr.get(strList[1]);
				}
				amountList.add(amount);
				taxList.add(tax);
				objArr.put(strList[0], amountList);
				objArr.put(strList[1], taxList);

				log.info("objArr->" + objArr);

			}

			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
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

	public static TestEnsureRequest compareTaxData(JsonNode navActionParams, String file,String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {

			String object = getJsonFieldValue(navActionParams, "object");
			String objects = getJsonFieldValue(navActionParams, "objects");
			String taxamount = getJsonFieldValue(navActionParams, "taxamount");
			DecimalFormat f = new DecimalFormat("##.00");
			boolean equal = true;
			if (object != null && objects != null) {
				String[] strList = objects.split(",");
				List<String> amountList = objArr.get(strList[0]);
				List<String> taxList = objArr.get(strList[1]);

				List<String> costList = objArr.get(object);
				float taxTotal = 0;
				for (int index = 0; index < costList.size(); index++) {
					float actualCost = Float.parseFloat(costList.get(index));
					double acualAmount = Float.parseFloat(amountList.get(index));
					double actualTax = Float.parseFloat(taxList.get(index));
					double expectedTax = actualCost * 0.05;
					taxTotal += Float.parseFloat(f.format(expectedTax));
					double expectedAmount = expectedTax + actualCost;
					log.info("acualAmount->" + f.format(acualAmount) + "actualCost->" + f.format(actualCost)
							+ "actualTax->" + f.format(actualTax) + "expectedTax->" + f.format(expectedTax)
							+ "expectedAmount" + f.format(expectedAmount));
					if (!f.format(acualAmount).equals(f.format(actualCost))
							|| !f.format(actualTax).equals(f.format(expectedTax)))
						equal = false;
				}
				log.info("taxTotal + " + taxTotal);
				variables.put(taxamount, String.valueOf(taxTotal));

			}
			if (equal)
				testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
			else
				testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, IssueEnum.PRODUCTION_BUG,
						"fileName", 1);
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

	public static TestEnsureRequest keyBoardEvent(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		try {
			log.info("Executing Navaction typeTextWithEnter for JsonNode\n" + navActionParams);

			String event = getXPathKeyFromJSON(navActionParams);
			int count = 0;
			try {
				count = Integer.parseInt(getXPathKeyValueFromJSON(navActionParams, event));
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("Key Event Count :::" + navActionParams.get(event).asText());
			switch (event) {
			case "tab":
				keyTab(count);
				break;
			case "arrowDown":
				keyArrowDown(count);
				break;
			case "enter":
				keyEnter(count);
				break;
			case "backSpace":
				keyBackSpace(count);
				break;
			case "rightClick":
				rightClick();
				break;
			case "type":
				String value = getXPathKeyValueFromJSON(navActionParams, event);
				keyEnter(value);
				break;
			default:
				log.info("No event specified");
				break;
			}
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
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

	public static TestEnsureRequest sleep(JsonNode navActionParams, String file,String testCaseStepId) {
		TestEnsureRequest testEnsureRequest = null;
		try {
			log.info("Executing Navaction sleep for JsonNode\n" + navActionParams);

			String sec = getXPathKeyFromJSON(navActionParams);
			int sleepTime = Integer.parseInt(getXPathKeyValueFromJSON(navActionParams, sec));
			log.info("Sleep Time:::" + navActionParams.get(sec).asText() + "=" + sleepTime);
			Thread.sleep(sleepTime);
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
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

	public static String randomString(int length) {
		int leftLimit = 65; // letter 'A'
		int rightLimit = 90; // letter 'Z'
		int targetStringLength = length;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		log.info("generatedString" + generatedString);
		return generatedString;
	}

	public static String randomNumber(int length) {

		String generatedNumber = String.valueOf(length < 1 ? 0
				: new Random().nextInt((9 * (int) Math.pow(10, length - 1)) - 1) + (int) Math.pow(10, length - 1));

		log.info("generatedNumber" + generatedNumber);
		return generatedNumber;
	}

	public static TestEnsureRequest processNavActionNode(JsonNode navActionParams, String processType, String file, String testCaseStepId)
			throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		String xpathKey = null, finalXpath = null, keyValue = null;
		String ignore = getJsonFieldValue(navActionParams, "ignore");

		try {
			log.info("Executing Navaction " + processType + " for JsonNode\n" + navActionParams);

			if (ignore != null && ignore.equals("yes")) {
				String waitTime = getJsonFieldValue(navActionParams, "waitTime");
				driver.manage().timeouts().implicitlyWait(Long.parseLong(waitTime), TimeUnit.SECONDS);
			}

			if (!navActionParams.isArray()) {
				// TODO: Build generic methods to parse the various types of generic NavAction
				// JsonNodes.
				xpathKey = getXPathKeyFromJSON(navActionParams);
				finalXpath = getXPathValueFromProperty(xpathKey);
				keyValue = getXPathKeyValueFromJSON(navActionParams, xpathKey);
				if (!keyValue.isEmpty()) {
					finalXpath = MessageFormat.format(finalXpath, keyValue);
				} else if (navActionParams.fields().next().getKey().contains("getXpath")) {
					JsonNode validationActionXpathValue = navActionParams.fields().next().getValue();
					finalXpath = getJsonFinalXpath(validationActionXpathValue, true);
				}
			} else if (navActionParams.isArray()) {
				finalXpath = getJsonFinalXpath(navActionParams, false);
				keyValue = getJsonFieldValue(navActionParams, "value");
				int length = 3;
				try {
					length = Integer.parseInt(getJsonFieldValue(navActionParams, "randomLength"));
				} catch (Exception e) {
					String methodName = new Object() {
					}.getClass().getEnclosingMethod().getName();
					log.info("length::" + methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
							+ ":: testcasefailed::" + testCaseFailed);
					// e.printStackTrace();
				}
				if (keyValue != null && keyValue.equals("randomString")) {
					keyValue = randomString(length);
				}
				if (keyValue != null && keyValue.equals("randomNumber")) {
					keyValue = randomNumber(length);
				}
				if (keyValue != null && keyValue.contains("$")) {
					String key = null;
					if (release != null) {
						log.info("current project" + currentProject);
						log.info("current module" + currentModule);
						log.info("testdata" + testData);
						key = String.valueOf(testData.get(keyValue.replace("$", "")));

						if (keyValue.contains(".")) {
							String[] tempValues = keyValue.split("\\.");
							tempValues[0] = tempValues[0].replace("$", "");
							int index = Integer.parseInt(tempValues[1]);
							key = testData.get(tempValues[0]).split(",")[index - 1];
						}

						if (key == null)
							keyValue = String.valueOf(testData.get(keyValue.replace("$", "")));
						else
							keyValue = key;
					} else {
						try {
							if (file.contains("/") && inputMap.size() != 0) {
								if (!keyValue.contains(".")) {
									key = inputMap.get(file.split("/")[1]).get(keyValue.replace("$", ""));
								} else {
									String[] tempValues = keyValue.split("\\.");
									tempValues[0] = tempValues[0].replace("$", "");
									int index = Integer.parseInt(tempValues[1]);
									key = inputMap.get(file.split("/")[1]).get(tempValues[0]).split(",")[index - 1];
								}
							} else if (jsonsInputMap.size() != 0) {
								if (!keyValue.contains(".")) {
									key = jsonsInputMap.get(keyValue.replace("$", ""));
								} else {
									String[] tempValues = keyValue.split("\\.");
									tempValues[0] = tempValues[0].replace("$", "");
									int index = Integer.parseInt(tempValues[1]);
									key = jsonsInputMap.get(tempValues[0]).split(",")[index - 1];
								}
							}
							log.info("key &&& " + key);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// key = inputMap.get(file.split("/")[1]).get(keyValue.replace("$", ""));
						if (key == null)
							keyValue = jsonsInputMap.get(keyValue.replace("$", ""));
						else
							keyValue = key;
					}
				}
				if (keyValue == null) {
					keyValue = variables.get(getJsonFieldValue(navActionParams, "varName"));
				}

				try {
					if (keyValue != null && keyValue.contains("#") && !keyValue.equals("#date")) {
						log.info("Variable value" + variables);
						log.info("Bfore Hash KeyValue->" + keyValue);
						String hashKey = keyValue.replace("#", "");
						log.info("hashkey->" + hashKey);
						keyValue = variables.get(keyValue.replace("#", ""));
						log.info("Hash KeyValue->" + keyValue);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (keyValue == null && getJsonFieldValue(navActionParams, "fileName") != null) {
					keyValue = System.getProperty("user.dir")
							+ envRelativePath("\\resources\\inputs\\" + getJsonFieldValue(navActionParams, "fileName"));

				}

				if (keyValue != null && keyValue.equals("#date")) {
					String days = getJsonFieldValue(navActionParams, "days");
					String dateFormat = getJsonFieldValue(navActionParams, "dateFormat");
					String varName = getJsonFieldValue(navActionParams, "varName");
					log.info("Release data  " + release != null);
					if (release != null) {
						log.info("testData " + testData);
						log.info("days " + days);
						log.info("dateFormat " + dateFormat);
						try {
							if (days.contains("$"))
								days = String.valueOf(testData.get(days.replace("$", "")));
							if (dateFormat.contains("$"))
								dateFormat = String.valueOf(testData.get(dateFormat.replace("$", "")));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					else {
						try {
							log.info("days" + days);
							if (days.equals("random") && TestBase.randomDate != null) {
								days = TestBase.randomDate;
							}
							if (days.equals("random")) {
								int min = 0;
								int max = 10;
								Random rand = new Random();
								int randomNum = rand.nextInt((max - min) + 1) + min;
								log.info("RandomNumber" + randomNum);
								days = String.valueOf(randomNum);
								log.info("days" + days);
								TestBase.randomDate = days;

							}
							log.info("inputMap" + inputMap);
							log.info("file" + file);
							if (days.contains("$") && file.contains("/") && inputMap.size() != 0) {
								days = inputMap.get(file.split("/")[1]).get(days.replace("$", ""));
							} else if (days.contains("$") && inputMap.size() != 0) {
								days = inputMap.get(file).get(days.replace("$", ""));
							}
							if (days.contains("$") && jsonsInputMap.size() != 0) {
								days = jsonsInputMap.get(getJsonFieldValue(navActionParams, "days").replace("$", ""));
							}

							if (dateFormat.contains("$") && file.contains("/") && inputMap.size() != 0) {
								dateFormat = inputMap.get(file.split("/")[1]).get(dateFormat.replace("$", ""));
							} else if (dateFormat.contains("$") && inputMap.size() != 0) {
								dateFormat = inputMap.get(file).get(dateFormat.replace("$", ""));
							}
							if (dateFormat.contains("$") && jsonsInputMap.size() != 0) {
								dateFormat = jsonsInputMap
										.get(getJsonFieldValue(navActionParams, "dateFormat").replace("$", ""));
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (varName != null) {
						keyValue = addDaysToDate(dateFormat, days);
						// varName = keyValue;
						log.info("varName-------->" + varName);
						variables.put(varName, keyValue);
					} else {
						keyValue = addDaysToDate(dateFormat, days);
					}
				}

				log.info("keyValue-------->" + keyValue);
			}
			try {
				// waitUntilVisible(finalXpath, By.xpath(finalXpath), get_timout, 5);
				// wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(finalXpath)));

				if (finalXpath != null) {
					waitUntilVisible(finalXpath, By.xpath(finalXpath), get_timout, 5);
				}

				// wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(finalXpath)));
			} catch (Exception e) {
				String methodName = new Object() {
				}.getClass().getEnclosingMethod().getName();
				log.info("waituntilvisible::" + methodName + "::" + testEnsureRequest + ":: teststepstatus::"
						+ testStepStatus + ":: testcasefailed::" + testCaseFailed);
				e.printStackTrace();
			}

			if (finalXpath != null && ignore != null && !ignore.equals("yes")
					&& !isElementExist(driver, By.xpath(finalXpath))) {
				log.info("Element not found " + By.xpath(finalXpath));
				String javascript = "return document.evaluate(\"" + finalXpath.replace("\"", "'")
						+ "\",document,null,XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue;";
				log.info("javascript :: " + javascript);
				log.info("******************** Trying to find element using JavaScriptExecutor ***********");
				JavascriptExecutor js = (JavascriptExecutor) driver;
				WebElement webElement = (WebElement) js.executeScript(javascript);
				log.info("WebElement status :: " + webElement);
				try {
					log.info("WebElement status :: " + webElement.toString());
				} catch (Exception e) {
				}
				log.info("******************** Find element using JavaScriptExecutor ends ***********");

				if (webElement == null) {
					if (noOfRetries < retryCount) {
						noOfRetries++;
						log.info("********** Failed to process NavAction Node *** Retrying " + noOfRetries
								+ " time ***********");
						processNavActionNode(navActionParams, processType, file, testCaseStepId);
					}
					testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, IssueEnum.TO_INVESTIAGE,
							"fileName", 2);
					TestBase.testStepStatus = false;
					TestBase.testCaseFailed = true;
					return testEnsureRequest;
				}

			}
			String ptr = getJsonFieldValue(navActionParams, "ptr");
			try {
				if (ptr != null) {
					if (ptr.contains("#"))
						ptr = variables.get(ptr.replace("#", ""));
					String value = getJsonFieldValue(navActionParams, "value");
					log.info("ObjectValue->" + value);
					log.info("ObjectkeyValue->" + objects.get(value));
					log.info("ObjectPtr->" + ptr);
					if (objects.get(value) != null) {
						keyValue = objects.get(value)[Integer.parseInt(ptr)];
					}

				}
			} catch (Exception e) {

			}
			log.info("^^^^^^^^^^^^^^^^^ keyvalue :: " + keyValue + "^^^^^^^^^^^^^^^^^");

			// if(finalXpath!=null)
			switch (processType) {

			case ProcessConstants.TYPE:
				clearInputs(By.xpath(finalXpath), driver);
				type(By.xpath(finalXpath), keyValue, "Type in a text box", driver);
				break;
			case ProcessConstants.CLICKELEMENT:
				click(By.xpath(finalXpath), finalXpath, driver);
				break;
			case ProcessConstants.CLICKLASTELEMENT:
				clickLastElement(By.xpath(finalXpath), finalXpath, driver);
				break;
			case ProcessConstants.TYPEMEASUREMENTVALUES:
				typeMeasurementValues(By.xpath(finalXpath), keyValue, driver);
				break;
			case ProcessConstants.CLICKVISIBLEELEMENT:
				clickVisibleElement(By.xpath(finalXpath), finalXpath, driver);
				break;
			case ProcessConstants.DOUBLECLICKELEMENT:
				doubleClick(By.xpath(finalXpath), xpathKey, driver);
				break;
			case ProcessConstants.TYPETEXTWITHENTER:
				Thread.sleep(500);
				click(By.xpath(finalXpath), finalXpath, driver);
				Thread.sleep(500);
				clearInputs(By.xpath(finalXpath), driver);
				Thread.sleep(500);
				enterinputValuesWithEnter(By.xpath(finalXpath), driver, keyValue);
				break;
			case ProcessConstants.TYPETEXTWITHTABKEY:
				Thread.sleep(500);
				click(By.xpath(finalXpath), finalXpath, driver);
				Thread.sleep(500);
				clearInputs(By.xpath(finalXpath), driver);
				Thread.sleep(500);
				enterinputValuesWithTabKey(By.xpath(finalXpath), driver, keyValue);
				break;
			case ProcessConstants.TEXTCLEAR:
				click(By.xpath(finalXpath), finalXpath, driver);
				Thread.sleep(500);
				clearInputs(By.xpath(finalXpath), driver);
				break;
			case ProcessConstants.GETTEXT:
				TestBase.getText(navActionParams, finalXpath);
				log.info("variables" + variables.toString());
				break;
			case ProcessConstants.CLICKPURCHASEORDERADVICE:
				WebElement wel = driver.findElement(By.xpath(finalXpath));
				String id = wel.getAttribute("id").toString();
				log.info("ID" + id);
				String strValue = id.split("-")[5];
				String xpath = "//*[@id='whina3110m000-grid-n1-select-" + strValue
						+ "'] | //*[@id='whina3110m000-grid-1-select-" + strValue + "']";
				log.info("xpath" + xpath);
				click(By.xpath(xpath), xpath, driver);
				break;
			case ProcessConstants.GETPEGGINGORDER:
				String varName = getJsonFieldValue(navActionParams, "varName");
				WebElement webl = driver.findElement(By.xpath(finalXpath));
				String text1 = webl.getText();
				log.info("text1---->" + text1);
				String strVal[] = text1.split("\\|");
				String strval = strVal[1].toString();
				log.info("strVal---->" + strVal[1].toString());
				variables.put(varName, strval);
				log.info(variables.toString());
//				String strVal1 = strVal.split("|")[1];
//				log.info("strVal1" + strVal1);
//				int num = Integer.parseInt(strValue2);
//				int num1 = num-1;
//				String strValue3 = Integer.toString(num1);
//				log.info("strValue3" + strValue3);
//				String xpath1 = "//*[@id='cprrp0520m000-grid-n1-orno-n31-n"+ strValue3+ "']";
//				log.info("xpath" + xpath1);
//				click(By.xpath(xpath1), xpath1, driver);
//				TestBase.getText(navActionParams, xpath1);
				break;

			case ProcessConstants.CLICKPURCHASEORDER:
				WebElement wel2 = driver.findElement(By.xpath(finalXpath));
				String id2 = wel2.getAttribute("id").toString();
				log.info("ID" + id2);
				String strValue4 = id2.split("-")[5];
				String strValue5 = strValue4.split("n")[1];
				int num2 = Integer.parseInt(strValue5);
				int num3 = num2 + 1;
				String strValue6 = Integer.toString(num3);
				log.info("strValue3" + strValue6);
				String xpath2 = "//*[@id='cprrp0520m000-grid-n1-drilldown-n" + strValue6 + "']";
				log.info("xpath" + xpath2);
				click(By.xpath(xpath2), xpath2, driver);
				// TestBase.getText(navActionParams, xpath2);
				break;
			}
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		} catch (

		Exception e) {
			e.printStackTrace();
			try {
				log.info("------------------------ exception caught --------------------------------------");
				JavascriptExecutor js = (JavascriptExecutor) driver;
				WebElement element = null;
				try {
					if (finalXpath != null)
						element = driver.findElement(By.xpath(finalXpath));
				} catch (Exception ex) {
					ex.printStackTrace();
					log.info(
							"------------------------ exception caught --------------------------------------");
					String javascript = "return document.evaluate(\"" + finalXpath.replace("\"", "'")
							+ "\",document,null,XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue;";
					log.info("javascript :: " + javascript);
					log.info(
							"******************** Trying to find element using JavaScriptExecutor ***********");
					element = (WebElement) js.executeScript(javascript);
					log.info("WebElement status :: " + element);
					log.info(
							"******************** Trying to find element using JavaScriptExecutor ends ***********");
				}
				log.info("element status :: " + element);
				ItemStatus status = ItemStatus.PASSED;
				if (processType.contains("type")) {
					log.info("************** JavaScriptExecutor type starts **************");
					boolean typeSuccess = false;
					try {
						log.info("keyValue::" + keyValue);
						element.sendKeys(keyValue);
						log.info("keyValue type using driver find element :: keyValue::" + keyValue);
						typeSuccess = true;
					} catch (Exception e1) {
						typeSuccess = false;
					}
					try {
						if (!typeSuccess) {
							log.info("keyValue::" + keyValue);
							js.executeScript("arguments[0].value='" + keyValue + "';", element);
							log.info("keyValue type using java script executor :: keyValue::" + keyValue);
						}
					} catch (Exception e1) {
						status = ItemStatus.FAILED;
					}
					log.info("************** JavaScriptExecutor type ended **************");
					if (processType.toLowerCase().contains("tab")) {
						keyTab(1);
					}
					if (processType.toLowerCase().contains("enter")) {
						keyEnter(1);
					}
				} else if (processType.contains("click")) {
					log.info("************** JavaScriptExecutor click starts **************");
					boolean clickSuccess = false;
					try {
						element.click();
						log.info("click using driver find element");
						clickSuccess = true;
					} catch (Exception e1) {
						clickSuccess = false;
					}
					try {
						if (!clickSuccess)
							js.executeScript("arguments[0].click();", element);
						log.info("click using javascript executor");
					} catch (Exception e1) {
						status = ItemStatus.FAILED;
					}
					log.info("************** JavaScriptExecutor click ended **************");
				}
				testEnsureRequest = RequestBuilder.build("bugTitle", status, null, "fileName", 1);
			} catch (Exception ex) {
				if (noOfRetries < retryCount) {
					noOfRetries++;
					log.info("********** Failed to process NavAction Node *** Retrying " + noOfRetries
							+ " time ***********");
					e.printStackTrace();
					processNavActionNode(navActionParams, processType, file, testCaseStepId);
				}
				testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.FAILED, IssueEnum.AUTOMATION_BUG,
						"fileName", 1);
				String methodName = new Object() {
				}.getClass().getEnclosingMethod().getName();
				TestBase.testStepStatus = false;
				TestBase.testCaseFailed = true;
				log.info(methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
						+ ":: testcasefailed::" + testCaseFailed);
				ex.printStackTrace();
			}
		}
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		String fileName = getJsonFieldValue(navActionParams, "fileName");

		if (fileName != null && !fileName.equals("")) {
			renameFile(fileName);
		}

		if (ignore != null && ignore.equals("yes")) {
			driver.manage().timeouts().implicitlyWait(TestUtil.implicit_wait, TimeUnit.SECONDS);
			testEnsureRequest = RequestBuilder.build("bugTitle", ItemStatus.PASSED, null, "fileName", 1);
		}

		log.info("Final values :: " + methodName + "::" + testEnsureRequest + ":: teststepstatus::" + testStepStatus
				+ ":: testcasefailed::" + testCaseFailed);
		return testEnsureRequest;
	}

}
