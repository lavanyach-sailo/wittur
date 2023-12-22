package com.sailotech.testautomation.util;

import static io.restassured.RestAssured.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.epam.reportportal.listeners.ItemStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.beans.TestCase;
import com.sailotech.testautomation.beans.TestProject;
import com.sailotech.testautomation.beans.TestStep;
import com.sailotech.testautomation.beans.TestSuite;
import com.sailotech.testautomation.database.StatusUpdater;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RestAPIUtil extends TestBase {

	// This will fetch the response body as is and log it. given and when are
	// optional here
	public static String generateLaunchName(String endpoint, String bearerToken, String project, String launch,
			int numberFormat, String delimeter) {
		String latestLaunchName = null;
		String launchName = null;
		String url = endpoint + "/api/v1/" + project + "/launch/latest?page.sort=desc";
		try {
			Response response = given().headers("Authorization", "Bearer " + bearerToken, "Content-Type",
					ContentType.JSON, "Accept", ContentType.JSON).when().get(url).then().contentType(ContentType.JSON)
					.extract().response();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.asString());
			Iterator<JsonNode> jsonNodes = root.get("content").elements();
			while (jsonNodes.hasNext()) {
				JsonNode jsonNode = jsonNodes.next();
				if (jsonNode.get("name").asText().contains(launch)) {
					latestLaunchName = jsonNode.get("name").asText();
				}
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		log.info("Latest Launch Name::" + latestLaunchName);
		if (latestLaunchName == null || !latestLaunchName.contains(launch) || !latestLaunchName.contains(delimeter)) {
			launchName = launch + "-" + String.format("%0" + numberFormat + "d", 1);
		} else {
			launchName = launch + "-" + String.format("%0" + numberFormat + "d",
					Integer.parseInt(latestLaunchName.split(delimeter)[latestLaunchName.split(delimeter).length - 1])
							+ 1);
		}
		log.info("New launchName::" + launchName);

		return launchName;
	}

	public static void postLaunchDataByUUid(String endpoint, String bearerToken, String project, String uuid) {
		String url = endpoint + "/api/v1/" + project + "/launch/uuid/" + uuid;

		log.info("url::" + url);

		try {
			Response response = given().headers("Authorization", "Bearer " + bearerToken, "Content-Type",
					ContentType.JSON, "Accept", ContentType.JSON).when().get(url).then().contentType(ContentType.JSON)
					.extract().response();
			ObjectMapper mapper = new ObjectMapper();
			// Thread.sleep(5000);
			JsonNode jsonNode = mapper.readTree(response.asString());
			String id = jsonNode.get("id").asText();
			String name = jsonNode.get("name").asText();
			String number = jsonNode.get("number").asText();
			String startTime = jsonNode.get("startTime").asText();
			String status = jsonNode.get("status").asText();
			// String endTime = jsonNode.get("endTime").asText();
			log.info("id " + id + "::" + "uuid " + uuid + "::" + "name " + name + "::" + "number " + number
					+ "::" + "startTime " + startTime + "::" + "endTime::");
			String jobURL = endpoint + "/ui/#" + project + "/launches/all/" + id;
			StatusUpdater.saveJobURL(runID, jobURL, null);
			/*
			 * if ((jenkinsBuildNumber != null && releaseID != null && !releaseID.isEmpty())
			 * || (runID != null && !runID.isEmpty())) {
			 * MongoDBUtil.insertReportPortalData(mongodbProp.getProperty(
			 * "reportPortalCollection"), project, id, uuid, name, number, startTime,
			 * status); }
			 */
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void postLaunchDataByUUid(String endpoint, String bearerToken, String project, String uuid,
			String launchId) {
		String url = endpoint + "/api/v1/" + project + "/launch/uuid/" + uuid;

		log.info("url::" + url);

		try {
			if (endpoint != null) {
				Response response = given()
						.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
								ContentType.JSON)
						.when().get(url).then().contentType(ContentType.JSON).extract().response();
				ObjectMapper mapper = new ObjectMapper();
				log.info("response" + response.toString());
				// Thread.sleep(5000);
				log.info(response.asString());
				JsonNode launchJsonNode = mapper.readTree(response.asString());
				launchId = launchJsonNode.get("id").asText();
				url = endpoint + "/api/v1/" + project + "/item?filter.eq.launchId=" + launchId;
				response = given()
						.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
								ContentType.JSON)
						.when().get(url).then().contentType(ContentType.JSON).extract().response();
				log.info("response" + response.asString());
				JsonNode jsonNodes = mapper.readTree(response.asString());
				int pages = Integer.parseInt(jsonNodes.get("page").get("totalPages").asText());
				log.info("No of Pages::" + pages);

				List<TestProject> testProjects = new ArrayList<TestProject>();
				List<TestSuite> testSuites = new ArrayList<TestSuite>();
				// List<TestCase> testCases = new ArrayList<TestCase>();
				List<TestStep> testSteps = new ArrayList<TestStep>();

				for (int page = 1; page <= pages; page++) {
					String newUrl = url + "&page.page=" + page;
					Response pageResponse = given()
							.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON,
									"Accept", ContentType.JSON)
							.when().get(newUrl).then().contentType(ContentType.JSON).extract().response();
					jsonNodes = mapper.readTree(pageResponse.asString()).get("content");
					// log.info("jsonNodes::"+jsonNodes);
					for (JsonNode jsonNode : jsonNodes) {
						try {
							log.info("jsonNode" + jsonNode);
							String id = null;
							boolean suiteCheck = false;
							Iterator<String> fields = jsonNode.get("pathNames").fieldNames();

							while (fields.hasNext()) {
								String fieldName = fields.next();
								if (fieldName.equals("itemPaths")) {
									suiteCheck = true;
								}
							}
							if (jsonNode.get("type").asText().equals("SUITE") && !suiteCheck) {
								TestProject testProject = new TestProject();
								JsonNode statistics = jsonNode.get("statistics").get("executions");
								testProject.setDescription(jsonNode.get("description").asText());
								try {
									testProject.setEndTime(jsonNode.get("endTime").asText());
								} catch (Exception e) {
								}
								try {
									testProject.setFailed(statistics.get("failed").asText());
								} catch (Exception e) {
								}
								try {
									testProject.setSkipped(statistics.get("skipped").asText());
								} catch (Exception e) {
								}
								testProject.setId(jsonNode.get("id").asText());
								testProject.setLaunchId(jsonNode.get("launchId").asText());
								testProject.setName(jsonNode.get("name").asText());
								try {
									testProject.setPassed(statistics.get("passed").asText());
								} catch (Exception e) {
								}
								testProject.setStartTime(jsonNode.get("startTime").asText());
								testProject.setStatus(jsonNode.get("status").asText());
								try {
									testProject.setTotal(statistics.get("total").asText());
								} catch (Exception e) {
								}
								testProject.setUuid(jsonNode.get("uuid").asText());
								// testProject.setParent(jsonNode.get("parent").asText());
								testProjects.add(testProject);
							} else if (jsonNode.get("type").asText().equals("SUITE") && suiteCheck) {
								TestSuite testSuite = new TestSuite();
								JsonNode statistics = null;
								try {
									statistics = jsonNode.get("statistics").get("executions");
								} catch (Exception e) {
								}
								testSuite.setDescription(jsonNode.get("description").asText());
								try {
									testSuite.setEndTime(jsonNode.get("endTime").asText());
								} catch (Exception e) {
								}
								try {
									testSuite.setFailed(statistics.get("failed").asText());
								} catch (Exception e) {
								}
								try {
									testSuite.setSkipped(statistics.get("skipped").asText());
								} catch (Exception e) {
								}
								testSuite.setId(jsonNode.get("id").asText());
								testSuite.setLaunchId(jsonNode.get("launchId").asText());
								testSuite.setName(jsonNode.get("name").asText());
								try {
									testSuite.setPassed(statistics.get("passed").asText());
								} catch (Exception e) {
								}
								testSuite.setStartTime(jsonNode.get("startTime").asText());
								testSuite.setStatus(jsonNode.get("status").asText());
								try {
									testSuite.setTotal(statistics.get("total").asText());
								} catch (Exception e) {
								}
								testSuite.setUuid(jsonNode.get("uuid").asText());
								testSuite.setParent(jsonNode.get("parent").asText());
								for (TestProject testProject : testProjects) {
									if (testProject.getId().equals(testSuite.getParent())) {
										testSuites = testProject.getTestSuites();
										if (testSuites == null) {
											testSuites = new ArrayList<TestSuite>();
										}
										testSuites.add(testSuite);
										testProject.setTestSuites(testSuites);
									}
									log.info(testProject);
								}
							} else if (Boolean.valueOf(jsonNode.get("hasStats").asText())) {
								id = jsonNode.get("id").asText();
								TestCase testCase = new TestCase();
								testCase.setId(id);
								testCase.setDescription(jsonNode.get("description").asText());
								try {
									testCase.setEndTime(jsonNode.get("endTime").asText());
								} catch (Exception e) {
								}
								testCase.setLaunchID(launchId);
								testCase.setName(jsonNode.get("name").asText());
								testCase.setStartTime(jsonNode.get("startTime").asText());
								testCase.setStatus(jsonNode.get("status").asText());
								testCase.setParent(jsonNode.get("parent").asText());
								testCase.setTestCaseId(jsonNode.get("testCaseId").asText());
								testCase.setUuid(jsonNode.get("uuid").asText());
								// testCases.add(testCase);
								for (TestSuite testSuite : testSuites) {
									if (testSuite.getId().equals(testCase.getParent())) {
										List<TestCase> testCases = testSuite.getTestCases();
										if (testCases == null) {
											testCases = new ArrayList<TestCase>();
										}
										testCases.add(testCase);
										testSuite.setTestCases(testCases);
									} else {
									}
								}
							} else {
								try {
									TestStep testStep = new TestStep();
									testStep.setEndTime(jsonNode.get("endTime").asText());
									testStep.setId(jsonNode.get("id").asText());
									testStep.setName(jsonNode.get("name").asText());
									testStep.setStartTime(jsonNode.get("startTime").asText());
									testStep.setStatus(jsonNode.get("status").asText());
									testStep.setTestCaseID(jsonNode.get("parent").asText());
									testStep.setUuid(jsonNode.get("uuid").asText());
									testSteps.add(testStep);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				for (TestSuite testSuite : testSuites) {
					List<TestCase> testCases = testSuite.getTestCases();
					for (TestCase testCase : testCases) {
						List<TestStep> steps = new ArrayList<TestStep>();
						for (TestStep testStep : testSteps) {
							if (testCase.getId().equals(testStep.getTestCaseID())) {
								steps.add(testStep);
							}
						}
						testCase.setTestSteps(steps);
					}
				}

				testResults.setTestProjects(testProjects);

				/*
				 * if ((jenkinsBuildNumber != null && releaseID != null && !releaseID.isEmpty())
				 * || (runID != null && !runID.isEmpty())) {
				 * MongoDBUtil.updateReportPortalData(testResults); }
				 */
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateLaunchDataByUUID(String endpoint, String bearerToken, String project, String uuid) {
		String status = null;
		String url = endpoint + "/api/v1/" + project + "/launch/uuid/" + uuid;

		log.info("url::" + url);

		if (uuid != null && !uuid.equals("")) {
			try {
				// Thread.sleep(5000);
				Response response = given()
						.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
								ContentType.JSON)
						.when().get(url).then().contentType(ContentType.JSON).extract().response();
				ObjectMapper mapper = new ObjectMapper();
				log.info(response.asString());
				JsonNode jsonNode = mapper.readTree(response.asString());
				String startTimestamp = jsonNode.get("startTime").asText();
				String endTimestamp = "";
				try {
					endTimestamp = jsonNode.get("endTime").asText();
				} catch (Exception e) {
				}
				String lastModified = jsonNode.get("lastModified").asText();
				status = jsonNode.get("status").asText();
				String approximateDuration = jsonNode.get("approximateDuration").asText();
				JsonNode statistics = jsonNode.get("statistics").get("executions");
				int total = 0;
				try {
					total = statistics.get("total").asInt();
				} catch (Exception e) {
				}
				int passed = 0;
				int failed = 0;
				int skipped = 0;
				try {
					// Thread.sleep(10000);
					log.info("approximateDuration" + approximateDuration);
					if (approximateDuration.equals("0.0")) {
						Date startTime = new Date(startTimestamp);
						log.info("startTime => " + startTime);

						Date endTime = new Date(endTimestamp);
						log.info("endTime => " + endTime);

						long approximate = startTime.getTime() - endTime.getTime();
						// approximateDuration = String.valueOf(approximate / (1000));
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (approximateDuration.equals("0.0")) {
						// SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm:ss");
						// // Parsing the Time Period
						// Date startTime = simpleDateFormat.parse(TestBase.startTime);
						// Date endTime = simpleDateFormat.parse(TestBase.endTime);
						// // Calculating the difference in milliseconds
						// long differenceInMilliSeconds = Math.abs(endTime.getTime() -
						// startTime.getTime());
						// int approx = (int) (differenceInMilliSeconds / (60 * 1000));
						// approximateDuration = Integer.toString(approx);
					}
				}
				try {
					passed = statistics.get("passed").asInt();
				} catch (Exception e) {
				}
				try {
					failed = statistics.get("failed").asInt();
				} catch (Exception e) {
				}
				try {
					skipped = statistics.get("skipped").asInt();
				} catch (Exception e) {
				}
				log.info("endTime " + endTimestamp + "::" + "uuid " + uuid + "::" + "lastModified "
						+ lastModified + "::" + "status " + status + "::" + "approximateDuration " + approximateDuration
						+ "::" + "total::" + total + "passed::" + passed + "failed::" + failed + "skipped::" + skipped);
				/*
				 * if ((jenkinsBuildNumber != null && releaseID != null && !releaseID.isEmpty())
				 * || (runID != null && !runID.isEmpty())) { if (TestBase.manualTestCase) status
				 * = "UNTESTED";
				 * MongoDBUtil.updateReportPortalDataByUUID(mongodbProp.getProperty(
				 * "reportPortalCollection"), uuid, endTimestamp, lastModified, status,
				 * approximateDuration, total, passed, failed, skipped); }
				 */
				// postLaunchDataByUUid(endpoint, bearerToken, project, uuid, launchID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateLaunchDataByID(String endpoint, String bearerToken, String project, String id) {
		String status = null;
		String url = endpoint + "/api/v1/" + project + "/launch/" + id;

		log.info("url::" + url);

		if (id != null && !id.equals("")) {
			try {
				Response response = given()
						.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
								ContentType.JSON)
						.when().get(url).then().contentType(ContentType.JSON).extract().response();
				ObjectMapper mapper = new ObjectMapper();
				// Thread.sleep(5000);
				log.info(response.asString());
				JsonNode jsonNode = mapper.readTree(response.asString());
				String startTimestamp = jsonNode.get("startTime").asText();
				String endTimestamp = "";
				try {
					endTimestamp = jsonNode.get("endTime").asText();
				} catch (Exception e) {
				}
				String lastModified = jsonNode.get("lastModified").asText();
				status = jsonNode.get("status").asText();
				String approximateDuration = jsonNode.get("approximateDuration").asText();
				JsonNode statistics = jsonNode.get("statistics").get("executions");
				int total = statistics.get("total").asInt();
				int passed = 0;
				int failed = 0;
				int skipped = 0;
				try {
					// Thread.sleep(10000);
					if (approximateDuration.equals("0.0")) {
						Date startTime = new Date(startTimestamp);
						log.info("startTime => " + startTime);

						Date endTime = new Date(endTimestamp);
						log.info("endTime => " + endTime);

						// long approximate = startTime.getTime() - endTime.getTime();
						// approximateDuration = String.valueOf(approximate / (1000));
					}
				} catch (Exception e) {
					e.printStackTrace();
					// SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm:ss");
					// // Parsing the Time Period
					// Date startTime = simpleDateFormat.parse(TestBase.startTime);
					// Date endTime = simpleDateFormat.parse(TestBase.endTime);
					// // Calculating the difference in milliseconds
					// long differenceInMilliSeconds = Math.abs(endTime.getTime() -
					// startTime.getTime());
					// int approx = (int) (differenceInMilliSeconds / (60 * 1000));
					// approximateDuration = Integer.toString(approx);
				}
				try {
					passed = statistics.get("passed").asInt();
				} catch (Exception e) {
				}
				try {
					failed = statistics.get("failed").asInt();
				} catch (Exception e) {
				}
				try {
					skipped = statistics.get("skipped").asInt();
				} catch (Exception e) {
				}
				log.info("endTime " + endTimestamp + "::" + "id " + id + "::" + "lastModified " + lastModified
						+ "::" + "status " + status + "::" + "approximateDuration " + approximateDuration + "::"
						+ "total::" + total + "passed::" + passed + "failed::" + failed + "skipped::" + skipped);
				/*
				 * if ((jenkinsBuildNumber != null && releaseID != null && !releaseID.isEmpty())
				 * || (runID != null && !runID.isEmpty())) { if (TestBase.manualTestCase) status
				 * = "UNTESTED"; MongoDBUtil.updateReportPortalDataByID(mongodbProp.getProperty(
				 * "reportPortalCollection"), id, endTimestamp, lastModified, status,
				 * approximateDuration, total, passed, failed, skipped); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Map<String, Map<String, String>> getLaunchRerunDetails(String endpoint, String bearerToken,
			String project, String launchId) {
		String testCaseId = null;
		String id = null;
		String status = null;
		Map<String, Map<String, String>> results = new HashMap<String, Map<String, String>>();
		String url = endpoint + "/api/v1/" + project + "/item?filter.eq.launchId=" + launchId;

		log.info("url::" + url);

		try {
			Response response = given().headers("Authorization", "Bearer " + bearerToken, "Content-Type",
					ContentType.JSON, "Accept", ContentType.JSON).when().get(url).then().contentType(ContentType.JSON)
					.extract().response();
			ObjectMapper mapper = new ObjectMapper();
			// Thread.sleep(5000);
			log.info(response.asString());
			JsonNode jsonNodes = mapper.readTree(response.asString());
			int pages = Integer.parseInt(jsonNodes.get("page").get("totalPages").asText());
			log.info("No of Pages::" + pages);
			for (int page = 1; page <= pages; page++) {
				String newUrl = url + "&page.page=" + page;
				Response pageResponse = given()
						.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
								ContentType.JSON)
						.when().get(newUrl).then().contentType(ContentType.JSON).extract().response();
				jsonNodes = mapper.readTree(pageResponse.asString()).get("content");
				// log.info("jsonNodes::"+jsonNodes);
				for (JsonNode jsonNode : jsonNodes) {
					try {
						testCaseId = jsonNode.get("testCaseId").asText();
						status = jsonNode.get("status").asText();
						id = jsonNode.get("id").asText();
					} catch (Exception e) {
					}
					/*
					 * if (testCaseId != null && status != null && id != null &&
					 * !status.equals(ItemStatus.PASSED.toString())) { Map<String, String> dataMap =
					 * new HashMap<String, String>(); dataMap.put(id, status);
					 * results.put(testCaseId, dataMap); }
					 */
				}
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public static void updateTestItem(String endpoint, String bearerToken, String project, String id,
			ItemStatus status) {
		String url = endpoint + "/api/v1/" + project + "/item/" + id + "/update";

		log.info("url::" + url);

		JSONObject attributeParams = new JSONObject();
		attributeParams.put("key", "status");
		attributeParams.put("value", status.toString());

		JSONArray attributeArray = new JSONArray();
		attributeArray.add(attributeParams);

		JSONObject requestParams = new JSONObject();
		requestParams.put("status", status.toString());
		requestParams.put("attributes", attributeArray);

		log.info(requestParams.toJSONString());

		Response response = given()
				.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
						ContentType.JSON)
				.body(requestParams.toString()).when().put(url).then().contentType(ContentType.JSON).extract()
				.response();

		log.info(response.asString());
	}

	public static void searchProject(String endpoint, String bearerToken, String project) {
		String url = endpoint + "/api/v1/project/names";

		log.info("url::" + url);

		try {
			Response response = given()
					.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON,
							"Accept", ContentType.JSON)
					.when().get(url).then().contentType(ContentType.JSON).extract().response();

			log.info(response.asString());

			String[] projects = response.asString().replace("[", "").replace("]", "").replace("\"", "").split(",");

			boolean projectFound = false;

			for (String rpproject : projects) {
				if (rpproject.toLowerCase().equals(project.toLowerCase())) {
					projectFound = true;
					log.info("Project Found " + project);
				}
			}

			if (!projectFound) {
				log.info("Creating new project :: " + project);

				JSONObject requestParams = new JSONObject();
				requestParams.put("entryType", "INTERNAL");
				requestParams.put("projectName", project);

				url = endpoint + "/api/v1/project";
				response = given()
						.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
								ContentType.JSON)
						.body(requestParams.toString()).when().post(url).then().contentType(ContentType.JSON).extract()
						.response();

				log.info(response.asString());
			} else if (TestBase.rpUsername != null && !TestBase.rpUsername.equals("")) {
				url = endpoint + "/api/v1/project/" + project + "/assign";
				log.info("assign url " + url);

				JSONObject params = new JSONObject();
				params.put(TestBase.rpUsername, "PROJECT_MANAGER");

				JSONObject requestParams = new JSONObject();
				requestParams.put("userNames", params);

				response = given()
						.headers("Authorization", "Bearer " + bearerToken, "Content-Type", ContentType.JSON, "Accept",
								ContentType.JSON)
						.body(requestParams.toString()).when().put(url).then().contentType(ContentType.JSON).extract()
						.response();

				log.info("response.asString() => " + response.asString());

			}
		} catch (Exception e) {

		}
	}

	public static void getLaunchRerunDetails() {
		String testCaseId = null;
		String id = null;
		String status = null;
		String bearerToken = "UeM6tBPUJwa0a1cUHK6Ia_UMp9r78JUqIvK7Td17fUw1";
		Map<String, Map<String, String>> results = new HashMap<String, Map<String, String>>();
		String url = "https://api.testproject.io/v2/projects/HTsTdJO1MU2-uSBC-WBHkw/jobs/QSToaT78Ok-KtfM6DCHFYA/reports/a-5CWYXIq0KmmPmmK4YScA?format=TestProject";

		log.info("url::" + url);

		try {
			Response response = given()
					.headers("Authorization", bearerToken, "Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
					.when().get(url).then().contentType(ContentType.JSON).extract().response();
			ObjectMapper mapper = new ObjectMapper();
			// Thread.sleep(5000);
			log.info(response.asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		/*
		 * RestAPIUtil.postLaunchDataByUUid("http://73.135.240.210:9080",
		 * "f19c7add-9d21-48c5-bbb4-a199fbcb1580", "infor_ln_test", "3182","");
		 */

		/*
		 * RestAPIUtil.postLaunchDataByUUid("http://73.135.240.210:9080",
		 * "f19c7add-9d21-48c5-bbb4-a199fbcb1580", "infor_ln", "3202","");
		 */
		getLaunchRerunDetails();

		/*
		 * Map<String, Map<String, String>> results =
		 * getLaunchRerunDetails("http://73.135.240.210:9080",
		 * "f19c7add-9d21-48c5-bbb4-a199fbcb1580", "infor_ln_test", "1929");
		 * log.info(results.toString());
		 * log.info(results.get("SLS_049").keySet().iterator().next());
		 * 
		 * updateTestItem("http://73.135.240.210:9080",
		 * "f19c7add-9d21-48c5-bbb4-a199fbcb1580", "infor_ln_test", "344035",
		 * ItemStatus.PASSED);
		 * 
		 * 
		 * searchProject("http://73.135.240.210:9080",
		 * "f19c7add-9d21-48c5-bbb4-a199fbcb1580", "PayPal1");
		 */

	}

}