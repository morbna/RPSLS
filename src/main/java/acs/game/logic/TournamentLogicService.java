package acs.game.logic;

import java.util.ArrayList;

import acs.data.ActionEntity;
import acs.data.ElementEntity;

public interface TournamentLogicService {

  public Object handle(ActionEntity ae);

  // Scheduled
  public void handleTournaments();

  // generate battles
  public ArrayList<String> buildBattleCouples(ElementEntity ee, ArrayList<String> activePlayers);

  public boolean enterTournament(ActionEntity ae);

  /*
   * Enter an active Tournament
   * 
   * Input : tournamentId(String), myId(String)
   * 
   * Output: status(boolean)
   * 
   */

  public int tournamentReply(ActionEntity ae);
  /*
   * Input: myId(String), tournamentId(String)
   * 
   * 
   * Output: missing players(int)
   * 
   */

  public Object isOver(ActionEntity ae);
  /*
   * Input: myId(String), tournamentId(String)
   * 
   * 
   * Output: status(boolean)
   * 
   */
}