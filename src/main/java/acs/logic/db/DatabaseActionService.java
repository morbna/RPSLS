package acs.logic.db;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

import acs.boundaries.ActionBoundary;

import acs.dal.ActionDao;
import acs.data.ActionEntity;
import acs.data.ElementEntity;
import acs.data.UserRole;
import acs.logic.ActionPaginationService;
import acs.game.logic.GameLogicService;
import acs.game.logic.TournamentLogicService;
import acs.logic.util.EntityConverter;
import acs.logic.util.Validator;

@Service
public class DatabaseActionService implements ActionPaginationService {

    private String projectName;
    private EntityConverter entityConverter;
    private Validator validator;
    private ActionDao actionDao;
    private GameLogicService gameLogicService;
    private TournamentLogicService tournamentLogicService;
    private static Log LOGGER = LogFactory.getLog(DatabaseActionService.class);

    @Autowired
    public DatabaseActionService(ActionDao actionDao, EntityConverter entityConverter,
            GameLogicService gameLogicService, Validator validator, TournamentLogicService tournamentLogicService) {
        super();
        this.actionDao = actionDao;
        this.gameLogicService = gameLogicService;
        this.entityConverter = entityConverter;
        this.validator = validator;
        this.tournamentLogicService = tournamentLogicService;

    }

    @Value("${spring.application.name}")
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    @Transactional
    public Object invokeAction(ActionBoundary action) {

        // check for null values
        validator.assertNotNull(action);
        validator.assertNotNull(action.getType(), action.getActionAttributes());
        validator.assertValidActionInvokedBy(action.getInvokedBy());
        validator.assertValidActionElement(action.getElement());

        // check that invoking user is a PLAYER
        validator.assertUserExistsWithRole(action.getInvokedBy().getUserId().getDomain(),
                action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER);

        // check that invoked element exists
        ElementEntity elm = validator.assertElementExists(action.getElement().getElementId().getDomain(),
                action.getElement().getElementId().getId());

        // and active
        if (elm.getActive() == false)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission Denied");

        // create id
        String id = UUID.randomUUID().toString();

        // create entity
        ActionEntity entity = entityConverter.toEntity(action);
        entity.setId(id);
        entity.setDomain(projectName);

        // save to database
        ActionEntity rv = actionDao.save(entity);
        Object o;

        // handle logic
        o = gameLogicService.handle(rv);
        if (o == null)
            o = tournamentLogicService.handle(rv);

        // return created action
        return o;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail) {

        LOGGER.info("getAllActions::");

        validator.assertUserExistsWithRole(adminDomain, adminEmail, UserRole.ADMIN);

        return StreamSupport.stream(this.actionDao.findAll().spliterator(), false).map(this.entityConverter::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllActions(String adminDomain, String adminEmail) {

        LOGGER.info("deleteAllActions::");

        validator.assertUserExistsWithRole(adminDomain, adminEmail, UserRole.ADMIN);

        this.actionDao.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail, int size, int page) {

        LOGGER.info("getAllActions::");

        validator.assertValidPaginations(size, page);
        validator.assertUserExistsWithRole(adminDomain, adminEmail, UserRole.ADMIN);

        return actionDao.findAll(PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "actionId.id"))
                .getContent().stream().map(this.entityConverter::fromEntity).collect(Collectors.toList());
    }
}
