package grepy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class NFA {
	//The NFA 5-tuple
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
	
	//creates a DFA from the NFA and in necessary a dot language at the given file path
	public DFA createDFA(NFA nfa, Path dfaFile) throws IOException {
		ArrayList<State> theStates = new ArrayList<State>();
		ArrayList<Transition> theTransitionFunction = new ArrayList<Transition>();
		State theInitialState = nfa.initialState;
		theStates.add(theInitialState);
		ArrayList<State> theAcceptingStates = new ArrayList<State>();
		ArrayList<String> theAlphabet = nfa.alphabet;
		String content = "digraph dfa { \n";
		
		for (int i = 0; i < nfa.transitionFunction.size(); i++) {
			if (nfa.transitionFunction.get(i).startState.equals(theInitialState) &&
					nfa.transitionFunction.get(i).symbol == "") {
				theStates.remove(theInitialState);
				theInitialState = nfa.transitionFunction.get(i).endState;
				theStates.add(theInitialState);
				i = 0;
			}
		}
		
		//line 52-149
		//gets all the states that can be reached from each existing state and creates a new state with that name
		//then check to see if a state with that name exists. If yes then transition to that state. If no
		//Transition to a newState with that name. Does this until all states are created that are needed.
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
						nextName += potentialStates.get(l).name;
					}
					int index = -1;
					for (int l = 0; l < theStates.size(); l++) {
						if (theStates.get(l).name.equals(nextName)) {
							alreadyThere = true;
							index = l;
						}
					}
					int errorIndex = -1;
					boolean errorThere = false;
					if (nextName.equals("")) {
						State nextState = new State("errorState");
						for (int l = 0; l < theStates.size(); l++) {
							if (theStates.get(l).name.equals("errorState")) {
								errorThere = true;
							}
						}
						if (!errorThere) {
							theStates.add(0, nextState);
							errorIndex = 0;
						}
					}
					if (!nextName.equals("") && !alreadyThere) {
						State nextState = new State(nextName);
						theStates.add(nextState);
						theTransitionFunction.add(new Transition (theStates.get(i), theAlphabet.get(j), nextState));
					} else if (alreadyThere) {
						theTransitionFunction.add(new Transition (theStates.get(i), theAlphabet.get(j), theStates.get(index)));
					} else if (errorIndex != -1){
						theTransitionFunction.add(new Transition (theStates.get(i), theAlphabet.get(j), theStates.get(errorIndex)));
					}
					nextName = "";
					alreadyThere = false;
					potentialStates.removeAll(potentialStates);
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
						nextName += potentialStates.get(l).name;
					}
					int index = -1;
					for (int l = 0; l < theStates.size(); l++) {
						if (theStates.get(l).name.equals(nextName)) {
							alreadyThere = true;
							index = l;
						}
					}
					int errorIndex = -1;
					boolean errorThere = false;
					if (nextName.equals("")) {
						State nextState = new State("errorState");
						for (int l = 0; l < theStates.size(); l++) {
							if (theStates.get(l).name.equals("errorState")) {
								errorThere = true;
							}
						}
						if (!errorThere) {
							theStates.add(nextState);
							errorIndex = theStates.indexOf(nextState);
						}
					}
					if (!nextName.equals("") && !alreadyThere) {
						State nextState = new State(nextName);
						theStates.add(nextState);
						theTransitionFunction.add(new Transition (theStates.get(i), theAlphabet.get(j), nextState));
					} else if (alreadyThere) {
						theTransitionFunction.add(new Transition (theStates.get(i), theAlphabet.get(j), theStates.get(index)));
					} else if (errorIndex != -1){
						theTransitionFunction.add(new Transition (theStates.get(i), theAlphabet.get(j), theStates.get(errorIndex)));
					}
					nextName = "";
					alreadyThere = false;
					potentialStates.removeAll(potentialStates);
				}
			}
		}
		
		//Goes through each state and if any part of the name is one of the accepting states in the nfa then 
		//that state becomes accepting in the DFA
		for (int i = 0; i < theStates.size(); i++) {
			for (int j = 0; j < nfa.acceptingStates.size(); j ++) {
				if (theStates.get(i).name.indexOf(nfa.acceptingStates.get(j).name) != -1) {
					theAcceptingStates.add(theStates.get(i));
				}
			}
		}
		
		//finds the error state and assigns it an index for later. 
		//also adds a transition on every symbol from itsef to itself
		int errorIndex = -1;
		for (int j = 0; j < theStates.size(); j++) {
			if (theStates.get(j).name.equals("errorState")) {
				for (int i = 0; i < theAlphabet.size(); i++) {
					errorIndex = j;
					theTransitionFunction.add(new Transition (theStates.get(j), theAlphabet.get(i), theStates.get(j)));
				}
			}
		}
		
		//this boolean checks to see if a transition exists
		boolean exists;
		for (int j = 0; j < theStates.size(); j++) {
			for (int i = 0; i < theAlphabet.size(); i++) {
				//set to false before each iteration through the alphabet
				exists = false;
				for (int n = 0; n < theTransitionFunction.size(); n++) {
					if (theTransitionFunction.get(n).startState.name.equals(theStates.get(j).name)
							&& theTransitionFunction.get(n).symbol.equals(theAlphabet.get(i))) {
						//Set to true because it exists
						exists = true;
					}
				}
				//If it does not exist, create a transition from the current state to the error state(errorIndex)
				if (!exists) {
					theTransitionFunction.add(new Transition(theStates.get(j), theAlphabet.get(i), theStates.get(errorIndex)));
				}
			}
		}
		
		//generates the dot language using the transition state
		for (int i = 0; i < theTransitionFunction.size(); i ++) {
			content += "	" + theTransitionFunction.get(i).startState.name + " -> " + theTransitionFunction.get(i).endState.name 
					+ " [label=" + theTransitionFunction.get(i).symbol + "]\n";
		}
		for (int i = 0; i < theAcceptingStates.size(); i++) {
			content += "	" + theAcceptingStates.get(i).name + " [peripheries=2]\n";
		}
		content += "}";
		
		//writes to the file specified. /temp is a temp path that indicates no specified path
		if (!(dfaFile.compareTo(Paths.get("/temp")) == 0)) {
			Files.deleteIfExists(dfaFile);
			Files.write(dfaFile, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
		}
		
		//return the DFA
		return new DFA(theStates, theAlphabet, theTransitionFunction, theInitialState, theAcceptingStates);
	}
}
