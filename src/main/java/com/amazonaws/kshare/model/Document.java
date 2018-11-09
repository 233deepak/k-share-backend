package com.amazonaws.kshare.model;

import java.util.List;

public class Document {

	private String htmlContent;
	private List<String> videoLinks;
	private List<DocLink> docLinks;
	private String guid;
	private String docId;
	private Long version;
	
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	
	public List<String> getVideoLinks() {
		return videoLinks;
	}
	public void setVideoLinks(List<String> videoLinks) {
		this.videoLinks = videoLinks;
	}
	public List<DocLink> getDocLinks() {
		return docLinks;
	}
	public void setDocLinks(List<DocLink> docLinks) {
		this.docLinks = docLinks;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "Document [htmlContent=" + htmlContent + ", videoLinks=" + videoLinks + ", docLinks=" + docLinks
				+ ", guid=" + guid + ", docId=" + docId + ", version=" + version + "]";
	}
	
	
	
}
