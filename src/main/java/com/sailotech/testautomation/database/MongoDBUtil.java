package com.sailotech.testautomation.database;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;

import static com.mongodb.client.model.Filters.eq;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.beans.Project;
import com.sailotech.testautomation.beans.Release;
import com.sailotech.testautomation.beans.TestResults;
import com.sailotech.testautomation.util.JsonReader;
import com.sailotech.testautomation.beans.Module;
import com.sailotech.testautomation.beans.ModuleResponse;

public class MongoDBUtil extends TestBase {

	private static MongoClient mongoClient = null;

	@SuppressWarnings("resource")
	private static MongoDatabase getDataBase() {
		log.info("mongodbProp" + mongodbProp);
		if (mongodbProp == null) {
			new TestBase();
		}
		log.info("connectionString" + mongodbProp.getProperty("connectionString"));
		try {
			MongoClient mongoDBClient = new MongoClient(
					new MongoClientURI(mongodbProp.getProperty("connectionString")));
			mongoClient = mongoDBClient;
		} finally {
		}
		log.info("mongoClient::" + mongoClient);
		return mongoClient.getDatabase(mongodbProp.getProperty("database"));
	}

	public static void insertReportPortalData(String collection, String rpproject, String rpid, String uuid,
			String name, String number, String startTime, String status) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
			Date dt = new Date();

