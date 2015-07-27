package weka.classifiers.rules;


import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import mat.unical.it.learner.engine.basic.Configuration;
import mat.unical.it.learner.engine.geneticAlgorithm.GARepAlgoTypes;
import mat.unical.it.learner.engine.geneticAlgorithm.GASelectionAlgoTypes;
import mat.unical.it.learner.engine.geneticAlgorithm.LearnerParameterSet;
import mat.unical.it.learner.wrapper.core.OlexGAparameters;
import mat.unical.it.learner.wrapper.core.SFManager;
import mat.unical.it.learner.wrapper.core.WrapperManager;

import weka.classifiers.Classifier;
import weka.core.AdditionalMeasureProducer;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;

/**
 * 
 * @author Adriana Pietramala
 * 
 */
public class OlexGA extends Classifier implements OptionHandler, AdditionalMeasureProducer {

	private static final long serialVersionUID = 8647630661819994437L;

	/***********************************************/
	/*** START LIST OF AVAILABLE OLEX-GA OPTIONS ***/
	/***********************************************/
	private final char SCORING_FUNCTION = 'Y';

	private final char POSITIVE_TERMS_SIZE = 'P';

	private final char XOVER_METHOD = 'X';

	private final char XOVER_RATE = 'R';

	private final char MUTATION_RATE = 'M';

	private final char SELECTION_ALGO = 'S';

	private final char ELITISM_PROPORTION = 'E';

	private final char POPULATION_SIZE = 'I';

	private final char GENERATIONS = 'G';

	private final char ATTEMPTS = 'A';
	
	private final char LEARNED_CATEGORY_INDEX = 'C';

	/***********************************************/
	/**** END LIST OF AVAILABLE OLEX-GA OPTIONS ****/
	/***********************************************/

	private final int numOfOptions = 11;

	private List<Configuration> configurations = null;

	protected OlexGAparameters olexGAParams = null;

	protected Instances m_Instances = null;

	public static final Tag[] TAGS_OlexGA_SCORING_FUNCTION_TYPES = {
			new Tag(SFManager.CHI,
					SFManager.getScoringFunctions()[SFManager.CHI]),
			new Tag(SFManager.IG, SFManager.getScoringFunctions()[SFManager.IG]),
			new Tag(SFManager.ODDS,
					SFManager.getScoringFunctions()[SFManager.ODDS]) };

	public static final Tag[] TAGS_OlexGA_XOVER_METHODS = {
			new Tag(
					GARepAlgoTypes.UNIFORM_XOVER_WITH_MUTATION,
					GARepAlgoTypes.getRepAlgoritms()[GARepAlgoTypes.UNIFORM_XOVER_WITH_MUTATION]),
			new Tag(
					GARepAlgoTypes.TWO_POINT_XOVER_WIH_MUTATION,
					GARepAlgoTypes.getRepAlgoritms()[GARepAlgoTypes.TWO_POINT_XOVER_WIH_MUTATION]),
			new Tag(
					GARepAlgoTypes.BINARY_XOVER_WITH_MUTATION,
					GARepAlgoTypes.getRepAlgoritms()[GARepAlgoTypes.BINARY_XOVER_WITH_MUTATION]) };

	public static final Tag[] TAGS_OlexGA_SELECTION_METHOS = { new Tag(
			GASelectionAlgoTypes.ROULETTE_WHEEL_SELECTION,
			GASelectionAlgoTypes.getSelectionAlgorithmTypes()[GASelectionAlgoTypes.ROULETTE_WHEEL_SELECTION]),
			new Tag(GASelectionAlgoTypes.TOURNAMENT_SELECTION,
			GASelectionAlgoTypes.getSelectionAlgorithmTypes()[GASelectionAlgoTypes.TOURNAMENT_SELECTION])};
	
	public static final Tag[] TAGS_LEARNED_CLASS_VALUE_INDEX = { 
		new Tag(0,"0") ,
		new Tag(1,"1")
		};

	/*****************************************/
	/*** START LIST OF ADDITIONAL MEASURES ***/
	/*****************************************/
	
	private static final String F_TRAIN_MEASURE = "measureFtrain";
	
	private static final String SELECTED_LEARNED_CATEGORY_INDEX= "measureLearnedCategoryIndex";		
	
