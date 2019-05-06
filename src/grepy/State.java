package grepy;

public class State {
	//name of the state. not necessary but it made things easier i think
	public String name;
	
	public State(String theName) {
		name = theName;
	}
	
	//pring out the name not the address
	public String toString() {
		return name;
	}
}
