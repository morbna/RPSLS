package acs.game.logic;

import org.springframework.stereotype.Service;

import acs.game.data.Shape;

@Service
public class GameRuler {

    // number of wins needed to win battle
    final int BEST_OF = 3;

    public GameRuler() {

    }

    // round phrase
    public String getPhrase(Shape s1, Shape s2) {
        int sum = (int) (Math.pow(2, s1.ordinal()) + Math.pow(2, s2.ordinal()));

        switch (sum) {
            case 6:
                return "Scissors cuts Paper!";
            case 3:
                return "Paper covers Rock!";
            case 9:
                return "Rock crushes Lizard!";
            case 24:
                return "Lizard poisons Spock!";
            case 20:
                return "Spock smashes Scissors!";
            case 12:
                return "Scissors decapitates Lizard!";
            case 10:
                return "Lizard eats Paper!";
            case 18:
                return "Paper disproves Spock!";
            case 17:
                return "Spock vaporizes Rock!";
            case 5:
                return "Rock crushes Scissors!";
            default:
                return "Tie!";
        }
    }

    public int rule(Shape p1, Shape p2) { // -1: lose | 0: draw | 1: win
        switch (p1) {
            case ROCK:
                switch (p2) {
                    case PAPER:
                    case SPOCK:
                        return -1;
                    case SCISSORS:
                    case LIZARD:
                        return 1;
                    default:
                        return 0;
                }
            case PAPER:
                switch (p2) {
                    case SCISSORS:
                    case LIZARD:
                        return -1;
                    case ROCK:
                    case SPOCK:
                        return 1;
                    default:
                        return 0;
                }
            case SCISSORS:
                switch (p2) {
                    case ROCK:
                    case SPOCK:
                        return -1;
                    case PAPER:
                    case LIZARD:
                        return 1;
                    default:
                        return 0;
                }
            case LIZARD:
                switch (p2) {
                    case ROCK:
                    case SCISSORS:
                        return -1;
                    case PAPER:
                    case SPOCK:
                        return 1;
                    default:
                        return 0;
                }
            case SPOCK:
                switch (p2) {
                    case LIZARD:
                    case PAPER:
                        return -1;
                    case ROCK:
                    case SCISSORS:
                        return 1;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    public boolean isOver(int p1Wins, int p2Wins) {
        if (p1Wins >= this.BEST_OF || p2Wins >= this.BEST_OF)
            return true;
        return false;
    }

}
