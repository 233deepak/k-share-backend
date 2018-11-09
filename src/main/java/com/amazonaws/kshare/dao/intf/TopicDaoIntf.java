package com.amazonaws.kshare.dao.intf;

import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.exception.UnableToUpdateException;
import com.amazonaws.kshare.model.Page;
import com.amazonaws.kshare.model.PageRequest;
import com.amazonaws.kshare.model.Topic;

public interface TopicDaoIntf {

	public void createTopicTable() throws TableExistsException;

	public Topic addTopic(Topic topic) throws CouldNotCreateTopicException;
	
    public Page<Topic> getTopics(PageRequest pageRequest) throws TableDoesNotExistException;
    
    public Page<Topic> filterTopics(String exclusiveStartOrderId) throws TableDoesNotExistException;
    
    public Topic updateTopic(Topic topic) throws UnableToUpdateException, TableDoesNotExistException ;
}
