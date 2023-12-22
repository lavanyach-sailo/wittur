package com.sailotech.testautomation.util;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import com.sailotech.testautomation.accelarators.TestBase;

public class DateFormatUtil {
	static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

	public LocalDateTime getTimeNow() {
		return LocalDateTime.now();
	}

	public long getDurationEpoch(Duration duration) {
		Date date = new Date();
		try {
			String time = String.format("%02d:%02d:%02d", duration.toHours() % 24, duration.toMinutes() % 60,
					duration.toSeconds() % 60);
			date = df.parse(time);
			TestBase.log.info("date " + date);
		} catch (Exception e) {
		}
		return date.getTime();
	}

	public static void main(String args[]) {
		DateFormatUtil dateFormats = new DateFormatUtil();
		try {
			LocalDateTime startTime = dateFormats.getTimeNow();
			Thread.sleep(20000);
			LocalDateTime endTime = dateFormats.getTimeNow();
			// long duration = startTime.toEpochSecond(ZoneOffset.MIN) -
			// endTime.toEpochSecond(ZoneOffset.MIN);
			Duration duration = Duration.between(startTime, endTime);
			String time = String.format("%02d:%02d:%02d", duration.toHours() % 24, duration.toMinutes() % 60,
					duration.toSeconds() % 60);
			TestBase.log.info("startTime => " + startTime);
			TestBase.log.info("endTime => " + endTime);
			TestBase.log.info("duration => " + duration);

			long epoch = dateFormats.getDurationEpoch(duration);
			TestBase.log.info("epoch == " + epoch);
		} catch (Exception e) {
		}

	}

}
