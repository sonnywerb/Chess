package chess;

/**
 * Class representing the cell of a ChessBoard.
 * A cell in chessboard can have a specific color
 * and can optionally contains a chessPiece
 * @author Dev Patel and Eric Chan
 *
 */
public class ChessBoardCell {
	
	/**
	 * Row index for the cell.
	 */
	private int row;

	/**
	 * Col index for the cell.
	 */
	private int col;
	
	/**
	 * Flag to tell if it is a white cell.
	 */
	private boolean whiteCell;
	
	/**
	 * Chess Piece kept on the cell (optional)
	 */
	private ChessPiece piece;

	/**
	 * Create a cell wiht given row,col index and color
	 * @param row
	 * @param col
	 * @param whiteCell true if it is a white cell else false
	 */
	public ChessBoardCell(int row, int col, boolean whiteCell) {
		this(row, col, whiteCell, null);
	}
	/**
	 * method to create the copy of a cell with the piece.
	 * @param other
	 */
	public ChessBoardCell(ChessBoardCell other) {
		this(other.row, other.col, other.whiteCell, other.piece);
	}

	/**
	 * Method to create the cell with specific color and piece on 
	 * the specific row/col index.
	 * @param row
	 * @param col
	 * @param whiteCell
	 * @param piece
	 */
	public ChessBoardCell(int row, int col, boolean whiteCell, ChessPiece piece) {
		this.row = row;
		this.col = col;
		this.whiteCell = whiteCell;
		this.piece = piece;
	}
	
	/**
	 * Getter for row
	 * @return row index from 0 to SIDE-1
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Getter for col
	 * @return col index from 0 to SIDE-1
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Getter for chess piece on the cell which may or may not be present
	 * @return Chess piece object if present else null
	 */
	public ChessPiece getPiece() {
		return piece;
	}

	/**
	 * Method to put a chess piece on the cell
	 * @param piece
	 */
	public void putPiece(ChessPiece piece) {
		if(piece != null) {
			this.piece = piece;
		}
	}
	
	/**
	 * Method to remove the current piece on the cell
	 * @return the removed piece if any
	 */
	public ChessPiece removePiece() {
		ChessPiece removedPiece = piece;
		piece = null;
		return removedPiece;
	}
	
	/**
	 * method to get the row/col Position in a wrapper object
	 * @return
	 */
	public Position getCellPosition() {
		return new Position(row, col);
	}

	@Override
	public String toString() {
		if(piece == null) {
			return whiteCell ? "  " : "##";
		}
		return piece.owner.toString() + piece.symbol;
	}
}
