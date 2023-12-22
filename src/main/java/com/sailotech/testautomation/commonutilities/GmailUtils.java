package com.sailotech.testautomation.commonutilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.sailotech.testautomation.accelarators.TestBase;




public class GmailUtils extends TestBase{
	
	
	public static List<String> labels = new ArrayList<String>();
	public static  Gmail Gmailservice;
	 protected static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	 protected static HttpTransport HTTP_TRANSPORT;
	 protected static FileDataStoreFactory DATA_STORE_FACTORY;
	 protected static final java.io.File DATA_STORE_DIR = new java.io.File(
		        System.getProperty("user.home"), ".credentials/ttuserclient");
	 protected static final String APPLICATION_NAME =
		        "ttuserclient";
	 private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/gmail.modify");
	public static String userId = "youremail@gmail.com";
	public static Map<String, String> replyMailValuesFromReadMail = new HashMap<String, String>();
	public static String emailHTMLContentWithResponseLinks;
	

	
	static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
	
	public static String searchInboxBySubjectAndVerifyBody(
			String searchSubject, String verifyBodyValue) throws Exception {
		String verificationCode=null;
		labels.add("INBOX");
		List<Message> searchedMessages = listMessagesWithLabels(Gmailservice,
				userId, labels);
		log.info(searchedMessages.size());
		for (Message message1 : searchedMessages) {
			Message message = getMessage(Gmailservice, userId, message1.getId());
			log.info("Inside For Loop..");
			log.info(getMessageSubject(message));
			if (getMessageSubject(message).contains("Verify your identity in Salesforce")) {

				log.info("Got the message....."
						+ getMessageSubject(message));
				replyMailValuesFromReadMail.put("messageId", message.getId());
				replyMailValuesFromReadMail.put("threadId",
						message.getThreadId());
					emailHTMLContentWithResponseLinks = StringUtils
							.newStringUtf8(com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64
									.decodeBase64(message.getPayload().getBody().getData()));
					log.info(emailHTMLContentWithResponseLinks);
					String lines[] = emailHTMLContentWithResponseLinks.split("\\r?\\n");
					for (int i = 0; i < lines.length; i++) {
						if(lines[i].contains("Verification Code:")) {
							String verificationCodeArr[]=lines[i].split(":");
							verificationCode=verificationCodeArr[1].trim();
							break;
						}
					}
				break;
			}
		}
		return verificationCode;
	}
	
	public static void deleteInbox() throws Exception {
		authorize();
		labels.add("INBOX");
		List<Message> searchedMessages = listMessagesWithLabels(Gmailservice,
				userId, labels);
		for (Message message : searchedMessages) {
			Gmailservice.users().messages().trash(userId, message.getId()).execute();
		}
	}

	public static List<Message> listMessagesWithLabels(Gmail service,
			String userId, List<String> labelIds) throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId)
				.setLabelIds(labelIds).execute();

		List<Message> messages = new ArrayList<Message>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId)
						.setLabelIds(labelIds).setPageToken(pageToken)
						.execute();
			} else {
				break;
			}
		}

		for (Message message : messages) {
			log.info(message.toPrettyString());
		}

		return messages;
	}

	public static Message getMessage(Gmail service, String userId,
			String messageId) throws IOException {
		Message message = service.users().messages().get(userId, messageId)
				.setFormat("full").execute();
		return message;
	}

	public static String getMessageSubject(Message message) throws Exception {

		List<MessagePartHeader> headers = message.getPayload().getHeaders();
		for (MessagePartHeader header : headers) {
			log.info(header.getName());
			if (header.getName().toString().equals("Subject")) {
				log.info(header.getValue());
				return header.getValue().toString();
			}
		}
		return "";
	}

	public static void authorize() throws Exception {
		// Load client secrets.
		
		String CLIENT_SECRET_PATH= System.getProperty("user.dir")+"\\src\\main\\resources\\client_secret_658703561614-l7iv64467r58au0b2jgjjpmhcuo38ijg.apps.googleusercontent.com.json";
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,  new FileReader(CLIENT_SECRET_PATH));
		

	    // Build flow and trigger user authorization request.
	    GoogleAuthorizationCodeFlow flow =
	            new GoogleAuthorizationCodeFlow.Builder(
	                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
	            .setDataStoreFactory(DATA_STORE_FACTORY)
	            .setAccessType("offline")
	            .build();
	    Credential credential = new AuthorizationCodeInstalledApp(
	        flow, new LocalServerReceiver()).authorize("user");
	    log.info(
	            "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
	    
	  
		log.info(credential.getAccessToken());
		log.info(credential.getRefreshToken());
	    Gmailservice =new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
	            .setApplicationName(APPLICATION_NAME)
	            .build();
	   // return credential;
	}
	
	public static String verifyTTISEmailResponseLinks(String msgSubject,
			String msgBody) throws Exception {
		authorize();
		Thread.sleep(10000);
		return searchInboxBySubjectAndVerifyBody(msgSubject, msgBody);
	}
	
	public static String parseHTMLStringToGetEmailLinks(String HTMLString) {

		Document html = Jsoup.parse(HTMLString);
		String href = html.body().getElementsByTag("a").attr("href");

		log.info("Input HTML String to JSoup :" + HTMLString);

		log.info("HREF : " + href);

		return href;
	}
	
	public static void trashMessage(Gmail service, String user, String msgId)
			throws IOException {
		service.users().messages().trash(user, msgId).execute();
		log.info("Message with id: " + msgId + " has been trashed.");
	}
	
	public static void trashThread(Gmail service, String userId, String threadId)
			throws IOException {
		service.users().messages().trash(userId, threadId).execute();
		System.out
				.println("Thread with id: " + threadId + " has been trashed.");
	}
	
}
