import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Main {
	
	int placedBishops = 0;
	int squaresTaken = 0;
	
	private List<List<String>> solutions = new ArrayList<List<String>>();
	 
	
	public static void main(String[] args) {
		
		//BENCHMARK START:
		long start = System.nanoTime();
		
		Main main = new Main();
		List<List<String>> results = main.SolveBishop();
		//printing
		for(int i = 0; i < results.size(); i++) {
			for( int j = 0; j < results.get(i).size(); j++) {
				System.out.println(results.get(i).get(j));
			}
			System.out.println();
		}
		
		//BENCHMARK END
		long elapsedTime = System.nanoTime() - start;
		System.out.printf("Nanoseconds: %d\n", elapsedTime );

		}
		
	public List<List<String>> SolveBishop(){
		//creating a board
		char[][] emptyBoard = new char[8][8];
		//fill the board
		for( int i = 0; i < 8; i++ ) {
			for( int j = 0 ; j < 8; j++) {
				emptyBoard[i][j] = '.';
			}
		}
		
		backtrack(0,0, new HashSet<>(), new HashSet<>(), emptyBoard);
		
		return solutions;
	}
	
	//function converts char[][] of state to list<strings>
	private List<String> createBoard(char [][] state){
		List<String> board = new ArrayList<String>();
		for( int row = 0; row < 8; row++) { //Iterates through all rows
			String currentRow = new String(state[row]); //add to list of strings
			board.add(currentRow);
		}
		return board;
	}
	
	//changes board state according to controlled squares
	private void boardRefresh(int row, int col, char [][]state) {
		//solution one: state taken squares with x
		int diagID = row - col;
		int antiDiagID = row + col;
		for( int curr_row = 0; curr_row < 8; curr_row++)
		{
			for( int curr_col = 0; curr_col < 8; curr_col++) 
			{
				//if univisited & equals diagID or antiDiagID
				if(state[curr_row][curr_col] == '.' && ( (curr_row - curr_col) == diagID ||  (curr_row + curr_col) == antiDiagID) )
				{
					state[curr_row][curr_col] = 'x';
				}
			}
		}	
	}
	
	//undo marked squares
	private void boardUndo(int row, int col, char [][]state) {
		//solution one: state taken squares with x
		int diagID = row - col;
		int antiDiagID = row + col;
		for( int curr_row = 0; curr_row < 8; curr_row++)
		{
			for( int curr_col = 0; curr_col < 8; curr_col++) 
			{
				//if unvisited & equals diagID or antiDiagID
				if(state[curr_row][curr_col] == 'x' && ( (curr_row - curr_col) == diagID ||  (curr_row + curr_col) == antiDiagID) )
				{
					state[curr_row][curr_col] = '.';
					//don't count squares taken for every board, but for created boards
				}
			}
		}	
	}
	
	//counts how many squares are controlled
	private int squaresTaken(char [][]state) {
		int squaresTaken = 0;
		for( int curr_row = 0; curr_row < 8; curr_row++) //Iterating the whole board
		{
			for( int curr_col = 0; curr_col < 8; curr_col++) 
			{
				if(state[curr_row][curr_col] == 'x' )
				{
					squaresTaken++; //count if controlled
				}
			}
		}	
		return squaresTaken;
	}
	
	private void backtrack(int row, int col, Set<Integer> diagonals, Set<Integer> antiDiagonals, char[][] state ) {
		if( placedBishops == 8) {
			
			//FORMULATING THE FINAL BOARD
			//find every Bishop coordinates, when bishop is found, refresh board
			for(int curr_row = 0; curr_row < 8; curr_row++) 
			{
				for(int curr_col = 0; curr_col < 8; curr_col++)
				{
					if(state[curr_row][curr_col] == 'B')
					{
						boardRefresh(curr_row, curr_col, state);
					}
				}
			}
			
			//calculate amount of squares taken:
			squaresTaken = squaresTaken(state);
			//check solution
			if( placedBishops == 8 && squaresTaken == 56) {
				//break completely
				solutions.add( createBoard(state));
				System.out.println("Result found.\n");
				return;
			}
			
			//undoing all of the boards if bishop is incorrect
			for(int curr_row = 0; curr_row < 8; curr_row++) 
			{
				for(int curr_col = 0; curr_col < 8; curr_col++)
				{
					if(state[curr_row][curr_col] == 'B')
					{
						boardUndo(curr_row, curr_col, state);
					}
				}
			}
			return;
		}
		
		
		for(int current_row = row; current_row < 8; current_row++) {
			
			
			if( squaresTaken == 56) {
				return;
			}
			
			
			//when we ask for next column it still start with zero
			for( int current_col = col; current_col < 8; current_col++) { //iterating through whole colums
				
				
				if( squaresTaken == 56) {
					//break completely
					return;
				}
				
				int currDiagonal = current_row - current_col;
				int currAntiDiagonal = current_row + current_col;
				
				//Optimization
				if( diagonals.contains(currDiagonal) || antiDiagonals.contains(currAntiDiagonal)) {
					continue;
				}
				
				//if diagonal are not taken, bishop can be placed
				diagonals.add(currDiagonal);
				antiDiagonals.add(currAntiDiagonal);
				state[current_row][current_col] = 'B';
				placedBishops++;
				
				backtrack( current_row, current_col + 1, diagonals, antiDiagonals, state);
				
				diagonals.remove(currDiagonal);
				antiDiagonals.remove(currAntiDiagonal);
				state[current_row][current_col] = '.';
				placedBishops--;	
			}
		}
	}
}

