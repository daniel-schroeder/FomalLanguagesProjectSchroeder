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
		
		ArrayList<State> potentialStates = new ArrayList<State>();
		String nextName = "";
		boolean alreadyThere = false;
		for (int i = 0; i < theStates.size(); i++) {
			if (theStates.get(i).name.length() > 2) {
				for (int j = 0; j < theAlphabet.size(); j++) {
					for (int n = 0; n < theStates.get(i).name.length(); n += 2) {
						for (int k = 0; k < nfa.transitionFunction.size(); k ++) {
							if (nfa.transitionFunction.get(k).startState.name.equals(theStates.get(i).name.substring(n, n+2))
									&& theAlphabet.get(j).equals(nfa.transitionFunction.get(k).symbol)) {
								potentialStates.add(nfa.transitionFunction.get(k).endState);
							}
						}
					}
					for (int l = 0; l < potentialStates.size(); l++) {
						nextName += potentialStates.get(i).name;
					}
					theStates.add(new State(nextName));
					nextName = "";
				}
			} else {
				for (int j = 0; j < theAlphabet.size(); j++) {
						for (int k = 0; k < nfa.transitionFunction.size(); k ++) {
							if (nfa.transitionFunction.get(k).startState.name.equals(theStates.get(i).name)
									&& theAlphabet.get(j).equals(nfa.transitionFunction.get(k).symbol)) {
								potentialStates.add(nfa.transitionFunction.get(k).endState);
							}
						}
					for (int l = 0; l < potentialStates.size(); l++) {
						nextName += potentialStates.get(i).name;
					}
					for (int l = 0; l < theStates.size(); l++) {
						if (theStates.get(i).name == nextName) {
							alreadyThere = true;
						}
					}
					if (!alreadyThere) {
						theStates.add(new State(nextName));
					}
					nextName = "";
				}
			}
		}
		
		for (int i = 0; i < theStates.size(); i++) {
			for (int j = 0; j < nfa.acceptingStates.size(); j ++) {
				if (theStates.get(i).name.indexOf(nfa.acceptingStates.get(j).name) != -1) {
					theAcceptingStates.add(theStates.get(i));
				}
			}
		}
		
		return new DFA(theStates, theAlphabet, theTransitionFunction, theInitialState, theAcceptingStates);
		//return new DFA(nfa.states, nfa.alphabet, nfa.transitionFunction, nfa.initialState, nfa.acceptingStates);
	}
}
