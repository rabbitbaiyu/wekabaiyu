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
import org.jaga.selection.TournamentSelection;
import org.jaga.util.BitString;
import org.jaga.util.FittestIndividualResult;

public class ReducedLearnerGAMainClass {

	private int populationSize = 5;

	private int iterations = 10;

	private GAParameterSet params;

	private static Category category;

	private Configuration dtSelectorConf;

	public static double eliteProp = 0.2;

	public static double badProp = 0.1;

	public static double tournamentSelectionProp = 0.8;

	private ReducedClassifierFitnessEvaluator fitnessEvaluator;

	private int gaPopulationInitType = 0;

	private int posSize = 0;

	private int negSize = 0;

	private int attempts = 3;

	private int reproductionAlgoType = -1;

	private int selectionAlgoType = 0;

	public int getSelectionAlgoType() {
	    return selectionAlgoType;
	}

	public int getReproductionAlgoType() {
		return reproductionAlgoType;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public ReducedLearnerGAMainClass(int type) {
		this.params = new LearnerParameterSet();
		this.fitnessEvaluator = new ReducedClassifierFitnessEvaluator();
		this.gaPopulationInitType = type;
	}

	public void setTrainingSet(DocumentSet trainingSet) {
		fitnessEvaluator.setTrainingSet(trainingSet);
	}

	public void setPositiveReducedVocabulary(
			List<DiscriminativeTerm> posReducedVocabulary) {
		fitnessEvaluator.setPositiveReducedVocabulary(posReducedVocabulary);
	}

	public void setNegativeReducedVocabulary(
			List<DiscriminativeTerm> negReducedVocabulary) {
		fitnessEvaluator.setNegativeReducedVocabulary(negReducedVocabulary);
	}

	public Configuration[] exec() {
		// Setta la dimensione della popolazione iniziale
		params.setPopulationSize(getPopulationSize());
		// Setta la funzione di valutazione della fitness
		params.setFitnessEvaluationAlgorithm(fitnessEvaluator);
		// Setta l'algoritmo di selezione
		
		switch (getSelectionAlgoType()) {
			case GASelectionAlgoTypes.ROULETTE_WHEEL_SELECTION: 
				params.setSelectionAlgorithm(new RouletteWheelSelection(0));
				break;
			case GASelectionAlgoTypes.TOURNAMENT_SELECTION: 
				int tournamentSize = Math.max(1,(int) ((populationSize - (eliteProp * populationSize)) * 0.0125));
				
				double selectionProb = 0.7;
				System.out.println("using tournamentSize = " + tournamentSize);
				params.setSelectionAlgorithm(new TournamentSelection(tournamentSize, selectionProb));
				break;	
			default:
				break;
			}
//		params.setSelectionAlgorithm(new RouletteWheelSelection(0));
		// params.setSelectionAlgorithm(new TournamentSelection(6,
		// tournamentSelectionProp));
		// Setta il numero massimo di iterazioni dell'algoritmo
		params.setMaxGenerationNumber(getIterations());

		switch (getReproductionAlgoType()) {
//		case GARepAlgoTypes.BINARY_XOVER:
//			params.setReproductionAlgorithm(new ClassifierSimpleBinaryXOver(LearnerParameterSet.xOverRate));
//			break;
		case GARepAlgoTypes.BINARY_XOVER_WITH_MUTATION:
			params.setReproductionAlgorithm(new ClassifierBinaryXOverWithMutation(LearnerParameterSet.xOverRate,LearnerParameterSet.mutationRate));
			break;
//		case GARepAlgoTypes.TWO_POINT_XOVER:
//			params.setReproductionAlgorithm(new ClassifierTwoPointXOver(LearnerParameterSet.xOverRate));
//			break;
		case GARepAlgoTypes.TWO_POINT_XOVER_WIH_MUTATION:
			params.setReproductionAlgorithm(new ClassifierTwoPointXOverWithMutation(LearnerParameterSet.xOverRate,LearnerParameterSet.mutationRate));
			break;
//		case GARepAlgoTypes.UNIFORM_XOVER:
//			params.setReproductionAlgorithm(new ClassifierUniformXOver(LearnerParameterSet.xOverRate));
//			break;
		case GARepAlgoTypes.UNIFORM_XOVER_WITH_MUTATION:
			params.setReproductionAlgorithm(new ClassifierUniformXOverWithMutation(LearnerParameterSet.xOverRate,LearnerParameterSet.mutationRate));
			break;
		default:
			break;

		}
		Configuration[] allConf = new Configuration[getAttempts()];

		// Eseguo l'algoritmo solo se il numero di termini � superiore a zero
		// --> precisione
		ReducedClassifierIndividualFactory fact = new ReducedClassifierIndividualFactory(getPosSize(), getNegSize());
		params.setIndividualsFactory(fact);

		LearnerElitistGA ga = new LearnerElitistGA(params, eliteProp, badProp);
		ClassifierIndividual bestDTIndividual = null;
		// Attivo solo se la modalit� di inizializzazione della popolazione
		// �
		// pilotata dall'ottimizzatore
		if (getGaPopulationInitType() == GAInitPopulationAlgoTypes.DTSELECTION_DRIVEN_POPULATION_INIT) {
			// bestDTIndividual =
			// GAUtil.buildsIndividual(fitnessEvaluator.getReducedVocabulary(),
			// getDtSelectorConf(), params);
			bestDTIndividual = GAUtil.buildsIndividual(fitnessEvaluator
					.getPositiveReducedVocabulary(), fitnessEvaluator
					.getNegativeReducedVocabulary(), getDtSelectorConf(),
					params);
			// System.out.println("Best ottenuto: " +
			// bestDTIndividual.toString());
		}

		ga.setBestDTIndividual(bestDTIndividual);
		ga.setGaExecutionType(getGaPopulationInitType());

		// LearnerGA ga = new LearnerGA(params);

		BetterResultHook hook = new BetterResultHook();
		ga.addHook(hook);

		// AnalysisHook hook = new AnalysisHook();
		// hook.setLogStream(System.out);
		// hook.setUpdateDelay(1500);
		// ga.addHook(hook);

		GAResult[] allResults = new GAResult[getAttempts()];

		for (int i = 0; i < getAttempts(); i++) {
			hook.resetEvaluationsCounter();

//			 long startTime = System.currentTimeMillis();

			GAResult result = ga.exec();

//			 long stopTime = System.currentTimeMillis() - startTime;

			Configuration fittestConfig = buildsFittestConfiguration(result);

			allConf[i] = fittestConfig;
			// System.out.println("\nDONE.\n");
			// System.out.println("Total fitness evaluations: " +
			// hook.getFitnessEvaluations());
			allResults[i] = result;
		}

//		 System.out.println("\nALL DONE.\n");
//		 String s = "\n===================== Summary =====================\n";
//		 StringBuffer sb = new StringBuffer();
//		 if (null == sb)
//		 sb = new StringBuffer();
//			
//		 for (int i = 0; i < getAttempts(); i++) {
//		 s += "\nBest Individual for attempt " + (i + 1) + ": " +
//		 allResults[i];
//		 }
//		 sb.append("\n" + s);
//		 System.out.println(sb);
		return allConf;
	}

	private Configuration buildsFittestConfiguration(GAResult result) {
		Individual individual = ((FittestIndividualResult) result)
				.getFittestIndividual();

		ReducedClassifierIndividual indiv = (ReducedClassifierIndividual) individual;
		// System.out.println("individuo: " +
		// indiv.getBitStringRepresentation().toString());
		// Il BitString che rappresenta i termini positivi dell'individuo
		BitString posTermIndividual = null;
		try {
			posTermIndividual = indiv.getBitCode(0);
		} catch (Exception e) {
		}

		BitString negTermIndividual = null;
		try {
			negTermIndividual = indiv.getBitCode(1);
		} catch (Exception ex) {

		}

		List<DiscriminativeTerm> posDiscrTerms = GAUtil.extractTerms(
				posTermIndividual, fitnessEvaluator
						.getPositiveReducedVocabulary());
		List<DiscriminativeTerm> negDiscrTerms = GAUtil.extractTerms(
				negTermIndividual, fitnessEvaluator
						.getNegativeReducedVocabulary());

		Configuration fittestConfig = GAUtil.buildsConfiguration(posDiscrTerms,
				negDiscrTerms, getCategory());

		/* FIXME: riabilitare pulizia configurazione finale */
		Configuration cleanedFittestConfig = /* fittestConfig */
		GAUtil.cleanConfiguration(fittestConfig);

		GAUtil.setPerformanceValues(cleanedFittestConfig, posDiscrTerms,
				negDiscrTerms, ((ReducedClassifierFitnessEvaluator) params
						.getFitnessEvaluationAlgorithm()).getTrainingSet());
		cleanedFittestConfig.setFmeasure(((AbsoluteFitness) indiv.getFitness())
				.getValue());
		return cleanedFittestConfig;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
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
	}

	public static Category getCategory() {
		return category;
	}

	public static void setCategory(Category category) {
		ReducedLearnerGAMainClass.category = category;
	}

	public Configuration getDtSelectorConf() {
		return dtSelectorConf;
	}

	public void setDtSelectorConf(Configuration dtSelectorConf) {
		this.dtSelectorConf = dtSelectorConf;
	}

	public int getGaPopulationInitType() {
		return gaPopulationInitType;
	}

	public void setGaPopulationInitType(short gaPopulationInitType) {
		this.gaPopulationInitType = gaPopulationInitType;
	}

	public int getPosSize() {
		return posSize;
	}

	public void setPosSize(int posSize) {
		this.posSize = posSize;
	}

	public int getNegSize() {
		return negSize;
	}

	public void setNegSize(int negSize) {
		this.negSize = negSize;
	}

	public void setReproductionAlgoType(int reprAlgoType) {
		this.reproductionAlgoType = reprAlgoType;
	}

	public void setSelectionAlgoType(int reproductionAlgo) {

	    this.selectionAlgoType  = reproductionAlgo;
	}
}