			MongoDatabase database = getDataBase();
			Document document = new Document();
			document.append("jenkinsJobID", jenkinsBuildNumber);
			testResults.setJenkinsJobID(jenkinsBuildNumber);
			document.append("jenkinsJobName", jenkinsJobName);
			testResults.setJenkinsJobName(jenkinsJobName);
			document.append("rpproject", rpproject);
			testResults.setRpproject(rpproject);
			document.append("rpid", rpid);
			testResults.setRpID(rpid);
			document.append("uuid", uuid);
			testResults.setLaunchUUID(uuid);
			document.append("launchName", name);
			testResults.setLaunchName(name);
			document.append("number", number);
			testResults.setNumber(number);
			if (release != null && !release.equals("")) {
				String jsonFiles = "";
				String tags = "";

				List<Project> projects = release.getProjects();
				for (Project project : projects) {
					List<Module> modules = project.getModules();
					for (Module module : modules) {
						jsonFiles = jsonFiles + "," + module.getModuleName();
						tags = tags + "," + module.getTags().toString();
					}
				}
				document.append("jsonFiles", jsonFiles.substring(1));
				testResults.setJsonFiles(jsonFiles.substring(1));
				document.append("tags", tags.substring(1));
				testResults.setTags(tags.substring(1));
				document.append("releaseID", release.getReleaseID());
				document.append("companyId", release.getCompany());
				document.append("launchedBy", release.getCreatedBy());
			} else {
				document.append("jsonFiles", System.getProperty("jsonFiles"));
				document.append("tags", System.getProperty("tags"));
				document.append("launchedBy", System.getProperty("launchedBy"));
			}
			testResults.setLaunchedBy(System.getProperty("launchedBy"));
			document.append("startTime", startTime);
			testResults.setStartTime(startTime);
			document.append("datetime", dateFormat.format(dt));
			testResults.setDateTime(dateFormat.format(dt));
			document.append("status", status);
			if (totalTestCases == 0)
				document.append("total", 1);
			else
				document.append("total", totalTestCases);
			document.append("releaseID", System.getProperty("releaseID"));
			document.append("companyId", release != null ? release.getCompany() : "");
			testResults.setStatus(status);
			// Inserting the document into the collection
			database.getCollection(collection).insertOne(document);
			log.info("Document inserted successfully");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Document insertion failed");
		} finally {
			closeMongoDBConnection();
		}
	}

	public static void updateReportPortalData(TestResults testResults) {
		try {
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss"); Date dt
			 * = new Date();
			 * 
			 * MongoDatabase database = getDataBase(); Gson gson = new Gson();
			 * log.info(gson.toJson(testResults)); // Document obj = new
			 * Document("str", gson.toJson(testResults)); BasicDBObject query = new
			 * BasicDBObject(); //query.put("uuid", uuid); query.put("report", "RP");
			 * 
			 * BasicDBObject document = new BasicDBObject(); document.append("$set", new
			 * BasicDBObject() .append("endTime", testResults.getEndTime())
			 * .append("lastModified", testResults.getLastModified()) .append("status",
			 * testResults.getStatus()) .append("approximateDuration",
			 * testResults.getApproximateDuration()) .append("total",
			 * testResults.getTotal()) .append("passed", testResults.getPassed())
			 * .append("failed", testResults.getFailed()) .append("skipped",
			 * testResults.getSkipped()));
			 * 
			 * 
			 * List<TestProject> testProjects = testResults.getTestProjects();
			 * for(TestProject testProject : testProjects) { BasicDBObject
			 * testProjectDocument = new BasicDBObject(); testProjectDocument.append("id",
			 * testProject.getId()); document.append("projects", testProjectDocument); }
			 * 
			 * // database.getCollection(mongodbProp.getProperty("testResultsCollection")).
			 * updateOne(query, document);
			 */
			MappingPOJO.update(testResults);
			log.info("Document inserted successfully");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Document insertion failed");
		} finally {
			closeMongoDBConnection();
		}
	}

	public static void updateReportPortalDataByUUID(String collection, String uuid, String endTime, String lastModified,
			String status, String approximateDuration, int total, int passed, int failed, int skipped) {
		try {

			BasicDBObject query = new BasicDBObject();
			query.put("uuid", uuid);

			MongoDatabase database = getDataBase();

			approximateDuration = getApproximationDuration();

			if (totalTestCases != 0)
				total = totalTestCases;

			BasicDBObject document = new BasicDBObject();
			document.append("$set", new BasicDBObject().append("endTime", endTime).append("lastModified", lastModified)
					.append("status", status).append("approximateDuration", approximateDuration).append("total", total)
					.append("passed", passed).append("failed", failed).append("skipped", skipped));

			testResults.setEndTime(endTime);
			testResults.setLastModified(lastModified);
			testResults.setStatus(status);
			testResults.setApproximateDuration(approximateDuration);
			testResults.setTotal(String.valueOf(total));
			testResults.setPassed(String.valueOf(passed));
			testResults.setFailed(String.valueOf(failed));
			testResults.setSkipped(String.valueOf(skipped));

			database.getCollection(collection).updateOne(query, document);

			// Inserting the document into the collection
			// database.getCollection(collection).replaceOne(query, document);
			log.info("Document updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Document updation failed");
		} finally {
			closeMongoDBConnection();
		}
	}

	public static String getApproximationDuration() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		Date date1 = null;
		Date date2 = null;
		try {
			log.info("startTime " + startTime);
			// date1 = format.parse(startTime);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			// endTime = dtf.format(now);
			log.info("endTime " + endTime);
			// date2 = format.parse(endTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long difference = date2.getTime() - date1.getTime();
		return String.valueOf(difference / 1000);
	}

	public static void updateReportPortalDataByID(String collection, String id, String endTime, String lastModified,
			String status, String approximateDuration, int total, int passed, int failed, int skipped) {
		try {

			BasicDBObject query = new BasicDBObject();
			query.put("rpid", id);

			MongoDatabase database = getDataBase();

			if (totalTestCases != 0)
				total = totalTestCases;

			BasicDBObject document = new BasicDBObject();
			document.append("$set", new BasicDBObject().append("endTime", endTime).append("lastModified", lastModified)
					.append("status", status).append("approximateDuration", approximateDuration).append("total", total)
					.append("passed", passed).append("failed", failed).append("skipped", skipped));

			testResults.setEndTime(endTime);
			testResults.setLastModified(lastModified);
			testResults.setStatus(status);
			testResults.setApproximateDuration(approximateDuration);
			testResults.setTotal(String.valueOf(total));
			testResults.setPassed(String.valueOf(passed));
			testResults.setFailed(String.valueOf(failed));
			testResults.setSkipped(String.valueOf(skipped));

			database.getCollection(collection).updateOne(query, document);

			// Inserting the document into the collection
			// database.getCollection(collection).replaceOne(query, document);
			log.info("Document updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Document updation failed");
		} finally {
			closeMongoDBConnection();
		}
	}

	public static Map<String, String> getJsonsInputs() {
		Map<String, String> jsonsInputMap = new HashMap<>();
		try {
			MongoDatabase database = getDataBase();
			MongoCollection<Document> collection = database.getCollection("jsonInputs");
			Document document = collection.find().first();

			BasicDBObject whereQuery = new BasicDBObject();
			String[] jsonFiles = System.getProperty("jsonFiles").split(",");
			log.info(jsonFiles[0]);
			String project = jsonFiles[0].split("/")[0];
			whereQuery.put("project", project);
			MongoCursor<Document> cursor = collection.find(whereQuery).cursor();
			while (cursor.hasNext()) {
				document = cursor.next();
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(document.toJson());
			log.info(document.toJson());
			Iterator<String> rootFieldNames = root.fieldNames();
			String key = null, value = null;
			while (rootFieldNames.hasNext()) {
				key = rootFieldNames.next();
				value = root.get(key).asText();
				if (key.equals("inputs")) {
					JsonNode jsonNode = root.get(key);
					Iterator<String> fieldNames = jsonNode.fieldNames();
					while (fieldNames.hasNext()) {
						key = fieldNames.next();
						value = jsonNode.get(key).asText();
						jsonsInputMap.put(key, value);
					}
				}
				if (key.equals("json")) {
					String jsonString = root.get(key).toPrettyString();
					jsonsInputMap.put(key, jsonString);
				} else {
					jsonsInputMap.put(key, value);
				}
			}
			log.info(jsonsInputMap.toString());

		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeMongoDBConnection();
		}
		return jsonsInputMap;
	}

	public static Map<String, Map<String, String>> getJsonInputs() {
		Map<String, Map<String, String>> inputMap = new HashMap<String, Map<String, String>>();
		try {
			String key = null, value = null, mapKey = null;
			MongoDatabase database = getDataBase();
			MongoCollection<Document> collection = database.getCollection("jsonInputs");
			Document document = collection.find().first();

			BasicDBObject whereQuery = new BasicDBObject();
			String[] jsonFiles = System.getProperty("jsonFiles").split(",");
			log.info(jsonFiles[0]);
			String project = jsonFiles[0].split("/")[0];
			whereQuery.put("project", project);
			MongoCursor<Document> cursor = collection.find(whereQuery).cursor();
			while (cursor.hasNext()) {
				document = cursor.next();
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(document.toJson());
			log.info(document.toJson());

			Iterator<String> rootIterator = root.fieldNames();
			while (rootIterator.hasNext()) {
				mapKey = rootIterator.next();
				if ((Object) root.get(mapKey) instanceof JsonNode && root.get(mapKey).isContainerNode()) {
					// log.info(mapKey
					// +","+value+"=>"+root.get(mapKey).isContainerNode());
					Map<String, String> tempMap = new HashMap<String, String>();
					JsonNode jsonNode = root.get(mapKey);
					Iterator<String> jsonFileds = jsonNode.fieldNames();
					while (jsonFileds.hasNext()) {
						key = jsonFileds.next();
						value = jsonNode.get(key).asText();
						tempMap.put(key, value);
					}
					// log.info(mapKey);
					inputMap.put(mapKey, tempMap);
					// log.info(inputMap.toString());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeMongoDBConnection();
		}
		return inputMap;
	}

	public static Release getReleaseData(String releaseID) {
		Release release = new Release();
		try {
			MongoDatabase database = getDataBase();
			MongoCollection<Document> collection = database.getCollection("releases");
			Document document = collection.find(eq("_id", new ObjectId(releaseID))).first();
			if (document != null) {
				release.setReleaseName(document.get("releaseName").toString());
				release.setReleaseDesc(document.get("releaseDesc").toString());
				release.setCompany(document.get("company").toString());
				release.setCreatedBy(document.get("createdBy").toString());
				List<Document> projectsDocument = (List<Document>) document.get("projects");
				List<Project> projects = new ArrayList<Project>();
				for (Document projectDocument : projectsDocument) {
					Project project = new Project();
					project.setProjectID(projectDocument.getString("projectID"));
					MongoCollection<Document> projectCollection = database.getCollection("projects");
					Document projectDoc = projectCollection.find(eq("_id", new ObjectId(project.getProjectID())))
							.first();
					project.setProjectName(projectDoc.get("projectName").toString());
					List<Document> modulesDocument = (List<Document>) projectDocument.get("modules");
					List<Module> modules = new ArrayList<Module>();
					for (Document moduleDocument : modulesDocument) {
						Module module = new Module();
						module.setJsonFile(moduleDocument.get("jsonFile").toString());
						module.setModuleID(moduleDocument.get("moduleID").toString());
						MongoCollection<Document> moduleCollection = database.getCollection("modules");
						Document moduleDoc = moduleCollection.find(eq("_id", new ObjectId(module.getModuleID())))
								.first();
						module.setModuleName(moduleDoc.get("moduleName").toString());
						module.setModuleDesc(moduleDoc.get("moduleDesc").toString());
						// module.setModuleName(moduleDocument.get("moduleName").toString());
						if (moduleDocument.get("priority") != null)
							module.setPriority(moduleDocument.get("priority").toString());
						module.setTags((List<String>) moduleDocument.get("tags"));
						try {
							module.setTestCaseFile(moduleDocument.get("testCaseFile").toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						module.setTestData((Map<String, String>) moduleDocument.get("testData"));
						if (moduleDocument.get("testDataFile") != null)
							module.setTestDataFile(moduleDocument.get("testDataFile").toString());
						module.setUserStoryFile(null);
						String path = "./resources/modules/" + project.getProjectName() + "/" + module.getModuleName()
								+ "/";
						Files.createDirectories(Paths.get(path));
						String strURL = MessageFormat.format(prop.getProperty("moduleFileDownloadURL"),
								System.getProperty("hostName"), module.getModuleID(), "jsonFile");
						log.info("strURL => " + strURL);
						URL url = new URL(strURL);
						// URL url = new URL("http://localhost:5000/api/config/downloadModule/" +
						// module.getModuleID() + "/jsonFile/download"); // Input URL
						String jsonPath = path + module.getModuleName() + ".json";
						FileOutputStream out = new FileOutputStream(new File(jsonPath)); // Output file
						module.setJsonPath(jsonPath);
						module.setJsonText(JsonReader.readFileAsString(module.getJsonPath()));
						if (moduleDoc.get("version") != null)
							module.setVersion(moduleDoc.get("version").toString());
						if (moduleDoc.get("totalTestCases") != null)
							module.setTotalTestCases(Integer.parseInt(moduleDoc.get("totalTestCases").toString()));
						if (moduleDoc.get("totalTestSteps") != null)
							module.setTotalTestSteps(Integer.parseInt(moduleDoc.get("totalTestSteps").toString()));
						out.write(url.openStream().readAllBytes());
						out.close();
						modules.add(module);
					}
					boolean projectExists = false;
					for (Project proj : projects) {
						if (proj.getProjectName().equals(project.getProjectName())) {
							projectExists = true;
							List<Module> projModules = null;
							projModules = Stream.concat(proj.getModules().stream(), modules.stream())
									.collect(Collectors.toList());
							proj.setModules(projModules);
						}
					}
					if (!projectExists) {
						project.setModules(modules);
						projects.add(project);
					}
				}
				release.setProjects(projects);
				log.info("******************* release ***********************");
				log.info(release);
				log.info(release.getProjects().size());
				log.info("******************* release ***********************");
				projects = release.getProjects();
				for (Project project : projects) {
					List<com.sailotech.testautomation.beans.Module> modules = project.getModules();
					log.info("************************* Module ***********************");
					for (com.sailotech.testautomation.beans.Module module : modules) {
						log.info(module.toString());
						if (module.getTotalTestCases() != 0)
							totalTestCases += module.getTotalTestCases();
					}
					log.info("********************************************************");
				}
				log.info("TOTAL TEST CASES : " + totalTestCases);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeMongoDBConnection();
		}
		return release;
	}

	public static String getModuleJson(ModuleResponse module) {
		String json = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			json = ow.writeValueAsString(module);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public static void closeMongoDBConnection() {
		if (mongoClient != null) {
			mongoClient.close();
			log.info("MongoDB connection closed");
		}
	}

	public static void main(String args[]) {
		new TestBase();
		MongoDBUtil.getReleaseData("61aa02366aae124c08d88c3c");
	}

}
