package chess;

import java.util.Scanner;

/**
 * Main class to run a Chess application
 * @author Dev Patel and Eric Chan
 *
 */
public class Chess {
	
	/**
	 * Start point of the chess application
	 * it prompts the players to enter the move
	 * and works with ChessBoard class to simulate 
	 * the game.
	 */
	public static void main(String[] args) {
		ChessBoard board = new ChessBoard();

		Scanner sc = new Scanner(System.in);
		
		System.out.println(board);

		board.promptUser();
		while(sc.hasNextLine()) {
			board.processCommand(sc.nextLine());
			
			if(board.hasGameFinalized()) {
				break;
			}
			
			board.promptUser();
		}
		
		board.printGameResult();
		
		sc.close();
	}

}
