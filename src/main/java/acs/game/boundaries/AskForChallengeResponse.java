package acs.game.boundaries;

import acs.game.data.BattleStatus;

public class AskForChallengeResponse {

    private boolean askResponse; // if theres a waiting battle
    private String battleId; // battleId
    private String otherId; // opponent id
    private String status; // BattleStatus PRE/PERI/POST/REJECTED

    public AskForChallengeResponse() {
    }

    public AskForChallengeResponse(boolean askResponse) {
        this.askResponse = askResponse;
    }

    public AskForChallengeResponse(boolean askResponse, String battleId, String otherId, BattleStatus status) {
        this.askResponse = askResponse;
        this.battleId = battleId;
        this.otherId = otherId;
        this.status = status.toString();
    }

    public boolean isAskResponse() {
        return askResponse;
    }

    public void setAskResponse(boolean askResponse) {
        this.askResponse = askResponse;
    }

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AskForChallengeResponse [askResponse=" + askResponse + ", battleId=" + battleId + ", otherId=" + otherId
                + ", status=" + status + "]";
    }

}