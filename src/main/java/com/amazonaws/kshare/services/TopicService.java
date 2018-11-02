package com.amazonaws.kshare.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.configuration.AppConfig;
import com.amazonaws.kshare.dao.intf.TopicDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.Page;
import com.amazonaws.kshare.model.PageRequest;
import com.amazonaws.kshare.model.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TopicService implements ResourceMethodHandler<Topic> {

	private TopicDaoIntf topicDao;
	private ObjectMapper objectMapper;

	public TopicService() {
		this.topicDao = AppConfig.getInstance().topicDao();
		this.objectMapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		objectMapper.setDateFormat(df);
	}

	@Override
	public void doGet(Request request) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<Topic> doPost(Request request) {

		try {
			PageRequest pageRequest = objectMapper.readValue(request.getBody().toString(), PageRequest.class);
			return topicDao.getTopics(pageRequest);
		} catch (TableDoesNotExistException | IOException e) {
			e.printStackTrace();
		}
		return null;

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

	public void createTable() throws TableExistsException {
		topicDao.createTopicTable();

	}

	public void intializeTopicTable() {

		/*try {
			createTable();
			InputStream fileURL = this.getClass().getClassLoader().getResourceAsStream("mockTopics.json");
			List<Topic> topics = objectMapper.readValue(fileURL, objectMapper.getTypeFactory().constructCollectionType(List.class, Topic.class));
			//String topicJSON = "{\"guid\":\"FDSNNN2i01\",\"title\":\"Spanning Tree\",\"createdOn\":1443664800000,\"createdBy\":\"Eric Domain\",\"hasVideos\":true,\"hasNotes\":true,\"views\":10,\"status\":\"DRAFT\",\"tags\":\"bput,data\",\"documentId\":\"1234jbjsa\",\"category\":\"computer science\"}";
			for (int i = 0; i < 10; i++) {
				for(Topic topic :topics)
				topicDao.addTopic(topic);
			}
		} catch (TableExistsException | IOException | CouldNotCreateTopicException e) {
			e.printStackTrace();
		}*/
		 
	}

}
