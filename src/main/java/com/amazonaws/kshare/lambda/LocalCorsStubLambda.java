package com.amazonaws.kshare.lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;

public class LocalCorsStubLambda implements RequestHandler<Object, Object> {

	@Override
	public Object handleRequest(Object input, Context context) {
		Map<String, String> headers = new HashMap<>();
		//headers.put("Content-Type", "application/json");
		headers.put("Access-Control-Allow-Origin", "*");
		headers.put("Access-Control-Allow-Methods", "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT");
		headers.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token");
		return new GatewayResponse("", headers, 200);
	}

}
