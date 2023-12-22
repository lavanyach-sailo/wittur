package com.sailotech.testautomation.database;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

public class ImageToBase64 {

	public static String getBase64(File file, String ext) {
		// String filePath = "C:\\Users\\lavanya.chinta\\Downloads\\salesforce.png";
		String encodedString = null;
		byte[] fileContent;
		try {
			fileContent = FileUtils.readFileToByteArray(file);
			encodedString = "data:/" + ext + ";base64," + Base64.getEncoder().encodeToString(fileContent);
			// log.info("encodedString => " + encodedString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encodedString;
	}

}
