package acs.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import acs.logic.UserPaginationService;
import acs.boundaries.NewUserDetails;
import acs.boundaries.UserBoundary;

@RestController
public class UserController {

	private UserPaginationService userService;
	private static Log LOGGER = LogFactory.getLog(UserController.class);

	@Autowired
	public UserController(UserPaginationService userService) {
		this.userService = userService;
	}

	@RequestMapping(path = "/acs/users/login/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(@PathVariable String userDomain, @PathVariable String userEmail) {

		LOGGER.info("login::at: " + userDomain + "/" + userEmail);

		UserBoundary user = userService.login(userDomain, userEmail);
		LOGGER.info("login:: " + user);

		return user;
	}

	@RequestMapping(path = "/acs/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createUser(@RequestBody NewUserDetails newUserDetails) {

		LOGGER.info("createUser::new:: " + newUserDetails);

		UserBoundary user = new UserBoundary(newUserDetails);
		LOGGER.info("createUser::created:: " + user);

		return userService.createUser(user);
	}

	@RequestMapping(path = "/acs/users/{userDomain}/{userEmail}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(@RequestBody UserBoundary update, @PathVariable String userDomain,
			@PathVariable String userEmail) {

		LOGGER.info("updateUser::at: " + userDomain + "/" + userEmail);
		LOGGER.info("updateUser::to_update: " + update);

		UserBoundary user = userService.updateUser(userDomain, userEmail, update);
		LOGGER.info("updateUser::updated:: " + user);
	}

}
