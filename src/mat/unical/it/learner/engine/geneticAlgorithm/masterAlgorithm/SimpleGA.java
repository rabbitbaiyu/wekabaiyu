package mat.unical.it.learner.engine.geneticAlgorithm.masterAlgorithm;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;
import mat.unical.it.learner.engine.geneticAlgorithm.ClassifierFitnessEvaluator;
import mat.unical.it.learner.engine.geneticAlgorithm.ClassifierIndividual;
import mat.unical.it.learner.engine.geneticAlgorithm.ClassifierSimpleBinaryMutation;
import mat.unical.it.learner.engine.geneticAlgorithm.GAInitPopulationAlgoTypes;
import mat.unical.it.learner.engine.geneticAlgorithm.drivenInitialization.DocumentClassifierBuilder;

import org.jaga.definitions.Fitness;
import org.jaga.definitions.FitnessEvaluationAlgorithm;
import org.jaga.definitions.GAParameterSet;
import org.jaga.definitions.GAResult;
import org.jaga.definitions.Individual;
import org.jaga.definitions.Population;
import org.jaga.definitions.ReproductionAlgorithm;
import org.jaga.definitions.SelectionAlgorithm;
import org.jaga.hooks.SimpleGAHook;
import org.jaga.selection.AbsoluteFitnessIndividualComparator;
import org.jaga.util.BitString;
import org.jaga.util.FittestIndividualResult;
import org.jaga.util.SimpleCollectionOfIndividuals;

public class SimpleGA extends org.jaga.masterAlgorithm.SimpleGA {

	private ClassifierIndividual bestDTIndividual;

	private ArrayList<SimpleGAHook> hooks = null;

	private int gaExecutionType = 0;

	public SimpleGA() {
	}

	public GAResult exec(GAParameterSet params) {
		if (null == params)
			throw new NullPointerException("Parameters to a GA may not be null");

		int age = 0;
		double probability = 0.001;

		// Genera randomaticamente gli individui della popolazione iniziale
		Population pop = null;

		if (getGaExecutionType() == GAInitPopulationAlgoTypes.SPONTANEOUS_POPULATION_INIT) {
			pop = createInitialPopulation(params);
		} else if (getGaExecutionType() == GAInitPopulationAlgoTypes.DTSELECTION_DRIVEN_POPULATION_INIT) {
			pop = createInitialPopulation(params, getBestDTIndividual(),
					probability);
		} else if (getGaExecutionType() == GAInitPopulationAlgoTypes.SUPERVISED_WHIT_MUTATION_POPULATION_INIT) {
			pop = createInitialPopulation(params, probability);
		} else if (getGaExecutionType() == GAInitPopulationAlgoTypes.VOCABULARY_DRIVEN_POPULATION_INIT) {
			;
		} else if (getGaExecutionType() == GAInitPopulationAlgoTypes.IDEAL_CLASSIFICATION_DRIVEN_POPULATION_INIT) {
			pop = createInitialPopulation(params, true);
		} else {
			System.err.println("Doesn't supported. System exit.");
			System.exit(0);
		}

		// Genera gli individui della popolazione iniziale mediante una
		// mutazione dell'individuo best
		// calcolato a partire dall'algoritmo DTSelector
		// Population pop = createInitialPopulation(params,
		// getBestDTIndividual(), probability);

		// Genera gli individui della popolazione iniziale mediante una
		// mutazione degli individui
		// Population pop = createInitialPopulation(params, probability);

		FittestIndividualResult result = (FittestIndividualResult) createResult();
		for (int i = 0; i < pop.getSize(); updateIndividualFitness(pop.getMember(i++), pop, age, params))
			;

		checkForBetterResult(result, pop, params);
		notifyInitialisationDone(pop, age, result, params);

		while (!terminationConditionApplies(pop, age, result, params)) {

			Individual best = result.getFittestIndividual();
			// System.out.println("\nFitness:  " +
			// ((AbsoluteFitness)best.getFitness()).getValue());

			Population nextPop = generateNextPopulation(pop, age, result,
					params);

			pop = nextPop;
			age++;

			notifyGenerationChanged(pop, age, result, params);

			if (result.getFittestIndividual() != best)
				notifyFoundNewResult(pop, age, result, params);

		}

		notifyTerminationConditionApplies(pop, age, result, params);

		return result;
	}

