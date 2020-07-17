
package acs.game.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import acs.boundaries.ElementBoundary;
import acs.dal.ElementDao;
import acs.data.ActionEntity;
import acs.data.ElementEntity;
import acs.data.sub.ElementIdPk;
import acs.game.boundaries.AskForChallengeResponse;
import acs.game.boundaries.BattleReplyResponse;
import acs.game.boundaries.PlayerScore;
import acs.game.data.ActionType;
import acs.game.data.BattleStatus;
import acs.game.data.ElementType;
import acs.game.data.Shape;
import acs.logic.util.EntityConverter;

@Service
@EnableScheduling
public class DatabaseGameLogicService implements GameLogicService {

    private ElementDao elementDao;
    private EntityConverter entityConverter;
    private GameRuler gameRuler;
    private String projectName;
    private final long updateRate = 5000;
    private final int size = 1000;

    private static Log LOGGER = LogFactory.getLog(DatabaseGameLogicService.class);

    @PostConstruct
    public void init() { // create root element for player invokeAction

        ElementEntity root = new ElementEntity(projectName, "GAME_LOGIC", ElementType.SYSTEM.toString(), "GAME_LOGIC",
                true, new Date(), projectName, "GAME_LOGIC", 0.0, 0.0, null);

        elementDao.save(root);
    }

    @Autowired
    public DatabaseGameLogicService(ElementDao elementDao, EntityConverter entityConverter, GameRuler gameRuler) {
        super();
        this.entityConverter = entityConverter;
        this.elementDao = elementDao;
        this.gameRuler = gameRuler;
    }

    @Value("${spring.application.name}")
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Object handle(ActionEntity ae) {

        ActionType at;
        try {
            at = ActionType.valueOf(ae.getType());
        } catch (IllegalArgumentException e) {
            at = ActionType.NULL;
        }

        switch (at) {
            case PLAYER_GET:
                return getPlayer(ae);
            case PLAYER_SCORE:
                return loadScoreboard(ae);
            case MAP_EVENT:
                updatePlayerLocation(ae);
                break;
            case MAP_LOAD:
                return loadMap(ae);
            case BATTLE_ACTION:
                battleAction(ae);
                break;
            case BATTLE_REPLY:
                return battleReply(ae);
            case CHALLENGE_REPLY:
                challengeReply(ae);
                break;
            case CHALLENGE_ASK:
                return askForChallenge(ae);
            case CHALLENGE_PLAYER:
                return challengeRequest(ae);
            default:
                return null;
        }
        return ae;
    }

