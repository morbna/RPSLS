package acs.logic.db;

import acs.logic.ElementPaginationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import acs.data.ElementEntity;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.data.sub.ElementIdPk;
import acs.logic.util.EntityConverter;
import acs.logic.util.Validator;
import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;
import acs.dal.ElementDao;

@Service
public class DatabaseElementService implements ElementPaginationService {

	private String projectName;
	private EntityConverter entityConverter;
	private Validator validator;
	private ElementDao elementDao;
	private static Log LOGGER = LogFactory.getLog(DatabaseElementService.class);

	@Autowired
	public DatabaseElementService(ElementDao elementDao, EntityConverter entityConverter, Validator validator) {
		super();
		this.entityConverter = entityConverter;
		this.validator = validator;
		this.elementDao = elementDao;
	}

	@Value("${spring.application.name}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	@Transactional
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element) {

		LOGGER.info("create::");

		validator.assertNotNull(element);
		validator.assertNotNull(element.getLocation(), element.getType(), element.getName());

		validator.assertUserExistsWithRole(managerDomain, managerEmail, UserRole.MANAGER);

		// create id
		String id = UUID.randomUUID().toString();

		// create entity
		ElementEntity elm = entityConverter.toEntity(element);
		elm.setId(id);
		elm.setDomain(projectName);
		elm.setUserDomain(managerDomain);
		elm.setUserEmail(managerEmail);

		LOGGER.info("create::saving to db:: " + elm);

		// save to database
		this.elementDao.save(elm);

		return this.entityConverter.fromEntity(elm);
	}

