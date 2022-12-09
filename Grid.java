/**
 * Bryan Lee
 */

import java.util.*;

public class Grid {
	// Turn state
	private boolean isHuman;
	// Grid state
	private final Line[][] gameGrid;
	// All possible states
	public List<Grid> availableStates;
	// The current line that was added by the player
	private Line insertLine;
	// Holds the  input row and column
	private final Line inputLine;

	// Score for AI
	private int botScore;
	// Score for Human
	private int humanScore;
	// Holds number of total completed boxes
	private int totalCompletedBoxes;

	// Depth of the current Grid Node in MiniMax
	public int currDepth;
	// Value of the current grid used in MiniMax or the leaf node value used in MiniMax
	public int gridValue;

	// Constructor with parameters
	public Grid(int rows, int cols, boolean isHuman) {
		this.isHuman = isHuman;
		this.insertLine = new Line(0, 0, 0);
		this.botScore = 0;
		this.gridValue = 0;
		this.currDepth = 0;
		this.availableStates = new ArrayList<>();
		this.totalCompletedBoxes = 0;

		int rowLen = (rows * 2) + 1;
		int colLen = (cols * 2) + 1;
		this.inputLine = new Line(rows, cols, 0);
		this.gameGrid = new Line[rowLen][colLen];

		// Initialize game grid with 0's
		for (int i = 0; i < rowLen; i++) {
			for (int j = 0; j < colLen; j++) {
				if (j % 2 == 0 || i % 2 == 0) {
					gameGrid[i][j] = new Line(i, j, 0);
				} else {
					// Insert randomized points for each possible mox
					gameGrid[i][j] = new Line(i, j, (int) (Math.floor(Math.random() * 5) + 1));
				}

			}
		}
	}

	// Copy constructor
	public Grid(Grid grid) {
		this.gameGrid = new Line[grid.gameGrid.length][grid.gameGrid[0].length];
		for (int i = 0; i < this.gameGrid.length; i++) {
			System.arraycopy(grid.gameGrid[i], 0, this.gameGrid[i], 0, this.gameGrid[i].length);
		}
		this.insertLine = grid.insertLine;
		this.inputLine = grid.inputLine;
		this.isHuman = grid.isHuman;
		this.botScore = grid.botScore;
		this.humanScore = grid.humanScore;
		this.gridValue = grid.gridValue;
		this.currDepth = grid.currDepth;
		this.availableStates = new ArrayList<>();
		this.totalCompletedBoxes = grid.totalCompletedBoxes;

	}

	// Adds new possible grid state to available grid list
	public void addMove(int row, int col) {
		Grid gridNode = new Grid(this);
		gridNode.currDepth++;
		gridNode.makeMove(row, col);
		availableStates.add(gridNode);
	}

	// Inserts a line at the specified row,col coordinate given by the player
	public boolean makeMove(int row, int col) {
		// Error handling
		if (row < 0 || row >= gameGrid.length || col < 0 || col >= gameGrid[0].length) {
			System.out.println("Error-- Invalid Line position");
			return false;
		}
		if ((row % 2 == 0) == (col % 2 == 0)) {
			System.out.println("Error -- You attempted to draw a line outside of the box");
			return false;
		}
		if (hasConnection(row, col)) {
			System.out.println("Error -- There is already a line here");
			return false;
		}

		// Insert the line at specified position with value 1 which means the spot is taken now
		insertLine = new Line(row, col, 1);
		gameGrid[row][col] = insertLine;

		// Checks if box is completed and increases score for current player
		checkAndUpdateScore(row, col);

		// Switches turn to next player
		isHuman = !isHuman;

		return true;
	}


	// Get all possible moves, used in minimax algorithm
	public void getAvailableMoves() {
		for (int row = 0; row < gameGrid.length; row++) {
			// Alternating iteration account for spaces in between dots
			if (row % 2 == 0) {
				for (int col = 1; col < gameGrid[row].length; col += 2) {
					if (gameGrid[row][col].getValue() < 1)
						addMove(row, col);
				}
			} else {
				for (int col = 0; col < gameGrid[row].length; col += 2) {
					if (gameGrid[row][col].getValue() < 1) {
						addMove(row, col);
					}
				}
			}
		}
	}

	// Prints all available moves to console for user
	public void printAvailableMoves() {
		for (int row = 0; row < gameGrid.length; row++) {
			if (row % 2 == 0) {
				for (int col = 1; col < gameGrid[row].length; col += 2) {
					if (gameGrid[row][col].getValue() < 1) {
						System.out.print(gameGrid[row][col] + " ");
					}
				}
			} else {
				for (int col = 0; col < gameGrid[row].length; col += 2) {
					if (gameGrid[row][col].getValue() < 1) {
						System.out.print(gameGrid[row][col] + " ");
					}
				}
			}
			System.out.println();
		}
	}

