package acs.game.logic;

import acs.boundaries.ElementBoundary;
import acs.data.ActionEntity;
import acs.game.boundaries.PlayerScore;

public interface GameLogicService {

    public Object handle(ActionEntity ae);

    // get player element by user id
    public ElementBoundary getPlayer(ActionEntity ae);

    // return all players scoreboard details
    public PlayerScore[] loadScoreboard(ActionEntity ae);

    // return all active players in my area
    public ElementBoundary[] loadMap(ActionEntity ae);

    // notify player current location
    public void updatePlayerLocation(ActionEntity ae);
    /*
     * INPUT: myId(string) lat(double) lng(double)
     */

    // update map by active players
    public void drawMap();

    // ask if any battles waiting
    public Object askForChallenge(ActionEntity ae);
    /*
     * INPUT: myId(string)
     */

    /*
     * OUTPUT: askResponse(boolean): battleId(string) otherId(String) status(string)
     */

    public void challengeReply(ActionEntity ae);
    /*
     * INPUT: battleId(string) replyResponse(boolean)
     * 
     * // 1: accept, 0:reject
     */

    public boolean challengeRequest(ActionEntity ae);
    /*
     * INPUT: myId(string) otherId(string)
     * 
     */

    /*
     * OUTPUT: (boolean)
     * 
     * // true if available to battle, otherwise false
     */

    public void battleAction(ActionEntity ae);
    /*
     * INPUT: myId(string) battleId(string) shape(string)
     *
     */

    public Object battleReply(ActionEntity ae);
    /*
     * INPUT: round(int) myId(string) battleId(string)
     */

    /*
     * OUTPUT: ready(boolean) otherShape(Shape) result(int) phrase(String)
     * isOver(boolean): winner(boolean)
     */

}