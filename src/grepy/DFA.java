package grepy;

import java.util.ArrayList;

public class DFA {
	public ArrayList<State> states = new ArrayList<State>();
	public ArrayList<String> alphabet = new ArrayList<String>();
	public ArrayList<Transition> transitionFunction = new ArrayList<Transition>();
	public State initialState;
	public ArrayList<State> acceptingStates = new ArrayList<State>();
	
	public DFA (ArrayList<State> theStates, ArrayList<String> theAlphabet, 
			ArrayList<Transition> theTransitionFunction, State theInitialState, ArrayList<State> theAcceptingStates) {
		states = theStates;
		alphabet = theAlphabet;
		transitionFunction = theTransitionFunction;
		initialState = theInitialState;
		acceptingStates = theAcceptingStates;
	}
	
	public String evaluate(DFA theDFA, String theString, ArrayList<Transition> theTransition) {
		State currentState = theDFA.initialState;
		for (int i = 0; i < theString.length(); i++) {
			String currentSymbol = theString.substring(i, i+1);
			for (int j = 0; j < theTransition.size(); j++) {
				if (theTransition.get(j).startState.equals(currentState)) {
					if (theTransition.get(j).symbol.equals(currentSymbol)) {
						currentState = theTransition.get(j).endState;
					}
				}
			}
		}
		if (theDFA.acceptingStates.contains(currentState)) {
			return theString;
		}
		return "False";
	}
	
	public String toString() {
		System.out.print("States: ");
		for (int i = 0; i < this.states.size(); i++) {
			if (i < this.states.size() - 1) {
				System.out.print(this.states.get(i).toString() + ", ");
			} else {
				System.out.println(this.states.get(i).toString());
			}
		}
		System.out.print("Alphabet: ");
		for (int i = 0; i < this.alphabet.size(); i++ ) {
			if (i < this.alphabet.size() - 1) {
				System.out.print(this.alphabet.get(i) + ", ");
			} else {
				System.out.println(this.alphabet.get(i));
			}
		}
		System.out.print("Transition Function: ");
		for (int i = 0; i < this.transitionFunction.size(); i++) {
			if (i < this.transitionFunction.size() - 1) {
				System.out.print(this.transitionFunction.get(i).toString() + ", ");
			} else {
				System.out.println(this.transitionFunction.get(i).toString());
			}
		}
		System.out.print("Initial State: ");
		System.out.println(this.initialState.toString());
		System.out.print("Accepting States: ");
		for (int i = 0; i < this.acceptingStates.size(); i++) {
			if (i < this.acceptingStates.size() - 1) {
				System.out.print(this.acceptingStates.get(i).toString() + ", ");
			} else {
				System.out.println(this.acceptingStates.get(i).toString());
			}
		}
		
		return "";
	}
}
