package acs.logic.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort.Direction;

import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.data.ElementEntity;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.logic.UserPaginationService;
import acs.game.data.ElementType;
import acs.logic.util.EntityConverter;
import acs.logic.util.Validator;
import acs.boundaries.UserBoundary;

@Service
public class DatabaseUserService implements UserPaginationService {

    private String projectName;
    private EntityConverter entityConverter;
    private Validator validator;
    private UserDao userDao;
    private ElementDao elementDao;
    private static Log LOGGER = LogFactory.getLog(DatabaseUserService.class);

    @Autowired
    public DatabaseUserService(UserDao userDao, ElementDao elementDao, EntityConverter entityConverter,
            Validator validator) {
        super();
        this.userDao = userDao;
        this.elementDao = elementDao;
        this.entityConverter = entityConverter;
        this.validator = validator;
    }

    @Value("${spring.application.name}")
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    @Transactional
    public UserBoundary createUser(UserBoundary user) {

        LOGGER.info("createUser::");

        // check for null values
        validator.assertNotNull(user);
        validator.assertNotNull(user.getUserId(), user.getUsername());

        // check if valid UserRole
        validator.assertValidRole(user.getRole());

        // check if valid Email
        validator.assertValidEmail(user.getUserId().getEmail());

        // check if valid avatar
        validator.assertValidAvatar(user.getAvatar());

        // convert to entity
        UserEntity usr = this.entityConverter.toEntity(user);

        // set project domain
        usr.setDomain(projectName);

        LOGGER.info("createUser::checking if exists in db:: " + usr);

        // assert that userId is unique
        if (userDao.existsById(usr.getUserId()))
            throw new RuntimeException("userId already exists");

        LOGGER.info("createUser::saving to db::");

        // save to database
        UserEntity rv = userDao.save(usr);

        { // game logic

            // create player MAP element
            Map<String, Object> hm = new HashMap<String, Object>();
            hm.put("lastSeen", System.currentTimeMillis());
            hm.put("score", 0);
            hm.put("trophies", 0);

            ElementEntity player = new ElementEntity(projectName, UUID.randomUUID().toString(),
                    ElementType.PLAYER.toString(), rv.getUsername(), true, new Date(), rv.getDomain(), rv.getEmail(),
                    0.0, 0.0, hm);

            elementDao.save(player);
        }

        // return created user
        return this.entityConverter.fromEntity(rv);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary login(String userDomain, String userEmail) {

        LOGGER.info("login::");

        UserEntity rv = validator.assertUserExistsWithRole(userDomain, userEmail);

        // return user
        return this.entityConverter.fromEntity(rv);
    }

    @Override
    @Transactional
    public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {

        LOGGER.info("updateUser::");

        // check for null value
        validator.assertNotNull(update);

        // check valid role if updated
        if (update.getRole() != null)
            validator.assertValidRole(update.getRole());

        // check valid avatar if updated
        if (update.getAvatar() != null)
            validator.assertValidAvatar(update.getAvatar());

        // get user to update from db
        UserEntity rv = validator.assertUserExistsWithRole(userDomain, userEmail);

        // update any non-null values
        if (update.getRole() != null)
            rv.setRole(UserRole.valueOf(update.getRole()));

        if (update.getUsername() != null)
            rv.setUsername(update.getUsername());

        if (update.getAvatar() != null)
            rv.setAvatar(update.getAvatar());

        LOGGER.info("updateUser::saving to db:: " + rv);

        // save to database
        rv = userDao.save(rv);

        // return updated user
        return this.entityConverter.fromEntity(rv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {

        return getAllUsers(adminDomain, adminEmail, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    @Transactional
    public void deleteAllUsers(String adminDomain, String adminEmail) {

        LOGGER.info("deleteAllUsers::");

        validator.assertUserExistsWithRole(adminDomain, adminEmail, UserRole.ADMIN);

        this.userDao.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int size, int page) {

        LOGGER.info("getAllUsers::");

        validator.assertValidPaginations(size, page);
        validator.assertUserExistsWithRole(adminDomain, adminEmail, UserRole.ADMIN);

        return this.userDao.findAll(PageRequest.of(page, size, Direction.DESC, "username", "userId.email")).getContent()
                .stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());

    }

}