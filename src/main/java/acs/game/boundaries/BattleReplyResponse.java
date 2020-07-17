package acs.game.boundaries;

public class BattleReplyResponse {

	private boolean ready; // round result is ready
	private String otherShape; // opponent shape
	private int result; // prev round result -1/0/1, lose/tie/win
	private String phrase; // round phrase
	private boolean over; // battle is over
	private boolean winner; // true if im the winner

	public BattleReplyResponse(boolean ready) {
		this.ready = ready;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public String getOtherShape() {
		return otherShape;
	}

	public void setOtherShape(String otherShape) {
		this.otherShape = otherShape;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public boolean isOver() {
		return over;
	}

	public void setOver(boolean over) {
		this.over = over;
	}

	public boolean isWinner() {
		return winner;
	}

	public void setWinner(boolean winner) {
		this.winner = winner;
	}

	public BattleReplyResponse(boolean ready, String otherShape, int result, String phrase, boolean over,
			boolean winner) {
		this.ready = ready;
		this.otherShape = otherShape;
		this.result = result;
		this.phrase = phrase;
		this.over = over;
		this.winner = winner;
	}

}