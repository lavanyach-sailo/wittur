package com.sailotech.testautomation.commonutilities;

import java.io.IOException;
import com.sailotech.testautomation.accelarators.TestBase;

public class TestUtil extends TestBase{
	
	public	static long page_load_timeout=3500;
	public static long implicit_wait=60;
	public static long explicit_wait=120;
	public static long poEwait=5;
	
	public static void takeScreenshotAtEndOfTest() throws IOException {
		/*
		 * try { File scrFile = ((TakesScreenshot)
		 * driver).getScreenshotAs(OutputType.FILE); String currentDir =
		 * System.getProperty("user.dir"); SimpleDateFormat simpleDateFormat = new
		 * SimpleDateFormat("MM-dd-yy-HH-mm-ss"); Calendar calendar =
		 * Calendar.getInstance(); String timeStamp =
		 * simpleDateFormat.format(calendar.getTime()); FileUtils.copyFile(scrFile, new
		 * File(currentDir + "/screenshots/" + timeStamp + "_TestFAILED.png")); }
		 * catch(Exception e) {}
		 */
	}
	
	public static int findWhiteSpaces(String text) {
		 int let = 0;
		    int dig = 0;
		    int space= 0;
		    char[]arr=text.toCharArray();
		    
		for(int i=0;i<text.length();i++) {
	        if (Character.isDigit(arr[i])) {
	            dig++;
	        } else if (Character.isLetter(arr[i])) { 
	            let++;
	        } else if (Character.isWhitespace(arr[i])) {
	            space++;
	        }
	  
	    }
	    log.info("Number of Letters : "+let);
	    log.info("Number of Digits : "+dig);
	    log.info("Number of Whitespaces : "+space);
	    return space;
	}

}
