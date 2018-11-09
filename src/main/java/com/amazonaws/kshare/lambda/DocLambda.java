package com.amazonaws.kshare.lambda;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.RequestMapper;
import com.amazonaws.kshare.configuration.AppConfig;
import com.amazonaws.kshare.model.Document;
import com.amazonaws.kshare.services.DocSevice;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DocLambda implements RequestHandler<Object, Object> {

	private DocSevice docService;
	private ObjectMapper objectMapper;

	public DocLambda() {
		this.docService = AppConfig.getInstance().docSevice();
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public Object handleRequest(Object input, Context context) {
		Request request = RequestMapper.mapRequest((Map<String, Object>) input);
		JSONObject response = new JSONObject();
		Document document;
		switch (request.getHttpMethod()) {
		case PUT:
			document = docService.doPut(request);
			response = prepareTopicResponse(document);
			break;
		case GET : 
			document = docService.doGet(request);
			response = prepareTopicResponse(document);
			break;
		case PATCH:
			docService.initializeDocumentTable();
			try {
				response.put("data", "docTable created sucessfully");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		default:
			break;
		}
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		return new GatewayResponse(response.toString(), headers, 200);
	}

	private JSONObject prepareTopicResponse(Object object) {
		JSONObject response = new JSONObject();
		try {
			response.put("data", new JSONObject(objectMapper.writeValueAsString(object)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
}
