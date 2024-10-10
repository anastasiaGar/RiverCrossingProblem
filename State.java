
import java.util.List;
import java.util.Objects;

public class State {
    private final List<String> leftBank;
    private final List<String> rightBank;
    private final String lampPosition;
    private final int timeElapsed;
    private State parentState;
    private String parentAction;


    public State(List<String> leftBank, List<String> rightBank, String lampPosition, int timeElapsed) {
        this.leftBank = leftBank;
        this.rightBank = rightBank;
        this.lampPosition = lampPosition;
        this.timeElapsed = timeElapsed;

        this.parentState = null;
        this.parentAction = null;
    }

    public List<String> getLeftBank() {
        return leftBank;
    }

    public List<String> getRightBank() {
        return rightBank;
    }

    public String getLampPosition() {
        return lampPosition;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public State getParentState() { return parentState; }

    public String getParentAction() {
        return parentAction;
    }

    // Method to set the parent state and action.
    public void setParent(State parent, String action) {
        this.parentState = parent;
        this.parentAction = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())  return false; //checks if o not an instance of the state class or null
        State state = (State) o;
        return timeElapsed == state.timeElapsed &&
                Objects.equals(leftBank, state.leftBank) &&
                Objects.equals(rightBank, state.rightBank) &&
                Objects.equals(lampPosition, state.lampPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftBank, rightBank, lampPosition);
    }


}