	@Override
	@Transactional
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update) {

		LOGGER.info("update::");

		validator.assertNotNull(update);

		validator.assertUserExistsWithRole(managerDomain, managerEmail, UserRole.MANAGER);

		ElementEntity ent = validator.assertElementExists(elementDomain, elementId);

		// update any non-null values

		if (update.getType() != null)
			ent.setType(update.getType());

		if (update.getName() != null)
			ent.setName(update.getName());

		if (update.getActive() != null)
			ent.setActive(update.getActive());

		if (update.getLocation() != null) {
			if (update.getLocation().getlat() != null)
				ent.setLat(update.getLocation().getlat());

			if (update.getLocation().getlng() != null)
				ent.setLng(update.getLocation().getlng());
		}

		if (ent.getElementAttributes() != null)
			ent.setElementAttributes(update.getElementAttributes());

		this.elementDao.save(ent);

		return this.entityConverter.fromEntity(ent);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userDomain, String userEmail) {

		return getAll(userDomain, userEmail, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	@Transactional(readOnly = true)
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
			String elementId) {

		LOGGER.info("getSpecificElement::");

		UserEntity user = validator.assertUserExistsWithRole(userDomain, userEmail, UserRole.PLAYER, UserRole.MANAGER);
		ElementEntity rv = validator.assertElementExists(elementDomain, elementId);

		if (user.getRole() == UserRole.PLAYER && rv.getActive() == false)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission Denied");
		else
			return this.entityConverter.fromEntity(rv);
	}

	@Override
	@Transactional
	public void deleteAllElements(String adminDomain, String adminEmail) {

		LOGGER.info("deleteAllElements::");

		validator.assertUserExistsWithRole(adminDomain, adminEmail, UserRole.ADMIN);

		this.elementDao.deleteAll();
	}

	@Override
	@Transactional
	public void bind(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementIdBoundary elementIdBoundary) {

		LOGGER.info("bind::");

		validator.assertUserExistsWithRole(managerDomain, managerEmail, UserRole.MANAGER);
		validator.assertNotNull(elementIdBoundary);

		// get parent
		ElementEntity parent = validator.assertElementExists(elementDomain, elementId);

		LOGGER.info("bind::parent:: " + parent);

		// get child
		ElementEntity child = validator.assertElementExists(elementIdBoundary.getDomain(), elementIdBoundary.getId());

		LOGGER.info("bind::child:: " + child);

		// bind both ways
		child.getParentEntities().add(parent);
		parent.getChildEntities().add(child);

		this.elementDao.save(child);
		this.elementDao.save(parent);

	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getChildren(String userDomain, String userEmail, String elementDomain,
			String elementId) {

		return getChildren(userDomain, userEmail, elementDomain, elementId, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getParents(String userDomain, String userEmail, String elementDomain,
			String elementId) {

		return getParents(userDomain, userEmail, elementDomain, elementId, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page) {

		LOGGER.info("getAll::");

		validator.assertValidPaginations(size, page);

		UserEntity ue = validator.assertUserExistsWithRole(userDomain, userEmail, UserRole.PLAYER, UserRole.MANAGER);

		if (ue.getRole() == UserRole.PLAYER)
			return this.elementDao
					.findAllByActiveTrue(PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
		else
			return this.elementDao
					.findAll(PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.getContent().stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getChildren(String userDomain, String userEmail, String elementDomain,
			String elementId, int size, int page) {

		LOGGER.info("getChildren::");

		validator.assertValidPaginations(size, page);
		validator.assertNotNull(elementDomain, elementId);

		UserEntity ue = validator.assertUserExistsWithRole(userDomain, userEmail, UserRole.PLAYER, UserRole.MANAGER);

		if (ue.getRole() == UserRole.PLAYER)
			return this.elementDao
					.findAllByActiveTrueAndParentEntitiesElementId(new ElementIdPk(elementDomain, elementId),
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());

		return this.elementDao
				.findAllByParentEntitiesElementId(new ElementIdPk(elementDomain, elementId),
						PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
				.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getParents(String userDomain, String userEmail, String elementDomain, String elementId,
			int size, int page) {

		LOGGER.info("getParents::");

		validator.assertValidPaginations(size, page);
		validator.assertNotNull(elementDomain, elementId);

		UserEntity ue = validator.assertUserExistsWithRole(userDomain, userEmail, UserRole.PLAYER, UserRole.MANAGER);

		if (ue.getRole() == UserRole.PLAYER)
			return this.elementDao
					.findAllByActiveTrueAndChildEntitiesElementId(new ElementIdPk(elementDomain, elementId),
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());

		return this.elementDao
				.findAllByChildEntitiesElementId(new ElementIdPk(elementDomain, elementId),
						PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
				.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllByName(String userDomain, String userEmail, String name, int size, int page) {

		LOGGER.info("getAllByName:: ");

		validator.assertValidPaginations(size, page);
		validator.assertNotNull(name);

		UserEntity ue = validator.assertUserExistsWithRole(userDomain, userEmail, UserRole.PLAYER, UserRole.MANAGER);

		if (ue.getRole() == UserRole.PLAYER)
			return this.elementDao
					.findAllByNameLikeAndActiveTrue(name,
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
		else
			return this.elementDao
					.findAllByNameLike(name,
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllByType(String userDomain, String userEmail, String type, int size, int page) {

		LOGGER.info("getAllByType:: ");

		validator.assertValidPaginations(size, page);
		validator.assertNotNull(type);

		UserEntity ue = validator.assertUserExistsWithRole(userDomain, userEmail, UserRole.PLAYER, UserRole.MANAGER);

		if (ue.getRole() == UserRole.PLAYER)
			return this.elementDao
					.findAllByTypeLikeAndActiveTrue(type,
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());

		else
			return this.elementDao
					.findAllByTypeLike(type,
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllByLocation(String userDomain, String userEmail, String lat, String lng,
			String distance, int size, int page) {

		LOGGER.info("getAllByLocation:: ");

		validator.assertValidPaginations(size, page);
		validator.assertValidLocationAndDistance(lat, lng, distance);

		UserEntity ue = validator.assertUserExistsWithRole(userDomain, userEmail, UserRole.PLAYER, UserRole.MANAGER);

		Double Lat = Double.parseDouble(lat);
		Double Lng = Double.parseDouble(lng);
		Double Dist = Double.parseDouble(distance);

		if (ue.getRole() == UserRole.PLAYER)
			return this.elementDao
					.findAllByLatBetweenAndLngBetweenAndActiveTrue(Lat - Dist, Lat + Dist, Lng - Dist, Lng + Dist,
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());

		else
			return this.elementDao
					.findAllByLatBetweenAndLngBetween(Lat - Dist, Lat + Dist, Lng - Dist, Lng + Dist,
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "elementId.id"))
					.stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
	}

}
