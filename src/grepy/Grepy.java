package grepy;

import grepy.Transition;
import grepy.NFA;
import grepy.State;
import grepy.DFA;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Grepy {
	public static void main (String[] args) throws IOException {
		//paths for the dot outputs
		Path nfaFilePath = Paths.get("/temp");
		Path dfaFilePath = Paths.get("/temp");
		
		//lines to be read from the input file
		ArrayList<String> lines = new ArrayList<String>();
		
		//the regex to be transformed into a dfa
		String regex;
		
		//this string of if-else clauses checks to see if there has been an output file given
		//and if so is it nfa or dfa. then assigns the paths to corresponding variables,
		//the regex to its variable, and the lines to its variable
		if (!args[0].equals("-n") && !args[0].equals("-d")) {
			regex = args[0];
			lines = (ArrayList<String>) Files.readAllLines(Paths.get(args[1]), StandardCharsets.UTF_8);
		} else if (args[0].equals("-n") && !args[2].equals("-d")) {
			regex = args[2];
			lines = (ArrayList<String>) Files.readAllLines(Paths.get(args[3]), StandardCharsets.UTF_8);
			nfaFilePath = Paths.get(args[1]);
		} else if (args[0].equals("-d") && !args[2].equals("-n")) {
			regex = args[2];
			dfaFilePath = Paths.get(args[1]);
			lines = (ArrayList<String>) Files.readAllLines(Paths.get(args[3]), StandardCharsets.UTF_8);
		} else {
			regex = args[4];
			nfaFilePath = Paths.get(args[1]);
			dfaFilePath = Paths.get(args[3]);
			lines = (ArrayList<String>) Files.readAllLines(Paths.get(args[5]), StandardCharsets.UTF_8);
		}
		
		//determines the alphabet for the entire input file at once
		ArrayList<String> alphabet = new ArrayList<String>();
		for (int j = 0; j < lines.size(); j++) {
			for (int i = 0; i < lines.get(j).length(); i++) {
				//checks to make sure there are no duplicates before adding a new symbol
				if (alphabet.contains(lines.get(j).substring(i,i+1)) == false) {
					alphabet.add(lines.get(j).substring(i,i+1));
				}
			}
		}
		
		//creates the NFA using the regex, and the alphabet. Then if nfaFilePath has been properly initialized,
		//creates a dot language file at the specified path 
		NFA a = createNFA(regex, alphabet, nfaFilePath);
		
		//Creates a DFA from the NFA then if dfaFilePath has been properly initialized,
		//creates a dot language file at the specified path 
		DFA b = a.createDFA(a, dfaFilePath);
		
		//evaluate using DFA.evaluate(DFA, String) for each line in the ArrayList lines. 
		for (int i = 0; i < lines.size(); i++) {
			String temp = b.evaluate(b, lines.get(i));
			//Only print out lines that are accepted
			if (!temp.equals("Not Accepted")) {
				System.out.println(temp);
			}
		}
	}
	
	public static NFA createNFA(String regex, ArrayList<String> theAlphabet, Path nfaFile) throws IOException {
		ArrayList<State> states = new ArrayList<State>();
		ArrayList<Transition> transitionFunction = new ArrayList<Transition>();
		State initialState = new State("s0");
		State currentState = initialState;
		State previousState = initialState;
		State branchState = currentState;
		State nextState = new State("s1");
		states.add(initialState);
		ArrayList<State> acceptingStates = new ArrayList<State>();
		int sCounter = 2;
		String content = "digraph nfa { \n";
		
		//goes through each symbol of the regex and decides what to do
		for (int i = 0; i < regex.length(); i++) {
			if (regex.substring(i, i+1).equals("(")) {
				//set up a branch to go back to at the end of the parenthesis
				branchState = currentState;
			} else if (regex.substring(i, i+1).equals("+")) {
				//go back because its an OR
				nextState = currentState;
				currentState = previousState;
			} else if (regex.substring(i, i+1).equals("*")) {
				//just one symbol repeated
				//set up a transition on itself
				if (theAlphabet.contains(regex.substring(i-1, i))) {
					currentState = previousState;
					transitionFunction.remove(transitionFunction.size() - 1);
					states.remove(states.size() - 1);
					Transition t1 = new Transition(currentState, regex.substring(i-1, i), currentState);
					transitionFunction.add(t1);
					if (!states.contains(currentState)) {
						states.add(currentState);
					}
				} else if (regex.substring(i-1, i).equals(")")) {
					//multiple things repeated.
					//set up a different kind of loop
					State s1 = new State("s" + sCounter);
					sCounter++;
					Transition t1 = new Transition(s1, "" , branchState);
					Transition t2 = new Transition(nextState, "" , branchState);
					Transition t3 = new Transition(s1, "" , nextState);
					Transition t4 = new Transition(currentState, "" , nextState);
					
					states.add(s1);
					states.add(nextState);
					if (initialState.equals(branchState)) {
						initialState = s1;
					}
					transitionFunction.add(t1);
					transitionFunction.add(t2);
					transitionFunction.add(t3);
					transitionFunction.add(t4);
					
					previousState = currentState;
					currentState = nextState;
					nextState = new State("s" + sCounter);
					sCounter++;
				}
			} else if (theAlphabet.contains(regex.substring(i, i+1))) {
				//add a transition to the transitionfunction
				Transition t1 = new Transition(currentState, regex.substring(i, i+1), nextState);
				previousState = currentState;
				currentState = nextState;
				nextState = new State("s" + sCounter);
				sCounter++;
				transitionFunction.add(t1);
				if (!states.contains(currentState)) {
					states.add(currentState);
				}
			}
		}
		
		//the final state is accepting
		acceptingStates.add(currentState);
		
		//this should minimize the NFA
		NFA nfa = minimize(new NFA(states, theAlphabet, transitionFunction, initialState, acceptingStates));
		
		//generates the dot language using the transition state
		for (int i = 0; i < transitionFunction.size(); i ++) {
			content += "	" + transitionFunction.get(i).startState.name + " -> " + transitionFunction.get(i).endState.name 
					+ " [label=" + transitionFunction.get(i).symbol + "]\n";
		}
		for (int i = 0; i < acceptingStates.size(); i++) {
			content += "	" + acceptingStates.get(i).name + " [peripheries=2]\n";
		}
		content += "}";
		//writes to the file specified. /temp is a temp path that indicates no specified path
		if (!(nfaFile.compareTo(Paths.get("/temp")) == 0)) {
			Files.deleteIfExists(nfaFile);
			Files.write(nfaFile, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
		}
		
		return nfa;
	}
	
	public static NFA minimize(NFA theNFA) {
		ArrayList<State> theStates = new ArrayList<State>();
		ArrayList<String> theAlphabet = theNFA.alphabet;
		ArrayList<Transition> theTransitionFunction = theNFA.transitionFunction;
		State theInitialState = theNFA.initialState;
		ArrayList<State> theAcceptingStates = theNFA.acceptingStates;
		NFA minimizedNFA = new NFA(theStates, theAlphabet, theTransitionFunction, theInitialState, theAcceptingStates);
		
		//basically checks for a complicated loop involving "" transitions and changes them to a more basic loop
		for (int i = 0; i < theNFA.transitionFunction.size(); i++) {
			State nextState = new State("temp");
			String nextSymbol = "-1";
			if (theNFA.transitionFunction.get(i).symbol == ""
					&& theNFA.transitionFunction.get(i + 1).symbol == ""
					&& theNFA.transitionFunction.get(i + 2).symbol == ""
					&& theNFA.transitionFunction.get(i + 3).symbol == "") {
				State currentState = theNFA.transitionFunction.get(i).endState;
				if(theNFA.transitionFunction.get(i).startState.equals(theNFA.initialState)) {
					minimizedNFA.initialState = currentState;
				}
				if (theNFA.transitionFunction.size() > i + 4) {
					nextState = theNFA.transitionFunction.get(i + 4).endState;
					nextSymbol = theNFA.transitionFunction.get(i + 4).symbol;
					minimizedNFA.transitionFunction.remove(i+4);
				}
				minimizedNFA.transitionFunction.remove(i);
				minimizedNFA.transitionFunction.remove(i);
				minimizedNFA.transitionFunction.remove(i);
				minimizedNFA.transitionFunction.remove(i);
				for (int j = 0; j < minimizedNFA.transitionFunction.size(); j++) {
					if (minimizedNFA.transitionFunction.get(j).startState.equals(currentState)
							&& !minimizedNFA.transitionFunction.get(j).endState.equals(currentState)) {
						minimizedNFA.transitionFunction.add(new Transition(currentState, minimizedNFA.transitionFunction.get(j).symbol, currentState));
						minimizedNFA.transitionFunction.remove(j);
						j--;
					}
				}
				if (!nextSymbol.equals("-1") && !nextState.name.equals("temp")) {
					minimizedNFA.transitionFunction.add(new Transition (currentState, nextSymbol, nextState));
				}
			}
		}
		
		minimizedNFA.states.removeAll(minimizedNFA.states);
		minimizedNFA.states.add(minimizedNFA.initialState);
		for (int i = 0; i < minimizedNFA.transitionFunction.size(); i++) {
			if (!minimizedNFA.states.contains(minimizedNFA.transitionFunction.get(i).startState)) {
				minimizedNFA.states.add(minimizedNFA.transitionFunction.get(i).startState);
			}
			if (!minimizedNFA.states.contains(minimizedNFA.transitionFunction.get(i).endState)) {
				minimizedNFA.states.add(minimizedNFA.transitionFunction.get(i).endState);
			}
		}
		
		return minimizedNFA;
	}
}
