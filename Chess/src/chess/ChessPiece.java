package chess;

/**
 * Class representing a chess piece in the game of chess.
 * A piece has a owner and has a specific chess Symbol
 * such as King, queen, pawn etc.
 * @author Dev Patel and Eric Chan
 */
public class ChessPiece {

	/**
	 * symbol of the piece. Symbol can not be changed once assigned.
	 */
	public final PieceType symbol;

	/**
	 * owner of the piece. Owner can not be changed once assigned.
	 */
	public final Player owner;
	
	/**
	 * Method to create a chesspiece with given symbol and owner
	 * @param symbol
	 * @param owner
	 */
	public ChessPiece(PieceType symbol, Player owner) {
		this.symbol = symbol;
		this.owner = owner;
	}
}
