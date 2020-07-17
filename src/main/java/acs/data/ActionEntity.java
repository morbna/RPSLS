package acs.data;

import java.util.Date;
import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import acs.data.sub.ActionIdPk;

@Entity
@Table(name = "ACTIONS")
public class ActionEntity {

	private ActionIdPk actionId;
	private String type;
	private String elementDomain;
	private String elementId;
	private Date createdTimestamp;
	private String userDomain;
	private String userEmail;
	private Map<String, Object> actionAttributes;

	public ActionEntity() {
		this.actionId = new ActionIdPk();
		this.createdTimestamp = new Date();
	}

	public ActionEntity(String domain, String id, String type, String elementDomain, String elementId,
			Date createdTimestamp, String userDomain, String userEmail, Map<String, Object> actionAttributes) {
		this.actionId = new ActionIdPk(domain, id);
		this.type = type;
		this.elementDomain = elementDomain;
		this.elementId = elementId;
		this.createdTimestamp = createdTimestamp;
		this.userDomain = userDomain;
		this.userEmail = userEmail;
		this.actionAttributes = actionAttributes;
	}

	@EmbeddedId
	public ActionIdPk getActionId() {
		return actionId;
	}

	public void setActionId(ActionIdPk actionId) {
		this.actionId = actionId;
	}

	@Transient
	public String getDomain() {
		return actionId.getDomain();
	}

	public void setDomain(String domain) {
		this.actionId.setDomain(domain);
	}

	@Transient
	public String getId() {
		return actionId.getId();
	}

	public void setId(String id) {
		this.actionId.setId(id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getElementDomain() {
		return elementDomain;
	}

	public void setElementDomain(String elementDomain) {
		this.elementDomain = elementDomain;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getUserDomain() {
		return userDomain;
	}

	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Lob
	@Convert(converter = MapToJsonConverter.class)
	public Map<String, Object> getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(Map<String, Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}

	@Override
	public String toString() {
		return "ActionEntity [actionAttributes=" + actionAttributes + ", actionId=" + actionId + ", createdTimestamp="
				+ createdTimestamp + ", elementDomain=" + elementDomain + ", elementId=" + elementId + ", type=" + type
				+ ", userDomain=" + userDomain + ", userEmail=" + userEmail + "]";
	}

}
