package acs.game.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;

import acs.dal.ElementDao;
import acs.data.ActionEntity;
import acs.data.ElementEntity;
import acs.data.sub.ElementIdPk;
import acs.game.data.ActionType;
import acs.game.data.ElementType;
import acs.game.data.TournamentStatus;

@Service
@EnableScheduling
public class DatabaseTournamentLogicService implements TournamentLogicService {
    private ElementDao elementDao;
    private String projectName;
    private DatabaseGameLogicService databaseGameLogicService;
    private final int size = 1000;
    private final long updateRate = 5000;
    private static Log LOGGER = LogFactory.getLog(DatabaseTournamentLogicService.class);

    @Autowired
    public DatabaseTournamentLogicService(ElementDao elementDao, DatabaseGameLogicService databaseGameLogicService) {
        super();
        this.elementDao = elementDao;
        this.databaseGameLogicService = databaseGameLogicService;
    }

    @Value("${spring.application.name}")
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public Object handle(ActionEntity ae) {

        ActionType at;
        try {
            at = ActionType.valueOf(ae.getType());
        } catch (IllegalArgumentException e) {
            at = ActionType.NULL;
        }

        switch (at) {

            case TOURNAMENT_ENTER:
                return enterTournament(ae);
            case TOURNAMENT_REPLY:
                return tournamentReply(ae);
            case TOURNAMENT_IS_OVER:
                return isOver(ae);
            default:
                return null;
        }
    }

    @Override
    @Scheduled(fixedRate = updateRate)
    @Transactional
    public void handleTournaments() {

        TournamentStatus status;
        ArrayList<String> battles;
        ArrayList<String> winners = new ArrayList<>();
        boolean flag = true;

        List<ElementEntity> tournaments = elementDao
                .findAllByTypeLikeAndActiveTrue(ElementType.TOURNAMENT.toString(),
                        PageRequest.of(0, size, Direction.DESC, "createdTimestamp", "elementId.id"))
                .stream().collect(Collectors.toList());

        for (ElementEntity ent : tournaments) {
            Map<String, Object> hm = ent.getElementAttributes();
            status = TournamentStatus.valueOf(hm.get("tournamentStatus").toString());

            // tournament ready to start
            if (status.equals(TournamentStatus.FULL)) {
                ArrayList<String> players = (ArrayList<String>) (hm.get("players"));

                hm.put(("activePlayers"), players);
                hm.put("battles", buildBattleCouples(ent, players));
                hm.put(("tournamentStatus"), TournamentStatus.PERI);

                // mid round, check if over
            } else if (status.equals(TournamentStatus.PERI)) {

                battles = ((ArrayList<String>) (hm.get("battles")));

                for (String bId : battles) {
                    ElementEntity b = elementDao.findById(new ElementIdPk(projectName, bId))
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                    if (b.getActive() == true)
                        flag = false;
                }

                // round over
                if (flag) {

                    battles = ((ArrayList<String>) (hm.get("battles")));

                    // tournament over
                    if (battles.size() == 1) {
                        ElementEntity b = elementDao.findById(new ElementIdPk(projectName, battles.get(0)))
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                        hm.put("winner", b.getElementAttributes().get("winner").toString());
                        hm.put(("tournamentStatus"), TournamentStatus.POST);
                        ent.setActive(false);

                        // add trophies to winner
                        ElementEntity ee = this.elementDao
                                .findById(
                                        new ElementIdPk(projectName, b.getElementAttributes().get("winner").toString()))
                                .orElseThrow(() -> new RuntimeException());

                        ee.getElementAttributes().put("trophies",
                                Integer.parseInt(ee.getElementAttributes().get("trophies").toString()) + 1);

                        this.elementDao.save(ee);

                    } else // next round
                        hm.put(("tournamentStatus"), TournamentStatus.PERI_POST_ROUND);
                }
                // create round battles
            } else if (status.equals(TournamentStatus.PERI_POST_ROUND)) {

                battles = ((ArrayList<String>) (hm.get("battles")));

                for (String bId : battles) {
                    ElementEntity b = elementDao.findById(new ElementIdPk(projectName, bId))
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                    winners.add(b.getElementAttributes().get("winner").toString());
                }

                hm.put(("activePlayers"), winners);
                hm.put("battles", buildBattleCouples(ent, winners));
                hm.put(("tournamentStatus"), TournamentStatus.PERI);

            }
            elementDao.save(ent);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ArrayList<String> buildBattleCouples(ElementEntity ee, ArrayList<String> activePlayers) {

        ArrayList<String> battles = new ArrayList<>();

        for (int i = 0; i < activePlayers.size(); i += 2) {

            ActionEntity ae = new ActionEntity();
            Map<String, Object> attr = new HashMap<>();

            attr.put("myId", activePlayers.get(i));
            attr.put("otherId", activePlayers.get(i + 1));

            ae.setActionAttributes(attr);

            this.databaseGameLogicService.challengeRequest(ae);

            ElementEntity player = elementDao.findById(new ElementIdPk(projectName, activePlayers.get(i)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            ElementEntity battle = player.getChildEntities().stream()
                    .filter(child -> child.getActive() && child.getType().equals(ElementType.BATTLE.toString()))
                    .findFirst().get();

            attr.put("battleId", battle.getId());
            attr.put("replyResponse", true);

            battles.add(battle.getId());

            this.databaseGameLogicService.challengeReply(ae);
        }
        return battles;
    }

    @Override
    @Transactional
    public boolean enterTournament(ActionEntity ae) {

        LOGGER.info("enterTournament::");

        // get input
        String myId = ae.getActionAttributes().get("myId").toString();
        String tournamentId = ae.getActionAttributes().get("tournamentId").toString();

        // get tournament element
        ElementEntity tournament = this.elementDao.findById(new ElementIdPk(projectName, tournamentId)).get();

        int numOfPlayers = Integer.parseInt(tournament.getElementAttributes().get("numOfPlayers").toString());
        int numOfJoinedPlayers = ((ArrayList<String>) (tournament.getElementAttributes().get("players"))).size();

        if (numOfPlayers > numOfJoinedPlayers) {
            Map<String, Object> tourAttr = tournament.getElementAttributes();

            ArrayList<String> players = ((ArrayList<String>) (tourAttr.get("players")));

            players.add(myId);

            tourAttr.put("players", players);

            if (numOfPlayers - 1 == numOfJoinedPlayers)
                tourAttr.put("tournamentStatus", TournamentStatus.FULL.toString());

            this.elementDao.save(tournament);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public int tournamentReply(ActionEntity ae) {

        String tournamentId = String.valueOf(ae.getActionAttributes().get("tournamentId"));

        ElementEntity tournament = this.elementDao.findById(new ElementIdPk(projectName, tournamentId)).get();

        int numOfPlayers = Integer.parseInt(tournament.getElementAttributes().get("numOfPlayers").toString());

        int numOfJoinedPlayers = ((ArrayList<String>) (tournament.getElementAttributes().get("players"))).size();

        return numOfPlayers - numOfJoinedPlayers;
    }

    @Override
    @Transactional(readOnly = true)
    public Object isOver(ActionEntity ae) {

        String tournamentId = String.valueOf(ae.getActionAttributes().get("tournamentId"));

        ElementEntity tournament = this.elementDao.findById(new ElementIdPk(projectName, tournamentId)).get();

        if (TournamentStatus.valueOf(tournament.getElementAttributes().get("tournamentStatus").toString())
                .equals(TournamentStatus.POST))
            return true;

        return false;
    }

}