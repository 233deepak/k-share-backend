package com.amazonaws.kshare.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.dao.intf.UserDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.Page;
import com.amazonaws.kshare.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserService implements ResourceMethodHandler<User> {

	private UserDaoIntf userDao;
	private ObjectMapper objectMapper;
	
	public UserService(UserDaoIntf userDao, ObjectMapper objectMapper) {
		this.userDao = userDao;
		this.objectMapper = objectMapper;
	}

	@Override
	public User doGet(Request request) {
		if(request.getPathParameters()!=null && request.getPathParameters().has("user_id")) {
			try {
				String userId = request.getPathParameters().getString("user_id");
				return userDao.getUser(userId);
			} catch (JSONException | TableDoesNotExistException e) {
				e.printStackTrace();
			}
		}else if(request.getPathParameters()!=null && request.getPathParameters().has("email_Id") && request.getPathParameters().has("social_Site")) {
			try {
				String emailId = request.getPathParameters().getString("email_Id");
				String loggedInFrom = request.getPathParameters().getString("social_Site");
				return userDao.getUserByEmailIdAndSocialSite(emailId, loggedInFrom);
			} catch (JSONException | TableDoesNotExistException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Page<User> doPost(Request request) {
		
		return null;
	}

	@Override
	public User doPut(Request request) {
	   JSONObject requestBody = request.getBody();
		User user = null;
		try {
			user = objectMapper.readValue(requestBody.toString(), User.class);
			userDao.addUser(user);
		} catch (IOException | CouldNotCreateTopicException e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public void doOption(Request request) {
		
	}
	
	public void initializeUserTable() {
		try {
			userDao.createUserTable();
			InputStream fileURL = this.getClass().getClassLoader().getResourceAsStream("mockUsers.json");
			List<User> users = objectMapper.readValue(fileURL,
					objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
			for (int i = 0; i < 10; i++) {
				for(User user : users)
					userDao.addUser(user);
			}
		} catch (TableExistsException | IOException | CouldNotCreateTopicException e) {
			e.printStackTrace();
		}
	}

}
