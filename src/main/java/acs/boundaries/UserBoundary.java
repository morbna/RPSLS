package acs.boundaries;

import acs.boundaries.sub.UserId;

public class UserBoundary {

	private UserId userId;
	private String role;
	private String username;
	private String avatar;

	public UserBoundary() {
	}

	public UserBoundary(UserId userId, String role, String username, String avatar) {
		this.userId = userId;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	public UserBoundary(NewUserDetails details) {
		super();
		this.userId = new UserId(null, details.getEmail());
		this.role = details.getRole();
		this.username = details.getUsername();
		this.avatar = details.getAvatar();
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}

}
