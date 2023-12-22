package com.sailotech.testautomation.util;

import static io.restassured.RestAssured.given;

import java.math.BigInteger;
import java.security.MessageDigest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sailotech.testautomation.accelarators.TestBase;

import io.restassured.response.Response;

public class LambdaTestRestAPIUtil {

	public static JsonNode getSessionData(String endpoint, String username, String accessKey, String bearerToken,
			String sessionId) {
		String url = endpoint + "/automation/api/v1/sessions/" + sessionId;
		TestBase.log.info("url :: " + url);
		try {
			Response response = given().headers("Authorization", "Bearer " + bearerToken).when().get(url).then()
					.extract().response();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.asString());
			TestBase.log.info("response ++ " + response);
			TestBase.log.info("root ++ " + root);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getAuthKey(String username, String accessKey) {
		String authKey = null;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			String s = username + ":" + accessKey;
			m.update(s.getBytes(), 0, s.length());
			authKey = new BigInteger(1, m.digest()).toString(16);
			TestBase.log.info("MD5: " + authKey);
		} catch (Exception e) {

		}
		return authKey;
	}

	public static String getVideoUrl(String endpoint, String username, String accessKey, String sessionId) {
		String authToken = getAuthKey(username, accessKey);
		TestBase.log.info("authToken " + authToken);
		return (endpoint + "/public/video?testID=" + sessionId + "&auth=" + authToken);
	}

	public static void main(String args[]) {
		TestBase.log.info("getVideoUrl :: " + getVideoUrl("https://automation.lambdatest.com", "bhaktavatsal.cherukuri",
				"mjKchHDkwO04MQ4EzMezA4IqtbzH7HjloKiutOuhcxgCC3V2XK", "656ae6e3ca8818246e4fa6f3a1481ea"));
	}

}