package com.aws.codestar.projecttemplates.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.kshare.dao.intf.TopicDaoIntf;
import com.aws.codestar.projecttemplates.exception.CouldNotCreateTopicException;
import com.aws.codestar.projecttemplates.exception.TableExistsException;
import com.aws.codestar.projecttemplates.model.Topic;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public class TopicDao implements TopicDaoIntf {

	private DynamoDbClient dynamoDb;
	
	
	public TopicDao(DynamoDbClient dynamoDb) {
		super();
		this.dynamoDb = dynamoDb;
	}

	private static final String TABLENAME = "Topic-Metadata";
	private static final int PAGESIZE = 50;
	
	private static final String TOPIC_ID= "TopicId";
    
	@Override
	public void createTopicTable() throws TableExistsException {
		try {
            dynamoDb.createTable(CreateTableRequest.builder()
                    .tableName(TABLENAME)
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(TOPIC_ID)
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(TOPIC_ID)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .build());
        } catch (ResourceInUseException e) {
            throw new TableExistsException("Orders table already exists");
        }
		
	}

	@Override
	public Topic addTopic(Topic topic) throws CouldNotCreateTopicException {
		 if (topic == null) {
	            throw new IllegalArgumentException("CreateOrderRequest was null");
	        }
	        int tries = 0;
	        while (tries < 10) {
	            try {
	            	 Map<String, AttributeValue> item = createTopicItem(topic);
	                dynamoDb.putItem(PutItemRequest.builder()
	                        .tableName(TABLENAME)
	                        .item(item)
	                        .conditionExpression("attribute_not_exists(orderId)")
	                        .build());
	                
	                topic.setTopicId(item.get(TOPIC_ID).s());
	                topic.setVersion(Long.valueOf(item.get("version").n()));
	                return topic;
	            } catch (ConditionalCheckFailedException e) {
	                tries++;
	            } catch (ResourceNotFoundException e) {
	                throw e;
	            }
	        }
	        throw new CouldNotCreateTopicException(
	                "Unable to generate unique order id after 10 tries");
	}

	private Map<String, AttributeValue> createTopicItem(Topic topic) {
		 Map<String, AttributeValue> item = new HashMap<>();
	        item.put(TOPIC_ID, AttributeValue.builder().s(UUID.randomUUID().toString()).build());
	        item.put("version", AttributeValue.builder().n("1").build());
	        item.put("guid",
	                AttributeValue.builder().s(topic.getGuid()).build());
	        item.put("title",
	                AttributeValue.builder().s(topic.getTitle()).build());
	        if(topic.getCreatedOn()!=null)
	        item.put("createdOn",
	                AttributeValue.builder().s(topic.getCreatedOn().toString()).build());
	        item.put("createdBy",
	                AttributeValue.builder().s(topic.getCreatedBy()).build());
	        item.put("hasVideos",
	                AttributeValue.builder().bool(topic.isHasVideos()).build());
	        item.put("hasNotes",
	                AttributeValue.builder().bool(topic.isHasNotes()).build());
	        item.put("views",
	                AttributeValue.builder().n(String.valueOf(topic.getViews())).build());
	        item.put("status",
	                AttributeValue.builder().s(topic.getStatus().getStatus()).build());
	        item.put("tags",
	                AttributeValue.builder().s(topic.getTags()).build());
	        item.put("documentId",
	                AttributeValue.builder().s(topic.getDocumentId()).build());
	        return item;
		
	}

}
