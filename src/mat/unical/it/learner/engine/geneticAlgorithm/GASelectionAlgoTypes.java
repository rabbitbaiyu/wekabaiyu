package mat.unical.it.learner.engine.geneticAlgorithm;

public class GASelectionAlgoTypes {

    public static final int ROULETTE_WHEEL_SELECTION = 0;

    public static final int TOURNAMENT_SELECTION = 1;

    private static String[] selectionAlgorithmTypes = new String[] { "Roulette Wheel", "Tournament Selection" };

    public static String[] getSelectionAlgorithmTypes() {
	return selectionAlgorithmTypes;
    }

    public void setSelectionAlgorithmTypes(String[] selectionAlgorithmTypes) {
	GASelectionAlgoTypes.selectionAlgorithmTypes = selectionAlgorithmTypes;
    }

}
