package com.fb.fb_demo;

public class FbUser {
	private String userId;
	private String access_token;
	
	public FbUser(String userId, String access_token) {
	
		setUserId(userId);
		setAccess_token(access_token);
	}
	public String getUserId() {
		return userId;
	}
	private void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccess_token() {
		return access_token;
	}
	private void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	

}
