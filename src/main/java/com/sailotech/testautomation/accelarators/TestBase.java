package com.sailotech.testautomation.accelarators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.annotations.AfterMethod;
import org.testng.asserts.SoftAssert;

import com.epam.reportportal.listeners.ItemStatus;
import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.service.ReportPortal;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sailotech.testautomation.beans.JobUpdateRequest;
import com.sailotech.testautomation.beans.Release;
import com.sailotech.testautomation.beans.TestResults;
import com.sailotech.testautomation.commonutilities.SendMail;
import com.sailotech.testautomation.commonutilities.TestUtil;
import com.sailotech.testautomation.commonutilities.WebEventListener;
import com.sailotech.testautomation.database.ImageToBase64;
import com.sailotech.testautomation.database.StatusUpdater;
import com.sailotech.testautomation.exceptions.jsonparsing.JsonAutomationFormatIssue;
import com.sailotech.testautomation.test.enums.IssueEnum;
import com.sailotech.testautomation.test.parameterization.common.loopactions.LoopingActions;
import com.sailotech.testautomation.test.parameterization.common.manualactions.ManualActions;
import com.sailotech.testautomation.test.parameterization.common.navactions.NavigationActions;
import com.sailotech.testautomation.test.parameterization.common.validateactions.ValidateAction;
import com.sailotech.testautomation.test.parameterization.jsonformat.ParsingConstants;
import com.sailotech.testautomation.test.parameterization.jsonformat.TestNode;
import com.sailotech.testautomation.test.rputils.ReportPortalUtil;
import com.sailotech.testautomation.util.DateFormatUtil;
import com.sailotech.testautomation.util.LambdaTestRestAPIUtil;
import com.sailotech.testautomation.util.RequestBuilder;
import com.sailotech.testautomation.util.RestAPIUtil;
import com.sailotech.testautomation.util.TestEnsureRequest;

import edu.emory.mathcs.backport.java.util.Collections;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LAVANYA
 * 
 *         Base class to read inputs and initializes driver
 */
@SuppressWarnings("deprecation")
@Slf4j
public class TestBase extends TestListenerAdapter {

	public static WebDriver driver;
	public static Properties prop;
	public static Properties locatorsProp;
	public static Properties xpathParams;
	public static Properties reportPortalParams;
	public static Properties mongodbProp;
	public static Logger log = Logger.getLogger(TestBase.class);
	public static Logger logger = Logger.getLogger("tempLog");
	public static EventFiringWebDriver e_driver;
	public static WebEventListener eventListener;
	public static String EXECUTION_ENV = System.getProperty("os.name");
	public static String LINUX_ENV = "Linux";
	public static WebDriverWait wait;
	public static boolean testStepStatus = true;
	public static boolean manualTestCase = false;
	public static boolean continueTestNodes = false;
	public static boolean testSuiteFailed = false;
	public static boolean testCaseFailed = false;
	public static Map<String, String> variables = new HashMap<String, String>();
	public static String launchID = System.getProperty("launchID");
	public static int retryCount = 1;
	public static int noOfRetries = 0;

	public static Release release = null;
	public static String currentProject = null;
	public static String currentModule = null;
	public static Map<String, String> testData = null;
	public static String moduleId = null;
	public static String testCaseStepId = null;
	public static String testCaseId = null;

	public static SoftAssert softAssert = new SoftAssert();

	public static String jenkinsBuildNumber = System.getenv("BUILD_NUMBER");
	public static String jenkinsJobName = System.getenv("JOB_NAME");

	public static String releaseID = System.getProperty("releaseID");

	public static String runID = null;

	public static Map<String, String> jsonsInputMap = new HashMap<>();
	public static Map<String, Map<String, String>> inputMap = new HashMap<>();
	public static Map<String, Map<String, String>> rerunMap = new HashMap<String, Map<String, String>>();
	public static Map<String, List> fileVar = new HashMap<String, List>();
	public static Map<String, List> excelVar = new HashMap<String, List>();
	public static TestResults testResults = new TestResults();
	public static LocalDateTime jobStartTime = null;
	public static LocalDateTime jobEndTime = null;
	public static LocalDateTime moduleStartTime = null;
	public static LocalDateTime moduleEndTime = null;
	public static LocalDateTime startTime = null;
	public static LocalDateTime endTime = null;
	public static LocalDateTime duration = null;
	public static long durationSeconds = 0;
	public static long moduleDurationSec = 0;
	public static long jobDurationSec = 0;
	public static int timeoutInSeconds = 30;
	public static String randomDate = null;

	public static ItemStatus defectType = null;

	public static Map<String, String> testNodesMap = new HashMap<String, String>();
	public static List<TestNode> testNodes = null;

	public static Map<String, String[]> objects = new HashMap<>();
	public static Map<String, List> objArr = new HashMap<>(); 
	public static Map<String,String> objList = new HashMap<>();
	public static Map<String, Map> obj = new HashMap<String, Map>();

	public static Maybe<String> suiteRs = null;

	static Map<String, ItemStatus> testEnsureRequestStatusMap = new HashMap<String, ItemStatus>();

	public static String downloadFilepath = System.getProperty("user.dir") + "\\resources\\temp\\";
	public static String sourceFolderToZip = System.getProperty("user.dir") + "\\resources\\temp\\";
	public static String zipFilePath = System.getProperty("user.dir") + "\\resources\\temp\\";
	public static String deleteFolder = System.getProperty("user.dir") + "\\resources\\temp\\";

	public static String emailSubject = null;
	public static int totalTestCases = 0;

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static String launchName = null;

	public static boolean deleteScreenshotFile = false;

	public static String rpUsername = null;

	public static DateFormatUtil dateFormatUtil = null;

	/**
	 * Method to generate env relative path
	 * 
	 * @param windowsPath
	 * @return
	 */
	public static String envRelativePath(String windowsPath) {
		if (EXECUTION_ENV.equals(LINUX_ENV)) {
			return windowsPath.replaceAll("\\\\", "/");
		}
		return windowsPath;
	}

	public static int get_timout;

