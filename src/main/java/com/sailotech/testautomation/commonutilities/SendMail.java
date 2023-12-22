package com.sailotech.testautomation.commonutilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.testng.ITestResult;

import com.sailotech.testautomation.accelarators.TestBase;

public class SendMail {

	static String user_dir = System.getProperty("user.dir");

// this method is to zip the folder

	public static void zipFolder(final Path sourceFolderPath, Path zipPath) throws Exception {

		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
		Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
				Files.copy(file, zos);
				zos.closeEntry();
				return FileVisitResult.CONTINUE;
			}
		});

		zos.close();

		// calling send email attachment method
		// sendEmail_attachment();
	}

	// this method is to send an email attachment to the intended recipients
	
	public static void zipFiles(String sourceFolderToZip, String zipFilePath) throws Exception {
		try {
			File[] files = new File(sourceFolderToZip).listFiles();
			if (files != null) {
				SendMail.zipFolder(Paths.get(sourceFolderToZip), Paths.get(zipFilePath));
		        for (File file : files) {
		            file.delete();
		        }
		    }
			new File(sourceFolderToZip).delete();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendEmail_attachment(String sourceFolderToZip, String zipFilePath) throws EmailException {

		try {
			EmailAttachment attachment = new EmailAttachment();

			// attachment.setPath(user_dir + "\\OldFailedScreens\\escreen.zip");

			try {
				attachment.setPath(zipFilePath);
			} catch(Exception e) {
				e.printStackTrace();
			}

			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			// attachment.setDescription("WindowsError zip");
			//attachment.setName("SODC - PDF.zip");

			// Create the email message
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName("mail.sailotech.net");
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator("dev_stp@sailotech.net", "S@t@p$#2019"));
			//email.setSSLOnConnect(false);
			email.setStartTLSRequired(true);

			email.setFrom("lavanya.chinta@sailotech.com", "Lavanya Chinta");
			email.addTo("srilakshmi.supraja@sailotech.com", "Sri Lakshmi Supraja");
			email.addTo("lavanya.chinta@sailotech.com", "Lavanya Chinta");
			// email.addTo("krishna.chekuri@sailotech.com", "Krishna Chekuri");
			// email.addTo("rajasekhar.venati@sailotech.net", "Rajashekar");

			email.setSubject(TestBase.emailSubject);
		
			email.setMsg(
				"\n Hi, \n\n Please find the attached PDFs for your reference. \n\n Thank you, \n Test Automation Team. ");

			// add the attachment
			email.attach(attachment);

			// send the email
			email.send();

		} catch(Exception e) {
			e.printStackTrace();
		}
		TestBase.log.info("email attachment sent");

	}

	public static void main(String[] args) throws Exception {

		// set up the folder to zip and destination zip file location
		String folderToZip = user_dir + "\\resources\\temp\\2022-07-01-13-20-59";
		String destzipName = user_dir + "\\resources\\PDF.zip";

		// Instantiate and call the parameterized zipFolder method
		SendMail.zipFiles(folderToZip, destzipName);
		// sendEmail_attachment(null);

	}

}
