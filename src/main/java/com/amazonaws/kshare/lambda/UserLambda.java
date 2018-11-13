package com.amazonaws.kshare.lambda;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.RequestMapper;
import com.amazonaws.kshare.configuration.AppConfig;
import com.amazonaws.kshare.model.User;
import com.amazonaws.kshare.services.UserService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLambda implements RequestHandler<Object, Object> {

	private ObjectMapper objectMapper;
	private UserService userService;

	public UserLambda() {
		this.objectMapper = AppConfig.getInstance().objectMapper();
		this.userService = AppConfig.getInstance().userService();
	}

	@Override
	public Object handleRequest(Object input, Context context) {
		Request request = RequestMapper.mapRequest((Map<String, Object>) input);
		JSONObject response = new JSONObject();
		User user;
		switch (request.getHttpMethod()) {
		case PUT:
			user = userService.doPut(request);
			response = prepareTopicResponse(user);
			break;
		case GET:
			user = userService.doGet(request);
			response = prepareTopicResponse(user);
			break;
		case PATCH:
			userService.initializeUserTable();
			try {
				response.put("data", "userTable created sucessfully");
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
				response.put("data", new JSONObject(objectMapper.writeValueAsString(object)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

}
