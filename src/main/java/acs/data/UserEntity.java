package acs.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import acs.data.sub.UserIdPk;

@Entity
@Table(name = "USERS")
public class UserEntity {

	private UserIdPk userId;
	private UserRole role;
	private String username;
	private String avatar;

	public UserEntity() {
		this.userId = new UserIdPk();
		this.role = UserRole.PLAYER;
	}

	public UserEntity(String domain, String email, UserRole role, String username, String avatar) {
		this.userId = new UserIdPk(domain, email);
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	@EmbeddedId
	public UserIdPk getUserId() {
		return userId;
	}

	public void setUserId(UserIdPk userId) {
		this.userId = userId;
	}

	@Transient
	public String getDomain() {
		return userId.getDomain();
	}

	public void setDomain(String domain) {
		this.userId.setDomain(domain);
	}

	@Transient
	public String getEmail() {
		return userId.getEmail();
	}

	public void setEmail(String email) {
		this.userId.setEmail(email);
	}

	@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
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
		return "UserEntity [avatar=" + avatar + ", role=" + role + ", userId=" + userId + ", username=" + username
				+ "]";
	}

}
