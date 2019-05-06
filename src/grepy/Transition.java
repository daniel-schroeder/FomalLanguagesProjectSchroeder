package grepy;

//This class represents transitions between two states
public class Transition {
	public State startState;
	public State endState;
	public String symbol;
	
	//startState is the initial state, symbol is the symbol that 
	//moves the machine to the next state and endState is where the machine ends up after the transition
	public Transition(State start, String theSymbol, State end) {
		startState = start;
		endState = end;
		symbol = theSymbol;
	}
	
	//Print out the transitions in a pretty fashion
	public String toString() {
		if (endState != null) {
			return "[" + startState.toString() + ", " + symbol + ", " + endState.toString() + "]";
		} else {
			return "[" + startState.toString() + ", " + symbol + ", null]";
		}
	}
}
