package com.amazonaws.kshare.lambda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.RequestMapper;
import com.amazonaws.kshare.configuration.AppConfig;
import com.amazonaws.kshare.model.Comment;
import com.amazonaws.kshare.model.CommentResponse;
import com.amazonaws.kshare.model.Page;
import com.amazonaws.kshare.model.Topic;
import com.amazonaws.kshare.services.TopicService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TopicLambda implements RequestHandler<Object, Object> {

	private TopicService topicService;
	private ObjectMapper objectMapper;

	public TopicLambda() {
		this.topicService = AppConfig.getInstance().topicService();
		this.objectMapper = new ObjectMapper();
	}
	
	private static final String COMMENT = "comment";

	@Override
	public Object handleRequest(Object input, Context context) {

		Request request = RequestMapper.mapRequest((Map<String, Object>) input);
		JSONObject response = new JSONObject();
		
		switch (request.getHttpMethod()) {
		case PUT:
			if (!request.getResource().contains(COMMENT)) {
				Topic topic = topicService.doPut(request);
				response = prepareTopicResponse(topic);
			} else {
				Comment comment = topicService.putComment(request);
				response = prepareTopicResponse(comment);
			}

			break;
		case GET:
			if (!request.getResource().contains(COMMENT)) {
				topicService.doGet(request);
			}else {
				List<Comment> comments = topicService.getAllCommentsForTopic(request);
				CommentResponse commentResponse =new CommentResponse();
				commentResponse.setComments(comments);
				commentResponse.setCount(comments.size());
				response = prepareTopicResponse(commentResponse);
			}
			break;
		case POST :
			 Page<Topic> page = topicService.doPost(request);
			 response = prepareTopicResponse(page);
			 break;
		case PATCH:
			if (!request.getResource().contains(COMMENT)) {
				topicService.intializeTopicTable();
				try {
					response.put("data", "Topic Table created and intialized");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				topicService.initializeCommetTalbe();
				try {
					response.put("data", "Comment Table created and intialized");
				} catch (JSONException e) {
					e.printStackTrace();
				}
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