	public TestBase() {
		prop = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(
					System.getProperty("user.dir") + envRelativePath("\\resources\\selenium.properties"));
			loadLocatorProperties();
			loadMongoDBProperties();
			deleteScreensFromFolder();
			deleteFromTempFolder();
			dateFormatUtil = new DateFormatUtil();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			prop.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to load locators properties file
	 */
	private void loadLocatorProperties() {
		locatorsProp = new Properties();
		InputStream locfis = null;
		String propFilePath = System.getProperty("user.dir") + envRelativePath("\\resources\\locators.properties");
		try {
			locfis = new FileInputStream(propFilePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			locatorsProp.load(locfis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadReportPortalProperties() {
		reportPortalParams = new Properties();
		InputStream locfis = null;
		String propFilePath = System.getProperty("user.dir")
				+ envRelativePath("\\src\\test\\resources\\reportportal.properties");
		try {
			locfis = new FileInputStream(propFilePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			reportPortalParams.load(locfis);
			log.info("reportPortalParams::" + reportPortalParams.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to load mongodb properties file
	 */
	private static void loadMongoDBProperties() {
		mongodbProp = new Properties();
		InputStream locfis = null;
		String propFilePath = System.getProperty("user.dir") + envRelativePath("\\resources\\mongodb.properties");
		try {
			locfis = new FileInputStream(propFilePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mongodbProp.load(locfis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ListenerParameters standardParameters(String releaseName, String tagName, String accessToken) {
		ListenerParameters result = null;
		try {
			if (Boolean.parseBoolean((String) prop.get("updateReportPortal"))) {
				loadReportPortalProperties();
				String endpoint = reportPortalParams.getProperty("rp.endpoint");
				String uuid = reportPortalParams.getProperty("rp.uuid");
				if (accessToken != null && !accessToken.equals(""))
					uuid = accessToken;
				log.info("accessToken uuid => " + uuid);
				String projectName = reportPortalParams.getProperty("rp.project");
				if (jenkinsJobName != null && !jenkinsJobName.equals("")) {
					projectName = jenkinsJobName;
				}
				if (launchName != null)
					projectName = launchName;
				if (releaseName != null)
					projectName = releaseName;
				String launch = reportPortalParams.getProperty("rp.launch");
				ReportPortalUtil.endpoint = endpoint;
				ReportPortalUtil.projectName = projectName;
				ReportPortalUtil.bearerToken = uuid;
				if (launchID != null && !launchID.equals("")) {
					rerunMap = RestAPIUtil.getLaunchRerunDetails(endpoint, uuid, projectName, launchID);
					log.info("rerun cases count :: " + rerunMap.size());
					ReportPortalUtil.rerun = true;
				}
				result = new ListenerParameters();
				result.setBaseUrl(endpoint);
				result.setClientJoin(Boolean.valueOf(reportPortalParams.getProperty("rp.client.join")));
				result.setBatchLogsSize(Integer.parseInt(reportPortalParams.getProperty("rp.batch.size.logs")));
				if (tagName != null && !tagName.equals("")) {
					launch = launch + "_" + tagName.replace(",", "_").toUpperCase();
				}
				// String launchName = RestAPIUtil.generateLaunchName(endpoint, uuid,
				// projectName, launch, 4, "-");
				// String launchName = null;
				// String launchName = System.getProperty("launchName");
				if (releaseName != null && !releaseName.equals(""))
					launchName = releaseName;
				else
					launchName = System.getProperty("launchName");
				result.setLaunchName(launchName);
				result.setProjectName(projectName);
				result.setEnable(Boolean.valueOf(reportPortalParams.getProperty("rp.enable")));
				result.setApiKey(reportPortalParams.getProperty("rp.uuid"));
				if (accessToken != null && !accessToken.equals(""))
					result.setApiKey(accessToken);
				RestAPIUtil.searchProject(endpoint, uuid, launchName);
			}
		} catch (Exception e) {
		}
		return result;
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

	public static StartLaunchRQ standardLaunchRequest(final ListenerParameters params) {
		StartLaunchRQ rq = new StartLaunchRQ();
		// rq.setRerun(false);
		rq.setName(params.getLaunchName());
		rq.setStartTime(Calendar.getInstance().getTime());
		rq.setAttributes(params.getAttributes());
		rq.setMode(params.getLaunchRunningMode());
		rq.setRerun(params.isRerun());
		rq.setStartTime(Calendar.getInstance().getTime());
		// rq.setUuid(params.getApiKey());
		return rq;
	}

	public static FinishExecutionRQ standardLaunchFinishRequest() {
		FinishExecutionRQ rq = new FinishExecutionRQ();
		rq.setEndTime(Calendar.getInstance().getTime());
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

	public static FinishTestItemRQ positiveFinishRequest(ListenerParameters params) {
		FinishTestItemRQ rq = new FinishTestItemRQ();
		rq.setLaunchUuid(params.getApiKey());
		rq.setEndTime(Calendar.getInstance().getTime());
		rq.setStatus("PASSED");
		return rq;
	}

	public static JobUpdateRequest addLogsAndScreenhots(String fileName) throws IOException {
		List<String> files = new ArrayList<String>();
		JobUpdateRequest jobUpdateRequest = new JobUpdateRequest();
		if (log != null) {
			String logFile = System.getProperty("user.dir") + envRelativePath("\\logs\\") + "TestEnsureTest.log";
			BufferedReader reader = new BufferedReader(new FileReader(logFile));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			// delete the last new line separator
			try {
				stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			} catch (Exception e) {
			}
			reader.close();

			String content = stringBuilder.toString();

			String filePath = getScreenShot(fileName);
			ReportPortal.emitLog(content, "INFO", Calendar.getInstance().getTime(), new File(filePath));
			// File file = new File(filePath);
			// BufferedImage bImage = ImageIO.read(new File(filePath));
			// ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// ImageIO.write(bImage, "jpg", bos);
			// byte [] data = bos.toByteArray();
			// files.add(new File(filePath));

			String ext = FilenameUtils.getExtension(filePath);
			files.add(ImageToBase64.getBase64(new File(filePath), ext));
			jobUpdateRequest.setScreenshot(files);
			jobUpdateRequest.setLog(content);
		}
		return jobUpdateRequest;
	}

	public static void addLogs() throws IOException {
		if (log != null) {
			String logFile = System.getProperty("user.dir") + envRelativePath("\\logs\\") + "TestEnsureTest.log";
			BufferedReader reader = new BufferedReader(new FileReader(logFile));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			// delete the last new line separator
			try {
				stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			} catch (Exception e) {
			}
			reader.close();

			String content = stringBuilder.toString();

			ReportPortal.emitLog(content, "INFO", Calendar.getInstance().getTime());
		}
	}

	public static void addVideo(String fileName) throws IOException {
		ReportPortal.emitLog("", "INFO", Calendar.getInstance().getTime(), new File(fileName));
	}

	/**
	 * Method to zoom web page based on given percentage value
	 * 
	 * @param zoomLevelIncrease
	 */
	public static void zoomOutByZoomLevel(String zoomLevelIncrease) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.body.style.zoom='" + zoomLevelIncrease + "%'");
		log.info("Zoom level is set to " + zoomLevelIncrease);
	}

	/**
	 * This method sets headless option to true for Windows
	 */
	public static void setHeadlessOption() {
		if (!EXECUTION_ENV.equals(LINUX_ENV))
			System.setProperty("java.awt.headless", "false");
	}

	public static void setPermission(File file) throws IOException {
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);

		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);

		Files.setPosixFilePermissions(file.toPath(), perms);
	}

	/**
	 * This method initializes driver and opens web browser
	 */
	public static void initBrowser(String fileName) {

		log.info("Build Number:" + jenkinsBuildNumber);
		log.info("Job Name:" + jenkinsJobName);

		log.info("launching browser");

		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

		String tempFolder = (EXECUTION_ENV.equals(LINUX_ENV)) ? System.getProperty("user.dir") + "/resources/temp/"
				: System.getProperty("user.dir") + "\\resources\\temp\\";

		downloadFilepath = tempFolder + formatter.format(ts);

		sourceFolderToZip = (EXECUTION_ENV.equals(LINUX_ENV)) ? downloadFilepath + "/" : downloadFilepath + "\\";

		if (EXECUTION_ENV.equals(LINUX_ENV)) {
			zipFilePath = tempFolder;
			deleteFolder = tempFolder;
		}

		/*
		 * if (fileName.contains("/")) { zipFilePath = zipFilePath +
		 * fileName.split("/")[1] + "_" + formatter.format(ts) + ".zip"; emailSubject =
		 * fileName.split("/")[1] + " Execution"; } else { zipFilePath = zipFilePath +
		 * fileName + "_" + formatter.format(ts) + ".zip"; emailSubject = fileName +
		 * " Execution"; }
		 */

		log.info("downloadFilepath " + downloadFilepath);

		/*
		 * File destinationFolder = new File(downloadFilepath);
		 * 
		 * if (!destinationFolder.exists()) { destinationFolder.mkdirs(); }
		 */

		String browserName = prop.getProperty("browser");

		// File directory = new File(".");

		/*
		 * String chromeDriverPath = (EXECUTION_ENV.equals(LINUX_ENV)) ?
		 * directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length()
		 * - 2) + "/drivers/Linux/chromedriver_linux_114" :
		 * System.getProperty("user.dir") + "\\drivers\\chromedriver_103.exe";
		 */

		if (browserName.equalsIgnoreCase("chrome")) {
			// System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			System.setProperty("webdriver.chrome.verboseLogging", "false");

			ChromeOptions options = new ChromeOptions();
			log.info("####" + prop.get("enablePDFViewer") + "###");

			if (!Boolean.parseBoolean((String) prop.get("enablePDFViewer"))) {
				HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
				chromePrefs.put("profile.default_content_settings.popups", 0);
				chromePrefs.put("download.default_directory", downloadFilepath);
				chromePrefs.put("plugins.always_open_pdf_externally", true);
				options.setExperimentalOption("prefs", chromePrefs);
				log.info("enablePDFViewer Success");
			} else {
				options.addArguments("--disable-blink-features=BlockCredentialedSubresources");
				Map<String, Object> prefs = new HashMap<String, Object>();
				prefs.put("credentials_enable_service", false);
				prefs.put("profile.password_manager_enabled", false);
				prefs.put("plugins.always_open_pdf_externally", false);
				options.setExperimentalOption("prefs", prefs);
				log.info("enablePDFViewer Failed");
			}

			options.addArguments("start-maximized");
			// options.addArguments("--incognito");
			options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
			// options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
			// UnexpectedAlertBehaviour.ACCEPT);
			// options.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

			log.info("EXECUTION_ENV" + EXECUTION_ENV);
			log.info("LINUX_ENV" + LINUX_ENV);

			if (EXECUTION_ENV.equals(LINUX_ENV)) {
				// log.info("chromeDriverPath " + chromeDriverPath);
				/*
				 * File file = new File(chromeDriverPath); file.setWritable(true);
				 * file.setReadable(true); file.setExecutable(true); try { setPermission(new
				 * File(chromeDriverPath)); } catch (IOException e) { // TODO Auto-generated
				 * catch block e.printStackTrace(); log.info(e); }
				 */
				/*
				 * try { Runtime.getRuntime().exec("sh -c chmod -R 777 " + chromeDriverPath); }
				 * catch (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); log.info(e); }
				 */
				options.setBinary("/usr/bin/google-chrome");
				options.addArguments("--no-sandbox"); // Bypass OS security model
				// options.addArguments("--disable-dev-shm-usage");
				options.addArguments("--headless");
				options.addArguments("--disable-extensions");
				/*
				 * options.addArguments("--no-proxy-server");
				 * options.addArguments("--proxy-server='direct://'");
				 * options.addArguments("--proxy-bypass-list=*");
				 */
				options.addArguments("--start-maximized");
				// options.addArguments("--disable-gpu");
				options.addArguments("--ignore-certificate-errors");
			} else if (EXECUTION_ENV.equals(LINUX_ENV)) {
				// close the pop-ups
				options.addArguments("--disable-notifications");
				List<File> paths = new ArrayList<File>();
				paths.add(new File(System.getProperty("user.dir") + "\\extensions\\TruePath.crx"));
				paths.add(new File(System.getProperty("user.dir") + "\\extensions\\CSS-and-XPath-checker.crx"));
				options.addExtensions(paths);
				options.addArguments("--remote-allow-origins=*");
				System.setProperty("java.awt.headless", "false");
				// to enable screenShot and fix timeouts received from renderer
				options.addArguments("--disable-features=VizDisplayCompositor");
			}

			if (prop.get("runOn").toString().equals("saucelabs")) {
				options.setPlatformName(String.valueOf(prop.get("sauceLabs-platform")));
				options.setBrowserVersion(String.valueOf(prop.get("sauceLabs-browserVersion")));
				Map<String, Object> sauceOptions = new HashMap<>();

//				sauceOptions.put("username", "oauth-lavanya.brunda-41c09");
//				sauceOptions.put("accessKey", "1ff47407-07f8-4ed1-9845-1861dbe45810");
				sauceOptions.put("remoteAppsCacheLimit", String.valueOf(prop.get("sauceLabs-remoteAppsCacheLimit")));

				sauceOptions.put("username", String.valueOf(prop.get("sauceLabs-username")));
				sauceOptions.put("accessKey", String.valueOf(prop.get("sauceLabs-accessKey")));
				sauceOptions.put("build", String.valueOf(prop.get("sauceLabs-build")));
				sauceOptions.put("name", String.valueOf(prop.get("sauceLabs-name")));
				sauceOptions.put("screenResolution", String.valueOf(prop.get("sauceLabs-screenResolution")));
				options.setCapability("sauce:options", sauceOptions);

				log.info("sauceLabs-gridHubURL -> " + prop.getProperty("sauceLabs-gridHubURL"));
				driver = WebDriverManager.chromedriver().capabilities(options)
						.remoteAddress(prop.getProperty("sauceLabs-gridHubURL")).create();
			} else if (prop.get("runOn").toString().equals("lambdatest")) {
				String ltUsername = String.valueOf(prop.get("lambdatest-username"));
				String ltAccessKey = String.valueOf(prop.get("lambdatest-accessKey"));
				String ltAutomationEndpoint = String.valueOf(prop.get("lambdatest-automation-endpoint"));
				ChromeOptions browserOptions = new ChromeOptions();
				browserOptions.setPlatformName(String.valueOf(prop.get("lambdatest-platform")));
				browserOptions.setBrowserVersion(String.valueOf(prop.get("lambdatest-browserVersion")));
				HashMap<String, Object> ltOptions = new HashMap<String, Object>();
				ltOptions.put("username", ltUsername);
				ltOptions.put("accessKey", ltAccessKey);
				ltOptions.put("visual", true);
				ltOptions.put("video", true);
				ltOptions.put("build", launchName);
				ltOptions.put("project", launchName);
				ltOptions.put("w3c", true);
				ltOptions.put("plugin", "java-testNG");
				browserOptions.setCapability("LT:Options", ltOptions);

				String url = "http://" + String.valueOf(prop.get("lambdatest-username")) + ":"
						+ String.valueOf(prop.get("lambdatest-accessKey")) + prop.getProperty("lambdatest-gridHubURL");
				log.info("lambdatest-gridHubURL -> " + prop.getProperty("lambdatest-gridHubURL"));
				log.info("url :: " + url);
//				driver = WebDriverManager.chromedriver().capabilities(options).remoteAddress(url).create();
				try {
					RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(url), browserOptions);
					driver = remoteDriver;
					log.info("driver :: " + driver);
					SessionId testId = remoteDriver.getSessionId();
					log.info("driver test id " + testId);
					String ltVideoUrl = LambdaTestRestAPIUtil.getVideoUrl(ltAutomationEndpoint,
							ltUsername, ltAccessKey, testId.toString());
					log.info("ltVideoUrl ++ "+ltVideoUrl);
					StatusUpdater.saveJobURL(runID, null, ltVideoUrl);
//					driver = WebDriverManager.chromedriver().capabilities(options).remoteAddress(url).create();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				driver = WebDriverManager.chromedriver().capabilities(options).create();
			}

			// driver = new ChromeDriver(options);
			log.info("EXECUTION_ENV" + EXECUTION_ENV);
			log.info("LINUX_ENV" + LINUX_ENV);
			log.info("driver" + driver);
//			if (EXECUTION_ENV.equals(LINUX_ENV)) {
			driver.manage().window().setSize(new Dimension(1920, 1080));
//			}
			log.info("launching chrome browser");

		} else if (browserName.equalsIgnoreCase("firefox")) {
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\drivers\\geckodriver.exe");
			driver = new FirefoxDriver();
			log.info("launching firefox browser");
		} else if (browserName.equalsIgnoreCase("edge")) {
			driver = WebDriverManager.edgedriver().create();
		} else {
			log.info("no proper browser initialized");
		}
		e_driver = new EventFiringWebDriver(driver);
		eventListener = new WebEventListener();
		e_driver.register(eventListener);
		driver = e_driver;

		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(TestUtil.implicit_wait, TimeUnit.SECONDS);

		// driver.get(prop.getProperty("inforLNUrl"));
		log.info("entering into application URL");
		wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
	}

	public static void loginToInfor(String userName, String password) {
		log.info("launching browser");
		String inforLNUrl = "http://" + userName + ":" + password + "@73.135.240.210:8325/webui/servlet/login";
		driver.get(inforLNUrl);
		log.info("entering into application URL" + inforLNUrl);
	}

	public static void loginToInfor(String url, String username, String password) {
		log.info("launching browser");
		try {
			if (username.contains("$") || password.contains("$")) {
				log.info("username, password");
				username = username.replace("$", "");
				password = password.replace("$", "");
				username = jsonsInputMap.get(username);
				password = jsonsInputMap.get(password);
				if (url.equals("$url"))
					url = jsonsInputMap.get("url");
				log.info(" username password from DB :: " + username + ":" + password);
			}
			if (url.equals("$url") && release == null) {
				log.info("jsonsInputMap::" + jsonsInputMap);
				url = jsonsInputMap.get("url");
			}
			if (url.equals("$url") && release != null) {
				log.info("testData::" + testData);
				url = testData.get("url");
			}
			if (url != null) {
				if (url.contains("$")) {
					log.info("url");
					url = url.replace("$username", username).replace("$password", password);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("driver.get " + url);

		try {

			log.info("entering into application URL" + url);
			driver.get(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Method to take screenshot
	 * 
	 * @param result
	 */
	@AfterMethod // AfterMethod annotation - This method executes after every
					// test execution
	public void screenShot(ITestResult result) {
		// using ITestResult.FAILURE is equals to result.getStatus then it enter
		// into if condition
		if (ITestResult.FAILURE == result.getStatus() || ITestResult.SUCCESS == result.getStatus()) {
			try {
				// To create reference of TakesScreenshot
				TakesScreenshot screenshot = (TakesScreenshot) driver;
				// Call method to capture screenshot
				File src = screenshot.getScreenshotAs(OutputType.FILE);

				filesMovetoFolder();

				// Copy files to specific location
				// result.getName() will return name of test case so that
				// screenshot name will be same as test case name

				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
				Date dt = new Date();

				FileUtils.copyFile(src, new File(System.getProperty("user.dir") + envRelativePath("\\FailedScreens\\")
						+ "PassedScreenshot" + dateFormat.format(dt) + ".png"));
				log.info("Successfully captured a screenshot");

				FileUtils.copyFile(src, new File(
						System.getProperty("user.dir") + envRelativePath("\\FailedScreens\\") + "errorscreen.png"));
				log.info("Successfully captured an error screenshot");

				SendMail sm = SendMail(driver);
				sm.main(null);
				// sm.sendEmail_attachment(result);

				log.info("Error ScreenShot sent");

			} catch (Exception e) {
				log.info("Exception while taking screenshot: " + e.getMessage());
			}
		}
	}

	/**
	 * Method to take screenshot and saves with guven screenshot name
	 * 
	 * @param result
	 * @param screenshotName
	 */
	public void screenShot(ITestResult result, String screenshotName) {
		try {
			// Thread.sleep(3000);
			// To create reference of TakesScreenshot
			TakesScreenshot screenshot = (TakesScreenshot) driver;
			// Call method to capture screenshot
			File src = screenshot.getScreenshotAs(OutputType.FILE);

			FileUtils.copyFile(src, new File(screenshotName));
		} catch (Exception e) {
			log.info("Exception while taking screenshot: " + e.getMessage());
		}
	}

	/**
	 * Method to take screenshot
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getScreenShot(String fileName) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
		Date dt = new Date();
		String fileNameDt = fileName + dateFormat.format(dt) + ".png";
		String relitiveath = "Screens/" + fileNameDt;
		String filePath = System.getProperty("user.dir") + envRelativePath("\\Screens\\") + fileNameDt;

		try {
			// To create reference of TakesScreenshot
			TakesScreenshot screenshot = (TakesScreenshot) driver;
			// Call method to capture screenshot
			File src = screenshot.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(src, new File(filePath));
			log.info("Successfully captured a screenshot");

			return relitiveath;
		} catch (Exception e) {
			log.info("Exception while taking screenshot: " + e.getMessage() + " StackTrace: " + e.getStackTrace());
		}

		return relitiveath;

	}

	/**
	 * This method moves screenshots from oldfailed screens to failed screens folder
	 */
	private static void filesMovetoFolder() {
		File destinationFolder = new File(System.getProperty("user.dir") + envRelativePath("\\OldFailedScreens"));
		File sourceFolder = new File(System.getProperty("user.dir") + envRelativePath("\\FailedScreens"));

		if (!destinationFolder.exists()) {
			destinationFolder.mkdirs();
		}

		// Check weather source exists and it is folder.
		if (sourceFolder.exists() && sourceFolder.isDirectory()) {
			// Get list of the files and iterate over them
			File[] listOfFiles = sourceFolder.listFiles();

			if (listOfFiles != null) {
				for (File child : listOfFiles) {
					// Move files to destination folder
					child.renameTo(new File(destinationFolder + envRelativePath("\\") + child.getName()));

					log.info("Files Moved Successfully");
				}

				// Add if you want to delete the source folder
				// sourceFolder.delete();
			}
		} else {
			log.info(sourceFolder + "Folder does not exists");
		}
	}

	/**
	 * This method moves screenshots from oldfailed screens to failed screens folder
	 */
	public static String renameFile(String fileName) {
		File sourceFolder = new File(downloadFilepath);
		String filePath = sourceFolder + envRelativePath("\\") + fileName + ".pdf";

		try {
			// Check weather source exists and it is folder.
			log.info("downloadFilepath " + downloadFilepath);
			Thread.sleep(2000);
			if (sourceFolder.exists() && sourceFolder.isDirectory()) {
				// Get list of the files and iterate over them
				File[] listOfFiles = sourceFolder.listFiles();

				if (listOfFiles != null) {
					for (File child : listOfFiles) {
						// Move files to destination folder
						if (child.getName().contains("tmp")) {
							child.renameTo(new File(filePath));
						}

						log.info("Files Moved Successfully");
					}

					// Add if you want to delete the source folder
					// sourceFolder.delete();
				}
			} else {
				log.info(sourceFolder + "Folder does not exists");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * This method deletes screenshots
	 */
	private static void deleteScreensFromFolder() {
		File screensFolder = new File(System.getProperty("user.dir") + envRelativePath("\\Screens"));

		try {
			if (screensFolder.exists() && screensFolder.isDirectory()) {
				// Get list of the files and iterate over them
				File[] listOfFiles = screensFolder.listFiles();

				if (listOfFiles != null) {
					log.info("Deleting files :: " + listOfFiles.length);
					for (File child : listOfFiles) {
						child.delete();
					}
					log.info("Files delete in screens folder :: " + listOfFiles.length);
				}
			} else {
				log.info(screensFolder + "Folder does not exists");
			}
		} catch (Exception e) {
		}
	}

	/**
	 * This method deletes screenshots
	 */
	private static void deleteFromTempFolder() {
		File screensFolder = new File(System.getProperty("user.dir") + envRelativePath("\\resources\\temp"));

		try {
			FileUtils.deleteDirectory(screensFolder);
			if (screensFolder.exists() && screensFolder.isDirectory()) {
				// Get list of the files and iterate over them
				File[] listOfFiles = screensFolder.listFiles();

				if (listOfFiles != null) {
					log.info("Deleting files :: " + listOfFiles.length);
					for (File child : listOfFiles) {
						child.delete();
					}
					log.info("Files delete in screens folder :: " + listOfFiles.length);
				}
			} else {
				log.info(screensFolder + "Folder does not exists");
			}
			screensFolder.mkdirs();
		} catch (Exception e) {
		}
	}

	/**
	 * This method to send mail
	 * 
	 * @param driver2
	 * @return
	 */
	private SendMail SendMail(WebDriver driver2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method to generate random number
	 * 
	 * @param length
	 * @return
	 */
	public static String generateRandomNumber(int length) {
		String randomNumber = "1";
		int retryCount = 1;
		while (retryCount > 0) {
			String number = Double.toString(Math.random());
			number = number.replace(".", "");
			if (number.length() > length) {
				randomNumber = number.substring(0, length);
			} else {
				int remainingLength = length - number.length() + 1;
				randomNumber = generateRandomNumber(remainingLength);
			}
			if (randomNumber.length() < length) {
				retryCount++;
			} else {
				retryCount = 0;
			}
		}
		return randomNumber;
	}

	/**
	 * type
	 *
	 * @param locator     of (By)
	 * @param testData    of (String)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static void type(By locator, String testData, String locatorName, WebDriver driver) throws Throwable {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name : " + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : Type  ::  Locator : " + locatorName + " :: Data :" + testData);
			// WebDriverWait wait = new WebDriverWait(driver, 30);
			log.info("Waiting for element :");
			// wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			log.info("Locator is Visible :: " + locator);
			driver.findElement(locator).clear();
			log.info("Cleared the existing Locator data : ");
			driver.findElement(locator).sendKeys(testData);
			log.info("Typed the Locator data :: " + testData);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
		}
	}

	/**
	 * click
	 *
	 * @param locator     of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static void click(By locator, String locatorName, WebDriver driver) throws Throwable {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : click  ::  Locator : " + locatorName);
			// WebDriverWait wait = new WebDriverWait(driver, 60);
			// new
			// Actions(driver).moveToElement(driver.findElement(locator)).build().perform();
			log.info("Waiting for element");
			log.info("Locator is Visible :: " + locator);
			// wait.until(ExpectedConditions.elementToBeClickable(locator));
			log.info("Clicked on the Locator");
			driver.findElement(locator).click();
			log.info("identified the element :: " + locator);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
			clickUsingJavascriptExecutor(locator, locatorName, driver);
		}
	}

	/**
	 * click
	 *
	 * @param locator     of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static void clickLastElement(By locator, String locatorName, WebDriver driver) throws Throwable {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : click  ::  Locator : " + locatorName);
			// WebDriverWait wait = new WebDriverWait(driver, 60);
			// new
			// Actions(driver).moveToElement(driver.findElement(locator)).build().perform();
			log.info("Waiting for element");
			log.info("Locator is Visible :: " + locator);
			// wait.until(ExpectedConditions.elementToBeClickable(locator));
			List<WebElement> we = driver.findElements(locator);
			log.info("Clicked on the Locator" + we.size());
			we.get(we.size() - 1).click();
			log.info("identified the element :: " + locator);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
			clickUsingJavascriptExecutor(locator, locatorName, driver);
		}
	}
	public static void typeMeasurementValues(By locator, String strData1, WebDriver driver) throws Throwable {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			// log.info("Method : click :: Locator : " + locatorName);
			// WebDriverWait wait = new WebDriverWait(driver, 60);
			// new
			// Actions(driver).moveToElement(driver.findElement(locator)).build().perform();
			log.info("Waiting for element");
			log.info("Locator is Visible :: " + locator);
			// wait.until(ExpectedConditions.elementToBeClickable(locator));
			List<WebElement> we = driver.findElements(locator);

			log.info("List WebElements" + we.size());

			for (int i = 1; i <= we.size(); i = i + 1) {
				try {
					String xpath = "//*[@id='qmptc1115m000-grid-n1-qmptc115.mval-n20-n" + i + "-widget']";
					log.info("Measurement xapth----->" + xpath);
					log.info("testData [" + strData1 + "]");
					driver.findElement(By.xpath(xpath)).click();
					Thread.sleep(2000);

					driver.findElement(By.xpath(xpath)).sendKeys(strData1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// we.get(we.size() - 1).click();
			// log.info("identified the element :: " + locator);
			// log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
			// clickUsingJavascriptExecutor(locator, locatorName, driver);
		}
	}

	/**
	 * type
	 *
	 * @param locator     of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static void typeHandlingUnits(By locator, String start, String xpathLocator, String strData1,
			WebDriver driver) throws Throwable {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Waiting for element");
			log.info("Locator is Visible :: " + locator);
			int startValue = Integer.parseInt(start);
			log.info("Start Value:" + startValue);
			log.info("Input Locator-->" + xpathLocator);
			String qtyLocator = "//div[@data-mgcompnamevalue= 'Total Item Qty']";
			List<WebElement> qtyLocators = driver.findElements(By.xpath(qtyLocator));
			String xpath2 = "(//div[contains(@data-mgcompnamevalue,'Item') and not(contains(@class,'x-grid-cell-inner x-grid-cell-inner-row-numberer')) and not(contains(@data-mgcompnamevalue,'Total Item Qty')) and not(contains(@data-mgcompnamevalue,'Scanned Item Qty')) and not(contains(@data-mgcompnamevalue,'ItemStatic1_SITE'))  and not(contains(@data-mgcompnamevalue,'ScannedItemQtyStatic')) and not(contains(@data-mgcompnamevalue,'TotalItemQtyStatic'))])";
			List<WebElement> itemLocators = driver.findElements(By.xpath(xpath2));
			String xpath3 = xpath2 + "[" + itemLocators.size() + "]";
			driver.findElement(By.xpath(xpath3)).click();
			int i = startValue;
			for (int j = 1; j <= qtyLocators.size(); j++) {
				try {
					String xpath = "(//div[contains(@data-mgcompnamevalue,'Item') and not(contains(@class,'x-grid-cell-inner x-grid-cell-inner-row-numberer')) and not(contains(@data-mgcompnamevalue,'Total Item Qty')) and not(contains(@data-mgcompnamevalue,'Scanned Item Qty')) and not(contains(@data-mgcompnamevalue,'ItemStatic1_SITE'))  and not(contains(@data-mgcompnamevalue,'ScannedItemQtyStatic')) and not(contains(@data-mgcompnamevalue,'TotalItemQtyStatic'))])["
							+ i + "]";
					String xpath1 = "(//div[@data-mgcompnamevalue= 'Total Item Qty'])[" + j + "]";
					String qtyValue = driver.findElement(By.xpath(xpath1)).getText();
					log.info(
							"Item Qty----->" + qtyValue + " Item QTY Xpath-->" + xpath1 + "Item Xpath-->" + xpath);
					int k = 1;
					do {
						Thread.sleep(2000);
						log.info("K Value-->" + k);
						String txtValue = driver.findElement(By.xpath(xpath)).getText();
						log.info("Item Value-->" + txtValue);
						driver.findElement(By.xpath(xpathLocator)).sendKeys(txtValue);
						Thread.sleep(2000);
						keyTab(1);
						Thread.sleep(5000);
						String serialxapth = "//input[@data-mgcompnamevalue= 'SerialEdit']";
						if (driver.findElement(By.xpath(serialxapth)).isDisplayed()) {
							driver.findElement(By.xpath(serialxapth)).sendKeys(strData1);
							keyTab(1);
						}
						Thread.sleep(2000);
						k = k + 1;
					} while (k <= Integer.parseInt(qtyValue));

				} catch (Exception e) {
					e.printStackTrace();
				}
				i = i + 1;
			}
		} catch (Exception e) {
			log.info(e.toString());
		}
	}

	/**
	 * click
	 *
	 * @param locator     of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static void clickVisibleElement(By locator, String locatorName, WebDriver driver) throws Throwable {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : click  ::  Locator : " + locatorName);
			// WebDriverWait wait = new WebDriverWait(driver, 60);
			// new
			// Actions(driver).moveToElement(driver.findElement(locator)).build().perform();
			log.info("Waiting for element");
			log.info("Locator is Visible :: " + locator);
			// wait.until(ExpectedConditions.elementToBeClickable(locator));
			List<WebElement> webElements = driver.findElements(locator);
			log.info("Clicked on the Locator" + webElements.size());
			// we.get(we.size() - 1).click();
			int i = 0;
			driver.manage().timeouts().implicitlyWait(Long.parseLong("2000"), TimeUnit.SECONDS);
			for (WebElement webElement : webElements) {
				if (webElement.isDisplayed()) {
					log.info("to be clickeked " + i);
					// webElement.click();
					js.executeScript("arguments[0].click();", webElement);

				} else {
					log.info("not to be clickeked " + i);
				}
				i++;
			}
			driver.manage().timeouts().implicitlyWait(TestUtil.implicit_wait, TimeUnit.SECONDS);
			log.info("identified the element :: " + locator);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
			clickUsingJavascriptExecutor(locator, locatorName, driver);
		}
	}

	/**
	 * doubleClick
	 *
	 * @param locator     of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static void doubleClick(By locator, String locatorName, WebDriver driver) throws Throwable {
		try {
			Actions act = new Actions(driver);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : click  ::  Locator : " + locatorName);
			/*
			 * WebDriverWait wait = new WebDriverWait(driver, 60); new
			 * Actions(driver).moveToElement(driver.findElement(locator)).build().perform();
			 */
			log.info("Waiting for element");
			log.info("Locator is Visible :: " + locator);
			// wait.until(ExpectedConditions.elementToBeClickable(locator));
			log.info("DoubleClicked on the Locator");
			// driver.findElement(locator).click();
			// Double click on element
			act.doubleClick(driver.findElement(locator)).perform();
			log.info("identified the element :: " + locator);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
			clickUsingJavascriptExecutor(locator, locatorName, driver);
		}
	}

	/**
	 * Method to type web element
	 * 
	 * @param locator
	 * @param testData
	 * @param locatorName
	 * @param driver
	 * @throws Throwable
	 */
	public static void typeWebelement(WebElement locator, String testData, String locatorName, WebDriver driver)
			throws Throwable {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name : " + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : Type  ::  Locator : " + locatorName + " :: Data :" + testData);
			// WebDriverWait wait = new WebDriverWait(driver, 30);
			log.info("Waiting for element :");
			// wait.until(ExpectedConditions.visibilityOf(locator));
			log.info("Locator is Visible :: " + locator);
			locator.clear();
			log.info("Cleared the existing Locator data : ");
			locator.sendKeys(testData);
			log.info("Typed the Locator data :: " + testData);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
		}
	}

	/**
	 * clickUsingJavascriptExecutor
	 *
	 * @param locator     of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static void clickUsingJavascriptExecutor(By locator, String locatorName, WebDriver driver) throws Throwable {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// Thread.sleep(500);
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : " + getCallerMethodName() + "  ::  Locator : " + locatorName);
			WebElement element = driver.findElement(locator);
			isElementPresent(locator, locatorName);
			// isDisplayed(locator,locatorName);
			js.executeScript("arguments[0].click();", element);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("clicked : " + locatorName);
		} catch (Exception e) {
			log.info("++++++++++++++++++++++++++++Catch Block Start+++++++++++++++++++++++++++++++++++++++++++");
			String finalXpath = locatorName;
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			String javascript = "return document.evaluate(\"" + finalXpath.replace("\"", "'")
					+ "\",document,null,XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue;";
			log.info("javascript :: " + javascript);
			log.info("******************** Trying to find element using JavaScriptExecutor ***********");
			WebElement element = (WebElement) js.executeScript(javascript);
			log.info("WebElement status :: " + element);
			log.info("******************** Trying to find element using JavaScriptExecutor ends ***********");
			log.info("************** JavaScriptExecutor click starts **************");
			try {
				js.executeScript("arguments[0].click();", element);
			} catch (Exception e1) {
			}
			try {
				element.click();
			} catch (Exception e1) {
			}
			log.info("************** JavaScriptExecutor click ended **************");
			log.info("++++++++++++++++++++++++++++Catch Block End+++++++++++++++++++++++++++++++++++++++++++");
		}
	}

	/**
	 * isElementPresent
	 *
	 * @param by          of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static boolean isElementNotPresent(By by, String locatorName) throws Throwable {
		boolean status;
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// dynamicWait(by);
			driver.findElement(by);
			log.info("Element Found By : " + by);
			status = false;
		} catch (Exception e) {
			status = true;
			log.info(e.toString());
			log.info("Element not found By : " + by);
		}
		return status;
	}

	/**
	 * isElementPresent
	 *
	 * @param by          of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static boolean isElementPresent(By by, String locatorName) throws Throwable {
		boolean status;
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// dynamicWait(by);
			driver.findElement(by);
			log.info("Found Element By : " + by);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			status = true;
		} catch (Exception e) {
			status = false;
			log.info(e.toString());
			log.info("Element not Found By : " + by);
		}
		return status;
	}

	/**
	 * isDisplayed
	 *
	 * @param by          of (By)
	 * @param locatorName of (String)
	 * @return boolean
	 * @throws Throwable the throwable
	 */
	public static boolean isDisplayed(By by, String locatorName) throws Throwable {
		boolean status;
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// dynamicWait(by);
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(by)));
			status = driver.findElement(by).isDisplayed();
			log.info("Found Element By : " + by);
			log.info("Element Display status : " + status);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// status = true;
		} catch (Exception e) {
			status = false;
			log.info(e.toString());
			log.info("Element not Found By : " + by);
		}
		return status;
	}

	/**
	 * @param by
	 * @param locatorName
	 * @return
	 * @throws Throwable
	 */
	public static boolean isSelected(By by, String locatorName) throws Throwable {
		boolean status;
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// dynamicWait(by);
			driver.findElement(by).isSelected();
			log.info("Element selected " + by);
			status = true;
		} catch (Exception e) {
			status = false;
			log.info("Element is not selected " + by);
			log.info(e.toString());
		}
		return status;
	}

	/**
	 * @param by
	 * @param locatorName
	 * @return
	 * @throws Throwable
	 */
	public static boolean isNotSelected(By by, String locatorName) throws Throwable {
		boolean status;
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// dynamicWait(by);
			driver.findElement(by).isSelected();
			log.info("Element selected " + by);
			status = false;
		} catch (Exception e) {
			status = true;
			log.info("Element is not selected " + by);
			log.info(e.toString());
		}
		return status;
	}

	/**
	 * dynamicWait
	 *
	 * @param locator of (By)
	 * @throws Throwable the throwable
	 */
	public static void dynamicWait(By locator) {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name : " + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : " + getCallerMethodName() + "  ::  Locator : " + locator);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtil.explicit_wait));
			wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			log.info(locator + ":: displayed succussfully");
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());

		}
	}

	/**
	 * This method clicks web element
	 * 
	 * @param locator
	 * @param locatorName
	 * @param driver
	 */
	public static void clickUsingWebElement(WebElement locator, String locatorName, WebDriver driver) {
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : click  ::  Locator : " + locatorName);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
			new Actions(driver).moveToElement(locator).build().perform();
			log.info("Waiting for element");
			log.info("Locator is Visible :: " + locator);
			wait.until(ExpectedConditions.elementToBeClickable(locator));
			log.info("Clicked on the Locator");
			locator.click();
			log.info("identified the element :: " + locator);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		} catch (Exception e) {
			log.info(e.toString());
		}
	}

	/**
	 * This method clicks web element using JS Executor
	 * 
	 * @param locator
	 * @param locatorName
	 * @param driver
	 */
	public static void clickUsingWebElementUsingJSExecuter(WebElement locator, String locatorName, WebDriver driver) {
		boolean elementVisible = true;
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("Method : " + getCallerMethodName() + "  ::  Locator : " + locatorName);

			js.executeScript("arguments[0].click();", locator);
			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("clicked : " + locatorName);
		} catch (Exception e) {
			log.info("++++++++++++++++++++++++++++Catch Block Start+++++++++++++++++++++++++++++++++++++++++++");
			log.info("Class name" + getCallerClassName() + "Method name : " + getCallerMethodName());
			log.info("++++++++++++++++++++++++++++Catch Block End+++++++++++++++++++++++++++++++++++++++++++");
		}

	}

	public static WebElement getShadowRootElement(WebElement element) {
		WebElement ele = (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot",
				element);
		return ele;
	}

	/**
	 * getCallerClassName
	 *
	 * @return String
	 */
	protected static String getCallerClassName() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		return stElements[3].getClassName();
	}

	/**
	 * getCallerMethodName
	 *
	 * @return String
	 */
	protected static String getCallerMethodName() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		return stElements[3].getMethodName();
	}

	/**
	 * Method to verify By element
	 * 
	 * @param actual
	 * @param expected
	 * @return
	 */
	public static boolean verifyAssertion(By actual, String expected) {
		TakesScreenshot screenshot = (TakesScreenshot) driver;
		// Call method to capture screenshot

		log.info("Asserion value [" + expected.trim() + "], found [" + driver.findElement(actual).getText().trim()
				+ "]");
		log.info("Asserion value [" + expected.trim() + "], found [" + driver.findElement(actual).getText().trim()
				+ "]");
		return expected.trim().equals(driver.findElement(actual).getText().trim());

	}

	/**
	 * Method to verify Web element
	 * 
	 * @param element
	 * @param expected
	 * @return
	 */
	public static boolean assertVerify(WebElement element, String expected) {
		softAssert.assertEquals(element.getText().trim(), expected.trim());
		if (element.getText().trim().equalsIgnoreCase(expected.trim()))
			return true;
		else
			return false;
	}

	/**
	 * Method to verify two lists
	 * 
	 * @param actual
	 * @param expected
	 * @return
	 */
	public static boolean assertTwoLists(List<String> actual, List<String> expected) {
		log.info("Asserion expected values are-->" + expected);
		log.info("Asserion actual values are-->" + actual);
		return actual.containsAll(expected);
	}

	/**
	 * Method to verify By element
	 * 
	 * @param actual
	 * @param expected
	 * @param fileName
	 * @param bugTitle
	 * @return
	 */
	public static TestEnsureRequest verifyAssertion(By actual, String expected, String fileName, String bugTitle) {

		log.info("Asserion value [" + expected.trim() + "], found [" + driver.findElement(actual).getText().trim()
				+ "]");
		if (expected.trim().equals(driver.findElement(actual).getText().trim())) {
			return RequestBuilder.build(bugTitle, true, fileName, 1);
		} else {
			return RequestBuilder.build(bugTitle, false, fileName, 2);
		}

	}

	/**
	 * Method to verify Web element
	 * 
	 * @param element
	 * @param expected
	 * @param fileName
	 * @param bugTitle
	 * @return
	 */
	public static TestEnsureRequest assertVerify(WebElement element, String expected, String fileName,
			String bugTitle) {
		softAssert.assertEquals(element.getText().trim(), expected.trim());

		if (element.getText().trim().equalsIgnoreCase(expected.trim()))
			return RequestBuilder.build(bugTitle, true, fileName, 1);
		else
			return RequestBuilder.build(bugTitle, false, fileName, 2);
	}

	/**
	 * Method to verify two strings
	 * 
	 * @param actual
	 * @param expected
	 * @param fileName
	 * @param bugTitle
	 * @return
	 */
	public static boolean verifyTwoStrings(String actual, String expected) {
		boolean status;
		try {

			if (expected.trim().equals(actual.trim())) {
				status = true;
				log.info("Strings compared");
			} else {
				status = false;
			}

		} catch (Exception e) {
			status = false;
			log.info("String not compared");
			log.info(e.toString());

		}
		return status;
	}

	public static boolean autoUpload(String path) {
		boolean status;
		try {
			log.info("Path---------->" + path);
			Runtime.getRuntime().exec(path);
			status = true;
			log.info("File uploaded Successfully");

		} catch (Exception e) {
			status = false;
			log.info("File not uploaded");
			log.info(e.toString());

		}
		return status;
	}

	/**
	 * Method to verify two strings
	 * 
	 * @param actual
	 * @param expected
	 * @param fileName
	 * @param bugTitle
	 * @return
	 */
	public static TestEnsureRequest verifyTwoValues(String actual, String expected, String fileName, String bugTitle) {

		try {

			if (expected.trim().equals(actual.trim())) {
				return RequestBuilder.build(bugTitle, true, fileName, 1);
			} else {
				return RequestBuilder.build(bugTitle, false, fileName, 2);
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			return RequestBuilder.build(bugTitle, false, fileName, 2);

		}

	}

	/**
	 * Method to verify two boolean values
	 * 
	 * @param actual
	 * @param expected
	 * @param fileName
	 * @param bugTitle
	 * @return
	 */
	public static TestEnsureRequest verifyTwoBooleanValues(boolean actual, boolean expected, String fileName,
			String bugTitle) {

		try {

			int b = Boolean.compare(actual, expected);

			if (b == 0) {
				return RequestBuilder.build(bugTitle, true, fileName, 1);
			} else {
				return RequestBuilder.build(bugTitle, false, fileName, 2);
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			return RequestBuilder.build(bugTitle, false, fileName, 2);

		}

	}

	/**
	 * 
	 * Method to verify two String for NOt equal
	 * 
	 * @param actual
	 * @param expected
	 * @param fileName
	 * @param bugTitle
	 * @return
	 */
	public TestEnsureRequest verifyTwoValuesNotEq(String actual, String expected, String fileName, String bugTitle) {
		// TODO Auto-generated method stub

		try {
			if (!expected.trim().equals(actual.trim())) {
				return RequestBuilder.build(bugTitle, true, fileName, 1);
			} else {
				return RequestBuilder.build(bugTitle, false, fileName, 2);
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			return RequestBuilder.build(bugTitle, false, fileName, 2);

		}
	}

	/**
	 * Method to get list of web elements
	 * 
	 * @param by
	 * @return
	 */
	public static List<WebElement> getListofWebelements(By by) {
		return driver.findElements(by);
	}

	/**
	 * 
	 * Method to enter input values in text box
	 * 
	 * @param locator
	 * @param locatorName
	 * @param driver
	 * @param strData1
	 */
	public void enterinputValues(By locator, String locatorName, WebDriver driver, String strData1) {
		driver.findElement(locator).sendKeys("");
		driver.findElement(locator).sendKeys(Keys.BACK_SPACE);
		driver.findElement(locator).sendKeys(Keys.BACK_SPACE);
		driver.findElement(locator).sendKeys(strData1);
		driver.findElement(locator).sendKeys(Keys.TAB);
	}

	/**
	 * Method to wait Until Element Disappears
	 * 
	 * @param locatorName
	 * @param elementToWaitFor
	 * @param timeout
	 * @param pollTimeout
	 * @param driver1
	 */
	public static void waitUntilElementDisappear(String locatorName, WebElement elementToWaitFor, int timeout,
			int pollTimeout, WebDriver driver1) {

		try {

			log.info("<<<<<<<<<<<<<<<<in fluentWaitFor for  waitUntilElementDisappear >>>>>>>>>>>>" + locatorName);
			FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver1);
			fluentWait.withTimeout(Duration.ofSeconds(timeout));
			fluentWait.pollingEvery(Duration.ofSeconds(pollTimeout));
			log.info("elementToWaitFor.findElements(By.xpath(locatorName)).isEmpty()"
					+ elementToWaitFor.findElements(By.xpath(locatorName)).isEmpty());
			fluentWait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					log.info("elementToWaitFor.findElements(By.xpath(locatorName)).isEmpty()"
							+ elementToWaitFor.findElements(By.xpath(locatorName)).isEmpty());
					log.info("<<<<<<<<<<<<<<<<in fluentWaitFor polling for next >>>>>>>>>>>>" + pollTimeout);
					return (elementToWaitFor.findElements(By.xpath(locatorName)).isEmpty());
				}
			});
		} catch (TimeoutException e) {

			log.info("<<<<<<<<<<<<<<<<in fluentWaitFor for waitUntilElementDisappear >>>>>>>>>>>>" + locatorName);
			log.info("<<<<<<<< waitUntilElementDisappear Time Out On : " + locatorName);

		}

	}

	/**
	 * Method to wait Until Element Disappears
	 * 
	 * @param locatorName
	 * @param elementToWaitFor
	 * @param timeout
	 * @param pollTimeout
	 * @param driver1
	 */
	public static void waitUntilElementDisappear(String locatorName, By elementToWaitFor, int timeout, int pollTimeout,
			WebDriver driver1) {

		try {

			log.info("<<<<<<<<<<<<<<<<in fluentWaitFor for  waitUntilElementDisappear >>>>>>>>>>>>" + locatorName);
			FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver1);
			fluentWait.withTimeout(Duration.ofSeconds(timeout));
			fluentWait.pollingEvery(Duration.ofSeconds(pollTimeout));
			fluentWait.ignoring(Exception.class);

			fluentWait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					boolean result = true;
					try {
						result = driver.findElements(By.xpath(locatorName)).isEmpty();
					} catch (Exception e) {
					}
					log.info("<<<<<<<<<<<<<<<<in fluentWaitFor polling for next >>>>>>>>>>>>" + pollTimeout);
					return (result);
				}
			});
		} catch (TimeoutException e) {

			log.info("<<<<<<<<<<<<<<<<in fluentWaitFor for waitUntilElementDisappear >>>>>>>>>>>>" + locatorName);
			log.info("<<<<<<<< waitUntilElementDisappear Time Out On : " + locatorName);

		}

	}

	/**
	 * This method verifies element exists or not
	 * 
	 * @param flag
	 * @param driver
	 * @param by
	 * @param err_msg
	 * @return
	 */
	public static boolean isElementExist(boolean flag, WebDriver driver, By by, String err_msg) {

		boolean result = false;

		try {

			try {
				if (flag) {
					WebElement ele = driver.findElement(By.xpath("//p[contains(.,'" + err_msg + "')]"));
					By msg = By.xpath("//p[contains(.,'" + err_msg + "')]");
				} else {
					WebElement ele = driver.findElement(By.xpath("//p[contains(.,'" + err_msg + "')]"));

					get_timout = Integer.parseInt(prop.getProperty("timeout"));
					waitUntilElementDisappear(err_msg, ele, get_timout, 10, driver);
				}
			} catch (Exception e) {
				log.info("Message not found in isElementExist" + err_msg);
			}
			return !driver.findElements(by).isEmpty();

		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * Method to verify element exists or not with no wait
	 * 
	 * @param by
	 * @param err_msg
	 * @return
	 */
	public boolean isElementExistWithNoWait(By by, String err_msg) {

		WebDriverWait zeroWait = new WebDriverWait(driver, Duration.ofSeconds(5));
		ExpectedCondition<WebElement> c = ExpectedConditions.presenceOfElementLocated(by);
		try {

			zeroWait.until(c);
			log.debug("Element Exists");
			return true;
		} catch (TimeoutException e) {
			log.debug("TimeoutException: Element Does not Exists: " + e.getMessage());
			return false;
		} catch (NoSuchElementException e) {
			log.debug("NoSuchElementException: Element Does not Exists: " + e.getMessage());
			return false;
		}

	}

	/**
	 * Method to verify element exists or not in configurator
	 * 
	 * @param flag
	 * @param driver
	 * @param by
	 * @param err_msg
	 * @return
	 */
	public static boolean isElementExistConfigurator(boolean flag, WebDriver driver, By by, String err_msg) {

		boolean result = false;

		try {
			try {
				driver.findElements(by);
			} catch (Exception e) {
				log.info("Message not found in isElementExistConfigurator" + err_msg);
			}
			return !driver.findElements(by).isEmpty();

		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * Method to verify element exists or not
	 * 
	 * @param driver
	 * @param by
	 * @return
	 */
	public static boolean isElementExist(WebDriver driver, By by) {

		boolean result = false;
		try {

			if (driver.findElement(by).isDisplayed()) {
				result = true;
			}

		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * Method to close browser
	 */
	public void closeBrowser() {
		if (driver != null)
			driver.quit();
		// killChromeSession();
	}

	public void sendEmail() {
		try {
			log.info("downloadFilepath ---> " + downloadFilepath);
			log.info("sourceFolderToZip ---> " + sourceFolderToZip);
			log.info("zipFilePath ---> " + zipFilePath);
			SendMail.zipFiles(sourceFolderToZip, zipFilePath);
			// SendMail.sendEmail_attachment(sourceFolderToZip, zipFilePath);
		} catch (Exception e) {

		}
	}

	public void killChromeSession() {
		if (EXECUTION_ENV.equals(LINUX_ENV)) {
			String str;
			Process proc;
			try {
				proc = Runtime.getRuntime().exec("pkill chrome");
				BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while ((str = br.readLine()) != null)
					log.info("line: " + str);
				proc.waitFor();
				log.info("exit: " + proc.exitValue());
				proc.destroy();
				log.info("Kill Chrome command run success ...");
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Continuous scroll to END method by using JS
	 * 
	 * @param count
	 * @param driver
	 * @throws InterruptedException
	 */
	public static void continuousScrollbyJS(int count, WebDriver driver) throws InterruptedException {
		log.info("<<<<< In continuousScrollbyJS done in BuildNAR using control END button>>>>");
		Actions actions = new Actions(driver);

		int i = 1;
		while (i <= count) {
			actions.click();
			actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform();
			log.info("i=" + i);
			i++;
			Thread.sleep(6000);
		}
		log.info("continuousScrollbyJS done using control END button");
	}

	/**
	 * Continuous scroll to HOME method by using JS
	 * 
	 * @param count
	 * @param driver
	 * @throws InterruptedException
	 */
	public static void continuousScrollUPbyJS(int count, WebDriver driver) throws InterruptedException {
		log.info("<<<<< In continuousScrollbyJS done in BuildNAR using control UP button>>>>");
		Actions actions = new Actions(driver);

		int i = 1;
		while (i <= count) {
			actions.click();
			actions.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).perform();
			log.info("i=" + i);
			i++;
			Thread.sleep(6000);
		}
		log.info("continuousScrollbyJS done in BuildNAR using control UP button");
	}

	/**
	 * Method to scroll
	 * 
	 * @throws InterruptedException
	 */
	public static void continuousScrollUPbyJSExecutor() throws InterruptedException {
		log.info("<<<<< In continuousScrollbyJSExecutor done by using JS fields>>>");

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, -4000)", "");
		log.info("In continuousScrollbyJSExecutor done by using JS fields");
	}

	/**
	 * Method to scroll to view
	 * 
	 * @param by
	 */
	public void scrollbyConfiguratorJS(By by) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement scr1 = driver.findElement(by);
		js.executeScript("arguments[0].scrollIntoView();", driver.findElement(by));
		log.info("scrollbyJS using BY");
	}

	/**
	 * Method to scroll to web element
	 * 
	 * @param wEle
	 */
	public void scrollbyConfiguratorJS(WebElement wEle) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView();", wEle);
		log.info("scrollbyJS using WEBELEMENT");

	}

	/**
	 * Method to wait for web element until visible
	 * 
	 * @param elementToWaitFor
	 * @param timeout
	 * @param pollTimeout
	 * @return
	 */
	/*
	 * public WebElement waitUntilVisible(String locatorName, WebElement
	 * elementToWaitFor, int timeout, int pollTimeout) {
	 * log.info("<<<<<<<<<<<<<<<<in fluentWaitFor for >>>>>>>>>>>>" + locatorName);
	 * FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver);
	 * fluentWait.withTimeout(Duration.ofSeconds(timeout));
	 * fluentWait.pollingEvery(Duration.ofSeconds(pollTimeout));
	 * fluentWait.ignoring(Exception.class);
	 * 
	 * WebElement element = wait.until(new Function<WebDriver, WebElement>() {
	 * public WebElement apply(WebDriver driver) {
	 * 
	 * log.info("<<<<<<<<<<<<<<<<in fluentWaitFor polling for next >>>>>>>>>>>>" +
	 * pollTimeout); return elementToWaitFor; } });
	 * 
	 * return element; }
	 */

	/**
	 * Method to wait for By element until visible
	 * 
	 * @param elementToWaitFor
	 * @param timeout
	 * @param pollTimeout
	 * @return
	 */
	public static WebElement waitUntilVisible(String locatorName, By elementToWaitFor, int timeout, int pollTimeout) {
		WebElement element = null;
		try {
			log.info("<<<<<<<<<<<<<<<<in fluentWaitFor for >>>>>>>>>>>>" + locatorName);
			FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver);
			fluentWait.withTimeout(Duration.ofSeconds(timeout));
			fluentWait.pollingEvery(Duration.ofSeconds(pollTimeout));
			fluentWait.ignoring(Exception.class);

			element = fluentWait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {

					// log.info("<<<<<<<<<<<<<<<<in fluentWaitFor polling for next >>>>>>>>>>>>" +
					// pollTimeout);
					return driver.findElement(elementToWaitFor);
				}
			});

		} catch (Exception e) {
		}

		return element;
	}

	/**
	 * Method to zoom web page
	 * 
	 * @param zoomLevelIncrease
	 */
	public static void zoomOutByPercent(String zoomLevelIncrease) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.body.style.zoom='" + zoomLevelIncrease + "%'");
	}

	/**
	 * Method to zoom web page to 50%
	 */
	public static void zoomIn() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String zoomInJS = "document.body.style.zoom='50%'";
		js.executeScript(zoomInJS);
	}

	public static void enterinputValuesWithEnter(By locator, WebDriver driver, String strData1) {
		WebElement l = driver.findElement(locator);
		// input text
		// sending Ctrl+a by Keys.Chord()
		String s = Keys.chord(Keys.CONTROL, "a");
		l.sendKeys(s);
		// sending DELETE key
		l.sendKeys(Keys.DELETE);
		l.sendKeys(strData1);
		l.sendKeys(Keys.ENTER);
		l.sendKeys(Keys.TAB);
	}

	public static void enterinputValuesWithTabKey(By locator, WebDriver driver, String strData1) {
		WebElement l = driver.findElement(locator);
		// input text
		// sending Ctrl+a by Keys.Chord()
		String s = Keys.chord(Keys.CONTROL, "a");
		l.sendKeys(s);
		// sending DELETE key
		l.sendKeys(Keys.DELETE);
		l.sendKeys(strData1);
		l.sendKeys(Keys.TAB);
	}

	public static void enterinputs(By locator, WebDriver driver, String strData1) {
		WebElement l = driver.findElement(locator);
		l.sendKeys(strData1);
	}

	public static void clearInputs(By locator, WebDriver driver) {
		WebElement l = driver.findElement(locator);
		// input text
		// sending Ctrl+a by Keys.Chord()
		String s = Keys.chord(Keys.CONTROL, "a");
		l.sendKeys(s);
		// sending DELETE key
		l.sendKeys(Keys.DELETE);

	}

	public static void scrollTo(String finalXpath, String scrollNumber) {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		try {
			js.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(finalXpath)));
			js.executeScript("window.scrollBy(0, " + scrollNumber + ")", "");
			log.info("scrolled to " + finalXpath + "::" + scrollNumber);
			isElementPresent(By.xpath(finalXpath), "Sub Group");
		} catch (Throwable e) {
		}
	}

	public static void keyTab(int counter) throws InterruptedException {
		Actions a = new Actions(driver);
		for (int i = 0; i < counter; i++) {
			Thread.sleep(1000);
			a.sendKeys(Keys.TAB).build().perform();
			log.info("Keys.TAB performed");
		}
	}

	public static void keyEnter(int counter) throws InterruptedException {
		Actions a = new Actions(driver);
		for (int i = 0; i < counter; i++) {
			Thread.sleep(1000);
			a.sendKeys(Keys.ENTER).build().perform();
			log.info("Keys.ENTER performed");
		}
	}

	public static void keyArrowDown(int counter) throws InterruptedException {
		Actions a = new Actions(driver);
		for (int i = 0; i < counter; i++) {
			Thread.sleep(1000);
			a.sendKeys(Keys.ARROW_DOWN).build().perform();
			log.info("Keys.ARROW_DOWN performed");
		}
	}

	public static void keyBackSpace(int counter) throws InterruptedException {
		Actions a = new Actions(driver);
		for (int i = 0; i < counter; i++) {
			// Thread.sleep(1000);
			a.sendKeys(Keys.BACK_SPACE).build().perform();
			log.info("Keys.ARROW_DOWN performed");
		}
	}

	public static void rightClick() throws InterruptedException {
		Actions a = new Actions(driver);

		try {
			a.moveByOffset(500, 700).build().perform();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(1000);
		a.contextClick().sendKeys(Keys.ARROW_DOWN).build().perform();
		log.info("Right click performed");
	}

	public static void keyEnter(String value) throws InterruptedException {
		Actions a = new Actions(driver);
		Thread.sleep(1000);
		a.sendKeys(value).build().perform();
		log.info("Keys.ENTER performed");
	}

	public static void keyEscape() throws InterruptedException {
		Actions a = new Actions(driver);
		Thread.sleep(1000);
		a.sendKeys(Keys.ESCAPE).build().perform();
		log.info("Keys.Escape performed");
	}

	public static void scrollTo(String finalXpath, String horScroll, String vertScroll) {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		try {
			js.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(finalXpath)));
			js.executeScript("window.scrollBy(" + horScroll + ", " + vertScroll + ")", "");
			log.info("scrolled to " + finalXpath + "::[" + vertScroll + ", " + horScroll + "]");
			isElementPresent(By.xpath(finalXpath), "Sub Group");
		} catch (Throwable e) {
		}
	}

	public static String addDaysToDate(String dateFormat, String days) {
		if (dateFormat == null || dateFormat.equals("") || dateFormat.contains("$")) {
			dateFormat = "dd/MM/yyyy";
			log.info("dateFormat:" + dateFormat);
		}
		if (days == null || days.equals("")) {
			days = "0";
			log.info("days:" + days);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Using today's date
		c.add(Calendar.DATE, Integer.parseInt(days)); // Adding 5 days
		String date = sdf.format(c.getTime());
		log.info("Date:" + date);
		return date;
	}

	public static String getJsonFieldExpectedValue(JsonNode validateActionValueNode) {
		String expectedValue = "";
		try {
			if (validateActionValueNode.isArray()) {

				for (JsonNode validateActionValueJson : validateActionValueNode) {
					String validationActionXpath = validateActionValueJson.fields().next().getKey();
					JsonNode validationActionXpathValue = validateActionValueJson.fields().next().getValue();

					log.info("validateActionValueJson:::\n" + validationActionXpath + "---"
							+ validationActionXpathValue);
					log.info("validationActionXpathValue::" + validationActionXpathValue);
					if (!validationActionXpathValue.asText().trim().isEmpty()) {
						expectedValue = validationActionXpathValue.asText();
					} else if (validationActionXpathValue.fields().next().getKey().contains("xpathparams")) {
						if (validationActionXpathValue.fields().next().getValue().isArray()) {
							for (int i = 0; i < validationActionXpathValue.fields().next().getValue().size(); i++) {
								JsonNode jsonNode = validationActionXpathValue.fields().next().getValue().get(i);
								expectedValue = jsonNode.asText();
							}
						}
					}
					log.info("Expected Value :: " + expectedValue);
				}
			}
		} catch (Exception e) {
		}
		return expectedValue;
	}

	public static String getJsonFieldValue(JsonNode validateActionValueNode, String fieldNameText) {
		String fieldValueText = null;
		try {
			if (validateActionValueNode.isArray()) {

				for (JsonNode validateActionValueJson : validateActionValueNode) {
					String validationActionXpath = validateActionValueJson.fields().next().getKey();
					JsonNode validationActionXpathValue = validateActionValueJson.fields().next().getValue();

					log.info("validateActionValueJson:::\n" + validationActionXpath + "---"
							+ validationActionXpathValue);
					log.info("validationActionXpathValue::" + validationActionXpathValue);

					if (validationActionXpathValue.isArray()) {
						for (JsonNode validateactionValueJson : validationActionXpathValue) {
							String validationactionXpath = validateactionValueJson.fields().next().getKey();
							JsonNode validationactionXpathValue = validateactionValueJson.fields().next().getValue();

							log.info("validationactionXpath:::\n" + validationactionXpath + "---"
									+ validationactionXpathValue);
							log.info("validationActionXpathValue::" + validationactionXpathValue);

							Iterator<Map.Entry<String, JsonNode>> fields = validationactionXpathValue.fields();

							while (fields.hasNext()) {
								Map.Entry<String, JsonNode> field = fields.next();
								String fieldName = field.getKey();
								JsonNode fieldValue = field.getValue();

								log.info("fields.hasNext()::" + fieldName + " = " + fieldValue.asText());
								if (fieldName.equals(fieldNameText))
									fieldValueText = fieldValue.asText();
							}
						}
					} else {
						Iterator<Map.Entry<String, JsonNode>> fields = validationActionXpathValue.fields();

						while (fields.hasNext()) {
							Map.Entry<String, JsonNode> field = fields.next();
							String fieldName = field.getKey();
							JsonNode fieldValue = field.getValue();

							log.info("fields.hasNext()::" + fieldName + " = " + fieldValue.asText());
							if (fieldName.equals(fieldNameText))
								fieldValueText = fieldValue.asText();
						}
					}

					log.info("fieldValueText::" + fieldValueText);
				}
			} else {
				Iterator<Map.Entry<String, JsonNode>> fields = validateActionValueNode.fields();

				while (fields.hasNext()) {
					Map.Entry<String, JsonNode> field = fields.next();
					String fieldName = field.getKey();
					JsonNode fieldValue = field.getValue();

					log.info("fields.hasNext()::" + fieldName + " = " + fieldValue.asText());
					if (fieldName.equals(fieldNameText))
						fieldValueText = fieldValue.asText();
				}
			}
		} catch (Exception e) {
		}
		return fieldValueText;
	}

	public static String getJsonFinalXpath(JsonNode navActionParams, boolean getXpath) throws Throwable {
		String finalXpath = null, keyValue;
		String resultXpath = "";
		// TODO: Parse JsonNode and invoke isDisplayed method in the driver class.
		try {
			if (navActionParams.isArray()) {
				int counter = 1;
				for (JsonNode validateActionValueJson : navActionParams) {
					log.info(" for loop iterating " + (counter++));
					String validationActionXpath = validateActionValueJson.fields().next().getKey();
					JsonNode validationActionXpathValue = validateActionValueJson.fields().next().getValue();

					log.info("clickProductNavActionValueJson:::\n" + validationActionXpath + "---"
							+ validationActionXpathValue);

					finalXpath = getXPathValueFromProperty(validationActionXpath);
					keyValue = getXPathKeyValueFromJSON(validateActionValueJson, validationActionXpath);
					if (validationActionXpathValue.isEmpty()) {
						if (getXpath) {
							resultXpath += finalXpath;
						} else {
							resultXpath = finalXpath;
						}
					}
					if (!validationActionXpathValue.asText().trim().isEmpty()) {
						if (getXpath) {
							resultXpath += generateDynamicXpath(finalXpath, keyValue);
						} else {
							resultXpath = generateDynamicXpath(finalXpath, keyValue);
						}
					} else if ((validationActionXpathValue.fields().hasNext())
							&& (validationActionXpathValue.fields().next().getKey().contains("xpathparams"))) {
						if (validationActionXpathValue.fields().next().getValue().isArray()) {

							List<String> array = new ArrayList<String>();
							for (int i = 0; i < validationActionXpathValue.fields().next().getValue().size(); i++) {

								JsonNode jsonNode = validationActionXpathValue.fields().next().getValue().get(i);
								array.add(jsonNode.asText());
							}
							Object[] objArr = array.toArray();
							if (getXpath) {
								resultXpath += generateDynamicXpath(finalXpath, null, objArr);
							} else {
								resultXpath = generateDynamicXpath(finalXpath, null, objArr);
							}
						}
					}
				}
			}
			log.info("finalXpath::" + resultXpath);
		} catch (Exception e) {
			log.info("got exception while getting finalXpath : " + e.getMessage());
		}
		return resultXpath;
	}

	/**
	 * Methods added by Mohan Kumar
	 * 
	 */
	public static String getXPathKeyFromJSON(JsonNode navActionParams) {
		String xpathKey = navActionParams.fieldNames().next();
		return xpathKey;
	}

	public static String getXPathKeyValueFromJSON(JsonNode navActionParams, String xpathKey) {
		String xpathKeyValue = navActionParams.get(xpathKey).asText();
		return xpathKeyValue;
	}

	public static String getXPathValueFromProperty(String xpathKey) {
		String finalXpath = (String) locatorsProp.get(xpathKey);
		return finalXpath;
	}

	public static String generateDynamicXpath(String xpath, String file, Object... params) {
		if (xpath != null) {
			log.info(xpath + "-" + params.length);
			boolean doubleQuote = false;
			boolean hashKey = false;
			List<String> paramsList = new ArrayList<String>();
			Collections.addAll(paramsList, params);
			for (int i = 0; i < params.length; i++) {
				if (paramsList.get(i).contains("\"")) {
					doubleQuote = true;
				}
				if (paramsList.get(i).contains("#")) {
					if (variables.get(paramsList.get(i).replace("#", "")) != null) {
						paramsList.set(i, variables.get(paramsList.get(i).replace("#", "")));
					} else if (testData != null && testData.get(paramsList.get(i).replace("#", "")) != null) {
						paramsList.set(i, testData.get(paramsList.get(i).replace("#", "")));
					} else if (jsonsInputMap != null && jsonsInputMap.get(paramsList.get(i).replace("#", "")) != null) {
						paramsList.set(i, jsonsInputMap.get(paramsList.get(i).replace("#", "")));
					} else if (file != null && inputMap != null
							&& inputMap.get(file.split("/")[1]).get(paramsList.get(i).replace("#", "")) != null) {
						paramsList.set(i, inputMap.get(file.split("/")[1]).get(paramsList.get(i).replace("#", "")));
					} else if (file != null && inputMap != null
							&& inputMap.get(file).get(paramsList.get(i).replace("#", "")) != null) {
						paramsList.set(i, inputMap.get(file).get(paramsList.get(i).replace("#", "")));
					}
					log.info("Hash Variable" + paramsList.get(i));
					hashKey = true;
					doubleQuote = true;
				}
			}

			if (!doubleQuote)
				xpath = MessageFormat.format(xpath, params);
			else {
				for (int i = 0; i < params.length; i++) {
					xpath = xpath.replace("\"{" + i + "}\"", "\'" + paramsList.get(i) + "\'");
					System.out
							.println("Replacing::" + "\"{" + i + "}\"" + "::with::" + "\'" + paramsList.get(i) + "\'");
					log.info("locator::" + xpath);
				}
			}
		}

		return xpath;
	}

	public static String getFinalXPathFromXPathParams(JsonNode validationActionXpathValue, String finalXpath) {
		if (validationActionXpathValue.fields().next().getValue().isArray()) {
			List<String> array = new ArrayList<String>();
			for (int i = 0; i < validationActionXpathValue.fields().next().getValue().size(); i++) {
				JsonNode jsonNode = validationActionXpathValue.fields().next().getValue().get(i);
				array.add(jsonNode.asText());
			}
			Object[] objArr = array.toArray();
			finalXpath = generateDynamicXpath(finalXpath, null, objArr);
		}
		return finalXpath;
	}

	public static List<String> getExpectedStringList(JsonNode validationActionXpathValue) {
		List<String> compareListArray = new ArrayList<String>();
		log.info("size of expected string list :: " + validationActionXpathValue.size());
		if (validationActionXpathValue.isArray()) {
			for (int i = 0; i < validationActionXpathValue.size(); i++) {
				JsonNode jsonNode = validationActionXpathValue.get(i);
				compareListArray.add(jsonNode.asText());
			}
		}
		return compareListArray;
	}

	public static String getValidateStringFromXPathParams(JsonNode validationActionXpathValue) {
		String expectedValue = "";
		if (validationActionXpathValue.fields().next().getValue().isArray()) {
			for (int i = 0; i < validationActionXpathValue.fields().next().getValue().size(); i++) {
				JsonNode jsonNode = validationActionXpathValue.fields().next().getValue().get(i);
				expectedValue = jsonNode.asText();
			}
		}
		return expectedValue;
	}

	public static String getValidationActionXPathValue(JsonNode validationActionXpathValue, String finalXpath,
			String keyValue) {
		finalXpath = generateDynamicXpath(finalXpath, keyValue);
		return finalXpath;
	}

	/**
	 * Method to verify two lists and return differences
	 * 
	 * @param actual
	 * @param expected
	 * @return List<String>
	 */
	public static List<String> assertTwoListValues(List<String> actual, List<String> expected) {
		log.info("Asserion expected values are-->" + expected);
		log.info("Asserion actual values are-->" + actual);
		List<String> differences = expected.stream().filter(element -> !actual.contains(element))
				.collect(Collectors.toList());
		return differences;
	}

	/**
	 * Method to verify two lists with order of insertion
	 * 
	 * @param actual
	 * @param expected
	 * @param orderRequired
	 * @return boolean
	 */
	public static boolean assertTwoListValues(List<String> actual, List<String> expected, boolean orderRequired) {
		log.info("Asserion expected values are-->" + expected);
		log.info("Asserion actual values are-->" + actual);
		if (actual.size() >= expected.size() && orderRequired) {
			for (int i = 0; i < expected.size(); i++) {
				if (!expected.get(i).trim().equals(actual.get(i).trim())) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public static TestEnsureRequest getText(JsonNode navActionParams, String finalXpath) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		navActionParams = navActionParams.get(0).fields().next().getValue();
		log.info("navActionParams.fields()" + navActionParams.toString());
		if (navActionParams.get("varName") != null) {
			String variable = navActionParams.get("varName").asText();
			String replace = null;
			String clipboard = null;
			try {
				replace = navActionParams.get("replace").asText();
			} catch (Exception e) {
			}
			try {
				clipboard = navActionParams.get("copyClipboard").asText();
			} catch (Exception e) {
			}
			String variableValue = null;
			if (finalXpath.contains("input")) {
				variableValue = driver.findElement(By.xpath(finalXpath)).getAttribute("value").toString();
			} else if (finalXpath.contains("label") || finalXpath.contains("td") || finalXpath.contains("div")
					|| finalXpath.contains("id")) {
				variableValue = driver.findElement(By.xpath(finalXpath)).getText();
			} else if (finalXpath.contains("span")) {
				variableValue = driver.findElement(By.xpath(finalXpath)).getAttribute("innerHTML");
			} else {
				log.info("unable to get text");
			}
			if (variableValue != null && replace != null) {
				variableValue = variableValue.replace(replace, "").trim();
			}
			log.info("variableValue" + variableValue);
			variables.put(variable, variableValue);
			log.info("$$$$$$$$$$$$$$$ variables $$$$$$$$$$$$$$$");
			for (String varName : variables.keySet()) {
				log.info("varName::" + varName + ", variableValue::" + variables.get(varName));
			}
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			if (variableValue != null && variableValue.contains("Showing")) {
				String[] str = variableValue.split("of");
				log.info("Split value" + str[1]);
				String[] str2 = str[1].split("entries");
				log.info("single variable--->" + variable);
				log.info("Split value 2---->" + str2[0]);
				variables.put(variable, str2[0]);
				log.info("Variables---->" + variables.toString());
			}
		}
		return testEnsureRequest;
	}

	public static void executeFinalTestNodes(List<TestNode> testNodes, String file) throws Throwable {
		List<TestEnsureRequest> testEnsureRequests = null;
		String firstTestCaseID = testNodes.get(0).getTestCaseID();
		log.debug("firstTestCaseID::" + firstTestCaseID);
		boolean skipTestCase = false;
		IssueEnum issueType = null;
		// TestBase.testNodes = testNodes;
		for (TestNode testNode : testNodes) {
			manualTestCase = false;
			durationSeconds = 0;
			String testId = testNode.get_id();
			if (testStepStatus)
				startTime = LocalDateTime.now();
			// String testCaseStepId = null;
			log.info("Start time before executing UI context of testCase:" + testNode.getTestCaseID() + "is::"
					+ startTime);

			Maybe<String> testRs = ReportPortalUtil.standardStartTestRequest(suiteRs, testNode.getTestCaseID(),
					testNode.getTestCaseID() + "_" + testNode.getTestCaseTitle(), testNode.getTestCaseDescription(),
					"STEP");
			testEnsureRequests = new ArrayList<TestEnsureRequest>();
			// Step1: Get all the testCaseSteps
			List<JsonNode> testCaseSteps = testNode.getTestCaseSteps();
			String testCaseId = testNode.getTestCaseID();
			ItemStatus dependsOn = testEnsureRequestStatusMap.get(testNode.getDependsOn());
			log.info("+++++TEstcaseID++++" + testCaseId);
			log.info("+++++DependsON++++++" + testNode.getDependsOn());
			log.info("+++++DependsON Status++++++" + dependsOn);
			log.debug("continueTestNodes" + continueTestNodes);
			if ((dependsOn != null && dependsOn != ItemStatus.FAILED) || testCaseId.equals(firstTestCaseID)
					|| testNode.getDependsOn().equals(ParsingConstants.MANUAL) || continueTestNodes) {
				// Step2: Process testCaseSteps
				log.debug("testStepStatus" + testStepStatus);
				log.debug("continueTestNodes" + continueTestNodes);
				log.debug("testCaseFailed" + testCaseFailed);
				testStepStatus = true;
				continueTestNodes = false;
				testCaseFailed = false;
				deleteScreenshotFile = true;
				for (JsonNode testCaseStep : testCaseSteps) {
					try {
						log.info(
								"testStepStatus::" + testStepStatus + "::continueTestNodes::" + continueTestNodes);
						PrintWriter pw = new PrintWriter(
								System.getProperty("user.dir") + envRelativePath("\\logs\\") + "TestEnsureTest.log");
						pw.close();

						Iterator<Entry<String, JsonNode>> fields = testCaseStep.fields();
						fields.hasNext();
						Entry<String, JsonNode> firstElement = fields.next();
						fields.hasNext();
						Entry<String, JsonNode> secondElement = fields.next();

						JSONObject jsonObj = new JSONObject();
						jsonObj.put(secondElement.getKey(), secondElement.getValue());

						testCaseStepId = firstElement.getValue().asText();
						String testCaseStepName = secondElement.getKey();
						JsonNode testStepFileds = objectMapper.readTree(jsonObj.toString());
						String testStepDescription = getJsonFieldValue(testStepFileds, "testStepDescription");
						if (testStepDescription == null) {
							log.info("testCaseStep.get(ParsingConstants.VALIDATE)"
									+ testCaseStep.get(ParsingConstants.VALIDATE));
							testStepDescription = getJsonFieldValue(testCaseStep.get(ParsingConstants.VALIDATE),
									"testStepDescription");
						}
						String content = getJsonFieldValue(testStepFileds, "expected");
						// Maybe<String> stepRs = ReportPortalUtil.standardStartTestRequest(testRs,
						// testCaseStepName, (testStepDescription!= null)? testStepDescription:
						// "testStepDescription comes here", "STEP");
						if (testStepStatus || continueTestNodes || testCaseStepName.equals(ParsingConstants.MANUAL)) {
							// log = Logger.getLogger(TestBase.class);
							testEnsureRequests = processTestCaseStep(testStepFileds, file, testCaseStepId);
							if (testEnsureRequests != null && testEnsureRequests.size() != 0) {
								List<TestEnsureRequest> tempTestEnsureRequests = new ArrayList<>();
								for (TestEnsureRequest testEnsureRequest : testEnsureRequests) {
									if (testEnsureRequest != null)
										tempTestEnsureRequests.add(testEnsureRequest);
								}
								log.info("+++++++++++++++++++++++++++++++++++++++++++");
								log.info("testEnsureRequests" + testEnsureRequests.toString());
								log.info("tempTestEnsureRequests" + tempTestEnsureRequests.toString());
								log.info("+++++++++++++++++++++++++++++++++++++++++++");
								testEnsureRequests = tempTestEnsureRequests;
								// addLogsAndScreenhots("fileName");
								// addVideo("C:\\Users\\gowtham.murikipudi\\Videos\\sample.mp4");
								// log.info("testensurerequests"+testEnsureRequests.toString());
								for (TestEnsureRequest testEnsureRequest : testEnsureRequests) {
									log.info("$$$$$$$$$$$ updating item status $$$$$$$$$$$$$$$");
									log.info(testEnsureRequest.toString());
									if (testEnsureRequest.getItemStatus() != null) {
										// ReportPortalUtil.finishItemRequest(stepRs,
										// testEnsureRequest.getItemStatus());
										if (testNode.getDependsOn().equals(ParsingConstants.MANUAL)) {
											ReportPortalUtil.addTestStep(testEnsureRequest, testStepDescription,
													testCaseStepName, content, moduleId, testId, testCaseStepId);
										} else
											ReportPortalUtil.addTestStep(testEnsureRequest, testStepDescription,
													testCaseStepName, moduleId, testId, testCaseStepId);
										issueType = testEnsureRequest.getIssueType();
										if (testEnsureRequest.getItemStatus().equals(ItemStatus.PASSED)) {
											testStepStatus = true;
											testCaseFailed = false;
										} else {
											testStepStatus = false;
											testCaseFailed = true;
										}
									}
									log.info("$$$$$$$$$$$ updating item status finished $$$$$$$$$$$$$$$");
								}
							}
							ReportPortalUtil.updateReportPortalData();
						} else {
							// ReportPortal.emitLog("Test Step skipped due to previous test step Failure",
							// "INFO", Calendar.getInstance().getTime());
							// ReportPortalUtil.finishItemRequest(stepRs, ItemStatus.SKIPPED);
							ReportPortalUtil.addSkippedTestStep(testCaseStepName);
						}
					} catch (Exception e) {

					}
					deleteScreenshotFile = false;
				}
			} else {
				log.info("skip statement");
				ReportPortal.emitLog("Test Case skipped due to dependant test case Failure", "INFO",
						Calendar.getInstance().getTime());
				ReportPortalUtil.finishItemRequest(testRs, ItemStatus.SKIPPED, IssueEnum.NO_DEFECT);
				skipTestCase = true;
			}

			endTime = LocalDateTime.now();

			Duration duration = Duration.between(startTime, endTime); // Total execution time in milli seconds
			durationSeconds = dateFormatUtil.getDurationEpoch(duration);
			Duration moduleDuration = Duration.between(moduleStartTime, endTime);
			moduleDurationSec = dateFormatUtil.getDurationEpoch(moduleDuration);
			jobDurationSec = dateFormatUtil.getDurationEpoch(moduleDuration);
			Duration jobDuration = Duration.between(jobStartTime, endTime);
			jobDurationSec = dateFormatUtil.getDurationEpoch(jobDuration);
			log.info("durationSeconds " + durationSeconds + " " + duration.getSeconds());
			log.info("jobDurationSeconds " + jobDurationSec + " " + jobDuration.getSeconds());
			log.info("Time take for executing UI context of testCase:" + testNode.getTestCaseID() + "is::" + duration);

			log.info("****************************************");
			log.info("testStepStatus :: " + testStepStatus);
			log.info("testCaseFailed :: " + testCaseFailed);
			log.info("testSuiteFailed :: " + testSuiteFailed);
			log.info("****************************************");

			testEnsureRequestStatusMap.put(testCaseId, (testStepStatus) ? ItemStatus.PASSED : ItemStatus.FAILED);

			log.info("skipTestCase testfailedstatus::" + testCaseFailed);
			log.info("testStepStatus ::" + testStepStatus);
			log.info("manualTestCase ::" + manualTestCase);
			if (manualTestCase) {
				log.info(testNode.getTestCaseID() + " => manual test case");
				ReportPortalUtil.manualFinishRequest(testNode.getTestCaseID(), testRs);
			} else if (!skipTestCase && testStepStatus && !testCaseFailed) {
				ReportPortalUtil.positiveFinishRequest(testNode.getTestCaseID(), testRs);
			} else if (!skipTestCase && !testStepStatus && testCaseFailed) {
				if (issueType == null)
					issueType = IssueEnum.TO_INVESTIAGE;
				ReportPortalUtil.finishItemRequest(testRs, ItemStatus.FAILED, issueType);
				testSuiteFailed = true;
			}

			log.info("startTime => " + startTime + ", endTime => " + endTime + ", duration => " + duration
					+ ", durationSeconds = " + durationSeconds);
			StatusUpdater.updateStatus(runID, testCaseStepId, testCaseFailed ? "FAILED" : "PASSED", moduleId,
					testCaseId, null);
		}

	}

	/**
	 * Processes a single testCaseStep from the {@link TestNode}
	 * 
	 * @param testCaseStep
	 * @return
	 * @throws Throwable
	 */
	public static List<TestEnsureRequest> processTestCaseStep(JsonNode testCaseStep, String file, String testCaseStepId)
			throws Throwable {
		// log.debug("Processing testCaseStep::::::::\n" +
		// testCaseStep.toPrettyString());
		// Process TestCaseStep
		List<TestEnsureRequest> testEnsureRequests = null;
		Iterator<String> fieldsIterator = testCaseStep.fieldNames();
		if (!fieldsIterator.hasNext()) {
			log.debug("Empty TestCaseStep, or invalid testCaseStep Node, hence skipping this node:\n"
					+ testCaseStep.toPrettyString());
		} else {
			String fieldName = fieldsIterator.next();
			// Validate if there is only a single field. report bad format otherwise.

			if (fieldsIterator.hasNext()) {
				throw new JsonAutomationFormatIssue("Multiple fields not allowed in a navAction/validate JsonNode :\n"
						+ testCaseStep.toPrettyString());
			}

			// Process validate node.
			if (fieldName.equals(ParsingConstants.VALIDATE)) {
				testEnsureRequests = processValidationNode(testCaseStep.get(ParsingConstants.VALIDATE), file,
						testCaseStepId);
			} else if (fieldName.equals(ParsingConstants.LOOP)) {
				testEnsureRequests = processLoopingActionNode(testCaseStep.get(ParsingConstants.LOOP), file,
						testCaseStepId);
			} else if (fieldName.equals(ParsingConstants.MANUAL)) {
				testEnsureRequests = processManulActionNode(testCaseStep, file, testCaseStepId);
			} else {
				// Process navAction Node.
				testEnsureRequests = processNavigationActionNode(testCaseStep, file, testCaseStepId);
			}
		}
		return testEnsureRequests;
	}

	/*
	 * @param testCaseStep
	 * 
	 * @throws Throwable
	 */
	public static List<TestEnsureRequest> processNavigationActionNode(JsonNode navActionNode, String file,
			String testCaseStepId) throws Throwable {
		// Process navActionNode
		List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
		TestEnsureRequest testEnsureRequest = null;
		log.debug("Processing navActionNode:::::\n" + navActionNode.toPrettyString());
		String navAction = navActionNode.fields().next().getKey();
		JsonNode navActionValue = navActionNode.fields().next().getValue();
		testEnsureRequest = NavigationActions.delegateNavAction(navAction, navActionValue, file, testCaseStepId);
		testEnsureRequests.add(testEnsureRequest);
		return testEnsureRequests;

	}

	/*
	 * @param testCaseStep
	 * 
	 * @throws Throwable
	 */
	public static List<TestEnsureRequest> processLoopingActionNode(JsonNode navActionNode, String file,
			String testCaseStepId) throws Throwable {
		// Process navActionNode
		List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
		TestEnsureRequest testEnsureRequest = null;
		log.debug("Processing loopActionNode:::::\n" + navActionNode.toPrettyString());
		String loopAction = navActionNode.fields().next().getKey();
		JsonNode loopActionValue = navActionNode.fields().next().getValue();
		testEnsureRequest = LoopingActions.delegateLoopAction(loopAction, loopActionValue, file, testCaseStepId);
		testEnsureRequests.add(testEnsureRequest);
		return testEnsureRequests;

	}

	/*
	 * @param testCaseStep
	 * 
	 * @throws Throwable
	 */
	public static List<TestEnsureRequest> processManulActionNode(JsonNode manualActionNode, String file,
			String testCaseStepId) throws Throwable {
		// Process navActionNode
		List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
		TestEnsureRequest testEnsureRequest = null;
		log.debug("Processing navActionNode:::::\n" + manualActionNode.toPrettyString());
		String manualAction = manualActionNode.fields().next().getKey();
		JsonNode manualActionValue = manualActionNode.fields().next().getValue();
		testEnsureRequest = ManualActions.delegateManualAction(manualAction, manualActionNode, file, testCaseStepId);
		testEnsureRequests.add(testEnsureRequest);
		return testEnsureRequests;

	}

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
	public static List<TestEnsureRequest> processValidationNode(JsonNode listJsonNode, String file,
			String testCaseStepId) throws Throwable {
		// Process validateActionNode
		List<TestEnsureRequest> testEnsureRequests = new ArrayList<>();
		TestEnsureRequest testEnsureRequest = null;
		ObjectMapper mapper = new ObjectMapper();
		log.info("Processing validateActionNode:::::\n" + listJsonNode.toPrettyString());
		try {
			for (JsonNode jsonNodeData : listJsonNode) {
				log.info("jsonNodeData" + jsonNodeData);
				String validateAction = jsonNodeData.fields().next().getKey();
				JsonNode validateActionValue = jsonNodeData.fields().next().getValue();
				log.info("validateActionValue" + validateActionValue);
				log.info("validateActionValue" + validateActionValue.size());

				/**
				 * <pre>
				 * { "isElementPresent":
						[
							{"fn_configurable_products":{"xpathparams":[""], "fileName" : "FN_Configurable_Products", "bugTitle" : "Unable to navigate to FN Configure Products"}}									
						]
					},
				 * </pre>
				 */
				// Expand and iterate over the validation mode in case of mutiple validations
				for (JsonNode jsonNode : validateActionValue) {
					log.info("jsonNode1" + jsonNode);
					String tempString = "[" + jsonNode + "]";
					jsonNode = mapper.readTree(tempString);
					log.info("tempJsonNode" + jsonNode);
					testEnsureRequest = ValidateAction.delegateValidateAction(validateAction, jsonNode, file,
							testCaseStepId);
					testEnsureRequests.add(testEnsureRequest);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return testEnsureRequests;
	}
}
