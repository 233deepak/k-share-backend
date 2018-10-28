package com.aws.codestar.projecttemplates.common;

import com.amazonaws.HttpMethod;
import org.codehaus.jettison.json.JSONObject;


public class Request {

	private HttpMethod httpMethod;
	private JSONObject body;
	private JSONObject requestContext;
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
	public JSONObject getBody() {
		return body;
	}
	public void setBody(JSONObject body) {
		this.body = body;
	}
	public JSONObject getRequestContext() {
		return requestContext;
	}
	public void setRequestContext(JSONObject requestContext) {
		this.requestContext = requestContext;
	}
	
	

}
