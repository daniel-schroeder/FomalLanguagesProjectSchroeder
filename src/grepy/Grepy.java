package grepy;

import grepy.Transition;
import grepy.NFA;
import grepy.State;
import grepy.DFA;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Grepy {
	public static void main (String[] args) throws IOException {
		/*State s1 = new State("s1");
		State s2 = new State("s2");
		State s3 = new State("s3");
		ArrayList<State> states = new ArrayList<State>();
		states.add(s1);
		states.add(s2);
		states.add(s3);
		Transition t1 = new Transition(s1, "a", s2);
		Transition t2 = new Transition(s1, "b", s1);
		Transition t3 = new Transition(s2, "a", s2);
		Transition t4 = new Transition(s2, "b", s3);
		Transition t5 = new Transition(s3, "a", s3);
		Transition t6 = new Transition(s3, "b", s3);
		ArrayList<Transition> transitionFunction = new ArrayList<Transition>();
		transitionFunction.add(t1);
		transitionFunction.add(t2);
		transitionFunction.add(t3);
		transitionFunction.add(t4);
		transitionFunction.add(t5);
		transitionFunction.add(t6);
		ArrayList<State> acceptingStates = new ArrayList<State>();
		acceptingStates.add(s3);*/
		String regex = args[0];
		ArrayList<String> lines = new ArrayList<String>();
		lines = (ArrayList<String>) Files.readAllLines(Paths.get(args[1]), StandardCharsets.UTF_8);
		ArrayList<String> alphabet = new ArrayList<String>();
		for (int j = 0; j < lines.size(); j++) {
			for (int i = 0; i < lines.get(j).length(); i++) {
				if (alphabet.contains(lines.get(j).substring(i,i+1)) == false) {
					alphabet.add(lines.get(j).substring(i,i+1));
				}
			}
		}
		NFA a = createNFA(regex, alphabet);
		DFA b = a.createDFA(a);
		b.toString();
		/*for (int i = 0; i < lines.size(); i++) {
			System.out.println(b.evaluate(b, lines.get(i), b.transitionFunction));
		}*/
	}
	
	public static NFA createNFA(String regex, ArrayList<String> theAlphabet) {
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
		
		for (int i = 0; i < regex.length(); i++) {
			if (regex.substring(i, i+1).equals("(")) {
				branchState = currentState;
			} else if (regex.substring(i, i+1).equals("+")) {
				nextState = currentState;
				currentState = previousState;
			} else if (regex.substring(i, i+1).equals("*")) {
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
		
		acceptingStates.add(currentState);
		
		return minimize(new NFA(states, theAlphabet, transitionFunction, initialState, acceptingStates));
	}
	
	public static NFA minimize(NFA theNFA) {
		ArrayList<State> theStates = new ArrayList<State>();
		ArrayList<String> theAlphabet = theNFA.alphabet;
		ArrayList<Transition> theTransitionFunction = theNFA.transitionFunction;
		State theInitialState = theNFA.initialState;
		ArrayList<State> theAcceptingStates = theNFA.acceptingStates;
		NFA minimizedNFA = new NFA(theStates, theAlphabet, theTransitionFunction, theInitialState, theAcceptingStates);
		
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
