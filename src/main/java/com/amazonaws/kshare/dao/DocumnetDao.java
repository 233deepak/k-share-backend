package com.amazonaws.kshare.dao;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.amazonaws.SdkClientException;
import com.amazonaws.kshare.dao.intf.DocumentDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.DocumentDoesNotExistException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.DocLink;
import com.amazonaws.kshare.model.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

public class DocumnetDao implements DocumentDaoIntf {

	private DynamoDbClient dynamoDb;
	private ObjectMapper objectMapper;

	public DocumnetDao(DynamoDbClient dynamoDb) {
		this.dynamoDb = dynamoDb;
		this.objectMapper = new ObjectMapper();
	}

	private static final String TABLENAME = "Document";
    private static final String DOC_ID = "DocId";

	@Override
	public void createDocumentTable() throws TableExistsException {
		try {
            dynamoDb.createTable(CreateTableRequest.builder()
                    .tableName(TABLENAME)
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(DOC_ID)
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(DOC_ID)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .build());
        } catch (ResourceInUseException e) {
            throw new TableExistsException("Document table already exists");
        }
	}

	@Override
	public Document addDocument(Document doc) throws CouldNotCreateTopicException {
		if (doc == null) {
            throw new IllegalArgumentException("CreateOrderRequest was null");
        }
        int tries = 0;
        while (tries < 10) {
            try {
            	 Map<String, AttributeValue> item = createDocumentItem(doc);
                dynamoDb.putItem(PutItemRequest.builder()
                        .tableName(TABLENAME)
                        .item(item)
                        .conditionExpression("attribute_not_exists(DocId)")
                        .build());
                
                doc.setDocId(item.get(DOC_ID).s());
                doc.setVersion(Long.valueOf(item.get("version").n()));
                return doc;
            } catch (ConditionalCheckFailedException e) {
                tries++;
            } catch (ResourceNotFoundException e) {
                throw e;
            }
        }
        throw new CouldNotCreateTopicException(
                "Unable to generate unique order id after 10 tries");
	}

	private Map<String, AttributeValue> createDocumentItem(Document doc) {
		Map<String, AttributeValue> item = new HashMap<>();
        item.put(DOC_ID, AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("version", AttributeValue.builder().n("1").build());
        item.put("guid",
                AttributeValue.builder().s(doc.getGuid()).build());
        item.put("htmlContent",
                AttributeValue.builder().s(doc.getHtmlContent()).build());
        try {
			if(doc.getVideoLinks() !=null) {
				String videoLinksJSON = objectMapper.writeValueAsString(doc.getVideoLinks());
				item.put("videoLinks",
						AttributeValue.builder().s(videoLinksJSON).build());
			}
			if(doc.getDocLinks() !=null) {
				String docLinksJSON = objectMapper.writeValueAsString(doc.getDocLinks());
				item.put("docLinks",
						AttributeValue.builder().s(docLinksJSON).build());
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
       
        return item;
	}

	@Override
	public Document getDocument(String documentId) throws TableDoesNotExistException {
		 try {
	            return Optional.ofNullable(
	            		dynamoDb.getItem(GetItemRequest.builder()
	                            .tableName(TABLENAME)
	                            .key(Collections.singletonMap(DOC_ID,
	                                    AttributeValue.builder().s(documentId).build()))
	                            .build()))
	                    .map(GetItemResponse::item)
	                    .map(this::convert)
	                    .orElseThrow(() -> new DocumentDoesNotExistException("Order "
	                            + documentId + " does not exist"));
	        } catch (AwsServiceException | SdkClientException | DocumentDoesNotExistException ee) {
	            throw new TableDoesNotExistException("Document table " + TABLENAME + " does not exist");
	        }
	}

	private Document convert(final Map<String, AttributeValue> item) {
		if (item == null || item.isEmpty()) {
			return null;
		}
        Document document =new Document();
        
		try {
			document.setDocId(item.get(DOC_ID).s());
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an DOC_ID attribute or it was not a String");
		}

		try {
			document.setVersion(Long.parseLong(item.get("version").n()));
		} catch (NullPointerException e) {
			throw new IllegalStateException("item did not have an version attribute or it was not a String");
		}

		try {
			document.setGuid(item.get("guid").s());
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalStateException("item did not have an preTaxAmount attribute or it was not a Number");
		}

		try {
			document.setHtmlContent(item.get("htmlContent").s());
		}  catch (NullPointerException  e) {
			throw new IllegalStateException("item did not have an htmlContent attribute ");
		}
		
		try {
			String videoLinksJSON = item.get("videoLinks").s();
			List<String> videoLinks =  objectMapper.readValue(videoLinksJSON, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
			document.setVideoLinks(videoLinks);
		}  catch (NullPointerException | IOException  e) {
			throw new IllegalStateException("item did not have an videoLinks attribute ");
		}
		
		try {
			String docLinksJSON = item.get("docLinks").s();
			List<DocLink> docLinks =  objectMapper.readValue(docLinksJSON, objectMapper.getTypeFactory().constructCollectionType(List.class, DocLink.class));
			document.setDocLinks(docLinks);
		}  catch (NullPointerException | IOException  e) {
			throw new IllegalStateException("item did not have an docLinks attribute ");
		}
		return document;
		
	}
}
