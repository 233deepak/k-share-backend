package com.aws.codestar.projecttemplates.configuration;

import java.net.URI;

import com.amazonaws.kshare.dao.intf.TopicDaoIntf;
import com.aws.codestar.projecttemplates.dao.TopicDao;
import com.aws.codestar.projecttemplates.services.TopicService;

import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

public class AppConfig {

	private TopicDao topicDao;
	//private DynamoDB dynamoDB;
	private DynamoDbClient dynamoDbClient;
	private TopicService topicService;
	private static AppConfig appConfig;

	/**public DynamoDB dynamoDB() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration("http://docker.for.mac.localhost:8000", "us-east-2"))
				.build();

		if (dynamoDB == null)
			dynamoDB = new DynamoDB(client);
		return dynamoDB;
	} */

	public TopicDaoIntf topicDao() {
		if (topicDao == null)
			topicDao = new TopicDao(dynamoDb());
		return topicDao;
	}

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
			topicService = new TopicService();
		return topicService;
	}

	public static AppConfig getInstance() {
		if (appConfig == null)
			appConfig = new AppConfig();
		return appConfig;
	}

}
