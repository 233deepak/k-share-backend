package com.amazonaws.kshare.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.amazonaws.kshare.dao.intf.TopicDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.FilterCondition;
import com.amazonaws.kshare.model.Page;
import com.amazonaws.kshare.model.PageRequest;
import com.amazonaws.kshare.model.Topic;
import com.amazonaws.kshare.model.TopicStatus;

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
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

public class TopicDao implements TopicDaoIntf {

	private DynamoDbClient dynamoDb;
	
	
	public TopicDao(DynamoDbClient dynamoDb) {
		super();
		this.dynamoDb = dynamoDb;
	}

	private static final String TABLENAME = "Topic-Metadata";
	private static final int PAGESIZE = 50;
	private static final int LIMIT = 20;
	
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
	        item.put("category",
	                AttributeValue.builder().s(topic.getCategory()).build());
	        return item;
		
	}

	@Override
	public Page<Topic> getTopics(PageRequest pageRequest) throws TableDoesNotExistException {

		Page<Topic> topicPage = new Page<Topic>();
		List<Topic> currentPageTopics = new ArrayList<Topic>();
		ScanResponse result;
		String exclusiveStartOrderId = pageRequest.getExclusiveStartId();
		do {
			Map<String, AttributeValue> expressionAttributeValues = 
				    new HashMap<String, AttributeValue>();
				expressionAttributeValues.put(":ser_key", AttributeValue.builder().s(pageRequest.getSerKey()).build()); 
			try {
				String filterExpression = buildFilterCondition(pageRequest, expressionAttributeValues);
				ScanRequest.Builder scanBuilder = ScanRequest.builder()
						                       .tableName(TABLENAME)
						                       .filterExpression(filterExpression)
						                       .expressionAttributeValues(expressionAttributeValues);
						                       //.limit(LIMIT);
				if (!isNullOrEmpty(exclusiveStartOrderId)) {
					scanBuilder.exclusiveStartKey(Collections.singletonMap(TOPIC_ID,
							AttributeValue.builder().s(exclusiveStartOrderId).build()));
				}
				result = dynamoDb.scan(scanBuilder.build());
			} catch (ResourceNotFoundException e) {
				throw new TableDoesNotExistException("Order table " + TABLENAME + " does not exist");
			}

			final List<Topic> topics = result.items().stream().map(this::convert).collect(Collectors.toList());
			currentPageTopics.addAll(topics);

			if (result.lastEvaluatedKey() != null && !result.lastEvaluatedKey().isEmpty()) {
				if ((!result.lastEvaluatedKey().containsKey(TOPIC_ID)
						|| isNullOrEmpty(result.lastEvaluatedKey().get(TOPIC_ID).s()))) {
					throw new IllegalStateException(
							"orderId did not exist or was not a non-empty string in the lastEvaluatedKey");
				} else {
					topicPage.setLastEvaluatedKey(result.lastEvaluatedKey().get(TOPIC_ID).s());
					exclusiveStartOrderId = topicPage.getLastEvaluatedKey();
				}
			}else {
				topicPage.setLastEvaluatedKey("");
			}
		} while (currentPageTopics.size() < pageRequest.getPageSize() && ! result.lastEvaluatedKey().isEmpty() );

		topicPage.setTopics(currentPageTopics);
		return topicPage;
	}

	private String buildFilterCondition(PageRequest pageRequest,
			Map<String, AttributeValue> expressionAttributeValues) {
		String filterExpression = "(contains(title,:ser_key) OR contains(createdBy,:ser_key) OR contains(tags,:ser_key) OR contains(category,:ser_key)) ";
		for(FilterCondition condition :pageRequest.getFilterConditions()) {
			filterExpression += "AND "+condition.getFieldName()+"= :"+condition.getFieldName()+"_val";
			expressionAttributeValues.put(":"+condition.getFieldName()+"_val", AttributeValue.builder().s((String) condition.getValue()).build());
		}
		return filterExpression;
	}

    private static boolean isNullOrEmpty(final String string) {
	        return string == null || string.isEmpty();
	}

	private Topic convert(final Map<String, AttributeValue> item) {
		if (item == null || item.isEmpty()) {
			return null;
		}
		Topic topic = new Topic();

		try {
			topic.setTopicId(item.get(TOPIC_ID).s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an topic attribute or it was not a String");
		}

		try {
			topic.setVersion(Long.valueOf(item.get("version").n()));
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an customerId attribute or it was not a String");
		}

		try {
			topic.setGuid(item.get("guid").s());
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalStateException("item did not have an preTaxAmount attribute or it was not a Number");
		}

		try {
			topic.setTitle(item.get("title").s());
		}  catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an title attribute ");
		}

		try {
			String createdOn = item.get("createdOn").s();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
			topic.setCreatedOn(df.parse(createdOn));
		} catch (NullPointerException | NumberFormatException | ParseException e) {
			//throw new IllegalStateException("item did not have an version attribute or it was not a Number");
		}

		try {
			topic.setCreatedBy(item.get("createdBy").s());
		} catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an createdBy attribute ");
		}
		
		try {
			topic.setHasVideos(item.get("hasVideos").bool());
		}  catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an hasVideos attribute ");
		}
		try {
			topic.setHasNotes((item.get("hasNotes").bool()));
		} catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an hasNotes attribute ");
		}
		try {
			 TopicStatus topicStatus = getTopicStatus((item.get("status").s()));
			topic.setStatus(topicStatus);
		} catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an status attribute ");
		}
		try {
			topic.setViews(Integer.valueOf((item.get("views").n())));
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalStateException("item did not have an views attribute or it was not a Number");
		}
		try {
			topic.setTags(item.get("tags").s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an tags attribute ");
		}
		try {
			topic.setDocumentId(item.get("documentId").s());
		} catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an documentId attribute ");
		}
		
		try {
			topic.setCategory(item.get("category").s());
		} catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an documentId attribute ");
		}
		
		return topic;
	}

	private TopicStatus getTopicStatus(String status) {
		TopicStatus topicStatus;
		switch (status) {
		case "draft":
			topicStatus = TopicStatus.DRAFT;
			break;
		case "published":
			topicStatus = TopicStatus.PUBLISHED;
			break;
		case "verfied":
			topicStatus = TopicStatus.VERIFIED;
			break;

		default:
			topicStatus = TopicStatus.DRAFT;
			break;
		}
		return topicStatus;
	}

	@Override
	public Page<Topic> filterTopics(String exclusiveStartOrderId) throws TableDoesNotExistException {
		
		return null;
	}
}
