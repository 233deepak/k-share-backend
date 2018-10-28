package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class UserHandler implements RequestHandler<Object, Object> {

	@Override
	public Object handleRequest(Object input, Context context) {
		return context;
		/* AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	        		.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://docker.for.mac.localhost:8000", "us-east-2")).build();
		 
		 DynamoDB dynamoDB = new DynamoDB(client);
		 String tableName = "User";

	        try {
	            System.out.println("Attempting to create table; please wait...");
	            Table table = dynamoDB.createTable(tableName,
	                Arrays.asList(new KeySchemaElement("emailId", KeyType.HASH), // Partition
	                                                                          // key
	                    new KeySchemaElement("firstName", KeyType.RANGE)), // Sort key
	                Arrays.asList(new AttributeDefinition("LastName", ScalarAttributeType.N)
	                    ),
	                new ProvisionedThroughput(10L, 10L));
	            table.waitForActive();
	            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

	        }
	        catch (Exception e) {
	            System.err.println("Unable to create table: ");
	            System.err.println(e.getMessage());
	        }
		 
		 Map<String, String> headers = new HashMap<>();
	        headers.put("Content-Type", "application/json");
	        String response = "{\"Output\" : \"Hello User!\"}";
	     return new GatewayResponse(response, headers, 200); */
	
	}

}
