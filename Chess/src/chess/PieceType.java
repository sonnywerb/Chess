package chess;

/**
 * Enum representing the possible types of chess pieces
 * such as Rook, Knight etc.
 * @author Dev Patel and Eric Chan
 *
 */
public enum PieceType {
	/**
	 * Rook 
	 */
	R("Rook"), 

	/**
	 * Knight, Note that Knight is presented with N and not K
	 */
	N("Knight"), 

	/**
	 * Bishop 
	 */
	B("Bishop"),

	/**
	 * Queen 
	 */
	Q("Queen"), 

	/**
	 * King 
	 */
	K("King"), 

	/**
	 * Pawn 
	 */
	p("Pawn");
	
	/**
	 * Member to keep the full name of the piece.
	 */
	private String name;
	
	/**
	 * Method to create a pieceType.
	 * @param s
	 */
	private PieceType(String s) {
		name = s;
	}
	
	public String toString() {
		return this.name();
	}
}
