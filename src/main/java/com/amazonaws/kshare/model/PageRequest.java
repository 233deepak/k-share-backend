package com.amazonaws.kshare.model;

import java.util.List;

public class PageRequest {

	private String serKey;
	private String exclusiveStartId;
	private int pageSize;
	private List<FilterCondition> filterConditions;
	
	public String getSerKey() {
		return serKey;
	}
	public void setSerKey(String serKey) {
		this.serKey = serKey;
	}
	public String getExclusiveStartId() {
		return exclusiveStartId;
	}
	public void setExclusiveStartId(String exclusiveStartId) {
		this.exclusiveStartId = exclusiveStartId;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<FilterCondition> getFilterConditions() {
		return filterConditions;
	}
	public void setFilterConditions(List<FilterCondition> filterConditions) {
		this.filterConditions = filterConditions;
	}
	
	
}
