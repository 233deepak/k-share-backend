package com.amazonaws.kshare.configuration;

import java.net.URI;

import com.amazonaws.kshare.dao.CommentDao;
import com.amazonaws.kshare.dao.DocumnetDao;
import com.amazonaws.kshare.dao.TopicDao;
import com.amazonaws.kshare.dao.UserDao;
import com.amazonaws.kshare.dao.intf.CommentDaoIntf;
import com.amazonaws.kshare.dao.intf.DocumentDaoIntf;
import com.amazonaws.kshare.dao.intf.TopicDaoIntf;
import com.amazonaws.kshare.dao.intf.UserDaoIntf;
import com.amazonaws.kshare.services.DocSevice;
import com.amazonaws.kshare.services.TopicService;
import com.amazonaws.kshare.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

public class AppConfig {

	private TopicDaoIntf topicDao;
	private DynamoDbClient dynamoDbClient;
	private TopicService topicService;
	private DocSevice docSevice;
	private DocumentDaoIntf documentDao;
	private UserService userService;
	private UserDaoIntf userDao;
	private CommentDaoIntf commentDao;
	private ObjectMapper objectMapper;

	private static AppConfig appConfig;

	public DynamoDbClient dynamoDb() {
		//final String endpoint = "http://docker.for.mac.localhost:8000";
		final String endpoint = "";

		DynamoDbClientBuilder builder = DynamoDbClient.builder();
		builder.httpClient(ApacheHttpClient.builder().build());
		if (endpoint != null && !endpoint.isEmpty()) {
			builder.endpointOverride(URI.create(endpoint));
		}

		if (dynamoDbClient == null)
			dynamoDbClient = builder.build();
		return dynamoDbClient;
	}

	public TopicService topicService() {
		if (topicService == null)
			topicService = new TopicService(commentDao(),topicDao());
		//topicService.intializeTopicTable();
		return topicService;
	}

	public TopicDaoIntf topicDao() {
		if (topicDao == null)
			topicDao = new TopicDao(dynamoDb());
		return topicDao;
	}
	
	public CommentDaoIntf commentDao() {
		if(commentDao == null) {
			commentDao = new CommentDao(dynamoDb(), objectMapper());
		}
		return commentDao;
	}

	public DocSevice docSevice() {
		if (docSevice == null) {
			docSevice = new DocSevice();
			//docSevice.initializeDocumentTable();
		}
		return docSevice;
	}

	public DocumentDaoIntf documentDao() {
		if (documentDao == null) {
			documentDao = new DocumnetDao(dynamoDb());
		}
		return documentDao;
	}

	public UserService userService() {
		if (userService == null) {
			userService = new UserService(userDao(), objectMapper());
			//userService.initializeUserTable();
		}
		return userService;
	}

	public ObjectMapper objectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	public UserDaoIntf userDao() {
		if (userDao == null) {
			userDao = new UserDao(dynamoDb());
		}
		return userDao;
	}

	public static AppConfig getInstance() {
		if (appConfig == null)
			appConfig = new AppConfig();
		return appConfig;
	}

}
