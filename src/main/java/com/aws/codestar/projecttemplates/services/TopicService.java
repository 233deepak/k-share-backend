package com.aws.codestar.projecttemplates.services;

import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.dao.intf.TopicDaoIntf;
import com.aws.codestar.projecttemplates.common.Request;
import com.aws.codestar.projecttemplates.configuration.AppConfig;
import com.aws.codestar.projecttemplates.exception.TableExistsException;
import com.aws.codestar.projecttemplates.model.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TopicService implements ResourceMethodHandler<Topic> {

	private TopicDaoIntf topicDao;
	private ObjectMapper objectMapper;

	public TopicService() {
		this.topicDao = AppConfig.getInstance().topicDao();
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void doGet(Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public Topic doPost(Request request) {
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	public Topic doPut(Request request) {
		JSONObject requestBody = request.getBody();
		Topic topic = null;
		try {
			topic = objectMapper.readValue(requestBody.toString(), Topic.class);
			topicDao.addTopic(topic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topic;
		
	}

	@Override
	public void doOption(Request request) {
		// TODO Auto-generated method stub

	}

	public void createTable() {
		try {
			topicDao.createTopicTable();
		} catch (TableExistsException e) {
			e.printStackTrace();
		}
	}
	
}
