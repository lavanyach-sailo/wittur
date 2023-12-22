/**
 * 
 */
package com.sailotech.testensure.tests;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.beans.Release;
import com.sailotech.testautomation.database.MongoDBUtil;

/**
 * @author rajsu To read jsons and to create instances
 *         accordingly
 *
 */
@Slf4j
public class TestEnsureTestFactory {

	static String jsonDelimeter = "#@#";
	static String fileNameDelimeter = "###";
	
	public static Release release = null;
	
	/**
	 * To create instances taking json as input
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@Factory(dataProvider = "dp")
	public Object[] createInstance(String json) throws Throwable {
		String tags = System.getProperty("tags");
		ArrayList<Object> tests = new ArrayList<Object>();
		if (json != null && !json.equals("null")) {
			if (!json.equals("{}")) {
				try {
					if(release != null) {
						tests.add(new TestEnsureTest(release));
					} else {
						tests.add(new TestEnsureTest(tags, json, jsonDelimeter, fileNameDelimeter));
					}
				} catch (Exception e) {
					log.info("Json Parse Exception");
					e.printStackTrace();
				}
			}
		}
		return tests.toArray();
	}

	/**
	 * Reads json and converts json into String array
	 * 
	 * @return
	 * @throws Exception
	 */
	@DataProvider(name = "dp")
	public static Object[] dataProvider() throws Exception {
		// For passing json
		String releaseID = System.getProperty("releaseID");
		log.info("releaseID => "+releaseID);
		
		List<String> jsons = new ArrayList<String>();
		
		if(releaseID != null && !releaseID.equals("")) {
			new TestBase();
			release = MongoDBUtil.getReleaseData(releaseID);
			release.setReleaseID(releaseID);
		} else {
//			String[] jsonFiles = System.getProperty("jsonFiles").split(",");
//			String jsonFileName, jsonFile;
//			for(int i = 0; i < jsonFiles.length ; i++) {
//				jsonFileName = jsonFiles[i];
//				jsonFile = "resources/" + jsonFiles[i] + ".json";
//				jsons.add(jsonFileName + fileNameDelimeter + JsonReader.readFileAsString(jsonFile));
//			}
		}
		
		Object[] dataArray = new Object[] { String.join(jsonDelimeter, jsons) };

		return dataArray;
	}
}