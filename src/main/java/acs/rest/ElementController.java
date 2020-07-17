package acs.rest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.logic.ElementPaginationService;
import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;

@RestController
public class ElementController {

	private ElementPaginationService elementService;

	@Autowired
	public ElementController(ElementPaginationService elementService) {
		this.elementService = elementService;
	}

	@RequestMapping(path = "/acs/elements/{managerDomain}/{managerEmail}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary createElement(@PathVariable String managerDomain, @PathVariable String managerEmail,
			@RequestBody ElementBoundary element) {

		return this.elementService.create(managerDomain, managerEmail, element);
	}

	@RequestMapping(path = "/acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable String managerDomain, @PathVariable String managerEmail,
			@PathVariable String elementDomain, @PathVariable String elementId, @RequestBody ElementBoundary element) {

		elementService.update(managerDomain, managerEmail, elementDomain, elementId, element);
	}

	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary retrieveSpecificElement(@PathVariable String userDomain, @PathVariable String userEmail,
			@PathVariable String elementDomain, @PathVariable String elementId) {

		return this.elementService.getSpecificElement(userDomain, userEmail, elementDomain, elementId);
	}

	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElements(@PathVariable String userDomain, @PathVariable String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.elementService.getAll(userDomain, userEmail, size, page).toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void bind(@PathVariable String managerDomain, @PathVariable String managerEmail,
			@PathVariable String elementDomain, @PathVariable String elementId, @RequestBody ElementIdBoundary child) {

		elementService.bind(managerDomain, managerEmail, elementDomain, elementId, child);
	}

	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getChildren(@PathVariable String userDomain, @PathVariable String userEmail,
			@PathVariable String elementDomain, @PathVariable String elementId,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.elementService.getChildren(userDomain, userEmail, elementDomain, elementId, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/parents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getParents(@PathVariable String userDomain, @PathVariable String userEmail,
			@PathVariable String elementDomain, @PathVariable String elementId,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.elementService.getParents(userDomain, userEmail, elementDomain, elementId, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/search/byName/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementsByName(@PathVariable String userDomain, @PathVariable String userEmail,
			@PathVariable String name, @RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.elementService.getAllByName(userDomain, userEmail, name, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/search/byType/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementsByType(@PathVariable String userDomain, @PathVariable String userEmail,
			@PathVariable String type, @RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.elementService.getAllByType(userDomain, userEmail, type, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementsByLocation(@PathVariable String userDomain, @PathVariable String userEmail,
			@PathVariable String lat, @PathVariable String lng, @PathVariable String distance,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.elementService.getAllByLocation(userDomain, userEmail, lat, lng, distance, size, page)
				.toArray(new ElementBoundary[0]);
	}

}
