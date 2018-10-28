package com.aws.codestar.projecttemplates.common;

import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.HttpMethod;

public class RequestMapper {

	public static final String HTTPMETHOD = "httpMethod";
	public static final String BODY = "body";
	public static final String RESOURCE = "resource";
	public static final String REQUESTCONTEXT = "requestContext";

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

		return request;
	}
}
