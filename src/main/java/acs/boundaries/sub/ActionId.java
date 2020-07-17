package acs.boundaries.sub;

public class ActionId {

	private String domain;
	private String id;

	public ActionId() {
	}

	public ActionId(String domain, String id) {
		this.domain = domain;
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ActionId [domain=" + domain + ", id=" + id + "]";
	}

}
