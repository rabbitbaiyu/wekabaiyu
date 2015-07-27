package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.reproduction.*;

public class ClassifierBinaryXOverWithMutation extends CombinedReproductionAlgorithm {

	public ClassifierBinaryXOverWithMutation() {
		super();
		insertReproductionAlgorithm(0, new ClassifierSimpleBinaryMutation());
		insertReproductionAlgorithm(1, new ClassifierSimpleBinaryXOver());
	}

	public ClassifierBinaryXOverWithMutation(double xOverProb, double mutProb) {
		super();
		insertReproductionAlgorithm(0, new ClassifierSimpleBinaryXOver(xOverProb));
		insertReproductionAlgorithm(1, new ClassifierSimpleBinaryMutation(mutProb));
	}

	public void setXOverProbability(double xOverProb) {
		((ClassifierSimpleBinaryXOver) getReproductionAlgorithm(0)).setXOverProbability(xOverProb);
	}

	public double getXOverProbability() {
		return ((ClassifierSimpleBinaryXOver) getReproductionAlgorithm(0)).getXOverProbability();
	}

	public void setMutationProbability(double mutProb) {
		((ClassifierSimpleBinaryMutation) getReproductionAlgorithm(1)).setMutationProbability(mutProb);
	}

	public double getMutationProbability() {
		return ((ClassifierSimpleBinaryMutation) getReproductionAlgorithm(1)).getMutationProbability();
	}

}