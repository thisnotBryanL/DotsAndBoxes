/**
 * Bryan Lee
 */

import java.util.Scanner;

public class Game {

	public static void main(String[] args) {
		System.out.println("-- CSC 480 Dots and Boxes--");

		// Main grid used for the game
		Grid gameGrid;

		// X,Y move coordinates that is in string format that will be parsed to coordinates in a line
		String[] moveStr;

		// Game error state
		boolean gameState;
		boolean validInput = false;

		// Scanned to read user input
		Scanner scanner = new Scanner(System.in);
		int widthInput = 0;
		int heightInput = 0;
		int numPlysInput = 0;
		int startingPlayerInput = 0;

		// Reading input and checking for valid inputs
		do {
			try {
				// Since the grid will be a square, only need one input for size
				System.out.println("\nThe game board will be a NxN square, please input how big you want the board (ex.4)");
				widthInput = heightInput = Integer.parseInt(scanner.nextLine());
				System.out.println("You have selected a " + widthInput + "x"+ heightInput + " board that will have a total of "
						+ widthInput * heightInput + " boxes available.");
				validInput = true;
			}catch(NumberFormatException e){
				System.err.println("Bad input for size: " + e);
				continue;
			}

			try {
				System.out.println("\nPlease enter the number of Plys. (Bot Difficulty) " +
						"The higher the number the harder the bot.");
				numPlysInput = Integer.parseInt(scanner.nextLine());
			} catch(NumberFormatException e) {
				System.err.print("Bad input for Plys: " + e);
				validInput = false;
				continue;
			}

			System.out.println("\nWho will go first?:");
			System.out.println("Enter '1' for Player");
			System.out.println("Enter '2' for Computer");
			try {
				startingPlayerInput = Integer.parseInt(scanner.nextLine());
			} catch(NumberFormatException e) {
				System.err.print("Bad input for Player Selection: " + e);
			}
			if(startingPlayerInput != 1 && startingPlayerInput != 2) {
				validInput = false;
			}
		}while(!validInput);

		if(startingPlayerInput == 2) {
			gameGrid = new Grid(heightInput, widthInput, false);
			aiMove(gameGrid, numPlysInput);
		}else{
			gameGrid = new Grid(heightInput, widthInput, true);
		}

		System.out.println("Enter your move in the format of '<row>,<col>'");
		gameGrid.printGame();

		// Main game loop
		while(!gameGrid.gameOver()) {
			Line humanLine = new Line(0,0,0);
			// Humans turn
			do {
				System.out.print("Your move (format \"<row>,<column>\"): ");
				moveStr = scanner.nextLine().split(",");
				gameState = true;
				if(moveStr.length != 2) {
					System.out.println("Error -- Invalid move. Please use the format: <row>,<column>.");
					gameState = false;
				} else {
					try {
						humanLine.x = Integer.parseInt(moveStr[0]);
						humanLine.y = Integer.parseInt(moveStr[1]);
					} catch (NumberFormatException e) {
						System.out.println("That move is invalid. Please enter the desired move in <row>,<column> format.");
						gameState = false;
					}

				}
				if(gameState) {
					// Human's entered move and check for valid move
					gameState = gameGrid.makeMove(humanLine.x, humanLine.y);
				}
			} while (!gameState); // After a valid move is placed, break loop and move onto AI turn

			System.out.println();
			// Print Game Board
			gameGrid.printGame();

			// Game has not ended yet and is AI's turn
			if(!gameGrid.gameOver()) {
				aiMove(gameGrid, numPlysInput);
				gameGrid.printGame(); // Print Game Board
			}
		}
		scanner.close();

		// Final score printed
		System.out.println("Final Score");
		System.out.println("Human score: " + gameGrid.getHumanScore());
		System.out.println("AI score: " + gameGrid.getBotScore());
		gameGrid.printWinner();
	}

	// Method that controls Ai's turn
	private static void aiMove(Grid gameGrid, int numPlysInput){
		System.out.print("AI's move: ");
		Line moveLine = MiniMaxSearch.makeMoveAI(gameGrid, numPlysInput);
		gameGrid.makeMove(moveLine.x, moveLine.y);
		System.out.println(moveLine.x + "," + moveLine.y);
	}
}
