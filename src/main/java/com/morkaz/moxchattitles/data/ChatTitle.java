package com.morkaz.moxchattitles.data;

public class ChatTitle {

	private String title;
	private String permission;
	private String titleIndex;

	public ChatTitle(String title, String titleIndex, String permission) {
		this.title = title;
		this.permission = permission;
		this.titleIndex = titleIndex;
	}

	public String getTitle() {
		return title;
	}

	public String getPermission() {
		return permission;
	}

	public String getTitleIndex() {
		return titleIndex;
	}
}
