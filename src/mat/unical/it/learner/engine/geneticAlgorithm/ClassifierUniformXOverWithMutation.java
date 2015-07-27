package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.reproduction.*;

public class ClassifierUniformXOverWithMutation extends CombinedReproductionAlgorithm {

    public ClassifierUniformXOverWithMutation() {
	super();
	insertReproductionAlgorithm(0, new ClassifierSimpleBinaryMutation());
	insertReproductionAlgorithm(1, new ClassifierUniformXOver());
    }

    public ClassifierUniformXOverWithMutation(double xOverProb, double mutProb) {
	super();
	insertReproductionAlgorithm(0, new ClassifierUniformXOver(xOverProb));
	insertReproductionAlgorithm(1, new ClassifierSimpleBinaryMutation(mutProb));
    }

    public void setXOverProbability(double xOverProb) {
	((ClassifierTwoPointXOver) getReproductionAlgorithm(0)).setXOverProbability(xOverProb);
    }

    public double getXOverProbability() {
	return ((ClassifierTwoPointXOver) getReproductionAlgorithm(0)).getXOverProbability();
    }

    public void setMutationProbability(double mutProb) {
	((ClassifierSimpleBinaryMutation) getReproductionAlgorithm(1)).setMutationProbability(mutProb);
    }

    public double getMutationProbability() {
	return ((ClassifierSimpleBinaryMutation) getReproductionAlgorithm(1)).getMutationProbability();
    }

}