package acs.logic.util;

import org.springframework.stereotype.Component;

import acs.data.ActionEntity;
import acs.data.ElementEntity;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.sub.ActionId;
import acs.boundaries.sub.CreatedBy;
import acs.boundaries.sub.Element;
import acs.boundaries.sub.ElementId;
import acs.boundaries.sub.InvokedBy;
import acs.boundaries.sub.Location;
import acs.boundaries.sub.UserId;

@Component
public class EntityConverter {

	public UserBoundary fromEntity(UserEntity entity) {
		UserBoundary boundary = new UserBoundary();

		boundary.setUserId(new UserId(entity.getDomain(), entity.getEmail()));
		boundary.setRole(entity.getRole().toString());
		boundary.setUsername(entity.getUsername());
		boundary.setAvatar(entity.getAvatar());

		return boundary;
	}

	public UserEntity toEntity(UserBoundary boundary) {
		UserEntity entity = new UserEntity();

		if (boundary.getUserId() != null) {
			entity.setDomain(boundary.getUserId().getDomain());
			entity.setEmail(boundary.getUserId().getEmail());
		}
		if (boundary.getRole() != null)
			entity.setRole(UserRole.valueOf(boundary.getRole()));

		entity.setUsername(boundary.getUsername());
		entity.setAvatar(boundary.getAvatar());

		return entity;
	}

	public ActionBoundary fromEntity(ActionEntity entity) {
		ActionBoundary boundary = new ActionBoundary();

		boundary.setActionId(new ActionId(entity.getDomain(), entity.getId()));
		boundary.setType(entity.getType());
		boundary.setElement(new Element(new ElementId(entity.getDomain(), entity.getElementId())));
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		boundary.setInvokedBy(new InvokedBy(new UserId(entity.getDomain(), entity.getUserEmail())));
		boundary.setActionAttributes(entity.getActionAttributes());

		return boundary;
	}

	public ActionEntity toEntity(ActionBoundary boundary) {
		ActionEntity entity = new ActionEntity();

		if (boundary.getActionId() != null) {
			entity.setDomain(boundary.getActionId().getDomain());
			entity.setId(boundary.getActionId().getId());
		}

		entity.setType(boundary.getType());

		if (boundary.getElement() != null && boundary.getElement().getElementId() != null) {
			entity.setElementDomain(boundary.getElement().getElementId().getDomain());
			entity.setElementId(boundary.getElement().getElementId().getId());
		}

		if (boundary.getCreatedTimestamp() != null)
			entity.setCreatedTimestamp(boundary.getCreatedTimestamp());

		if (boundary.getInvokedBy() != null && boundary.getInvokedBy().getUserId() != null) {
			entity.setUserDomain(boundary.getInvokedBy().getUserId().getDomain());
			entity.setUserEmail(boundary.getInvokedBy().getUserId().getEmail());
		}

		entity.setActionAttributes(boundary.getActionAttributes());

		return entity;
	}

	public ElementBoundary fromEntity(ElementEntity entity) {
		ElementBoundary boundary = new ElementBoundary();

		boundary.setElementId(new ElementId(entity.getDomain(), entity.getId()));
		boundary.setType(entity.getType());
		boundary.setName(entity.getName());
		boundary.setActive(entity.getActive());
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		boundary.setCreatedBy(new CreatedBy(new UserId(entity.getDomain(), entity.getUserEmail())));
		boundary.setLocation(new Location(entity.getLat(), entity.getLng()));
		boundary.setElementAttributes(entity.getElementAttributes());

		return boundary;
	}

	public ElementEntity toEntity(ElementBoundary boundary) {
		ElementEntity entity = new ElementEntity();

		if (boundary.getElementId() != null) {
			entity.setDomain(boundary.getElementId().getDomain());
			entity.setId(boundary.getElementId().getId());
		}

		entity.setType(boundary.getType());
		entity.setName(boundary.getName());
		entity.setActive(boundary.getActive());

		if (boundary.getCreatedBy() != null && boundary.getCreatedBy().getUserId() != null) {
			entity.setUserDomain(boundary.getCreatedBy().getUserId().getDomain());
			entity.setUserEmail(boundary.getCreatedBy().getUserId().getEmail());
		}

		if (boundary.getLocation() != null) {
			entity.setLat(boundary.getLocation().getlat());
			entity.setLng(boundary.getLocation().getlng());
		}

		if (boundary.getCreatedTimestamp() != null)
			entity.setCreatedTimestamp(boundary.getCreatedTimestamp());

		entity.setElementAttributes(boundary.getElementAttributes());

		return entity;
	}
}
