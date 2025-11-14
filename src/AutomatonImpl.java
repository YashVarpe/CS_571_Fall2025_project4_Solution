import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class AutomatonImpl implements Automaton {

    class StateLabelPair {
        int state;
        char label;
        public StateLabelPair(int state_, char label_) { state = state_; label = label_; }

        @Override
        public int hashCode() {
            return Objects.hash((Integer) state, (Character) label);
        }

        @Override
        public boolean equals(Object o) {
            StateLabelPair o1 = (StateLabelPair) o;
            return (state == o1.state) && (label == o1.label);
        }
    }

    HashSet<Integer> start_states;
    HashSet<Integer> accept_states;
    HashSet<Integer> current_states;
    HashMap<StateLabelPair, HashSet<Integer>> transitions;

    public AutomatonImpl() {
        start_states = new HashSet<Integer>();
        accept_states = new HashSet<Integer>();
        current_states = new HashSet<Integer>(); // Initialized in reset()
        transitions = new HashMap<StateLabelPair, HashSet<Integer>>();
    }

    @Override
    public void addState(int s, boolean is_start, boolean is_accept) {
        if (is_start) {
            start_states.add(s);
        }
        if (is_accept) {
            accept_states.add(s);
        }
    }

    @Override
    public void addTransition(int s_initial, char label, int s_final) {
        StateLabelPair key = new StateLabelPair(s_initial, label);
        // Find the set of transitions for this (state, label) pair, or create it
        transitions.putIfAbsent(key, new HashSet<Integer>());
        // Add the destination state
        transitions.get(key).add(s_final);
    }

    @Override
    public void reset() {
        // Reset current states to be a copy of the start states
        current_states = new HashSet<Integer>(start_states);
    }

    @Override
    public void apply(char input) {
        HashSet<Integer> next_states = new HashSet<Integer>();
        
        // Find all possible next states from all current states
        for (int state : current_states) {
            StateLabelPair key = new StateLabelPair(state, input);
            if (transitions.containsKey(key)) {
                // Add all destination states for this transition
                next_states.addAll(transitions.get(key));
            }
        }
        
        // Update the current states
        current_states = next_states;
    }

    @Override
    public boolean accepts() {
        // Accept if any of the current states is an accept state
        for (int state : current_states) {
            if (accept_states.contains(state)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasTransitions(char label) {
        // Check if any current state has a transition on the given label
        for (int state : current_states) {
            StateLabelPair key = new StateLabelPair(state, label);
            if (transitions.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

}