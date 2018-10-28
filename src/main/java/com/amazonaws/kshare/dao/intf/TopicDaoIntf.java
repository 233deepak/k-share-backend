package com.amazonaws.kshare.dao.intf;

import com.aws.codestar.projecttemplates.exception.CouldNotCreateTopicException;
import com.aws.codestar.projecttemplates.exception.TableExistsException;
import com.aws.codestar.projecttemplates.model.Topic;

public interface TopicDaoIntf {

	public void createTopicTable() throws TableExistsException;

	public Topic addTopic(Topic topic) throws CouldNotCreateTopicException;
}