	protected Population generateNextPopulation(Population oldPop, int age,
			GAResult result, GAParameterSet params) {
		FittestIndividualResult res = (FittestIndividualResult) result;
		Population newPop = createEmptyPopulation(params);
		while (newPop.getSize() < params.getPopulationSize()) {

			Individual[] parents = selectForReproduction(oldPop, age, params);
			notifySelectedForReproduction(parents, oldPop, age, result, params);

			Individual[] children = haveSex(parents, params);
			for (int i = 0; i < children.length; i++) {
				if (null != children[i].getFitness())
					continue;
				updateIndividualFitness(children[i], oldPop, age, params);
				if (children[i].getFitness().isBetter(res.getBestFitness()))
					res.setFittestIndividual(children[i]);
			}

			notifyReproduced(children, parents, oldPop, age, result, params);
			newPop.addAll(children);
		}

		return newPop;
	}

	protected GAResult createResult() {
		return new FittestIndividualResult();
	}

	protected boolean checkForBetterResult(GAResult oldResult,
			Population newPop, GAParameterSet params) {

		FittestIndividualResult result = (FittestIndividualResult) oldResult;
		Fitness best = result.getBestFitness();

		final int size = newPop.getSize();
		for (int i = 0; i < size; i++) {
			Fitness f = newPop.getMember(i).getFitness();
			if (f.isBetter(best)) {
				best = f;
				result.setFittestIndividual(newPop.getMember(i));
			}
		}

		return (result.getBestFitness() != best);
	}

	/**
	 * Crea gli individui della popolazione iniziale sfruttando una metodologia
	 * di tipo random
	 * 
	 * @param params
	 *            GAParameterSet
	 * @return Population - la popolazione iniziale
	 */
	protected Population createInitialPopulation(GAParameterSet params) {
		Population pop = createEmptyPopulation(params);
		while (pop.getSize() < params.getPopulationSize()) {
			Individual ind;
			ind = params.getIndividualsFactory().createRandomIndividual(params);
			pop.add(ind);
		}

		return pop;
	}

	private Population createInitialPopulation(GAParameterSet params, boolean b) {

		List<DiscriminativeTerm> reducedVocabulary = ((ClassifierFitnessEvaluator) params
				.getFitnessEvaluationAlgorithm()).getReducedVocabulary();
		DocumentSet trainingSet = ((ClassifierFitnessEvaluator) params
				.getFitnessEvaluationAlgorithm()).getTrainingSet();
		List<BitString> classBitStringList = DocumentClassifierBuilder
				.buildClassifiersFromDocuments(reducedVocabulary, trainingSet);
		params.setPopulationSize(classBitStringList.size());

		Population pop = createEmptyPopulation(params);
		for (int i = 0; i < classBitStringList.size(); i++) {
			ClassifierIndividual ind = (ClassifierIndividual) params
					.getIndividualsFactory().createDefaultIndividual(params);
			ind.setBitStringRepresentation(classBitStringList.get(i));
			pop.add(ind);

		}

		return pop;
	}