    @Override
    @Transactional(readOnly = true)
    public ElementBoundary getPlayer(ActionEntity ae) {

        LOGGER.info("getPlayer::");

        String domain = ae.getUserDomain();
        String email = ae.getUserEmail();

        ElementEntity ent = elementDao.findByUserDomainAndUserEmailAndTypeLike(domain, email,
                ElementType.PLAYER.toString());

        return entityConverter.fromEntity(ent);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerScore[] loadScoreboard(ActionEntity ae) {

        LOGGER.info("loadScoreboard::");

        List<PlayerScore> scores = new ArrayList<>();

        List<ElementEntity> players = elementDao
                .findAllByTypeLike(ElementType.PLAYER.toString(),
                        PageRequest.of(0, size, Direction.DESC, "createdTimestamp", "elementId.id"))
                .stream().collect(Collectors.toList());

        for (ElementEntity ent : players) {
            scores.add(
                    new PlayerScore(ent.getName(), Integer.parseInt(ent.getElementAttributes().get("score").toString()),
                            Integer.parseInt(ent.getElementAttributes().get("trophies").toString())));
        }

        return scores.toArray(new PlayerScore[0]);
    }

    @Scheduled(fixedRate = updateRate)
    @Transactional
    public void drawMap() {

        long time = System.currentTimeMillis();

        List<ElementEntity> players = elementDao
                .findAllByTypeLike(ElementType.PLAYER.toString(),
                        PageRequest.of(0, size, Direction.DESC, "createdTimestamp", "elementId.id"))
                .stream().collect(Collectors.toList());

        for (ElementEntity ent : players) {
            if (time - (long) ent.getElementAttributes().get("lastSeen") > updateRate)
                ent.setActive(false);
            else
                ent.setActive(true);

            elementDao.save(ent);
        }
    }

    @Override
    @Transactional
    public void updatePlayerLocation(ActionEntity ae) {
        double lat = Double.parseDouble(ae.getActionAttributes().get("lat").toString());
        double lng = Double.parseDouble(ae.getActionAttributes().get("lng").toString());

        ElementEntity player = elementDao
                .findById(new ElementIdPk(this.projectName, ae.getActionAttributes().get("id").toString()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        player.setLat(lat);
        player.setLng(lng);
        player.getElementAttributes().put("lastSeen", System.currentTimeMillis());
        elementDao.save(player);
    }

    @Override
    @Transactional(readOnly = true)
    public ElementBoundary[] loadMap(ActionEntity ae) {

        double lat = Double.parseDouble(ae.getActionAttributes().get("lat").toString());
        double lng = Double.parseDouble(ae.getActionAttributes().get("lng").toString());
        double distance = Double.parseDouble(ae.getActionAttributes().get("distance").toString());

        ElementBoundary[] players = elementDao
                .findAllByLatBetweenAndLngBetweenAndActiveTrueAndTypeLike(lat - distance, lat + distance,
                        lng - distance, lng + distance, ElementType.PLAYER.toString(),
                        PageRequest.of(0, size, Direction.DESC, "createdTimestamp", "elementId.id"))
                .stream().map(this.entityConverter::fromEntity).collect(Collectors.toList())
                .toArray(new ElementBoundary[0]);

        return players;
    }

    @Override
    @Transactional
    public Object askForChallenge(ActionEntity ae) {

        String otherId, myId = ae.getActionAttributes().get("myId").toString();
        ElementEntity player, battle;
        Map<String, Object> battleAtt;
        BattleStatus status;

        player = elementDao.findById(new ElementIdPk(projectName, myId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // check if any active battles exist
        Optional<ElementEntity> BattleO = player.getChildEntities().stream()
                .filter(child -> child.getActive() && child.getType().equals(ElementType.BATTLE.toString()))
                .findFirst();

        if (!BattleO.isPresent())
            return new AskForChallengeResponse(false);

        battle = BattleO.get();
        battleAtt = battle.getElementAttributes();

        // find if i initiated battle
        if (myId.equals(battleAtt.get("player1")))
            otherId = battleAtt.get("player2").toString();
        else
            otherId = battleAtt.get("player1").toString();

        status = BattleStatus.valueOf(battleAtt.get("battleStatus").toString());

        // if battle was rejected , update initiating player and end battle
        if (status == BattleStatus.REJECTED) {
            if (myId.equals(battleAtt.get("player1"))) {
                battle.setActive(false);
                elementDao.save(battle);
                return new AskForChallengeResponse(true, battle.getId(), otherId, BattleStatus.REJECTED);
            } else
                return new AskForChallengeResponse(false);
        }

        return new AskForChallengeResponse(true, battle.getId(), otherId, status);
    }

    @Override
    @Transactional
    public void battleAction(ActionEntity ae) {

        LOGGER.info("battleAction::");

        Object p_1, p_2;
        Shape prevP1, prevP2;
        int win1, win2, score1, score2;
        String myId = ae.getActionAttributes().get("myId").toString();
        String battleId = ae.getActionAttributes().get("battleId").toString();
        String shape = ae.getActionAttributes().get("shape").toString();

        // get battle element
        ElementEntity elm = elementDao.findById(new ElementIdPk(projectName, battleId)).get();

        // get battle attributes
        Map<String, Object> battleAtt = elm.getElementAttributes();

        // get ids
        String p1Id = battleAtt.get("player1").toString();
        String p2Id = battleAtt.get("player2").toString();

        // get player elements
        ElementEntity playerOne = elementDao.findById(new ElementIdPk(projectName, p1Id)).get();
        ElementEntity playerTwo = elementDao.findById(new ElementIdPk(projectName, p2Id)).get();

        // set player chosen shape
        if (myId.equals(p1Id))
            battleAtt.put("p1", shape);
        else
            battleAtt.put("p2", shape);

        // get players chosen shapes
        p_1 = battleAtt.get("p1");
        p_2 = battleAtt.get("p2");

        // if both player has chosen
        if (p_1 != null && p_2 != null) {

            // get round shapes
            prevP1 = Shape.valueOf(p_1.toString());
            prevP2 = Shape.valueOf(p_2.toString());

            // prepare for next round
            battleAtt.put("p1", null);
            battleAtt.put("p2", null);
            battleAtt.put("prevP1", prevP1.toString());
            battleAtt.put("prevP2", prevP2.toString());
            battleAtt.put("round", Integer.parseInt(battleAtt.get("round").toString()) + 1);

            // calculate
            switch (gameRuler.rule(prevP1, prevP2)) {
                case 1: // p1 wins
                    battleAtt.put("playerOneWins", Integer.parseInt(battleAtt.get("playerOneWins").toString()) + 1);
                    battleAtt.put("roundResult", 1);
                    break;
                case -1: // p2 wins
                    battleAtt.put("playerTwoWins", Integer.parseInt(battleAtt.get("playerTwoWins").toString()) + 1);
                    battleAtt.put("roundResult", 2);
                    break;
                default: // draw
                    battleAtt.put("roundResult", 0);
            }

            // get phrase
            battleAtt.put("phrase", gameRuler.getPhrase(prevP1, prevP2));

            win1 = Integer.parseInt(battleAtt.get("playerOneWins").toString());
            win2 = Integer.parseInt(battleAtt.get("playerTwoWins").toString());

            // set winner id and update score
            if (gameRuler.isOver(win1, win2)) {

                score1 = Integer.parseInt(playerOne.getElementAttributes().get("score").toString());
                score2 = Integer.parseInt(playerTwo.getElementAttributes().get("score").toString());

                if (win1 > win2) {
                    battleAtt.put("winner", p1Id);
                    score1 += 2;
                    score2 -= 1;
                } else {
                    battleAtt.put("winner", p2Id);
                    score2 += 2;
                    score1 -= 1;
                }

                // finalize battle
                battleAtt.put("battleStatus", BattleStatus.POST.toString());
                elm.setActive(false);

                // check if negative
                if (score1 < 0)
                    score1 = 0;

                if (score2 < 0)
                    score2 = 0;

                // put scores
                playerOne.getElementAttributes().put("score", score1);
                playerTwo.getElementAttributes().put("score", score2);

            }

            // save
            this.elementDao.save(playerOne);
            this.elementDao.save(playerTwo);
        }

        this.elementDao.save(elm);
    }

    @Override
    @Transactional(readOnly = true)
    public Object battleReply(ActionEntity ae) {

        String player1, phrase;
        String myId = ae.getActionAttributes().get("myId").toString();
        String battleId = ae.getActionAttributes().get("battleId").toString();
        int round = Integer.parseInt(ae.getActionAttributes().get("round").toString());
        String otherShape;
        int result, roundResult;
        boolean over, winner = false;

        // get battle element
        ElementEntity ee = this.elementDao.findById(new ElementIdPk(projectName, battleId))
                .orElseThrow(() -> new RuntimeException());

        // get battle attributes
        Map<String, Object> battleAtt = ee.getElementAttributes();

        // check if round is decided
        if (round != Integer.parseInt(battleAtt.get("round").toString()))
            return new BattleReplyResponse(false);

        roundResult = (int) battleAtt.get("roundResult"); // 0: tie | 1: P1 | 2: P2

        // get player id
        player1 = battleAtt.get("player1").toString();

        if (myId.equals(player1)) {
            otherShape = battleAtt.get("prevP2").toString();

            if (roundResult == 1)
                result = 1;
            else if (roundResult == 2)
                result = -1;
            else
                result = 0;
        } else {
            otherShape = battleAtt.get("prevP1").toString();

            if (roundResult == 2)
                result = 1;
            else if (roundResult == 1)
                result = -1;
            else
                result = 0;
        }

        // check if battle is over
        over = battleAtt.get("battleStatus").toString().equals(BattleStatus.POST.toString());
        if (over)
            winner = myId.equals(battleAtt.get("winner").toString());

        // get round phrase
        phrase = battleAtt.get("phrase").toString();

        return new BattleReplyResponse(true, otherShape, result, phrase, over, winner);
    }

    @Override
    @Transactional
    public boolean challengeRequest(ActionEntity ae) {

        LOGGER.info("challengeRequest::");

        // player IDs
        String myId = ae.getActionAttributes().get("myId").toString();
        String otherId = ae.getActionAttributes().get("otherId").toString();

        // player Entities
        ElementEntity playerOne = elementDao.findById(new ElementIdPk(projectName, myId)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "challengeRequest::myId not found"));

        ElementEntity playerTwo = elementDao.findById(new ElementIdPk(projectName, otherId)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "challengeRequest::otherId not found"));

        // Battle Entity
        if (playerTwo.getChildEntities().stream()
                .filter((elm) -> elm.getType().equals(ElementType.BATTLE.toString()) && elm.getActive() == true)
                .count() > 0)
            return false; // busy

        // Create battle entity and init parameters

        // add specific parameters
        Map<String, Object> battleAtt = new HashMap<>();
        battleAtt.put("player1", myId);
        battleAtt.put("player2", otherId);
        battleAtt.put("p1", null);
        battleAtt.put("p2", null);
        battleAtt.put("prevP1", null);
        battleAtt.put("prevP2", null);
        battleAtt.put("playerOneWins", 0);
        battleAtt.put("playerTwoWins", 0);
        battleAtt.put("battleStatus", BattleStatus.PRE);
        battleAtt.put("roundResult", null);
        battleAtt.put("round", 0);
        battleAtt.put("phrase", null);
        battleAtt.put("winner", null);

        // Create battle element
        String bid = UUID.randomUUID().toString();
        ElementEntity battle = new ElementEntity(projectName, bid, ElementType.BATTLE.toString(), "battle " + bid, true,
                new Date(), playerOne.getDomain(), playerOne.getUserEmail(),
                Double.parseDouble(playerTwo.getLat().toString()), Double.parseDouble(playerTwo.getLng().toString()),
                battleAtt);

        // Bind
        playerOne.getChildEntities().add(battle);
        playerTwo.getChildEntities().add(battle);
        battle.getParentEntities().add(playerOne);
        battle.getParentEntities().add(playerTwo);

        // Save in DB
        this.elementDao.save(battle);
        this.elementDao.save(playerTwo);
        this.elementDao.save(playerOne);

        return true;
    }

    @Override
    @Transactional
    // reply challenge request
    public void challengeReply(ActionEntity ae) {

        LOGGER.info("challengeReply::");

        boolean reply = Boolean.parseBoolean(ae.getActionAttributes().get("replyResponse").toString());
        LOGGER.info("challengeReply::" + reply);
        ElementEntity ee = this.elementDao
                .findById(new ElementIdPk(this.projectName, ae.getActionAttributes().get("battleId").toString())).get();

        Map<String, Object> battleAtt = ee.getElementAttributes();

        if (reply) { // accepted
            battleAtt.put("battleStatus", BattleStatus.PERI);
        } else { // rejected
            battleAtt.put("battleStatus", BattleStatus.REJECTED);
        }

        this.elementDao.save(ee);
    }

}