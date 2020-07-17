package acs.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ActionBoundary;
import acs.boundaries.UserBoundary;
import acs.logic.ActionPaginationService;
import acs.logic.ElementPaginationService;
import acs.logic.UserPaginationService;

@RestController
public class ActionController {

	private ActionPaginationService actionService;
	private UserPaginationService userService;
	private ElementPaginationService elementService;

	@Autowired
	public ActionController(UserPaginationService userService, ElementPaginationService elementService,
			ActionPaginationService actionService) {
		this.userService = userService;
		this.elementService = elementService;
		this.actionService = actionService;
	}

	@RequestMapping(path = "/acs/actions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeAction(@RequestBody ActionBoundary invokedAction) {

		return this.actionService.invokeAction(invokedAction);
	}

	@RequestMapping(path = "/acs/admin/users/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllUsers(@PathVariable String adminDomain, @PathVariable String adminEmail) {

		userService.deleteAllUsers(adminDomain, adminEmail);
	}

	@RequestMapping(path = "/acs/admin/elements/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllElements(@PathVariable String adminDomain, @PathVariable String adminEmail) {

		elementService.deleteAllElements(adminDomain, adminEmail);
	}

	@RequestMapping(path = "/acs/admin/actions/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllActions(@PathVariable String adminDomain, @PathVariable String adminEmail) {

		actionService.deleteAllActions(adminDomain, adminEmail);
	}

	@RequestMapping(path = "/acs/admin/users/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportAllUsers(@PathVariable String adminDomain, @PathVariable String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		List<UserBoundary> users = userService.getAllUsers(adminDomain, adminEmail, size, page);
		return users.toArray(new UserBoundary[0]);
	}

	@RequestMapping(path = "/acs/admin/actions/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] exportAllActions(@PathVariable String adminDomain, @PathVariable String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.actionService.getAllActions(adminDomain, adminEmail, size, page).toArray(new ActionBoundary[0]);
	}
}