	protected Population createInitialPopulation(GAParameterSet params,
			Individual indiv, double mutationProb) {

		Population pop = createEmptyPopulation(params);
		// add SIZE - 1 clones of the best individual to population pop
		while (pop.getSize() < params.getPopulationSize() - 1) {
			pop.add(indiv);
		}
		// do mutation
		ClassifierSimpleBinaryMutation initMutation = new ClassifierSimpleBinaryMutation(
				mutationProb);
		Individual[] generatedInd = initMutation.init(pop.getAllMembers(),
				params, mutationProb);

		// create an empty population pop1 and add all the generated individuals
		// to it
		Population pop1 = createEmptyPopulation(params);
		pop1.addAll(generatedInd);
		// add the best individual to this population
		pop1.add(indiv);

		// System.out.println("++++++++++++++++++ popolazione iniziale ++++++++++++++++++++++++++");
		// for (int i = 0; i < pop1.getSize(); i++) {
		// Individual bastardo = pop1.getMember(i);
		//	    
		// List<DiscriminativeTerm> posDiscrTerms =
		// GAUtil.extractTerms(bastardo,
		// ((ReducedClassifierFitnessEvaluator)params.getFitnessEvaluationAlgorithm()).getPositiveReducedVocabulary(),
		// 0);
		// List<DiscriminativeTerm> negDiscrTerms =
		// GAUtil.extractTerms(bastardo,
		// ((ReducedClassifierFitnessEvaluator)params.getFitnessEvaluationAlgorithm()).getNegativeReducedVocabulary(),
		// 1);
		//
		// Configuration fittestConfig =
		// GAUtil.buildsConfiguration(posDiscrTerms, negDiscrTerms,
		// ReducedLearnerGAMainClass.getCategory());
		// System.out.println(bastardo.toString());
		// System.out.println(fittestConfig.toString());
		// }
		// System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		return pop1;
	}

	private Population createInitialPopulation(GAParameterSet params,
			double probability) {
		Population pop = createEmptyPopulation(params);
		while (pop.getSize() < params.getPopulationSize()) {
			// Individual ind;
			// FIXME: verificare il funzionamento del metodo
			// createEmptyIndividual
			// ind = ((ClassifierIndividualFactory)
			// params.getIndividualsFactory()).createEmptyIndividual(params);
			// pop.add(ind);
		}
		ClassifierSimpleBinaryMutation initMutation = new ClassifierSimpleBinaryMutation(
				probability);
		Individual[] generatedInd = initMutation.init(pop.getAllMembers(),
				params, probability);

		Population pop1 = createEmptyPopulation(params);
		pop1.addAll(generatedInd);

		return pop1;

	}

	protected Population createEmptyPopulation(GAParameterSet params) {
		Population pop = new SimpleCollectionOfIndividuals();
		return pop;
	}

	protected boolean terminationConditionApplies(Population pop, int genNum,
			GAResult result, GAParameterSet params) {
		return genNum >= params.getMaxGenerationNumber();
	}

	protected Individual[] selectForReproduction(Population pop, int age,
			GAParameterSet params) {

		int selCount = params.getReproductionAlgorithm()
				.getRequiredNumberOfParents();
		if (0 > selCount)
			selCount = 2;

		SelectionAlgorithm selector = params.getSelectionAlgorithm();
		Individual[] selection = selector.select(pop, selCount, age, params);

		return selection;
	}

	protected Individual[] haveSex(Individual[] parents, GAParameterSet params) {
		ReproductionAlgorithm cosyPlace = params.getReproductionAlgorithm();
		Individual[] oops = cosyPlace.reproduce(parents, params);
		return oops;
	}

	protected void updateIndividualFitness(Individual indiv, Population pop,
			int genNum, GAParameterSet params) {

		FitnessEvaluationAlgorithm tester = params
				.getFitnessEvaluationAlgorithm();
		Fitness fitness = tester.evaluateFitness(indiv, genNum, pop, params);
		indiv.setFitness(fitness);

		updateFitnessCalculated(indiv, pop, genNum, params);
		// System.out.println("Individuo: " +
		// ((ClassifierIndividual)indiv).toString());
	}

	public boolean addHook(SimpleGAHook hook) {
		if (null == hook)
			return false;
		else if (null == this.hooks)
			this.hooks = new ArrayList<SimpleGAHook>();
		else if (this.hooks.contains(hook))
			return false;
		this.hooks.add(hook);
		return true;
	}

	public boolean removeHook(SimpleGAHook hook) {
		if (null == hook)
			return false;
		else if (null == this.hooks)
			return false;
		else if (!this.hooks.contains(hook))
			return false;
		this.hooks.remove(hook);
		if (0 == this.hooks.size())
			this.hooks = null;
		return true;
	}

