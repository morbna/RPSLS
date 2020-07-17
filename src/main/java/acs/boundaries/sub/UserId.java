package acs.boundaries.sub;

public class UserId {

	private String domain;
	private String email;

	public UserId() {
		this.domain = "undefined";
		this.email = "undefined";
	}

	public UserId(String domain, String email) {
		this.domain = domain;
		this.email = email;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserID [domain=" + domain + ", email=" + email + "]";
	}

}
