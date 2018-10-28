package com.aws.codestar.projecttemplates.model;

public enum TopicStatus {

	DRAFT("draft"), PUBLISHED("published"), VERIFIED("verfied");

	private String status;

	TopicStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	
}
