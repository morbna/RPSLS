package acs.logic.util;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import acs.boundaries.sub.Element;
import acs.boundaries.sub.InvokedBy;
import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.data.ElementEntity;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.data.sub.ElementIdPk;
import acs.data.sub.UserIdPk;

@Component
public class Validator {

	UserDao userDao;
	ElementDao elementDao;

	@Autowired
	public Validator(UserDao userDao, ElementDao elementDao) {
		super();
		this.userDao = userDao;
		this.elementDao = elementDao;
	}

	// check for null values
	public void assertNotNull(Object... args) {

		for (Object arg : args)
			if (arg == null)
				throw new RuntimeException("null value");
	}

	// check that role value is valid
	public void assertValidRole(String role) {
		if (role == null || !Stream.of(UserRole.values()).anyMatch(v -> v.toString().equals(role)))
			throw new RuntimeException("invalid UserRole");
	}

	// check that email is valid
	public void assertValidEmail(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

		if (email == null || !email.matches(regex))
			throw new RuntimeException("invalid Email");
	}

	// check that avatar is valid
	public void assertValidAvatar(String avatar) {

		if (avatar == null || avatar.length() == 0)
			throw new RuntimeException("invalid Avatar");

	}

	// check that Action invokedBy is valid
	public void assertValidActionInvokedBy(InvokedBy invokedby) {
		if (invokedby == null)
			throw new RuntimeException("invalid Action User Details");

		if (invokedby.getUserId() == null)
			throw new RuntimeException("invalid Action User Details");

		if (invokedby.getUserId().getDomain() == null || invokedby.getUserId().getEmail() == null)
			throw new RuntimeException("invalid Action User Details");
	}

	// check that Action element is valid
	public void assertValidActionElement(Element element) {

		if (element == null)
			throw new RuntimeException("invalid Element");

		if (element.getElementId() == null)
			throw new RuntimeException("invalid Element");

		if (element.getElementId().getDomain() == null || element.getElementId().getId() == null)
			throw new RuntimeException("invalid Element");
	}

	public void assertValidPaginations(int size, int page) {

		assertNotNull(size, page);

		if (size <= 0)
			throw new RuntimeException("size must be at least 1");

		if (page < 0)
			throw new RuntimeException("page must not be negative");

	}

	// check that user exists with permission
	public UserEntity assertUserExistsWithRole(String userDomain, String userEmail, UserRole... roles) {

		assertNotNull(userDomain, userEmail);

		UserEntity user = this.userDao.findById(new UserIdPk(userDomain, userEmail))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));

		if (roles.length > 0) {
			for (UserRole role : roles)
				if (user.getRole() == role)
					return user;

			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission Denied");
		}

		return user;
	}

	// check that element exists
	public ElementEntity assertElementExists(String elementDomain, String elementId) {

		assertNotNull(elementDomain, elementId);

		ElementEntity elm = this.elementDao.findById(new ElementIdPk(elementDomain, elementId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Element Not Found"));

		return elm;
	}

	// check for parseable Strings
	public void assertValidLocationAndDistance(String lat, String lng, String distance) {

		assertNotNull(lat, lng, distance);

		try {
			Double.parseDouble(lat);
			Double.parseDouble(lng);
			Double.parseDouble(distance);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid Location/Distance");
		}
	}
}
