package acs.boundaries.sub;

public class ElementId {

	private String domain;
	private String id;

	public ElementId() {
		this.domain = "undefined";
		this.id = "undefined";
	}

	public ElementId(String domain, String id) {
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

	public void setID(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ElementId [domain=" + domain + ", id=" + id + "]";
	}

}
