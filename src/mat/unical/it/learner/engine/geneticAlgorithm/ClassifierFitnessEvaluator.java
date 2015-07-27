package mat.unical.it.learner.engine.geneticAlgorithm;


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

public class ClassifierFitnessEvaluator implements FitnessEvaluationAlgorithm {

	public static double alphaValue = 0.5;

	/* The category training set Tc */
	private DocumentSet trainingSet;

	/* The reduced vocabulary */
	private List<DiscriminativeTerm> reducedVocabulary;

	/**
     * 
     */
	public ClassifierFitnessEvaluator() {
	}

	/**
	 * 
	 * @param trainingSet
	 *            DocumentSet
	 * @param reducedVocabulary
	 *            List<DiscriminativeTerm>
	 */
	public ClassifierFitnessEvaluator(DocumentSet trainingSet,
			List<DiscriminativeTerm> reducedVocabulary) {
		this.trainingSet = trainingSet;
		this.reducedVocabulary = reducedVocabulary;
	}

	@SuppressWarnings("unchecked")
	public Class getApplicableClass() {
		return ClassifierIndividual.class;
	}

	public Fitness evaluateFitness(Individual individual, int age,
			Population population, GAParameterSet params) {
		ClassifierIndividual indiv = (ClassifierIndividual) individual;
		System.out.println("individuo: "
				+ indiv.getBitStringRepresentation().toString());
		// Il BitString che rappresenta i termini positivi dell'individuo
		List<DiscriminativeTerm> posDiscrTerms = GAUtil.extractTerms(indiv,
				reducedVocabulary, 0);

		// System.out.println("+++++++++++++++++++++++++ Termini ottenuti ");
		// GAUtil.printReducedVocabulary(posDiscrTerms);
		List<DiscriminativeTerm> negDiscrTerms = GAUtil.extractTerms(indiv,
				reducedVocabulary, 1);
		// System.out.println("-------------------------- Termini ottenuti ");
		// GAUtil.printReducedVocabulary(negDiscrTerms);
		DocumentSet ac = GAUtil.buildAc(posDiscrTerms, negDiscrTerms);

		if (ac == null)
			return new AbsoluteFitness(0.0);
		// DocumentSet ac =new DocumentSet(6723);
		DocumentSet tpc = ac.intersectionOf(trainingSet);
		// System.out.println("ac : " + ac.getCardinality());
		// System.out.println("tpc: " + tpc.getCardinality());
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

	public List<DiscriminativeTerm> getReducedVocabulary() {
		return reducedVocabulary;
	}

	public void setReducedVocabulary(List<DiscriminativeTerm> reducedVocabulary) {
		this.reducedVocabulary = reducedVocabulary;
	}

}