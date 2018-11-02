package com.amazonaws.kshare.model;

import java.util.List;

public class Page<T> {

	private List<T> topics;
	private String lastEvaluatedKey;

	public List<T> getTopics() {
		return topics;
	}

	public void setTopics(List<T> topics) {
		this.topics = topics;
	}

	public String getLastEvaluatedKey() {
		return lastEvaluatedKey;
	}

	public void setLastEvaluatedKey(String lastEvaluatedKey) {
		this.lastEvaluatedKey = lastEvaluatedKey;
	}

}
