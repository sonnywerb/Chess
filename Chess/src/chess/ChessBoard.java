package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Class to represent a ChessBoard
 * This class contains the members such as current player
 * , chess pieces, game status etc, which is required to 
 * simulate the chess game.
 * @author Dev Patel and Eric Chan
 *
 */
public class ChessBoard {

	/**
	 * Member representing the number of blocks in 
	 * each row and column of the chess
	 */
	private final static int SIDE = 8;
	
	/**
	 * Array of ChessBoard cells.
	 */
	private ChessBoardCell cells[][];
	
	/**
	 * Stack registering the moves made by different Chesspieces
	 */
	private Stack<ChessPiece> moves;
	
	/**
	 * The player whose turn it is to make the next move
	 */
	private Player currentPayer;
	
	/**
	 * Flag representing if the current player's king is under check
	 */
	private boolean checkStarted;

	/**
	 * Member to keep the winner of the game on finalization
	 */
	private Player winner;
	/**
	 * Flag to keep the game status (Running or completed)
	 */
	private boolean playCompleted;
	/**
	 * If last player wants to prompt for Draw to next player
	 */
	private boolean drawPrompted;

	/**
	 * Create a ChessBoard with a SIDE*SIDE chess cells.
	 * Each cell alternates in white and black color
	 * Also, according to the players, the chess pieces are
	 * placed in the ranks.
	 */
	public ChessBoard() {
		cells = new ChessBoardCell[SIDE][SIDE];

		// place the required cells.
		for (int row = 0; row < SIDE; row++) {
			boolean white = row % 2 == 0;

			for (int col = 0; col < SIDE; col++) {
				cells[row][col] = new ChessBoardCell(row, col, white);
				white = !white;
			}
		}
		placePiecesInitially();
		moves = new Stack<>(); // To track the first moves of the pawns
		currentPayer = Player.White;
		checkStarted = false;
		playCompleted = false;
		drawPrompted = false;
		winner = null;
	}

	/**
	 * Method to place the white and black pieces on specific rows
	 * at the start of the game
	 */
	private void placePiecesInitially() {

		// place pieces for ranks 1,8
		for (int row : Arrays.asList(0, SIDE - 1)) {
			Player owner = (row == 0 ? Player.Black : Player.White);

			cells[row][0].putPiece(new ChessPiece(PieceType.R, owner));
			cells[row][SIDE - 1].putPiece(new ChessPiece(PieceType.R, owner));

			cells[row][1].putPiece(new ChessPiece(PieceType.N, owner));
			cells[row][SIDE - 2].putPiece(new ChessPiece(PieceType.N, owner));

			cells[row][2].putPiece(new ChessPiece(PieceType.B, owner));
			cells[row][SIDE - 3].putPiece(new ChessPiece(PieceType.B, owner));

			cells[row][3].putPiece(new ChessPiece(PieceType.Q, owner));
			cells[row][4].putPiece(new ChessPiece(PieceType.K, owner));
		}

		// Put pawns on rank 2 and 7
		for (int row : Arrays.asList(1, SIDE - 2)) {
			Player owner = (row == 1 ? Player.Black : Player.White);

			for (int col = 0; col < SIDE; col++) {
				cells[row][col].putPiece(new ChessPiece(PieceType.p, owner));
			}
		}
	}
	
	/**
	 * Method to check if a chess Piece exists on a given row/Col
	 * @param r Row from 0 to SIDE-1
	 * @param c Col from 0 to SIDE-1
	 * @return true if chess cell contains a piece
	 */
	private boolean isCellOccupied(int r, int c) {
		return cells[r][c].getPiece() != null;
	}
	
	/**
	 * method to verify if a given pair of (row, col) is a valid
	 * position on the board.
	 * @param row
	 * @param col
	 * @return true if position is valid
	 */
	private boolean isValid(int row, int col) {
		return !(row < 0 || row >= SIDE || col < 0 || col >= SIDE);
	}

	/**
	 * Method to get the owner of the piece on specifc row/col
	 * @param row Row from 0 to SIDE-1
	 * @param col Col from 0 to SIDE-1
	 * @return The player object if chess piece is present else null
	 */
	private Player getPlayer(int row, int col) {
		if (cells[row][col].getPiece() == null) {
			return null;
		}
		return cells[row][col].getPiece().owner;
	}

