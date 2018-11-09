package com.amazonaws.kshare.model;

import java.util.Date;

public class Comment {

	private String commentId;
	private String topicId;
	private Date commentedOn;
	private String commentedBy;
	private String userImageURL;
	private String userID;
	private String commentText;
	private Long version;

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public Date getCommentedOn() {
		return commentedOn;
	}

	public void setCommentedOn(Date commentedOn) {
		this.commentedOn = commentedOn;
	}

	public String getCommentedBy() {
		return commentedBy;
	}

	public void setCommentedBy(String commentedBy) {
		this.commentedBy = commentedBy;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getUserImageURL() {
		return userImageURL;
	}

	public void setUserImageURL(String userImageURL) {
		this.userImageURL = userImageURL;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
  
}
