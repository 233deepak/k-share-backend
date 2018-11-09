package com.amazonaws.kshare.dao.intf;

import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.User;

public interface UserDaoIntf {
	
	public void createUserTable() throws TableExistsException;

	public User addUser(User user) throws CouldNotCreateTopicException;
	
	public User getUser(String userId) throws TableDoesNotExistException;
	
	public User getUserByEmailIdAndSocialSite(String emailId ,String loggedInFrom) throws TableDoesNotExistException;
	
}
