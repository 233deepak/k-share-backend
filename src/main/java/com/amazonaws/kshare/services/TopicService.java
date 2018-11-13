package com.amazonaws.kshare.services;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.dao.intf.CommentDaoIntf;
import com.amazonaws.kshare.dao.intf.TopicDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateCommentException;
import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.Comment;
import com.amazonaws.kshare.model.Page;
import com.amazonaws.kshare.model.PageRequest;
import com.amazonaws.kshare.model.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TopicService implements ResourceMethodHandler<Topic> {

	private TopicDaoIntf topicDao;
	private ObjectMapper objectMapper;
	private CommentDaoIntf commentDao;

	public TopicService(CommentDaoIntf commentDao, TopicDaoIntf topicDao) {
		this.commentDao = commentDao;
		this.topicDao = topicDao;
		this.objectMapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		objectMapper.setDateFormat(df);
	}

	@Override
	public Topic doGet(Request request) {
		return null;

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
			if (request.getPathParameters() != null && request.getPathParameters().has("topic_id"))
				topic = topicDao.updateTopic(topic);
			else {
				topic.setCreatedOn(new Date());
				topicDao.addTopic(topic);
			}
				

		} catch (Exception e) {
			e.printStackTrace();
		}
		return topic;

	}

	@Override
	public void doOption(Request request) {

	}

	public void createTable() throws TableExistsException {
		topicDao.createTopicTable();

	}

	public Comment putComment(Request request) {
		JSONObject requestBody = request.getBody();
		Comment comment = null;
		try {
			comment = objectMapper.readValue(requestBody.toString(), Comment.class);
			if(request.getPathParameters()!=null && request.getPathParameters().has("topic_id")) {
				String topicId = request.getPathParameters().getString("topic_id");
				comment.setTopicId(topicId);
				commentDao.addComment(comment);
			}else {
				throw new IllegalArgumentException("comment should be associated wit a topic");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comment;
	}
	
	public List<Comment> getAllCommentsForTopic(Request request){
		List<Comment> comments = new ArrayList<>();
		if(request.getPathParameters()!=null && request.getPathParameters().has("topic_id")) {
			try {
				String topicId = request.getPathParameters().getString("topic_id");
				comments = commentDao.getCommentsForTopic(topicId);
			} catch (JSONException | TableDoesNotExistException e) {
				e.printStackTrace();
			}
		}else {
			throw new IllegalArgumentException("topic id must be provided for this operation");
		}
		return comments;
	}
	
	public void intializeTopicTable() {

		try {
			createTable();
			InputStream fileURL = this.getClass().getClassLoader().getResourceAsStream("mockTopics.json");
			List<Topic> topics = objectMapper.readValue(fileURL,
					objectMapper.getTypeFactory().constructCollectionType(List.class, Topic.class));
			for (int i = 0; i < 10; i++) {
				for (Topic topic : topics)
					topicDao.addTopic(topic);
			}
		} catch (TableExistsException | IOException | CouldNotCreateTopicException e) {
			e.printStackTrace();
		}
	}
	
	public void initializeCommetTalbe() {
		try {
			commentDao.createCommentTable();
			InputStream fileURL = this.getClass().getClassLoader().getResourceAsStream("mockComments.json");
			List<Comment> comments = objectMapper.readValue(fileURL, objectMapper.getTypeFactory().constructCollectionType(List.class, Comment.class));
			for (int i = 0; i < 10; i++) {
				for(Comment comment :comments)
					commentDao.addComment(comment);
			}
		} catch (TableExistsException | IOException | CouldNotCreateCommentException e) {
			e.printStackTrace();
		}
	}

}