	// Evaluation function used in minimax algorithm
	public int evaluation() {
		gridValue = botScore - humanScore;
		return gridValue;
	}

	// Checks for completed boxes after player makes a move, if the box is complete increment the score for current player.
	private void checkAndUpdateScore(int row, int col) {
		int up = row - 1;
		int down = row + 1;
		int left = col - 1;
		int right = col + 1;
		// Checks top and bottom
		if (row % 2 == 0) {
			if (checkCompletedBox(up, col)) {
				if (!isHuman) {
					botScore += gameGrid[up][col].getValue();
				} else {
					humanScore += gameGrid[up][col].getValue();
				}
				totalCompletedBoxes++;
			}
			if (checkCompletedBox(down, col)) {
				if (!isHuman) {
					botScore += gameGrid[down][col].getValue();
				} else {
					humanScore += gameGrid[down][col].getValue();
				}
				totalCompletedBoxes++;
			}
		} else {
			// Checks left and right
			if (checkCompletedBox(row, left)) {
				if (!isHuman) {
					botScore += gameGrid[row][left].getValue();
				} else {
					humanScore += gameGrid[row][left].getValue();
				}
				totalCompletedBoxes++;
			}
			if (checkCompletedBox(row, right)) {
				if (!isHuman) {
					botScore += gameGrid[row][right].getValue();
				} else {
					humanScore += gameGrid[row][right].getValue();
				}
				totalCompletedBoxes++;
			}
		}
	}


	// Checks if the box is completed at specified coordinate
	private boolean checkCompletedBox(int row, int col) {
		return (row > 0 && row < gameGrid.length && col > 0 && col < gameGrid[row].length) &&
				(gameGrid[row - 1][col].getValue() > 0 && gameGrid[row + 1][col].getValue() > 0 &&
						gameGrid[row][col - 1].getValue() > 0 && gameGrid[row][col + 1].getValue() > 0);
	}


	// Checks if all possible boxes are completed, if so game is over.
	public boolean gameOver() {
		return (totalCompletedBoxes == ((inputLine.x) * (inputLine.y)));
	}

	// Prints top part of board for user to easily pick coordinate points
	public String getBoardHeader() {
		StringBuilder sb = new StringBuilder(" ");
		for (int col = 0; col < gameGrid[0].length; col++) {
			sb.append(" ").append(col);
		}

		return sb.toString();
	}

	// Checks if the connection between to points have already been made
	public boolean hasConnection(int row, int col) {
		return gameGrid[row][col].getValue() > 0;
	}

	// Main Print method, prints game board and current score
	public void printGame() {
		// Only print available moves if it is humans turn
		if (isHuman) {
			System.out.println("\n---- Available moves ----");
			printAvailableMoves();
			System.out.println("-----------------------------");
		}

		System.out.println(getBoardHeader());
		int rowLen = gameGrid.length;
		int colLen = gameGrid[0].length;

		for (int row = 0; row < rowLen; row++) {
			System.out.print(row + " ");
			// If the spot is even then it is a space for a horizontal line
			if (row % 2 == 0) {
				for (int col = 0; col < gameGrid[row].length; col++) {
					if (col % 2 == 0) {
						System.out.print("*");
					} else if (hasConnection(row, col)) {
						System.out.print("---");
					} else {
						System.out.print("   ");
					}
				}
			} else {
				// if the space is odd then it is a space for vertical line
				for (int col = 0; col < colLen; col++) {
					if (col % 2 != 0) {
						System.out.print(" " + gameGrid[row][col].getValue() + " ");
					} else if (hasConnection(row, col)) {
						System.out.print("|");
					} else {
						System.out.print(" ");
					}
				}
			}
			System.out.println();
		}


		printScore();
	}

	public void printScore() {
		System.out.println("\n*───* Total Score *───*");
		System.out.println("Player: " + humanScore);
		System.out.println("AI: " + botScore);
		System.out.println("----------------------------\n");
	}

	public void printWinner() {
		if (humanScore > botScore) {
			System.out.println("The winner is: Human!");
			System.out.println("WooHoo!");
		} else if (humanScore < botScore) {
			System.out.println("The winner is: AI!");
			System.out.println("Better luck next time!");
		} else {
			System.out.println("Tie Game!");
		}
	}

	// Getter methods
	public boolean isHuman() {
		return isHuman;
	}

	public Line getInsertLine() {
		return insertLine;
	}

	public int getBotScore() {
		return botScore;
	}

	public int getHumanScore() {
		return humanScore;
	}
}
