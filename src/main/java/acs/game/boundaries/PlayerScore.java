package acs.game.boundaries;

public class PlayerScore {

    private String username;
    private int score;
    private int trophies;

    public PlayerScore() {

    }

    public PlayerScore(String username, int score, int trophies) {
        this.username = username;
        this.score = score;
        this.trophies = trophies;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

}