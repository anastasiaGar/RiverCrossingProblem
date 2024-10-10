import java.util.*;



public class RiverCrossingAStar {

    // Create a map to store family member names and their times.
    static Map<String, Integer> familyMemberTimes = new HashMap<>();
    
    public static Solution findOptimalSolution(State initialState, int maxTotalTimeAllowed) {
        PriorityQueue<State> openList = new PriorityQueue<>(Comparator.comparingInt(a -> a.getTimeElapsed() + estimateCost(a)));
        Set<State> closedList = new HashSet<>();

        openList.add(initialState);

        while (!openList.isEmpty()) {
            State currentState = openList.poll();
            closedList.add(currentState);

            if (isGoalState(currentState, initialState.getRightBank().size())) {
                return constructSolutionPath(currentState);
            }

            List<State> nextStates = generateNextStates(currentState);
            for (State nextState : nextStates) {
                // Check if the time constraint is met
                if (!closedList.contains(nextState)  && nextState.getTimeElapsed() <= maxTotalTimeAllowed) {
                    openList.add(nextState);
                }
            }
        }
        return null; // No solution found within the time limit.
    }

    private static boolean isGoalState(State state, int initialRightBankSize) {
        // Check if all family members are on the left bank, and the lamp is on the left bank.
        return state.getLeftBank().size() == initialRightBankSize && state.getLampPosition().equals("left");
    }

    private static List<State> generateNextStates(State currentState) {
        List<State> nextStates = new ArrayList<>();

        // Store the current positions of family members and the lamp.
        List<String> leftBank = new ArrayList<>(currentState.getLeftBank());
        List<String> rightBank = new ArrayList<>(currentState.getRightBank());
        String lampPosition = currentState.getLampPosition();


        // Iterate through each family member to check if they can cross.
        for (String member1 : familyMemberTimes.keySet()) {
            // Create a copy of the current state for modification.
            List<String> newLeftBank = new ArrayList<>(leftBank);
            List<String> newRightBank = new ArrayList<>(rightBank);
            String newLampPosition ;
            int newTimeElapsed = currentState.getTimeElapsed();

            // Check if the selected family members are on the same side as the lamp.
            if (leftBank.contains(member1)  && lampPosition.equals("left")) {
                newLeftBank.remove(member1);
                newRightBank.add(member1);
                newLampPosition = "right";
                newTimeElapsed += familyMemberTimes.get(member1);

                // Create a new state instance for this combination.
                State nextState = new State(newLeftBank, newRightBank, newLampPosition, newTimeElapsed);
                nextState.setParent(currentState, "Move " + member1 + " to the " + newLampPosition + " bank");
                nextStates.add(nextState);
            }else
                for (String member2 : familyMemberTimes.keySet()) {
                    List<String> innernewLeftBank = new ArrayList<>(leftBank);
                    List<String> innernewRightBank = new ArrayList<>(rightBank);
                    int innernewTimeElapsed = currentState.getTimeElapsed();

                    if (!member1.equals(member2)) {
                        if (rightBank.contains(member1) && rightBank.contains(member2) && lampPosition.equals("right")) {
                            innernewRightBank.remove(member1);
                            innernewRightBank.remove(member2);
                            innernewLeftBank.add(member1);
                            innernewLeftBank.add(member2);
                            newLampPosition = "left";
                            innernewTimeElapsed += Math.max(familyMemberTimes.get(member1), familyMemberTimes.get(member2));

                            // Create a new state instance for this combination.
                            State nextState = new State(innernewLeftBank, innernewRightBank, newLampPosition, innernewTimeElapsed);
                            nextState.setParent(currentState, "Move " + member1 + " and " + member2 + " to the " + newLampPosition + " bank");
                            nextStates.add(nextState);
                        }
                    }
                }
        }

        return nextStates;
    }


    private static int estimateCost(State state) {
        List<String> leftBank = state.getLeftBank();
        List<String> rightBank = state.getRightBank();
        String lampPosition = state.getLampPosition();

        int maxSingleMoveTime = 0;

        if (lampPosition.equals("right")) {
            for (String member : rightBank) {
                // Find the time needed for the slowest family member on the right bank.
                int memberTime = familyMemberTimes.get(member); // Get the time for the family member.
                maxSingleMoveTime = Math.max(maxSingleMoveTime, memberTime);
            }
        }
        if (lampPosition.equals("left")) {
            for (String member : leftBank) {
                // Find the time needed for the fastest family member on the left bank.
                int memberTime = familyMemberTimes.get(member); // Get the time for the family member.
                maxSingleMoveTime = Math.min(maxSingleMoveTime, memberTime);
            }
        }
        return maxSingleMoveTime;
    }


    private static Solution constructSolutionPath(State goalState) {
        List<String> actions = new ArrayList<>();
        State currentState = goalState;
        int totalTimeElapsed = currentState.getTimeElapsed();

        while (currentState != null) {
            if (currentState.getParentAction() != null) {
                actions.add(0, currentState.getParentAction());
            }
            currentState = currentState.getParentState();
        }
        return new Solution(actions, totalTimeElapsed);
    }



    public static void main(String[] args) {


        List<String> initialLeftBank = new ArrayList<>(); // Initially empty on the left bank.
        List<String> initialRightBank = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        int numFamilyMembers;

        // Prompt the user to enter the number of family members until a valid integer is entered.
        while (true) {
            System.out.print("Enter the number of family members: ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                try {
                    numFamilyMembers = Integer.parseInt(input);
                    break; // Valid input, exit the loop
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                }
            } else {
                System.out.println("Invalid input. Number of family members cannot be empty.");
            }
        }


        for (int i = 1; i <= numFamilyMembers; i++) {
            // Repeat the input until valid data is entered.
            int time;
            String memberName;

            while (true) {
                System.out.print("Enter the time for family member " + i + ": ");
                String input = scanner.nextLine();
                if (!input.isEmpty()) {
                    try {
                        time = Integer.parseInt(input);
                        break; // Valid input, exit the loop
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid integer.");
                    }
                } else {
                    System.out.println("Invalid input. Time cannot be empty.");
                }
            }
            while (true) {
                System.out.print("Enter the name for family member " + i + ": ");
                memberName = scanner.nextLine();

                if (!memberName.isEmpty() && !familyMemberTimes.containsKey(memberName)) {
                    // Valid input, exit the loop
                    familyMemberTimes.put(memberName, time);
                    initialRightBank.add(memberName);
                    break;
                } else if (memberName.isEmpty()) {
                    System.out.println("Invalid input. The name cannot be empty.");
                } else {
                    System.out.println("Invalid input. A family member with this name already exists.");
                }
            }
        }

        // Create the initial state with your specific problem parameters.
        State initialState = new State(initialLeftBank, initialRightBank, "right", 0);


        // Set the maximum total time allowed as the sum of family member times.
        int maxTotalTimeAllowed = familyMemberTimes.values().stream().mapToInt(Integer::intValue).sum();


        Solution solution = findOptimalSolution(initialState, maxTotalTimeAllowed);

        if (solution != null) {
            List<String> actions = solution.actions();
            int totalTimeElapsed = solution.totalTimeElapsed();
            System.out.println("Optimal solution: " + actions + "\nTotal Time Elapsed: " + totalTimeElapsed);
        } else {
            System.out.println("No solution found within the time limit.");
        }
    }
}
