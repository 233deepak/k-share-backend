package com.amazonaws.kshare.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.amazonaws.kshare.dao.intf.CommentDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateCommentException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.Comment;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public class CommentDao implements CommentDaoIntf {

	private DynamoDbClient dynamoDb;
	private ObjectMapper objectMapper;
	private SimpleDateFormat dateFormat;

	public CommentDao(DynamoDbClient dynamoDb, ObjectMapper objectMapper) {
		this.dynamoDb = dynamoDb;
		this.objectMapper = objectMapper;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
	}

	private static final String TABLENAME = "Comment";
	private static final String COMMENT_ID = "CommentId";
	private static final String TOPIC_ID = "TopicId";

	@Override
	public void createCommentTable() throws TableExistsException {
		Collection<KeySchemaElement> schemaElements = new ArrayList<>();
		schemaElements.add(KeySchemaElement.builder()
                            .attributeName(TOPIC_ID)
                            .keyType(KeyType.HASH)
                            .build());
		schemaElements.add(KeySchemaElement.builder()
                .attributeName(COMMENT_ID)
                .keyType(KeyType.RANGE)
                .build());
		
		Collection<AttributeDefinition> attributeDefinitions = new ArrayList<>();
		attributeDefinitions.add(AttributeDefinition.builder()
                            .attributeName(COMMENT_ID)
                            .attributeType(ScalarAttributeType.S)
                            .build());
		attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName(TOPIC_ID)
                .attributeType(ScalarAttributeType.S)
                .build());
		try {
            dynamoDb.createTable(CreateTableRequest.builder()
                    .tableName(TABLENAME)
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .keySchema(schemaElements)
                    .attributeDefinitions(attributeDefinitions)
                    .build());
        } catch (ResourceInUseException e) {
            throw new TableExistsException("Comment table already exists");
        }
	}

	@Override
	public Comment addComment(Comment comment) throws CouldNotCreateCommentException {
		if (comment == null) {
            throw new IllegalArgumentException("CreateCommentRequest was null");
        }
        int tries = 0;
        while (tries < 10) {
            try {
            	 Map<String, AttributeValue> item = createCommentItem(comment);
                dynamoDb.putItem(PutItemRequest.builder()
                        .tableName(TABLENAME)
                        .item(item)
                        .conditionExpression("attribute_not_exists(CommentId)")
                        .build());
                
                comment.setCommentId(item.get(COMMENT_ID).s());
                comment.setVersion(Long.valueOf(item.get("version").n()));
                return comment;
            } catch (ConditionalCheckFailedException e) {
                tries++;
            } catch (ResourceNotFoundException e) {
                throw e;
            }
        }
        throw new CouldNotCreateCommentException(
                "Unable to generate unique order id after 10 tries");
	}

	private Map<String, AttributeValue> createCommentItem(Comment comment) {
		Map<String, AttributeValue> item = new HashMap<>();
        item.put(COMMENT_ID, AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("version", AttributeValue.builder().n("1").build());
        item.put(TOPIC_ID,
                AttributeValue.builder().s(comment.getTopicId()).build());
		if (comment.getCommentedOn() != null) {
			String commentedOn = dateFormat.format(comment.getCommentedOn());
			item.put("commentedOn",
	                AttributeValue.builder().s(commentedOn).build());
		}
        item.put("commentedBy",
                AttributeValue.builder().s(comment.getCommentedBy()).build());
        item.put("userID",
                AttributeValue.builder().s(comment.getUserID()).build());
        item.put("userImageURL",
                AttributeValue.builder().s(comment.getUserImageURL()).build());
        item.put("commentText",
                AttributeValue.builder().s(comment.getCommentText()).build());
        return item;
	}

	@Override
	public List<Comment> getCommentsForTopic(String topicId) throws TableDoesNotExistException {

		Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":topic_Id", AttributeValue.builder().s(topicId).build());
		List<Comment> comments = new ArrayList<>();
		QueryResponse result;
		String exclusiveStartOrderId = "";
		do {

			try {
				QueryRequest.Builder queryBuilder = QueryRequest.builder()
						.tableName(TABLENAME)
						.keyConditionExpression("TopicId = :topic_Id")
						.expressionAttributeValues(expressionAttributeValues);
				if (!isNullOrEmpty(exclusiveStartOrderId)) {
					queryBuilder.exclusiveStartKey(Collections.singletonMap(COMMENT_ID,
							AttributeValue.builder().s(exclusiveStartOrderId).build()));
				}
				result = dynamoDb.query(queryBuilder.build());
			} catch (ResourceNotFoundException e) {
				throw new TableDoesNotExistException("Comment table " + TABLENAME + " does not exist");
			}

			final List<Comment> currentComments = result.items().stream().map(this::convert)
					.collect(Collectors.toList());
			comments.addAll(currentComments);

			if (result.lastEvaluatedKey() != null && !result.lastEvaluatedKey().isEmpty()) {
				if ((!result.lastEvaluatedKey().containsKey(TOPIC_ID)
						|| isNullOrEmpty(result.lastEvaluatedKey().get(TOPIC_ID).s()))) {
					throw new IllegalStateException(
							"orderId did not exist or was not a non-empty string in the lastEvaluatedKey");
				} else {
					exclusiveStartOrderId = result.lastEvaluatedKey().get(TOPIC_ID).s();
				}
			}
		} while (!result.lastEvaluatedKey().isEmpty());
		return comments;
	}

	private boolean isNullOrEmpty(String exclusiveStartOrderId) {
		return exclusiveStartOrderId == null || exclusiveStartOrderId.isEmpty();
	}

	private Comment convert(final Map<String, AttributeValue> item) {
		if (item == null || item.isEmpty()) {
			return null;
		}
		
		Comment comment = new Comment();
		try {
			comment.setCommentId(item.get(COMMENT_ID).s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an COMMENT_ID attribute or it was not a String");
		}

		try {
			comment.setVersion(Long.parseLong(item.get("version").n()));
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an version attribute or it was not a String");
		}

		try {
			comment.setCommentedBy(item.get("commentedBy").s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an commentedBy attribute ");
		}
		
		try {
			comment.setUserImageURL(item.get("userImageURL").s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an commentedBy attribute ");
		}
		
		try {
			comment.setUserID(item.get("userID").s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an commentedBy attribute ");
		}

		try {
			String commentedOn = item.get("commentedOn").s();
			comment.setCommentedOn(dateFormat.parse(commentedOn));
		} catch (NullPointerException | NumberFormatException | ParseException e) {
		}
		try {
			comment.setTopicId(item.get(TOPIC_ID).s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an topicId attribute ");
		}
		try {
			comment.setCommentText(item.get("commentText").s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an commentText attribute ");
		}

		return comment;

	}

}
