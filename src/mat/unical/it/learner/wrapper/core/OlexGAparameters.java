package mat.unical.it.learner.wrapper.core;

import mat.unical.it.learner.engine.geneticAlgorithm.GAInitPopulationAlgoTypes;
import mat.unical.it.learner.engine.geneticAlgorithm.GARepAlgoTypes;
import mat.unical.it.learner.engine.geneticAlgorithm.GASelectionAlgoTypes;


/**
 * 
 * @author Adriana Pietramala
 *
 */
public class OlexGAparameters  implements java.lang.Cloneable, java.io.Serializable {
	
	public static boolean FEATURE_SELECTION = true;
	
	public static int SCORING_FUNCTION = SFManager.CHI;

	public static int POSITIVE_TERMS_SIZE = 60;

	public static int NEGATIVE_TERMS_SIZE = 200;
	
	public static int INITIALIZATION_TYPE = GAInitPopulationAlgoTypes.SPONTANEOUS_POPULATION_INIT;
	
	public static int XOVER_METHOD = GARepAlgoTypes.UNIFORM_XOVER_WITH_MUTATION;
	
	public static double XOVER_RATE = 1.0;

	public static double MUTATION_RATE = 0.001;

	public static int SELECTION_ALGORITHM = GASelectionAlgoTypes.TOURNAMENT_SELECTION;
	
	public static double ELITE_PROPORTION = 0.2;
	
	public static double BAD_PROPORTION = 0.1;

	public static int POP_SIZE = 500;

	public static int GENERATIONS = 200;

	public static int ATTEMPTS = 1;
	
	public static double FMEASURE = 0.5;
	
	public static Integer MAX_CONJ_LENGHT = 1;
	
	public static int LEARNED_CLASS_VALUE_INDEX = 1;
	
}
