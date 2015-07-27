package mat.unical.it.learner.engine.geneticAlgorithm;


import java.util.LinkedList;
import java.util.List;

import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;
import mat.unical.it.learner.engine.exception.RunExperimentException;
import mat.unical.it.learner.engine.geneticAlgorithm.utils.GAUtil;
import mat.unical.it.learner.engine.rulesGeneration.ClassificationDetails;

import org.jaga.definitions.Fitness;
import org.jaga.definitions.FitnessEvaluationAlgorithm;
import org.jaga.definitions.GAParameterSet;
import org.jaga.definitions.Individual;
import org.jaga.definitions.Population;
import org.jaga.selection.AbsoluteFitness;

public class ReducedClassifierFitnessEvaluator implements
		FitnessEvaluationAlgorithm {

	public static double alphaValue = 0.5;

	/* The category training set Tc */
	private DocumentSet trainingSet;

	/* The reduced vocabulary */
	private List<DiscriminativeTerm> positiveReducedVocabulary = null;

	private List<DiscriminativeTerm> negativeReducedVocabulary = null;

	/* The reduced vocabulary */
	// private List<DiscriminativeTerm> reducedVocabulary = null;
	/**
     * 
     */
	public ReducedClassifierFitnessEvaluator() {
	}

	/**
	 * 
	 * @param trainingSet
	 *            DocumentSet
	 * @param reducedVocabulary
	 *            List<DiscriminativeTerm>
	 */
	public ReducedClassifierFitnessEvaluator(DocumentSet trainingSet,
			List<DiscriminativeTerm> positiveReducedVocabulary,
			List<DiscriminativeTerm> negativeReducedVocabulary) {
		this.trainingSet = trainingSet;
		this.positiveReducedVocabulary = positiveReducedVocabulary;
		this.negativeReducedVocabulary = negativeReducedVocabulary;
	}

	@SuppressWarnings("unchecked")
	public Class getApplicableClass() {
		return ClassifierIndividual.class;
	}

	public Fitness evaluateFitness(Individual individual, int age,
			Population population, GAParameterSet params) {
		ReducedClassifierIndividual indiv = (ReducedClassifierIndividual) individual;
		// System.out.println("idividuo: " +
		// indiv.getBitStringRepresentation().toString());
		// Il BitString che rappresenta i termini positivi dell'individuo
		List<DiscriminativeTerm> posDiscrTerms = new LinkedList<DiscriminativeTerm>();
		if (positiveReducedVocabulary.size() > 0)
			posDiscrTerms = GAUtil.extractTerms(indiv,
					positiveReducedVocabulary, 0);

		// System.out.println("+++++++++++++++++++++++++ Termini ottenuti ");
		// GAUtil.printReducedVocabulary(posDiscrTerms);
		List<DiscriminativeTerm> negDiscrTerms = new LinkedList<DiscriminativeTerm>();
		if (negativeReducedVocabulary.size() > 0)
			negDiscrTerms = GAUtil.extractTerms(indiv,
					negativeReducedVocabulary, 1);
		// System.out.println("-------------------------- Termini ottenuti ");
		// GAUtil.printReducedVocabulary(negDiscrTerms);
		DocumentSet ac = GAUtil.buildAc(posDiscrTerms, negDiscrTerms);

		if (ac == null) {
			if (!posDiscrTerms.isEmpty()) {
				System.err.println("\tPOS" + posDiscrTerms);
				System.err.println("\tNEG" + negDiscrTerms);
			}
			return new AbsoluteFitness(0.0);
		}

		// DocumentSet ac =new DocumentSet(6723);
		DocumentSet tpc = ac.intersectionOf(trainingSet);

		// FIXME: rendere parametrico l'ultimo campo
		double fitnessValue;
		Fitness fit = null;
		try {
			fitnessValue = ClassificationDetails.calculate_F(ac
					.getCardinality(), tpc.getCardinality(), trainingSet
					.getCardinality(), alphaValue);
			fit = new AbsoluteFitness(fitnessValue);
		} catch (RunExperimentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// GAUtil.buildsConfiguration(posDiscrTerms, negDiscrTerms);
		return fit;
	}

	public DocumentSet getTrainingSet() {
		return trainingSet;
	}

	public void setTrainingSet(DocumentSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	public List<DiscriminativeTerm> getPositiveReducedVocabulary() {
		return positiveReducedVocabulary;
	}

	public void setPositiveReducedVocabulary(
			List<DiscriminativeTerm> positiveReducedVocabulary) {
		this.positiveReducedVocabulary = positiveReducedVocabulary;
	}

	public List<DiscriminativeTerm> getNegativeReducedVocabulary() {
		return negativeReducedVocabulary;
	}

	public void setNegativeReducedVocabulary(
			List<DiscriminativeTerm> negativeReducedVocabulary) {
		this.negativeReducedVocabulary = negativeReducedVocabulary;
	}

	// public List<DiscriminativeTerm> getReducedVocabulary() {
	// return reducedVocabulary;
	// }
	//
	// public void setReducedVocabulary(List<DiscriminativeTerm>
	// reducedVocabulary) {
	// this.reducedVocabulary = reducedVocabulary;
	// }

}