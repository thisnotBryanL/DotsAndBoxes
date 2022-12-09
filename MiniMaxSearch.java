/**
 * Bryan Lee
 */

public class MiniMaxSearch {
	// Constants used for MiniMax
	private static final int MAX_VALUE = 99999;
	private static final int MIN_VALUE = -99999;

	public static Line makeMoveAI(Grid gridNode, int plys) {
		// Copy game grid
		Grid gridRoot = new Grid(gridNode);

		// Minimax recurse on all possible states from current grid state
		int value = miniMaxSearchAB(gridRoot, plys, MIN_VALUE, MAX_VALUE);
		Line moveLine = new Line (0,0,0);

		// Find best value from all possible game states
		for(Grid node : gridRoot.availableStates) {
			// Best value is found use this and stop search
			if(node.gridValue == value) {
				moveLine.x = node.getInsertLine().x;
				moveLine.y = node.getInsertLine().y;
				moveLine.value = 1;

				return moveLine;
			}
		}
		return moveLine;
	}

	// Helper method to check if leaf node or ply level reached or if the game is over from current grid state
	private static boolean checkTerminal(Grid gridRoot, int plys){
		return gridRoot.currDepth >= plys || gridRoot.gameOver();
	}

	// Main MiniMax recursive implementation
	private static int miniMaxSearchAB(Grid gridRoot, int plys, int alpha, int beta) {

		// Get all possible children
		gridRoot.getAvailableMoves();

		// Find out if we are at a leaf node or at the highest depth
		if(checkTerminal(gridRoot,plys)) {
			// Use evaluation function
			return gridRoot.evaluation();
		}

		// Value to be returned
		int resultValue;

		// Either the max value or min value depending on what player is being compared
		int minMaxVal;

		// Logic for MAX player
		if(!gridRoot.isHuman()) {
			resultValue = MIN_VALUE;
			for(Grid gridNode : gridRoot.availableStates) {
				// Recurse on possible states
				minMaxVal = miniMaxSearchAB(gridNode, plys-1, alpha, beta);
				resultValue = Math.max(minMaxVal,resultValue);
				resultValue = Math.max(alpha,resultValue);
				// Pruning
				if(beta <= alpha) {break;}
			}

		// Logic for MIN player
		}else{
			resultValue = MAX_VALUE;
			for(Grid gridNode : gridRoot.availableStates) {
				// Recurse on possible states
				minMaxVal = miniMaxSearchAB(gridNode, plys-1, alpha, beta);
				resultValue = Math.min(minMaxVal,resultValue);
				resultValue = Math.min(resultValue,beta);
				// Pruning
				if(beta <= alpha) {break;}
			}
		}
		gridRoot.gridValue = resultValue;
		return resultValue;
	}

}
