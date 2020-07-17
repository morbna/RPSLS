package acs.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import acs.data.sub.ElementIdPk;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {

	private ElementIdPk elementId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private String userDomain;
	private String userEmail;
	private Double lat;
	private Double lng;
	private Map<String, Object> elementAttributes;

	private Set<ElementEntity> parentEntities;

	private Set<ElementEntity> childEntities;

	public ElementEntity() {
		this.elementId = new ElementIdPk();
		this.createdTimestamp = new Date();

		this.parentEntities = new HashSet<>();
		this.childEntities = new HashSet<>();
	}

	public ElementEntity(String domain, String id, String type, String name, Boolean active, Date createdTimestamp,
			String userDomain, String userEmail, Double lat, Double lng, Map<String, Object> elementAttributes) {
		this.elementId = new ElementIdPk(domain, id);
		this.type = type;
		this.name = name;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.userDomain = userDomain;
		this.userEmail = userEmail;
		this.lat = lat;
		this.lng = lng;
		this.elementAttributes = elementAttributes;

		this.parentEntities = new HashSet<>();
		this.childEntities = new HashSet<>();
	}

	@EmbeddedId
	public ElementIdPk getElementId() {
		return elementId;
	}

	public void setElementId(ElementIdPk elementId) {
		this.elementId = elementId;
	}

	@Transient
	public String getDomain() {
		return elementId.getDomain();
	}

	public void setDomain(String domain) {
		this.elementId.setDomain(domain);
	}

	@Transient
	public String getId() {
		return elementId.getId();
	}

	public void setId(String id) {
		this.elementId.setId(id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	@Lob
	@Convert(converter = MapToJsonConverter.class)
	public Map<String, Object> getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(Map<String, Object> elementAttributes) {
		this.elementAttributes = elementAttributes;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	public Set<ElementEntity> getParentEntities() {
		return parentEntities;
	}

	public void setParentEntities(Set<ElementEntity> parentEntities) {
		this.parentEntities = parentEntities;
	}

	@ManyToMany(mappedBy = "parentEntities", fetch = FetchType.LAZY)
	public Set<ElementEntity> getChildEntities() {
		return childEntities;
	}

	public void setChildEntities(Set<ElementEntity> childEntities) {
		this.childEntities = childEntities;
	}

	@Override
	public String toString() {
		return "ElementEntity [active=" + active + ", createdTimestamp=" + createdTimestamp + ", elementAttributes="
				+ elementAttributes + ", elementId=" + elementId + ", lat=" + lat + ", lng=" + lng + ", name=" + name
				+ ", type=" + type + ", userDomain=" + userDomain + ", userEmail=" + userEmail + "]";
	}

}