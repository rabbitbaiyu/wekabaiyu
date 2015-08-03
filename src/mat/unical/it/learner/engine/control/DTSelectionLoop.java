package mat.unical.it.learner.engine.control;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import mat.unical.it.learner.engine.basic.Category;
import mat.unical.it.learner.engine.basic.Configuration;
import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;
import mat.unical.it.learner.engine.exception.RunExperimentException;
import mat.unical.it.learner.engine.geneticAlgorithm.GAInitPopulationAlgoTypes;
import mat.unical.it.learner.engine.geneticAlgorithm.ReducedLearnerGAMainClass;
import mat.unical.it.learner.engine.geneticAlgorithm.utils.GAUtil;
import mat.unical.it.learner.engine.optimization.ExperimentConfiguration;
import mat.unical.it.learner.engine.termSelection.VocabularyReducer;
import mat.unical.it.learner.wrapper.core.OlexGAparameters;

public class DTSelectionLoop {

	/**
	 * 
	 * @param experimentConfiguration
	 * @param runCategories
	 * @param f_measure_weigth
	 * @param max_conj_len
	 * @param ga_type
	 * @param negativeTermsNumber
	 * @param popSize
	 * @param iterations
	 * @return
	 * @throws RunExperimentException
	 */
	public static List<Configuration> loop(
			ExperimentConfiguration experimentConfiguration,
			Vector<Category> runCategories, double f_measure_weigth,
			Integer max_conj_len, int ga_type, int selectionType,
			int negativeTermsNumber, int popSize, int iterations, int attempts,
			int reproductionAlgo, int classForIRStatistics)
			throws RunExperimentException {

		List<Configuration> configurations = new Vector<Configuration>();

		for (int i = 0; i < runCategories.size(); i++) {
			Category category = (Category) runCategories.get(i);
			Configuration configuration = new Configuration(category);

			DocumentSet Tc = experimentConfiguration.getCategoryTc(category
					.getCategoryName());

			if ((null == Tc) || (classForIRStatistics != i)) {
				Configuration conf = new Configuration(category);
				configurations.add(conf);
				continue;
			}

			// Compute positive and negative candidates vocabularies.
			List<DiscriminativeTerm> posVocabulary = VocabularyReducer.vocabularies
					.get(category.getCategoryName());

			List<DiscriminativeTerm> posRed = null;

			if (posVocabulary != null)
				posRed = posVocabulary
						.subList(
								0,
								(posVocabulary.size() > OlexGAparameters.POSITIVE_TERMS_SIZE ? OlexGAparameters.POSITIVE_TERMS_SIZE
										: posVocabulary.size()));
			else {
				configurations.add(configuration);
				continue;
			}

			if (posRed.size() != 0) {
				Set<DiscriminativeTerm> wholeNegReducedVocabulary = new TreeSet<DiscriminativeTerm>();
				Enumeration<String> catsEnum = VocabularyReducer.vocabularies
						.keys();
				while (catsEnum.hasMoreElements()) {
					String cat = catsEnum.nextElement();
					if (cat.equals(category.getCategoryName())) {
						continue;
					}
					wholeNegReducedVocabulary
							.addAll(VocabularyReducer.vocabularies.get(cat));
				}

				List<DiscriminativeTerm> allNegatives = new Vector<DiscriminativeTerm>();
				allNegatives.addAll(wholeNegReducedVocabulary);

				boolean applyDisjointConstraint = false;

				List<DiscriminativeTerm> negReducedVocabulary = GAUtil
						.extractNegativeCandidatesWithLimit(allNegatives,
								posRed, Tc, negativeTermsNumber,
								applyDisjointConstraint);
				Vector<DiscriminativeTerm> ineedavector = new Vector<DiscriminativeTerm>();
				ineedavector.addAll(negReducedVocabulary);

				long lt = System.currentTimeMillis();
				System.out.println(category.getCategoryName() + ": + "
						+ posRed.size() + ", - " + ineedavector.size());

				long final_lt = 0;

				switch (ga_type) {
				case GAInitPopulationAlgoTypes.SPONTANEOUS_POPULATION_INIT:
				case GAInitPopulationAlgoTypes.IDEAL_CLASSIFICATION_DRIVEN_POPULATION_INIT:
					Configuration temp_conf = new Configuration(category);
					configuration = reducedGeneticAlgorithm(
							experimentConfiguration, runCategories, temp_conf,
							posRed, negReducedVocabulary, ga_type,
							selectionType, popSize, iterations, attempts,
							reproductionAlgo);
					break;
				default:
					break;
				}

				configurations.add(configuration);
				final_lt = (long) Math.ceil(((double) System
						.currentTimeMillis() - lt) / 1000);
				configuration.setLearning_time(final_lt);
			}
		}
		return configurations;
	}

	private static Configuration reducedGeneticAlgorithm(
			ExperimentConfiguration experimentConfiguration,
			Vector<Category> runCategories, Configuration dtSelectorConf,
			List<DiscriminativeTerm> posReducedVocabulary,
			List<DiscriminativeTerm> negReducedVocabulary, int gaType,
			int selectionType, int popSize, int iterations, int attempts,
			int reproductionAlgo) throws RunExperimentException {
		Category category = (Category) dtSelectorConf.getCategory();

		long lt = System.currentTimeMillis();
		DocumentSet Tc = experimentConfiguration.getCategoryTc(category
				.getCategoryName());

		// Creo un'istanza dell'algoritmo genetico per ogni categoria di
		// classificazione
		ReducedLearnerGAMainClass gaAlgorithm = new ReducedLearnerGAMainClass(
				gaType);
		// La categoria di apprendimento
		ReducedLearnerGAMainClass.setCategory(category);
		// Setto la configurazione ottenuta con il dtSelector
		gaAlgorithm.setDtSelectorConf(dtSelectorConf);
		// dimensione della popolazione deve essere resa parametrica
		gaAlgorithm.setPopulationSize(popSize);
		// numero di iterazioni deve essere parametrico
		gaAlgorithm.setIterations(iterations);
		// numero di tentativi
		gaAlgorithm.setAttempts(attempts);
		// Algoritmo di riproduzione
		gaAlgorithm.setReproductionAlgoType(reproductionAlgo);
		gaAlgorithm.setSelectionAlgoType(selectionType);

		// Setto la lista di termini positivi del vocabolario ridotto
		gaAlgorithm.setPositiveReducedVocabulary(posReducedVocabulary);
		gaAlgorithm.setNegativeReducedVocabulary(negReducedVocabulary);
		gaAlgorithm.setPosSize(posReducedVocabulary.size());
		gaAlgorithm.setNegSize(negReducedVocabulary.size());
		// Setto il parametro Tc
		gaAlgorithm.setTrainingSet(Tc);
		// Eseguo l'algoritmo genetico
		Configuration[] conf = gaAlgorithm.exec();

		Arrays.sort(conf);
		long final_lt = (long) Math
				.ceil(((double) System.currentTimeMillis() - lt) / 1000);

		conf[conf.length - 1].setLearning_time(final_lt);
		return conf[conf.length - 1];
	}

}
