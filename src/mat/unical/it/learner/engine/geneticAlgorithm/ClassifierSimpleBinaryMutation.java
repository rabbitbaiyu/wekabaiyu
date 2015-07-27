package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.definitions.GAParameterSet;
import org.jaga.definitions.Individual;
import org.jaga.definitions.RandomGenerator;
import org.jaga.individualRepresentation.greycodedNumbers.BinaryEncodedIndividual;
import org.jaga.reproduction.Mutation;
import org.jaga.util.BitString;

public class ClassifierSimpleBinaryMutation extends Mutation {

    @SuppressWarnings("unchecked")
    private static final Class applicableClass = BinaryEncodedIndividual.class;

    
    @SuppressWarnings("unchecked")
    public Class getApplicableClass() {
	return applicableClass;
    }

    public ClassifierSimpleBinaryMutation() {
	super();
    }

    public ClassifierSimpleBinaryMutation(double mutProb) {
	super(mutProb);
    }

    public Individual[] reproduce(Individual[] parents, GAParameterSet params) {
	final int kidsCount = parents.length;
	BinaryEncodedIndividual[] kids = new BinaryEncodedIndividual[kidsCount];
	final RandomGenerator rnd = params.getRandomGenerator();
	final double mutProb = getMutationProbability();
	final ClassifierIndividualFactory factory = (ClassifierIndividualFactory) params.getIndividualsFactory();

	for (int i = 0; i < kidsCount; i++) {

	    final int maxAttempts = params.getMaxBadReproductionAttempts();
	    int attempts = 0;
	    boolean kidIsValid = false;
	    do {

		if (!getApplicableClass().isInstance(parents[i]))
		    fireIllegalParentException(parents, i);

		BitString kidBits = (BitString) ((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation().clone();
		for (int b = 0; b < kidBits.getLength(); b++)
		    if (rnd.nextDouble() < mutProb)
			kidBits.flip(b);

		ClassifierIndividual tst = (ClassifierIndividual) factory.createSpecificIndividual(kidBits, params);
		kidIsValid = factory.valid(tst);

		if (kidIsValid)
		    kids[i] = tst;

		attempts++;
	    }
	    while (!kidIsValid && attempts <= maxAttempts);

	    if (!kidIsValid) {
		kids[i] = (BinaryEncodedIndividual) factory.createSpecificIndividual(((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation(), params);
		kids[i].setFitness(parents[i].getFitness());
	    }

	}

	return kids;
    }

    public Individual[] init(Individual[] parents, GAParameterSet params, double probability) {
	final int kidsCount = parents.length;
	BinaryEncodedIndividual[] kids = new BinaryEncodedIndividual[kidsCount];
	final RandomGenerator rnd = params.getRandomGenerator();
	final double mutProb = probability;
	final ClassifierIndividualFactory factory = (ClassifierIndividualFactory) params.getIndividualsFactory();

	for (int i = 0; i < kidsCount; i++) {

	    final int maxAttempts = params.getMaxBadReproductionAttempts();
	    int attempts = 0;
	    boolean kidIsValid = false;
	    do {

		if (!getApplicableClass().isInstance(parents[i]))
		    fireIllegalParentException(parents, i);

		BitString kidBits = (BitString) ((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation().clone();
		for (int b = 0; b < kidBits.getLength(); b++)
		    if (rnd.nextDouble() < mutProb)
			kidBits.flip(b);

		ClassifierIndividual tst = (ClassifierIndividual) factory.createSpecificIndividual(kidBits, params);
		kidIsValid = factory.valid(tst);

		if (kidIsValid){
		    kids[i] = tst;
		}
		attempts++;
	    }
	    
	    while (!kidIsValid && attempts <= maxAttempts);

	    if (!kidIsValid) {
		System.out.println("---------------------------------->>>>>>>>>>>>>< NO valid kids");
		kids[i] = (BinaryEncodedIndividual) factory.createSpecificIndividual(((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation(), params);
		kids[i].setFitness(parents[i].getFitness());
	    }

	}

	return kids;
    }

    private void fireIllegalParentException(Individual[] parents, int i) throws IllegalArgumentException {
	throw new IllegalArgumentException("SimpleBinaryMutation works " + "only on parents of type " + getApplicableClass() + ", but parent number " + i + " is of type " + parents[i].getClass().getName());
    }

}