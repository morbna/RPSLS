package acs.game.data;

public class Battle {

    // player IDs
    private String player1;
    private String player2;

    // selected round shapes
    private Shape p1;
    private Shape p2;

    // selected prev round shapes
    private Shape prevP1;
    private Shape prevP2;

    // number of wins
    private int playerOneWins;
    private int playerTwoWins;

    // status..
    private BattleStatus battleStatus;

    // 0:tie, 1:p1 win, 2:p2 win
    private int roundResult;

    // current round
    private int round;

    // round phrase
    private String phrase;

    // winner id
    private String winner;

    public Battle() {
    }

    public Battle(String player1, String player2, Shape p1, Shape p2, Shape prevP1, Shape prevP2, int playerOneWins,
            int playerTwoWins, BattleStatus battleStatus, int roundResult, int round, String phrase, String winner) {
        this.player1 = player1;
        this.player2 = player2;
        this.p1 = p1;
        this.p2 = p2;
        this.prevP1 = prevP1;
        this.prevP2 = prevP2;
        this.playerOneWins = playerOneWins;
        this.playerTwoWins = playerTwoWins;
        this.battleStatus = battleStatus;
        this.roundResult = roundResult;
        this.round = round;
        this.phrase = phrase;
        this.winner = winner;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public Shape getP1() {
        return p1;
    }

    public void setP1(Shape p1) {
        this.p1 = p1;
    }

    public Shape getP2() {
        return p2;
    }

    public void setP2(Shape p2) {
        this.p2 = p2;
    }

    public Shape getPrevP1() {
        return prevP1;
    }

    public void setPrevP1(Shape prevP1) {
        this.prevP1 = prevP1;
    }

    public Shape getPrevP2() {
        return prevP2;
    }

    public void setPrevP2(Shape prevP2) {
        this.prevP2 = prevP2;
    }

    public int getPlayerOneWins() {
        return playerOneWins;
    }

    public void setPlayerOneWins(int playerOneWins) {
        this.playerOneWins = playerOneWins;
    }

    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public void setPlayerTwoWins(int playerTwoWins) {
        this.playerTwoWins = playerTwoWins;
    }

    public BattleStatus getBattleStatus() {
        return battleStatus;
    }

    public void setBattleStatus(BattleStatus battleStatus) {
        this.battleStatus = battleStatus;
    }

    public int getRoundResult() {
        return roundResult;
    }

    public void setRoundResult(int roundResult) {
        this.roundResult = roundResult;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "Battle [battleStatus=" + battleStatus + ", p1=" + p1 + ", p2=" + p2 + ", phrase=" + phrase
                + ", player1=" + player1 + ", player2=" + player2 + ", playerOneWins=" + playerOneWins
                + ", playerTwoWins=" + playerTwoWins + ", prevP1=" + prevP1 + ", prevP2=" + prevP2 + ", round=" + round
                + ", roundResult=" + roundResult + ", winner=" + winner + "]";
    }

}