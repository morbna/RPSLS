package acs.game.data;

import java.util.List;

public class Tournament {

    private int capacity; // number of contenders
    private List<String> playerIds; // ids
    private TournamentStatus tournamentStatus; // status..
    private String winner; // winner id
    private List<String> battleIds; // battle ids

    public Tournament() {
    }

    public Tournament(int capacity, List<String> playerIds, TournamentStatus tournamentStatus, String winner,
            List<String> battleIds) {
        this.capacity = capacity;
        this.playerIds = playerIds;
        this.tournamentStatus = tournamentStatus;
        this.winner = winner;
        this.battleIds = battleIds;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public TournamentStatus getTournamentStatus() {
        return tournamentStatus;
    }

    public void setTournamentStatus(TournamentStatus tournamentStatus) {
        this.tournamentStatus = tournamentStatus;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<String> getBattleIds() {
        return battleIds;
    }

    public void setBattleIds(List<String> battleIds) {
        this.battleIds = battleIds;
    }

}