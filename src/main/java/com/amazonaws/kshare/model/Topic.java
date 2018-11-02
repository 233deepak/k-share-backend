package com.amazonaws.kshare.model;

import java.util.Date;

public class Topic {

	private String topicId;
	private Long version;
	private String guid;
	private String title;
	private Date createdOn;
	private String createdBy;
	private boolean hasVideos;
	private boolean hasNotes;
	private int views;
	private TopicStatus status;
	private String tags;
	private String documentId;
	private String category;
	
	

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public boolean isHasVideos() {
		return hasVideos;
	}

	public void setHasVideos(boolean hasVideos) {
		this.hasVideos = hasVideos;
	}

	public boolean isHasNotes() {
		return hasNotes;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public TopicStatus getStatus() {
		return status;
	}

	public void setStatus(TopicStatus status) {
		this.status = status;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Topic [guid=" + guid + ", title=" + title + ", createdOn=" + createdOn + ", createdBy=" + createdBy
				+ ", hasVideos=" + hasVideos + ", hasNotes=" + hasNotes + ", views=" + views + ", status=" + status
				+ ", tags=" + tags + ", documentId=" + documentId + "]";
	}

}
