package mat.unical.it.learner.engine.geneticAlgorithm;


import java.util.List;

import mat.unical.it.learner.engine.basic.Category;
import mat.unical.it.learner.engine.basic.Configuration;
import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;
import mat.unical.it.learner.engine.geneticAlgorithm.utils.GAUtil;

import org.jaga.definitions.GAParameterSet;
import org.jaga.definitions.GAResult;
import org.jaga.definitions.Individual;
import org.jaga.hooks.BetterResultHook;
import org.jaga.selection.AbsoluteFitness;
import org.jaga.selection.RouletteWheelSelection;
import org.jaga.util.FittestIndividualResult;

public class LearnerGAMainClass {

	private int populationSize = 5;

	private int iterations = 10;

	private int termsNumber = 0;

	private GAParameterSet params;

	private static Category category;

	private Configuration dtSelectorConf;

	public static double eliteProp = 0.2;

	public static double badProp = 0.1;

	public static double tournamentSelectionProp = 0.8;

	private ClassifierFitnessEvaluator fitnessEvaluator;

	private short gaPopulationInitType = 0;

	public LearnerGAMainClass(short type) {
		this.params = new LearnerParameterSet();
		this.fitnessEvaluator = new ClassifierFitnessEvaluator();
		this.gaPopulationInitType = type;
	}

	public void setTrainingSet(DocumentSet trainingSet) {
		fitnessEvaluator.setTrainingSet(trainingSet);
	}

	public void setReducedVocabulary(List<DiscriminativeTerm> reducedVocabulary) {
		fitnessEvaluator.setReducedVocabulary(reducedVocabulary);
	}

	public Configuration[] exec() {
		// Setta la dimensione della popolazione iniziale
		params.setPopulationSize(getPopulationSize());
		// Setta la funzione di valutazione della fitness
		params.setFitnessEvaluationAlgorithm(fitnessEvaluator);
		// Setta l'algoritmo di selezione
		
		params.setSelectionAlgorithm(new RouletteWheelSelection(0));
		// params.setSelectionAlgorithm(new TournamentSelection(6,
		// tournamentSelectionProp));
		// Setta il numero massimo di iterazioni dell'algoritmo
		params.setMaxGenerationNumber(getIterations());

		final int attempts = 3;
		Configuration[] allConf = new Configuration[attempts];

		// Eseguo l'algoritmo solo se il numero di termini � superiore a zero
		// --> precisione
		if (getTermsNumber() > 0) {

			int size = 2 * getTermsNumber();
			int precision = 1;
			int decimalScale = 0;
			ClassifierIndividualFactory fact = new ClassifierIndividualFactory(
					size, decimalScale, precision);
			// for(int i=0; i<size; i++){
			// fact.setConstraint(i, new RangeConstraint(Double.MIN_VALUE,
			// Double.MAX_VALUE));
			// // fact.setConstraint(1, new RangeConstraint(0,
			// Double.MAX_VALUE));
			// }
			params.setIndividualsFactory(fact);

			// ((LearnerParameterSet)params).mutationRate = 1.0 / size;
			// System.out.println("LearnerGAMainClass.exec() --> mutationRate = 1/size = "
			// + ((LearnerParameterSet)params).mutationRate);
			ClassifierIndividual bestDTIndividual = null;
			if (getDtSelectorConf() != null) {
				bestDTIndividual = GAUtil.buildsIndividual(fitnessEvaluator
						.getReducedVocabulary(), getDtSelectorConf(), params);
			}

			LearnerElitistGA ga = new LearnerElitistGA(params, eliteProp,
					badProp);
			ga.setBestDTIndividual(bestDTIndividual);
			ga.setGaExecutionType(getGaPopulationInitType());

			// LearnerGA ga = new LearnerGA(params);

			BetterResultHook hook = new BetterResultHook();
			ga.addHook(hook);

			// AnalysisHook hook = new AnalysisHook();
			// hook.setLogStream(System.out);
			// hook.setUpdateDelay(1500);
			// ga.addHook(hook);

			GAResult[] allResults = new GAResult[attempts];

			for (int i = 0; i < attempts; i++) {
				hook.resetEvaluationsCounter();

//				long startTime = System.currentTimeMillis();

				GAResult result = ga.exec();

//				long stopTime = System.currentTimeMillis() - startTime;

				Configuration fittestConfig = buildsFittestConfiguration(result);

				allConf[i] = fittestConfig;
				System.out.println("\nDONE.\n");
				System.out.println("Total fitness evaluations: "
						+ hook.getFitnessEvaluations());
				allResults[i] = result;
			}

			System.out.println("\nALL DONE.\n");
			for (int i = 0; i < attempts; i++) {
				System.out.println("Result " + i + " is: " + allResults[i]);
			}
		}
		return allConf;
	}

	private Configuration buildsFittestConfiguration(GAResult result) {
		Individual indiv = ((FittestIndividualResult) result)
				.getFittestIndividual();
		List<DiscriminativeTerm> posDiscrTerms = GAUtil.extractTerms(indiv,
				fitnessEvaluator.getReducedVocabulary(), 0);
		List<DiscriminativeTerm> negDiscrTerms = GAUtil.extractTerms(indiv,
				fitnessEvaluator.getReducedVocabulary(), 1);
		Configuration fittestConfig = GAUtil.buildsConfiguration(posDiscrTerms,
				negDiscrTerms, getCategory());
		fittestConfig.setFmeasure(((AbsoluteFitness) indiv.getFitness())
				.getValue());
		return fittestConfig;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		// System.out.println("POP SIZE: " + populationSize);
	}

	// public static void main(String[] unusedArgs) {
	// LearnerGAMainClass demo = new
	// LearnerGAMainClass(GAInitPopulationAlgoTypes.SPONTANEOUS_POPULATION_INIT);
	// demo.exec();
	// }

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
		// System.out.println("ITERATIONS: " + iterations);
	}

	public int getTermsNumber() {
		return termsNumber;
	}

	public void setTermsNumber(int termsNumber) {
		this.termsNumber = termsNumber;
	}

	public static Category getCategory() {
		return category;
	}

	public static void setCategory(Category category) {
		LearnerGAMainClass.category = category;
	}

	public Configuration getDtSelectorConf() {
		return dtSelectorConf;
	}

	public void setDtSelectorConf(Configuration dtSelectorConf) {
		this.dtSelectorConf = dtSelectorConf;
	}

	public short getGaPopulationInitType() {
		return gaPopulationInitType;
	}

	public void setGaPopulationInitType(short gaPopulationInitType) {
		this.gaPopulationInitType = gaPopulationInitType;
	}
}