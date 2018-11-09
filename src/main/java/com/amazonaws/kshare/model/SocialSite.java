package com.amazonaws.kshare.model;

public enum SocialSite {

	FACEBOOK("facebook"), GOOGLE("google");

	private final String name;

	SocialSite(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	
}
