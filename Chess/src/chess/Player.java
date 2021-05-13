package chess;

/**
 * Enum representing a chess player. Chess has only a white and a
 * black player 
 * @author Dev Patel and Eric Chan
 *
 */
public enum Player {
	/**
	 * black player
	 */
	Black("b"), 

	/**
	 * white player
	 */
	White("w");
	
	/**
	 * Keeping the short description of the player
	 */
	private String n;
	
	/**
	 * Method to create the enum
	 * @param s
	 */
	private Player(String s) {
		n = s;
	}

	public String toString() {
		return n;
	}
}