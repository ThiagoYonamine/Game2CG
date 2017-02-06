package toolBox;

public class Score {
	public int shots;
	public int kills;

	public Score() {
		reset();
	}

	public void increaseShots() {
		shots++;
	}

	public void increaseKills() {
		kills++;
	}

	public void reset() {
		shots = kills = 0;
	}
}
