package com.amazonaws.kshare.dao.intf;

import java.util.List;

import com.amazonaws.kshare.exception.CouldNotCreateCommentException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.Comment;

public interface CommentDaoIntf {

	public void createCommentTable() throws TableExistsException;

	public Comment addComment(Comment comment) throws CouldNotCreateCommentException;

	public List<Comment> getCommentsForTopic(String topicId) throws TableDoesNotExistException;

}
