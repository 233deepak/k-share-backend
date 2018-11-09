package com.amazonaws.kshare.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.amazonaws.SdkClientException;
import com.amazonaws.kshare.dao.intf.UserDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.DocumentDoesNotExistException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.SocialSite;
import com.amazonaws.kshare.model.User;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

public class UserDao implements UserDaoIntf {
	
	private DynamoDbClient dynamoDb;
	
	public UserDao(DynamoDbClient dynamoDb) {
		this.dynamoDb = dynamoDb;
	}
	
	private static final String TABLENAME = "User";
    private static final String USER_ID = "UserId";

	@Override
	public void createUserTable() throws TableExistsException {
		try {
            dynamoDb.createTable(CreateTableRequest.builder()
                    .tableName(TABLENAME)
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(USER_ID)
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(USER_ID)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .build());
        } catch (ResourceInUseException e) {
            throw new TableExistsException("User table already exists");
        }
	}

	@Override
	public User addUser(User user) throws CouldNotCreateTopicException {
		if (user == null) {
            throw new IllegalArgumentException("CreateUserRequest was null");
        }
        int tries = 0;
        while (tries < 10) {
            try {
            	 Map<String, AttributeValue> item = createUsertItem(user);
                dynamoDb.putItem(PutItemRequest.builder()
                        .tableName(TABLENAME)
                        .item(item)
                        .conditionExpression("attribute_not_exists(UserId)")
                        .build());
                
                user.setUserId(item.get(USER_ID).s());
                user.setVersion(Long.valueOf(item.get("version").n()));
                return user;
            } catch (ConditionalCheckFailedException e) {
                tries++;
            } catch (ResourceNotFoundException e) {
                throw e;
            }
        }
        throw new CouldNotCreateTopicException(
                "Unable to generate unique user id after 10 tries");
	}

	private Map<String, AttributeValue> createUsertItem(User user) {
		Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ID, AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("version", AttributeValue.builder().n("1").build());
        item.put("name",
                AttributeValue.builder().s(user.getName()).build());
        item.put("email",
                AttributeValue.builder().s(user.getEmail()).build());
        item.put("profilePicURL",
                AttributeValue.builder().s(user.getProfilePicURL()).build());
        item.put("socialSite",
                AttributeValue.builder().s(user.getSocialSite().getName()).build());
        return item;
	}

	@Override
	public User getUser(String userId) throws TableDoesNotExistException {
		 try {
	            return Optional.ofNullable(
	            		dynamoDb.getItem(GetItemRequest.builder()
	                            .tableName(TABLENAME)
	                            .key(Collections.singletonMap(USER_ID,
	                                    AttributeValue.builder().s(userId).build()))
	                            .build()))
	                    .map(GetItemResponse::item)
	                    .map(this::convert)
	                    .orElseThrow(() -> new DocumentDoesNotExistException("User "
	                            + userId + " does not exist"));
	        } catch (AwsServiceException | SdkClientException | DocumentDoesNotExistException ee) {
	            throw new TableDoesNotExistException("Document table " + TABLENAME + " does not exist");
	        }
	}
	
	private User convert(final Map<String, AttributeValue> item) {
		if (item == null || item.isEmpty()) {
			return null;
		}
        User user =new User();
        
		try {
			user.setUserId(item.get(USER_ID).s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an USER_ID attribute or it was not a String");
		}

		try {
			user.setVersion(Long.parseLong(item.get("version").n()));
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an version attribute or it was not a String");
		}

		try {
			user.setEmail(item.get("email").s());
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalStateException("item did not have an email attribute ");
		}

		try {
			user.setName(item.get("name").s());
		}  catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an name attribute ");
		}
		
		try {
			user.setProfilePicURL((item.get("profilePicURL").s()));
		}  catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an profilePicURL attribute ");
		}
		
		try {
			user.setSocialSite(getSocialSite(item.get("socialSite").s()));
		}  catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an profilePicURL attribute ");
		}
		
		return user;
		
	}

	private SocialSite getSocialSite(String socialSiteString) {
		SocialSite socialSite;
		switch (socialSiteString) {
		case "facebook":
			socialSite = SocialSite.FACEBOOK;
			break;
		case "google":
			socialSite = SocialSite.GOOGLE;
			break;
		default:
			socialSite = SocialSite.GOOGLE;
			break;
		}
		return socialSite;
	}

	@Override
	public User getUserByEmailIdAndSocialSite(String emailId, String loggedInFrom) throws TableDoesNotExistException {
        
		ScanResponse result;
		String exclusiveStartOrderId = "";
		do {
			Map<String, AttributeValue> expressionAttributeValues = 
				    new HashMap<String, AttributeValue>();
				expressionAttributeValues.put(":email_Id", AttributeValue.builder().s(emailId).build()); 
				expressionAttributeValues.put(":social_Site", AttributeValue.builder().s(loggedInFrom).build());
			try {
				String filterExpression = "contains(email,:email_Id) AND contains(socialSite,:social_Site)";
				ScanRequest.Builder scanBuilder = ScanRequest.builder()
						                       .tableName(TABLENAME)
						                       .filterExpression(filterExpression)
						                       .expressionAttributeValues(expressionAttributeValues);
						                       //.limit(LIMIT);
				if (!isNullOrEmpty(exclusiveStartOrderId)) {
					scanBuilder.exclusiveStartKey(Collections.singletonMap(USER_ID,
							AttributeValue.builder().s(exclusiveStartOrderId).build()));
				}
				result = dynamoDb.scan(scanBuilder.build());
			} catch (ResourceNotFoundException e) {
				throw new TableDoesNotExistException("USER table " + TABLENAME + " does not exist");
			}
			
			if(!result.items().isEmpty()) {
				User user = convert(result.items().get(0));
				return user;
			}

			

			if (result.lastEvaluatedKey() != null && !result.lastEvaluatedKey().isEmpty()) {
				if ((!result.lastEvaluatedKey().containsKey(USER_ID)
						|| isNullOrEmpty(result.lastEvaluatedKey().get(USER_ID).s()))) {
					throw new IllegalStateException(
							"userId did not exist or was not a non-empty string in the lastEvaluatedKey");
				} else {
					exclusiveStartOrderId = result.lastEvaluatedKey().get(USER_ID).s();
				}
			}
		} while (! result.lastEvaluatedKey().isEmpty() );
		return null;
	
	}
	 private static boolean isNullOrEmpty(final String string) {
	        return string == null || string.isEmpty();
	}

}
