package com.amazonaws.kshare;

import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.HttpMethod;

public class RequestMapper {

	public static final String HTTPMETHOD = "httpMethod";
	public static final String BODY = "body";
	public static final String RESOURCE = "resource";
	public static final String REQUESTCONTEXT = "requestContext";
	public static final String PATHPARAMETERS = "pathParameters";

	public static Request mapRequest(Map<String, Object> paylod) {

		Request request = new Request();
		String requestMethod = (String) paylod.get(HTTPMETHOD);
		HttpMethod httpMethod = null;
		switch (requestMethod) {
		case "GET":
			httpMethod = HttpMethod.GET;
			break;
		case "POST":
			httpMethod = HttpMethod.POST;
			break;
		case "PUT":
			httpMethod = HttpMethod.PUT;
			break;
		case "PATCH":
			httpMethod = HttpMethod.PATCH;
			break;
		}

		request.setHttpMethod(httpMethod);
		if (paylod.containsKey(BODY) && paylod.get(BODY) != null) {
			try {
				request.setBody(new JSONObject((String) paylod.get(BODY)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (paylod.containsKey(REQUESTCONTEXT) && paylod.get(REQUESTCONTEXT) != null) {
			request.setRequestContext(new JSONObject((Map) paylod.get(REQUESTCONTEXT)));
        }
		
		if (paylod.containsKey(PATHPARAMETERS) && paylod.get(PATHPARAMETERS) != null) {
			request.setPathParameters(new JSONObject((Map) paylod.get(PATHPARAMETERS)));
        }
		
		if (paylod.containsKey(RESOURCE) && paylod.get(RESOURCE) != null) {
            request.setResource((String) paylod.get(RESOURCE));
		}

		return request;
	}
}
