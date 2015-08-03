package mat.unical.it.learner.engine.geneticAlgorithm;

public class GAInitPopulationAlgoTypes {

    public static final int SPONTANEOUS_POPULATION_INIT = 0;

    public static final int DTSELECTION_DRIVEN_POPULATION_INIT = 1;

    public static final int SUPERVISED_WHIT_MUTATION_POPULATION_INIT = 2;

    public static final int VOCABULARY_DRIVEN_POPULATION_INIT = 3;

    public static final int IDEAL_CLASSIFICATION_DRIVEN_POPULATION_INIT = 4;

    private static String[] initPopulationType = new String[] { "Random" };

    public static String[] getInitPopulationType() {
        return initPopulationType;
    }

    public static void setInitPopulationType(String[] initPopulationType) {
        GAInitPopulationAlgoTypes.initPopulationType = initPopulationType;
    }

}
