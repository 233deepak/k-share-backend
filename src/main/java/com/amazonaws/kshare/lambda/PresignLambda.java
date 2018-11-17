package com.amazonaws.kshare.lambda;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.RequestMapper;
import com.amazonaws.kshare.configuration.AppConfig;
import com.amazonaws.kshare.services.PresignedURLGenerator;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PresignLambda implements RequestHandler<Object, Object> {

	private static final String FILE_NAME = "fileName";
	private static final String FILE_TYPE = "fileType";
	private ObjectMapper objectMapper;
	
   
	public PresignLambda() {
		this.objectMapper = AppConfig.getInstance().objectMapper();
	}

	@Override
	public Object handleRequest(Object input, Context context) {
		Request request = RequestMapper.mapRequest((Map<String, Object>) input);
		JSONObject response = new JSONObject();
		URL url = null;
		switch (request.getHttpMethod()) {
		case POST:
			JSONObject body = request.getBody();
			try {
				String fileName = body.getString(FILE_NAME);
				String fileType = body.getString(FILE_TYPE);
				url = PresignedURLGenerator.generateURL(fileName, fileType);
				response = prepareTopicResponse(url);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Access-Control-Allow-Origin", "*");
		return new GatewayResponse(response.toString(), headers, 200);
	}

	private JSONObject prepareTopicResponse(Object object) {
		JSONObject response = new JSONObject();
		try {
			if (object != null)
				response.put("data", object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
}
