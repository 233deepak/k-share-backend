package com.aws.codestar.projecttemplates.lambda;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.aws.codestar.projecttemplates.common.Request;
import com.aws.codestar.projecttemplates.common.RequestMapper;
import com.aws.codestar.projecttemplates.configuration.AppConfig;
import com.aws.codestar.projecttemplates.model.Topic;
import com.aws.codestar.projecttemplates.services.TopicService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TopicLambda implements RequestHandler<Object, Object> {

	private TopicService topicService;
	private ObjectMapper objectMapper;

	public TopicLambda() {
		this.topicService = AppConfig.getInstance().topicService();
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public Object handleRequest(Object input, Context context) {

		Request request = RequestMapper.mapRequest((Map<String, Object>) input);
		topicService.createTable();
		Topic topic = null;
		switch (request.getHttpMethod()) {
		case PUT:
			topic = topicService.doPut(request);
			break;
		case GET:
			topicService.doGet(request);
			break;
		default:
			break;
		}

		JSONObject response = new JSONObject();
		try {
			response.put("topic", objectMapper.writeValueAsString(topic));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		return new GatewayResponse(response.toString(), headers, 200);
	}

}