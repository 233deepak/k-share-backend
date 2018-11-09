package com.amazonaws.kshare.model;

public class User {

	private String userId;
	private String email;
	private String profilePicURL;
	private String name;
	private Long version;
	private SocialSite socialSite;

	public SocialSite getSocialSite() {
		return socialSite;
	}

	public void setSocialSite(SocialSite socialSite) {
		this.socialSite = socialSite;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfilePicURL() {
		return profilePicURL;
	}

	public void setProfilePicURL(String profilePicURL) {
		this.profilePicURL = profilePicURL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", email=" + email + ", profilePicURL=" + profilePicURL + ", name=" + name
				+ "]";
	}

}
