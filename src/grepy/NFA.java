package grepy;

import java.util.ArrayList;

public class NFA {
	public ArrayList<State> states = new ArrayList<State>();
	public ArrayList<String> alphabet = new ArrayList<String>();
	public ArrayList<Transition> transitionFunction = new ArrayList<Transition>();
	public State initialState;
	public ArrayList<State> acceptingStates = new ArrayList<State>();
	
	public NFA (ArrayList<State> theStates, ArrayList<String> theAlphabet, 
			ArrayList<Transition> theTransitionFunction, State theInitialState, ArrayList<State> theAcceptingStates) {
		states = theStates;
		alphabet = theAlphabet;
		transitionFunction = theTransitionFunction;
		initialState = theInitialState;
		acceptingStates = theAcceptingStates;
	}
	
	public DFA createDFA(NFA nfa) {
		ArrayList<State> theStates = new ArrayList<State>();
		ArrayList<Transition> theTransitionFunction = new ArrayList<Transition>();
		State theInitialState = nfa.initialState;
		theStates.add(theInitialState);
		ArrayList<State> theAcceptingStates = new ArrayList<State>();
		ArrayList<String> theAlphabet = nfa.alphabet;
		
		for (int i = 0; i < nfa.transitionFunction.size(); i++) {
			if (nfa.transitionFunction.get(i).startState.equals(theInitialState) &&
					nfa.transitionFunction.get(i).symbol == "") {
				theStates.remove(theInitialState);
				theInitialState = nfa.transitionFunction.get(i).endState;
				theStates.add(theInitialState);
				i = 0;
			}
		}
		
		theStates.addAll(getAllStates());
		
		return new DFA(theStates, theAlphabet, theTransitionFunction, theInitialState, theAcceptingStates);
	}
	
	public ArrayList<State> getAllStates() {
		ArrayList<State> allStates = new ArrayList<State>();
		
		return allStates;
	}
}