	/**
	 * This method adjusts the input fileRank param into numeric
	 * board row, col and return a Position object containing those.
	 * @param file char from 'a' to 'h'
	 * @param rank char from '1' to '8'
	 * @return the numeric Position for fileRank
	 */
	private Position fileRankToPosition(String fileRank) {
		int file = fileRank.charAt(0) - 'a';
		int rank = SIDE - (fileRank.charAt(1) - '0');
		return new Position(rank, file);
	}
	
	/**
	 * Method which returns all the possible moves which can be made by the 
	 * piece on the input chessCell.
	 * @param cell Board cell where piece has been kept
	 * @return the list of positions where the piece can move legally
	 */
	private ArrayList<Position> getValidMoves(ChessBoardCell cell) {

		ArrayList<Position> results = new ArrayList<>();
		if (cell.getPiece() == null) {
			return results;
		}

		Player forPlayer = cell.getPiece().owner;

		// inline lambda to add the position to results.
		Consumer<Position> addIfUnOccupied = x -> {
			if (isValid(x.r, x.c) && !isCellOccupied(x.r, x.c)) {
				results.add(x);
			}
		};

		Consumer<Position> addIfUnOccupiedOrOpponent = x -> {
			if (isValid(x.r, x.c) && (!isCellOccupied(x.r, x.c) || (getPlayer(x.r, x.c) != forPlayer))) {
				results.add(x);
			}
		};

		Consumer<Position> addIfOccupiedByOpponent = x -> {
			// We can not kill our piece
			if (isValid(x.r, x.c) && isCellOccupied(x.r, x.c) && getPlayer(x.r, x.c) != forPlayer) {
				results.add(x);
			}
		};

		// returns true if we need to continue, else returns false
		Predicate<Position> addIfValid = x -> {
			if (isValid(x.r, x.c) && getPlayer(x.r, x.c) != forPlayer) {
				results.add(x);
				return getPlayer(x.r, x.c) == null; // If it is opponent's piece, then we need to stop after.
			}
			return false;
		};

		ChessPiece piece = cell.getPiece();

		int r = cell.getRow();
		int c = cell.getCol();

		// pawn
		if (piece.symbol == PieceType.p) {
			int direction = 1;
			if (piece.owner == Player.White) {
				direction = -1;
			}

			// If this is the first move
			if (!moves.contains(piece)) {
				addIfUnOccupied.accept(new Position(r + direction * 2, c));
			}
			addIfUnOccupied.accept(new Position(r + direction, c));

			// It can capture diagonally as well if occupied
			addIfOccupiedByOpponent.accept(new Position(r + direction, c - 1));
			addIfOccupiedByOpponent.accept(new Position(r + direction, c + 1));
		}

		// King
		if (piece.symbol == PieceType.K) {

			// If this is the first move
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (!(i == 0 && j == 0)) {
						addIfValid.test(new Position(r + i, c + j));
					}
				}
			}
		}

		// Rook or Queen
		if (piece.symbol == PieceType.R || piece.symbol == PieceType.Q) {
			// Can not leap over other pieces
			for (int i = 1; i < SIDE; i++) {
				if (!addIfValid.test(new Position(r + i, c))) {
					break;
				}
			}
			for (int i = 1; i < SIDE; i++) {
				if (!addIfValid.test(new Position(r - i, c))) {
					break;
				}
			}
			for (int j = 1; j < SIDE; j++) {
				if (!addIfValid.test(new Position(r, c + j))) {
					break;
				}
			}
			for (int j = 1; j < SIDE; j++) {
				if (!addIfValid.test(new Position(r, c - j))) {
					break;
				}
			}
		}

		// Bishop or Queen
		if (piece.symbol == PieceType.B || piece.symbol == PieceType.Q) {
			// Can not leap over other pieces
			for (int i = 1; i < SIDE; i++) {
				if (!addIfValid.test(new Position(r + i, c + i))) {
					break;
				}
			}
			for (int i = 1; i < SIDE; i++) {
				if (!addIfValid.test(new Position(r - i, c + i))) {
					break;
				}
			}
			for (int i = 1; i < SIDE; i++) {
				if (!addIfValid.test(new Position(r + i, c - i))) {
					break;
				}
			}
			for (int i = 1; i < SIDE; i++) {
				if (!addIfValid.test(new Position(r - i, c - i))) {
					break;
				}
			}
		}

		// Knight
		if (piece.symbol == PieceType.N) {
			// Can leap over other pieces

			for (int i : Arrays.asList(1, 2)) {
				int j = (i == 1) ? 2 : 1;
				addIfUnOccupiedOrOpponent.accept(new Position(r + i, c + j));
				addIfUnOccupiedOrOpponent.accept(new Position(r + i, c - j));
				addIfUnOccupiedOrOpponent.accept(new Position(r - i, c + j));
				addIfUnOccupiedOrOpponent.accept(new Position(r - i, c - j));
			}
		}

		// Support for En passant
		results.addAll(getMovesForEnPassant(cell));

		return results;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < SIDE; row++) {
			for (int col = 0; col < SIDE; col++) {
				sb.append(cells[row][col].toString());
				sb.append(" ");
			}
			sb.append(SIDE - row);
			//sb.append(" " + row);
			sb.append("\n");
		}
		for (int col = 0; col < SIDE; col++) {
			if (col != 0) {
				sb.append(" ");
			}
			char c = (char) ('a' + col);
			//sb.append(col + "" + c);
			sb.append(String.format("%2c", c));
		}

		return sb.toString();
	}

	/**
	 * Method which processes the next command from user.
	 * @param cmd user instruction
	 */
	public void processCommand(String cmd) {
		System.out.println(cmd);
		
		// if it is a draw instruction
		if (cmd.startsWith("draw")) {
			if (drawPrompted) {
				playCompleted = true;
			}
		}
		
		else if (cmd.startsWith("resign")) {
			winner = (currentPayer == Player.Black ? Player.White : Player.Black);
			playCompleted = true;
		}

		else {
			// Player want to move its piece, lets do it.
			if (makeMove(cmd)) {
				System.out.println();
				System.out.println(this);
				togglePlayer();
			} else {
				System.out.println("\nIllegal move, try again");
			}
		}
	}
	
	/**
	 * Method to fetch the chessboard cell for the specific Position
	 * Precondition: Position is a valid position on board
	 * @param p
	 * @return ChessBoard cell object
	 */
	private ChessBoardCell getCellAtPosition(Position p) {
		return cells[p.r][p.c];
	}
	
	/**
	 * Method to alternate the turn between players
	 */
	private void togglePlayer() {
		if (currentPayer == Player.Black) {
			currentPayer = Player.White;
		} else {
			currentPayer = Player.Black;
		}
	}
	
	/**
	 * Method to get a duplicate chessBoardCells backup. It is required for the cases
	 * where after making some move, we realize that it can put own King under check
	 * and then would have to revert the board.
	 * note this method does a deep copy of ChessBoardCells.
	 * @return Array of Board cells.
	 */
	private ChessBoardCell[][] getBackupGrid() {
		ChessBoardCell[][] backupGrid = new ChessBoardCell[SIDE][SIDE];

		// place the required cells.
		for (int row = 0; row < SIDE; row++) {
			for (int col = 0; col < SIDE; col++) {
				// create copy, so that the composed piece do not
				// get moved from the original cell
				backupGrid[row][col] = new ChessBoardCell(cells[row][col]);
			}
		}

		return backupGrid;
	}

	/**
	 * Method which allows the user to move its piece from one position
	 * to other.
	 * @param cmd Command to move file which is like "f1r1 f2r2"
	 * @return True if the move was successful
	 */
	private boolean makeMove(String cmd) {
		String tokens[] = cmd.split("\\s+");
		Position fromLocation = fileRankToPosition(tokens[0]);
		Position toLocation = fileRankToPosition(tokens[1]);

		if (tokens.length == 3 && tokens[2].equals("draw?")) {
			drawPrompted = true;
		} else {
			drawPrompted = false;
		}

		ChessBoardCell startCell = getCellAtPosition(fromLocation);
		ChessBoardCell destCell = getCellAtPosition(toLocation);

		Player opponent = currentPayer == Player.Black ? Player.White : Player.Black;

		// If there is no piece at the mentioned position
		// Or the piece do not belong to the current player
		if (startCell.getPiece() == null || startCell.getPiece().owner != currentPayer) {
			return false;
		}

		ArrayList<Position> validMoves = getValidMoves(startCell);

		// Add Support for castling.
		validMoves.addAll(getMovesForCastling(startCell));

		// System.out.println(validMoves);

		if (!validMoves.contains(toLocation)) {
			return false;
		}

		ChessBoardCell[][] backupGrid = getBackupGrid();
		Stack<ChessPiece> backupMoves = (Stack<ChessPiece>) moves.clone();

		// Now move the piece.
		boolean isEnPassantMove = false;
		ChessPiece piece = startCell.getPiece();
		startCell.removePiece();
		if(destCell.getPiece() == null
				&& piece.symbol == PieceType.p
				&& Math.abs(destCell.getCol()-startCell.getCol()) == 1) {
			// A pawn can make the cross move only when enPassant move
			// and cross cell is empty.
			// In this case, we need to remove the middle piece as well.
			isEnPassantMove = true;
		}
		
		destCell.putPiece(piece);
		moves.add(piece);

		if(isEnPassantMove) {
			cells[startCell.getRow()][destCell.getCol()].removePiece();
		}
		
		moveRookIfNeeded(piece, startCell, destCell, true);

		// Check if currently we are under check, if yes, then only possible move
		// will be to get the king unchecked

		// checking if the current move made by us will bring our
		// own king under attack, then Revert grid.
		ArrayList<ChessBoardCell> playersKingCell = findCellWithPlayerAndPiece(PieceType.K, currentPayer);
		if (!playersKingCell.isEmpty()
				&& getAllPositionForAttack(opponent).contains(playersKingCell.get(0).getCellPosition())) {
			System.out.println("King comes under attack, hence reverting");
			cells = backupGrid;
			moves = backupMoves;
			return false;
		}

		if ((destCell.getRow() == 0 || destCell.getRow() == SIDE - 1) && (piece.symbol == PieceType.p)) {

			// We can promote the pawn at this point.
			// pawn can only be promoted to queen, rook, bishop, or knight of the same
			// color.
			destCell.putPiece(new ChessPiece(PieceType.Q, currentPayer));

			if (tokens.length == 3) {
				if (tokens[2].equals("N")) {
					destCell.putPiece(new ChessPiece(PieceType.N, currentPayer));
				} else if (tokens[2].equals("R")) {
					destCell.putPiece(new ChessPiece(PieceType.R, currentPayer));
				} else if (tokens[2].equals("B")) {
					destCell.putPiece(new ChessPiece(PieceType.B, currentPayer));
				}
			}
		}

		// Check if opponent king is under attack now. (Check)
		ArrayList<ChessBoardCell> oppponentKingCell = findCellWithPlayerAndPiece(PieceType.K, opponent);
		if (!oppponentKingCell.isEmpty()
				&& getAllPositionForAttack(currentPayer).contains(oppponentKingCell.get(0).getCellPosition())) {
			checkStarted = true;
			System.out.println("\nCheck");
		} else {
			checkStarted = false;
		}

		return true;
	}

	/**
	 * This Method moves the rook in case it needs to be moved as the result
	 * of castling
	 * @param piece Piece which has been moved in this round (Should be king 
	 * 	for castling)
	 * @param startCell Cell from where Piece started moving
	 * @param destCell Cell on which Piece has moved
	 * @param addMoves If we want to track the moves in the stack of moves.
	 */
	private void moveRookIfNeeded(ChessPiece piece, ChessBoardCell startCell, ChessBoardCell destCell,
			boolean addMoves) {

		if (piece.symbol == PieceType.K && Math.abs(destCell.getCol() - startCell.getCol()) == 2) {

			// King side castling
			if (destCell.getCol() > startCell.getCol()) {
				// Shift rook as well now.
				ChessBoardCell rookCell = cells[startCell.getRow()][SIDE - 1];
				ChessPiece rook = rookCell.removePiece();
				cells[startCell.getRow()][startCell.getCol() + 1].putPiece(rook);
				if (addMoves) {
					moves.add(rook);
				}
			}

			// Queen side
			else {
				ChessBoardCell rookCell = cells[startCell.getRow()][0];
				ChessPiece rook = rookCell.removePiece();
				cells[startCell.getRow()][startCell.getCol() - 1].putPiece(rook);
				if (addMoves) {
					moves.add(rook);
				}
			}
		}
	}

	/**
	 * this method scans the board to find a cell which has a specific kind of piece
	 * and belongs to specific player.
	 * @param pieceType Valid piece type of Chess like (K, R, B etc.)
	 * @param player Player White or black
	 * @return List of cells having such pieces
	 */
	ArrayList<ChessBoardCell> findCellWithPlayerAndPiece(PieceType pieceType, Player player) {
		ArrayList<ChessBoardCell> locs = new ArrayList<>();
		for (ChessBoardCell[] row : cells) {
			for (ChessBoardCell cell : row) {
				if (cell.getPiece() != null && cell.getPiece().symbol == pieceType && cell.getPiece().owner == player) {
					locs.add(cell);
				}
			}
		}
		return locs;
	}

	/**
	 * Method to return all the cells where an attack can be done by the pieces
	 * of mentioned player
	 * @param player Player White or black
	 * @return List of cells where given player can make the attack on opponent
	 */
	private ArrayList<Position> getAllPositionForAttack(Player player) {
		ArrayList<Position> results = new ArrayList<>();

		for (ChessBoardCell[] row : cells) {
			for (ChessBoardCell cell : row) {
				if (cell.getPiece() == null || cell.getPiece().owner != player) {
					continue;
				}
				results.addAll(getValidMoves(cell));
			}
		}

		return results;
	}

	/**
	 * Method to get the positions for pawn in case if it qualifies for an enPassant
	 * condition
	 * @param startCell Cell where pawn is contained
	 * @return List of cells on which pawn can move as part of EnPassant
	 */
	private ArrayList<Position> getMovesForEnPassant(ChessBoardCell startCell) {
		ArrayList<Position> results = new ArrayList<>();

		Player forPlayer = startCell.getPiece().owner;
		Player opponent = (forPlayer == Player.Black) ? Player.White : Player.Black;

		if (startCell.getPiece().symbol == PieceType.p) {

			int direction = 1;
			if (startCell.getPiece().owner == Player.White) {
				direction = -1;
			}

			int row = startCell.getRow();
			int col = startCell.getCol();

			if (isValid(row, col - 1) && cells[row][col - 1].getPiece() != null
					&& cells[row][col - 1].getPiece().symbol == PieceType.p
					&& cells[row][col - 1].getPiece().owner == opponent && !moves.empty()
					&& moves.peek() == cells[row][col - 1].getPiece() && isValid(row + direction, col - 1)
					&& cells[row + direction][col - 1].getPiece() == null) {
				results.add(new Position(row + direction, col - 1));
			}

			if (isValid(row, col + 1) && cells[row][col + 1].getPiece() != null
					&& cells[row][col + 1].getPiece().symbol == PieceType.p
					&& cells[row][col + 1].getPiece().owner == opponent && !moves.empty()
					&& moves.peek() == cells[row][col + 1].getPiece() && isValid(row + direction, col + 1)
					&& cells[row + direction][col + 1].getPiece() == null) {
				results.add(new Position(row + direction, col + 1));
			}
		}

		return results;
	}

	/**
	 * Method to get the positions for King in case if it qualifies for a castling move
	 * condition
	 * @param startCell Cell where King is contained
	 * @return List of cells on which King can move as part of Castling move
	 */
	private ArrayList<Position> getMovesForCastling(ChessBoardCell startCell) {
		ArrayList<Position> results = new ArrayList<>();

		if (checkStarted || startCell.getPiece() == null) {
			return results;
		}

		Player forPlayer = startCell.getPiece().owner;
		Player opponent = (forPlayer == Player.Black) ? Player.White : Player.Black;

		// Castling can only be performed, if the king and rook both have not moved even
		// once.
		if (startCell.getPiece().symbol == PieceType.K && !moves.contains(startCell.getPiece())) {
			ChessBoardCell king = startCell;
			ArrayList<ChessBoardCell> rookCell = findCellWithPlayerAndPiece(PieceType.R, forPlayer);

			for (ChessBoardCell rook : rookCell) {
				if (!moves.contains(rook.getPiece())) {

					boolean isSpaceFree = true;
					ArrayList<Position> kingMovesPosition = new ArrayList<>();

					// Add king position in move
					kingMovesPosition.add(new Position(startCell.getRow(), startCell.getCol()));
					Position destination = null;

					// King side castling
					if (rook.getCol() > king.getCol()) {
						for (int i = king.getCol() + 1; i < rook.getCol(); i++) {
							if (cells[king.getRow()][i].getPiece() != null) {
								isSpaceFree = false;
							}
							kingMovesPosition.add(new Position(king.getRow(), i));
						}
						destination = new Position(rook.getRow(), rook.getCol() - 1);
					}
					// Queen side castling
					else {
						for (int i = rook.getCol() + 1; i < king.getCol(); i++) {
							if (cells[king.getRow()][i].getPiece() != null) {
								isSpaceFree = false;
							}

							// King moves do not cover adjacent square to rook in Queen side castling.
							if (i != rook.getCol() + 1)
								kingMovesPosition.add(new Position(king.getRow(), i));
						}
						destination = new Position(rook.getRow(), rook.getCol() + 2);
					}

					if (!isSpaceFree) {
						continue;
					}

					ArrayList<Position> positionsUnderAttack = getAllPositionForAttack(opponent);

					// No cell of the move should be under attack
					ArrayList<Position> intersection = new ArrayList<>(positionsUnderAttack);
					intersection.retainAll(kingMovesPosition);
					if (!intersection.isEmpty()) {
						continue;
					}

					// It can be a valid move now.
					results.add(destination);
				}
			}
		}

		return results;
	}

	/**
	 * Method to prompt the current user to make a move
	 */
	public void promptUser() {
		if (currentPayer != Player.Black) {
			System.out.print("\nWhite's move: ");
		} else {
			System.out.print("\nBlack's move: ");
		}
	}

	/**
	 * Method which tells if the game has reached a terminal stage. Game reaches to
	 * a terminal stage when it is won, draw or resigned.
	 * @return true if reached to terminal stage
	 */
	public boolean hasGameFinalized() {
		if (playCompleted || (winner != null)) {
			return true;
		}
		
		Player opponent = (currentPayer == Player.Black) ? Player.White : Player.Black;
		
		// Flag to check if player has a possible move
		boolean noMovePossible = true;
		
		// current player needs to make some move so that
		// its king is no longer under attack.
		for (int row = 0; row < SIDE; row++) {
			for (int col = 0; col < SIDE; col++) {
				ChessBoardCell cell = cells[row][col];
				if (cell.getPiece() == null || cell.getPiece().owner != currentPayer) {
					continue;
				}

				ArrayList<Position> validMoves = getValidMoves(cell);
				
				if(!validMoves.isEmpty()) {
					noMovePossible = false;
				}
				
				if(checkStarted) {
					// we can shift our piece on all these positions
					// and check if it is possible to save our king.
					for (Position movedPos : validMoves) {
						ChessBoardCell[][] backupGrid = getBackupGrid();
						ChessBoardCell destCell = cells[movedPos.r][movedPos.c];
						cell = cells[row][col];
						ChessPiece piece = cell.getPiece();
						
						// place the piece
						destCell.putPiece(piece);
						cell.removePiece();
						moves.add(piece);
	
						moveRookIfNeeded(piece, cell, destCell, false);
	
						boolean underAttack = true;
	
						// checking if the current move can save king
						ArrayList<ChessBoardCell> playersKingCell = findCellWithPlayerAndPiece(PieceType.K, currentPayer);
						if (!playersKingCell.isEmpty()
								&& !getAllPositionForAttack(opponent).contains(playersKingCell.get(0).getCellPosition())) {
							underAttack = false;
						}
	
						// Revert grid now as we do not want to make any change to grid.
						cells = backupGrid;
						moves.pop();
						
						// We player is under check and has a possible move
						// to avoid check, then we are good for next turn
						if (!underAttack) {
							return false;
						}
					}		
				}
			}
		}
		
		if(checkStarted) {
			// If we come here, it means we do not have
			// a valid response to check.
			// *** Checkmate
			// Set that the game is won by the other player
			winner = opponent;
			System.out.println("\nCheckmate");
			return true;
		}
		
		// If no move was possible, it is again a draw game
		if(noMovePossible) {
			playCompleted = true;
			winner = null;
			return true;
		}

		return false;
	}
	
	/**
	 * Method to print the result of the game
	 */
	public void printGameResult() {
		if (winner != null) {
			System.out.println("\n" + winner.name() + " wins");
		} else {
			System.out.println("draw");
		}
	}
}