	/*****************************************/
	/***  END LIST OF ADDITIONAL MEASURES  ***/
	/*****************************************/

	/**
	 * 
	 */
	public OlexGA() {
		String[] dummy = {};
		try {
			setOptions(dummy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds the model
	 * 
	 * @param insts
	 *            Instances
	 * 
	 */
	@Override
	public void buildClassifier(Instances insts) throws Exception {
		
		// can classifier handle the data?
		getCapabilities().testWithFail(insts);
		
		// remove instances with missing class
		this.m_Instances = new Instances(insts);
		this.m_Instances.deleteWithMissingClass();

		configurations = WrapperManager.doLearning(insts);
	}

	public double[] distributionForInstance(Instance instance) throws Exception {
		return WrapperManager.doValidation(instance, configurations);
	}

	/**
	 * 
	 * @return String
	 */
	public String globalInfo() {
		return "Olex-GA is a genetic algorithm for the induction of rule-based text classifiers. "
				+ "It relies on an efficient several-rules-per-individual binary representation and "
				+ "uses the F-measure as the fitness function.\n\n" 
				+ getTechnicalInformation().toString();
	}

	public void setOptions(String[] options) throws Exception {
		String tmpStr;

		// Scoring function
		tmpStr = Utils.getOption(SCORING_FUNCTION, options);
		if (tmpStr.length() != 0)
			setScoringFunction(new SelectedTag(Integer.parseInt(tmpStr),
					TAGS_OlexGA_SCORING_FUNCTION_TYPES));
		else
			setScoringFunction(new SelectedTag(SFManager.CHI,
					TAGS_OlexGA_SCORING_FUNCTION_TYPES));

		// Crossover method
		tmpStr = Utils.getOption(XOVER_METHOD, options);
		if (tmpStr.length() != 0)
			setXOver(new SelectedTag(Integer.parseInt(tmpStr),
					TAGS_OlexGA_XOVER_METHODS));
		else
			setXOver(new SelectedTag(
					GARepAlgoTypes.UNIFORM_XOVER_WITH_MUTATION,
					TAGS_OlexGA_XOVER_METHODS));

		// Positive terms size
		tmpStr = Utils.getOption(POSITIVE_TERMS_SIZE, options);
		if (tmpStr.length() != 0)
			setNumOfFeatures(Integer.parseInt(tmpStr));
		else
			setNumOfFeatures(OlexGAparameters.POSITIVE_TERMS_SIZE);

		// Crossover rate
		tmpStr = Utils.getOption(XOVER_RATE, options);
		if (tmpStr.length() != 0)
			setXOverRate(Double.parseDouble(tmpStr));
		else
			setXOverRate(OlexGAparameters.XOVER_RATE);

		// Mutation rate
		tmpStr = Utils.getOption(MUTATION_RATE, options);
		if (tmpStr.length() != 0)
			setMutationRate(Double.parseDouble(tmpStr));
		else
			setMutationRate(OlexGAparameters.MUTATION_RATE);

		// Selection algorithm
		tmpStr = Utils.getOption(SELECTION_ALGO, options);
		if (tmpStr.length() != 0)
			setSelectionAlgorithm(new SelectedTag(Integer.parseInt(tmpStr),	TAGS_OlexGA_SELECTION_METHOS));
		else
			setSelectionAlgorithm(new SelectedTag(
					GASelectionAlgoTypes.TOURNAMENT_SELECTION,
					TAGS_OlexGA_SELECTION_METHOS));

		// Population size
		tmpStr = Utils.getOption(POPULATION_SIZE, options);
		if (tmpStr.length() != 0)
			setPopulationSize(Integer.parseInt(tmpStr));
		else
			setPopulationSize(OlexGAparameters.POP_SIZE);

		// Generations
		tmpStr = Utils.getOption(GENERATIONS, options);
		if (tmpStr.length() != 0)
			setNumOfGenerations(Integer.parseInt(tmpStr));
		else
			setNumOfGenerations(OlexGAparameters.GENERATIONS);

		// Attempts
		tmpStr = Utils.getOption(ATTEMPTS, options);
		if (tmpStr.length() != 0)
			setNumOfRuns(Integer.parseInt(tmpStr));
		else
			setNumOfRuns(OlexGAparameters.ATTEMPTS);

		// Elite proportion
		tmpStr = Utils.getOption(ELITISM_PROPORTION, options);
		if (tmpStr.length() != 0)
			setElitismRate(Double.parseDouble(tmpStr));
		else
			setElitismRate(OlexGAparameters.ELITE_PROPORTION);
		
		tmpStr = Utils.getOption(LEARNED_CATEGORY_INDEX, options);
		if (tmpStr.length() != 0)
			setClassIndex(new SelectedTag(Integer.parseInt(tmpStr),	TAGS_LEARNED_CLASS_VALUE_INDEX));
		else
			setClassIndex(new SelectedTag(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX, TAGS_LEARNED_CLASS_VALUE_INDEX));
	}

	

	public String[] getOptions() {
		String[] options = new String[2 * numOfOptions];

		int current = 0;
		options[current++] = "-" + SCORING_FUNCTION;
		options[current++] = "" + OlexGAparameters.SCORING_FUNCTION;
		options[current++] = "-" + POSITIVE_TERMS_SIZE;
		options[current++] = "" + OlexGAparameters.POSITIVE_TERMS_SIZE;
		options[current++] = "-" + XOVER_METHOD;
		options[current++] = "" + OlexGAparameters.XOVER_METHOD;
		options[current++] = "-" + XOVER_RATE;
		options[current++] = "" + OlexGAparameters.XOVER_RATE;
		options[current++] = "-" + MUTATION_RATE;
		options[current++] = "" + OlexGAparameters.MUTATION_RATE;
		options[current++] = "-" + SELECTION_ALGO;
		options[current++] = "" + OlexGAparameters.SELECTION_ALGORITHM;
		options[current++] = "-" + POPULATION_SIZE;
		options[current++] = "" + OlexGAparameters.POP_SIZE;
		options[current++] = "-" + GENERATIONS;
		options[current++] = "" + OlexGAparameters.GENERATIONS;
		options[current++] = "-" + ATTEMPTS;
		options[current++] = "" + OlexGAparameters.ATTEMPTS;
		options[current++] = "-" + ELITISM_PROPORTION;
		options[current++] = "" + OlexGAparameters.ELITE_PROPORTION;
		options[current++] = "-" + LEARNED_CATEGORY_INDEX;
		options[current++] = "" + OlexGAparameters.LEARNED_CLASS_VALUE_INDEX;

		while (current < options.length) {
			options[current++] = "";
		}

		return options;
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing detailed
	 * information about the technical background of this class, e.g., paper
	 * reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result;
		TechnicalInformation additional;
		
		result = new TechnicalInformation(Type.INPROCEEDINGS);
		
		result.setValue(TechnicalInformation.Field.AUTHOR, "A. Pietramala, Veronica L. Policicchio, P. Rullo, I. Sidhu");
		result.setValue(TechnicalInformation.Field.YEAR, "2008");
		result.setValue(TechnicalInformation.Field.TITLE, "A Genetic Algorithm for Text Classification Rule Induction");
		
		result.setValue(Field.BOOKTITLE, "LNAI - Machine Learning and Knowledge Discovery in Databases - Part II");
		result.setValue(Field.PAGES, "188-203");
		
		additional = result.add(Type.MISC);
		additional.setValue(TechnicalInformation.Field.URL, "http://www.unical.it/Olex-GA/");
		additional.setValue(TechnicalInformation.Field.NOTE, "You need to include the two packages olexGA.jar and jaga.jar in the CLASSPATH");
		return result;
	}

	public Enumeration<Option> listOptions() {
		Vector<Option> result;

		result = new Vector<Option>();

		 result.addElement(
		 new Option(
		 "\tSet type of scoring function used/to use for feature selection (default: " + OlexGAparameters.SCORING_FUNCTION + ")\n"
		 + "\t\t 0 = CHI\n"
		 + "\t\t 1 = IG\n"
		 + "\t\t 2 = ODDS\n",
		 "Y", 1, "-Y <int>"));
			    
		 result.addElement(
		 new Option(
		 "\tSet max positive terms size (default: " + OlexGAparameters.POSITIVE_TERMS_SIZE + ")",
		 "P", 1, "-P <int>"));	
				    
		  result.addElement(
		 new Option(
		 "\tSet the crossover method (default:  " +  OlexGAparameters.XOVER_METHOD + ")\n" 
		 + "\t\t 0 = UNIFORM XOVER WITH MUTATION\n"
		 + "\t\t 1 = TWO POINT XOVER WIH MUTATION\n"
		 + "\t\t 2 = BINARY XOVER WITH MUTATION\n",
		 "X", 1, "-X <int>"));
		//		    
		 result.addElement(
		 new Option(
		 "\tSet the crossover rate (default: " + OlexGAparameters.XOVER_RATE + ")",
		 "R", 1, "-R <double>"));

		  result.addElement(		
		 new Option(
		 "\tSet the mutation rate (default: " + OlexGAparameters.MUTATION_RATE + ")",
		 "M", 1, "-M <double>"));

		  result.addElement(
		 new Option(
		 "\tSet the selection algorithm type (default: " + OlexGAparameters.SELECTION_ALGORITHM + "\n)"
		 + "\t\t 0 = ROULETTE WHEEL SELECTION\n"
		 + "\t\t 1 = TOURNAMENT SELECTION\n",
		 "S", 1, "-S"));

		 result.addElement(
		 new Option(
		 "\tSet the population size (default: " + OlexGAparameters.POSITIVE_TERMS_SIZE + ")",
		 "I", 1, "-I <int>"));
		//		    
		 result.addElement(
		 new Option(
		 "\tSet the number of generations (default: " + OlexGAparameters.GENERATIONS + ")",
		 "G", 1, "-G <int>"));
		 
		 result.addElement(
		 new Option(
		 "\tSet the number of attempts (default: " + OlexGAparameters.ATTEMPTS + ")",
		 "A", 1, "-A <int>"));

		 result.addElement(new Option(	
				"\tSet the elit rate (default: " + OlexGAparameters.ELITE_PROPORTION + ")",
		 "E", 1, "-E"));

		 result.addElement(new Option(	
					"\tSet the class value used for IR statistics(default: " + OlexGAparameters.LEARNED_CLASS_VALUE_INDEX + ")",
			 "C", 1, "-C"));
   
		Enumeration<Option> en = super.listOptions();
		while (en.hasMoreElements())
			result.addElement(en.nextElement());

		return result.elements();
	}

	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();

		// attributes
		result.enable(Capability.NUMERIC_ATTRIBUTES);

		// class
		result.enable(Capability.BINARY_CLASS);
		return result;
	}

	public String toString() {
		return "Olex-GA Classifier, available at www.mat.unical.it/Olex-GA/ web site";
	}

	public void setNumOfFeatures(int posSize) {
		OlexGAparameters.POSITIVE_TERMS_SIZE = posSize;
	}

	public int getNumOfFeatures() {
		return OlexGAparameters.POSITIVE_TERMS_SIZE;
	}
	
	public void setScoringFunction(SelectedTag value) {
		if (value.getTags() == TAGS_OlexGA_SCORING_FUNCTION_TYPES) {
			OlexGAparameters.SCORING_FUNCTION = value.getSelectedTag().getID();
		}
	}

	public SelectedTag getScoringFunction() {
		return new SelectedTag(OlexGAparameters.SCORING_FUNCTION,
				TAGS_OlexGA_SCORING_FUNCTION_TYPES);
	}
	
	public void setXOver(SelectedTag value) {
		if (value.getTags() == TAGS_OlexGA_XOVER_METHODS)
			OlexGAparameters.XOVER_METHOD = value.getSelectedTag().getID();
	}

	public SelectedTag getXOver() {
		return new SelectedTag(OlexGAparameters.XOVER_METHOD,
				TAGS_OlexGA_XOVER_METHODS);
	}

	
	public void setSelectionAlgorithm(SelectedTag value) {
		if (value.getTags() == TAGS_OlexGA_SELECTION_METHOS)
			OlexGAparameters.SELECTION_ALGORITHM = value.getSelectedTag()
					.getID();
	}

	public SelectedTag getSelectionAlgorithm() {
		return new SelectedTag(OlexGAparameters.SELECTION_ALGORITHM,
				TAGS_OlexGA_SELECTION_METHOS);
	}
	
	public void setClassIndex(SelectedTag value) {
		OlexGAparameters.LEARNED_CLASS_VALUE_INDEX = value.getSelectedTag().getID();
	}
	
	public SelectedTag getClassIndex() {
		return new SelectedTag(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX, TAGS_LEARNED_CLASS_VALUE_INDEX);
	}

	
	public void setXOverRate(double rate) {
		OlexGAparameters.XOVER_RATE = rate;
		LearnerParameterSet.xOverRate = OlexGAparameters.XOVER_RATE;
	}

	public double getXOverRate() {
		return OlexGAparameters.XOVER_RATE;
	}

	
	public void setMutationRate(double mutation) {
		OlexGAparameters.MUTATION_RATE = mutation;
		LearnerParameterSet.mutationRate = OlexGAparameters.MUTATION_RATE;
	}

	public double getMutationRate() {
		return OlexGAparameters.MUTATION_RATE;
	}

	
	public void setElitismRate(double prop) {
		OlexGAparameters.ELITE_PROPORTION = prop;
	}

	public double getElitismRate() {
		return OlexGAparameters.ELITE_PROPORTION;
	}

	public void setPopulationSize(int size) {
		OlexGAparameters.POP_SIZE = size;
	}

	public int getPopulationSize() {
		return OlexGAparameters.POP_SIZE;
	}

	public void setNumOfGenerations(int gen) {
		OlexGAparameters.GENERATIONS = gen;
	}

	public int getNumOfGenerations() {
		return OlexGAparameters.GENERATIONS;
	}
	
	public void setNumOfRuns(int attempts) {
		OlexGAparameters.ATTEMPTS = attempts;
	}

	public int getNumOfRuns() {
		return OlexGAparameters.ATTEMPTS;
	}
	
	public String populationSizeTipText() {
		return "Number of chromosomes or individuals";
	}

	public String xOverRateTipText() {
		return "The chance that two chromosomes will swap their bits.";
	}

	public String xOverMethodTipText() {
		return "The method used for individuals reproduction.";
	}

	public String elitismProportionTipText() {
		return "The percentage of the best chromosomes to be copied into the new population.";
	}

	public String generationsTipText() {
		return "Number of times the process of going from the current population to the next on is executed.";
	}

	public String attemptsTipText() {
		return "Number of times the genetic algorithm is executed.";
	}
	
	public String selectionAlgorithmTipText() {
		return "The method of choosing members from the population of chromosomes.";
	}

	public String scoringFunctionTipText() {
		return "If featureSelection is FALSE, the scoring function that will be used to perform feature selection." +
			    "If featureSelection is TRUE, the scoring function that you have used to perform feature selection.";
	}

	public String initializationTypeTipText() {
		return "The method used to initialize the population.";
	}

	public String performFeatureSelectionTipText() {
		return "TRUE if you already perform feature selection, FALSE otherwise.";
	}

	public String mutationRateTipText() {
		return "The chance that a bit within a chromosome will be flipped.";
	}

	public String maxPositiveTermsSizeTipText() {
		return "The max size of the subset of terms of the given vocabulary used as candidate positive.";
	}
	
	
	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration enumerateMeasures() {
		Vector<String> newVector = new Vector<String>(2);
		newVector.addElement(SELECTED_LEARNED_CATEGORY_INDEX);
		newVector.addElement(F_TRAIN_MEASURE);
		return newVector.elements();
	}

	@Override
	public double getMeasure(String additionalMeasureName) {
		if (additionalMeasureName.compareToIgnoreCase(SELECTED_LEARNED_CATEGORY_INDEX) == 0){
			return configurations.get(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX).getCategory().getCategoryNumber();
		}
		
		if (additionalMeasureName.compareToIgnoreCase(F_TRAIN_MEASURE) == 0){
			return configurations.get(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX).getFmeasure();
		}
		
		else
			throw new IllegalArgumentException(additionalMeasureName
					+ " not supported (Olex-GA)");
	}
	
	/**
	 * Main method.
	 * 
	 * @param args
	 *            the options for the classifier
	 */
	public static void main(String[] args) {
		runClassifier(new OlexGA(), args);
	}

}