	protected void notifyInitialisationDone(Population pop, int age,
			GAResult result, GAParameterSet params) {
		if (!params.getUseMainAlgorithmHooks())
			return;
		if (null == hooks)
			return;
		for (Iterator<SimpleGAHook> hook = hooks.iterator(); hook.hasNext(); ((SimpleGAHook) hook
				.next()).initialisationDone(this, pop, age, result, params))
			;

		// //////////
		Individual[] allIndividuals = pop.getAllMembers();
		Arrays.sort(allIndividuals, new AbsoluteFitnessIndividualComparator());

		// for (int i = 0; i < allIndividuals.length; i++) {
//		Individual indiv1 = allIndividuals[allIndividuals.length - 2];
//		Individual indiv2 = allIndividuals[allIndividuals.length - 1];
		// System.out.println(((AbsoluteFitness) indiv.getFitness()).getValue()
		// + "--- fit");
		// LearnerManager.p.print("\tFit: " + ((AbsoluteFitness)
		// indiv1.getFitness()).getValue());
		// LearnerManager.p.print("\tFit: " + ((AbsoluteFitness)
		// indiv2.getFitness()).getValue());

		// }
	}

	protected void notifyFoundNewResult(Population pop, int age,
			GAResult result, GAParameterSet params) {
		if (!params.getUseMainAlgorithmHooks())
			return;
		if (null == hooks)
			return;
		for (Iterator<SimpleGAHook> hook = hooks.iterator(); hook.hasNext(); ((SimpleGAHook) hook
				.next()).foundNewResult(this, pop, age, result, params))
			;
	}

	protected void notifyGenerationChanged(Population pop, int age,
			GAResult result, GAParameterSet params) {
		// System.out.println("notifyGenerationChanged: " + age);
		if (!params.getUseMainAlgorithmHooks()) {
			return;
		}

		if (null == hooks) {
			return;
		}
		for (Iterator<SimpleGAHook> hook = hooks.iterator(); hook.hasNext();) {
			SimpleGAHook sgah = ((SimpleGAHook) hook.next());
			sgah.generationChanged(this, pop, age, result, params);
		}
	}

	protected void notifyTerminationConditionApplies(Population pop, int age,
			GAResult result, GAParameterSet params) {
		if (!params.getUseMainAlgorithmHooks())
			return;
		if (null == hooks)
			return;
		for (Iterator<SimpleGAHook> hook = hooks.iterator(); hook.hasNext(); ((SimpleGAHook) hook
				.next()).terminationConditionApplies(this, pop, age, result,
				params))
			;
	}

	protected void notifySelectedForReproduction(Individual[] selectedParents,
			Population pop, int age, GAResult result, GAParameterSet params) {
		if (!params.getUseMainAlgorithmHooks())
			return;
		if (null == hooks)
			return;
		for (Iterator<SimpleGAHook> hook = hooks.iterator(); hook.hasNext(); ((SimpleGAHook) hook
				.next()).selectedForReproduction(this, selectedParents, pop,
				age, result, params))
			;
	}

	protected void notifyReproduced(Individual[] children,
			Individual[] parents, Population pop, int age, GAResult result,
			GAParameterSet params) {
		if (!params.getUseMainAlgorithmHooks())
			return;
		if (null == hooks)
			return;
		for (Iterator<SimpleGAHook> hook = hooks.iterator(); hook.hasNext(); ((SimpleGAHook) hook
				.next()).reproduced(this, children, parents, pop, age, result,
				params))
			;
	}

	protected void updateFitnessCalculated(Individual updated, Population pop,
			int age, GAParameterSet params) {
		if (!params.getUseMainAlgorithmHooks())
			return;
		if (null == hooks)
			return;
		for (Iterator<SimpleGAHook> hook = hooks.iterator(); hook.hasNext(); ((SimpleGAHook) hook
				.next()).fitnessCalculated(this, updated, pop, age, params))
			;
	}

	public ClassifierIndividual getBestDTIndividual() {
		return bestDTIndividual;
	}

	public void setBestDTIndividual(ClassifierIndividual bestDTIndividual) {
		this.bestDTIndividual = bestDTIndividual;
	}

	public int getGaExecutionType() {
		return gaExecutionType;
	}

	public void setGaExecutionType(int gaExecutionType) {
		this.gaExecutionType = gaExecutionType;
	}
}