package grepy;

public class Transition {
	public State startState;
	public State endState;
	public String symbol;
	
	public Transition(State start, String theSymbol, State end) {
		startState = start;
		endState = end;
		symbol = theSymbol;
	}
	
	public String toString() {
		return "[" + startState.toString() + ", " + symbol + ", " + endState.toString() + "]";
	}
}
