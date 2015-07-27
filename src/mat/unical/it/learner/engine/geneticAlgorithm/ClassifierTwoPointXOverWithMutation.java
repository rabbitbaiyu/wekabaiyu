package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.reproduction.*;

public class ClassifierTwoPointXOverWithMutation extends CombinedReproductionAlgorithm {

    public ClassifierTwoPointXOverWithMutation() {
	super();
	insertReproductionAlgorithm(0, new ClassifierSimpleBinaryMutation());
	insertReproductionAlgorithm(1, new ClassifierTwoPointXOver());
    }

    public ClassifierTwoPointXOverWithMutation(double xOverProb, double mutProb) {
	super();
	insertReproductionAlgorithm(0, new ClassifierTwoPointXOver(xOverProb));
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