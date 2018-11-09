package com.amazonaws.kshare;

import com.amazonaws.HttpMethod;
import org.codehaus.jettison.json.JSONObject;


public class Request {

	private HttpMethod httpMethod;
	private JSONObject body;
	private JSONObject requestContext;
	private JSONObject pathParameters;
	private String resource;
	
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
	public JSONObject getPathParameters() {
		return pathParameters;
	}
	public void setPathParameters(JSONObject pathParameters) {
		this.pathParameters = pathParameters;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	
}
