package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.definitions.*;
import org.jaga.util.*;
import org.jaga.individualRepresentation.greycodedNumbers.*;
import org.jaga.reproduction.*;

public class ClassifierUniformXOver extends XOver {

    @SuppressWarnings("unchecked")
    private static final Class applicableClass = BinaryEncodedIndividual.class;

    public ClassifierUniformXOver() {
	super();
    }

    public ClassifierUniformXOver(double xOverProb) {
	super(xOverProb);
    }

    @SuppressWarnings("unchecked")
    public Class getApplicableClass() {
	return applicableClass;
    }

    /**
     * @param parents Individual[]
     * @param params GAParameterSet
     */
    public Individual[] reproduce(Individual[] parents, GAParameterSet params) {
	if (parents.length != getRequiredNumberOfParents())
	    throw new IllegalArgumentException("Need " + getRequiredNumberOfParents() + " parents for reproduction (not " + parents.length + ")");

	// Get parents bitstrings:
	BitString p1 = ((BinaryEncodedIndividual) parents[0]).getBitStringRepresentation();
	BitString p2 = ((BinaryEncodedIndividual) parents[1]).getBitStringRepresentation();

	// x-over:
	final int maxAttempts = params.getMaxBadReproductionAttempts();

	int bitLenght = checkParentsTypeAndLength(parents);
	
	int attempts = 0;
	boolean kidsAreValid = false;
	do {
	    kidsAreValid = false;
	    // int posXOverPoint = rnd.nextInt(1, bitLen/2);
	    // int negXOverPoint = rnd.nextInt(bitLen/2+1, bitLen);
	    //
	    // // offspring bit strings:
	    // BitString c1 = new BitString(bitLen);
	    // BitString c2 = new BitString(bitLen);
	    //
	    // // copy before posxover-point:
	    // for (int i = 0; i < posXOverPoint; i++) {
	    // c1.set(i, p1.get(i));
	    // c2.set(i, p2.get(i));
	    // }
	    //
	    // // copy between posxover-point and negxover-point:
	    // for (int i = posXOverPoint; i < negXOverPoint; i++) {
	    // c1.set(i, p2.get(i));
	    // c2.set(i, p1.get(i));
	    // }
	    //
	    // // copy after negxover-point:
	    // for (int i = negXOverPoint; i < bitLen; i++) {
	    // c1.set(i, p1.get(i));
	    // c2.set(i, p2.get(i));
	    // }
	    // The first mask
	    BitString mask1 = ((BinaryEncodedIndividual) params.getIndividualsFactory().createRandomIndividual(params)).getBitStringRepresentation();
	    // The inverse mask
	    BitString mask2 = (BitString) mask1.clone();

	    for (int b = 0; b < mask2.getLength(); b++)
		mask2.flip(b);

	    
	    BitString offspring1 = new BitString(bitLenght);
	    BitString offspring2 = new BitString(bitLenght);
	    
	    for (int b = 0; b < mask1.getLength(); b++){
		if(mask1.get(b)){
		    offspring1.set(b, p1.get(b));
		}
		else{
		    offspring1.set(b, p2.get(b));
		}
	    }
	    
	    for (int b = 0; b < mask2.getLength(); b++){
		if(mask2.get(b)){
		    offspring2.set(b, p1.get(b));
		}
		else{
		    offspring2.set(b, p2.get(b));
		}
	    }
	    
	    // create children and check if children are valid:
	    ClassifierIndividual[] kids = createKidsFromEncoding(params, offspring1, offspring2);
	    kidsAreValid = kidsSatisfyConstraints(kids, params);

	    // return valid kids or have another attempts:
	    if (kidsAreValid)
		return kids;
	    else
		attempts++;

	}
	while (!kidsAreValid && attempts < maxAttempts);


	// all attempts failed:
	return makeCopyOfParents(parents, params);
    }

    protected Individual[] makeCopyOfParents(Individual[] parents, GAParameterSet params) {
	Individual[] kids = new Individual[parents.length];
	for (int i = 0; i < kids.length; i++) {
	    kids[i] = params.getIndividualsFactory().createSpecificIndividual(((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation(), params);
	    // kids[i] = (BinaryEncodedIndividual) ((BinaryEncodedIndividual)
	    // parents[i]).clone();
	    kids[i].setFitness(parents[i].getFitness());
	}
	return kids;
    }

    private int checkParentsTypeAndLength(Individual[] parents) throws IllegalArgumentException {
	// Now make sure that parents are of right type and equal length:
	int bitLen = -1;
	for (int i = 0; i < parents.length; i++) {
	    // Check type:
	    if (!getApplicableClass().isInstance(parents[i]))
		throw new IllegalArgumentException("SimpleBinaryXOver works only for " + getApplicableClass().getName() + ", but parent is " + parents[i].getClass().getName());
	    if (0 == i) // Remember bit len of first parent:
		bitLen = ((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation().getLength();
	    else // And compare it to the bit len of all other parents:
	    if (bitLen != ((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation().getLength())
		throw new IllegalArgumentException("SimpleBinaryXOver works only on " + getApplicableClass() + " of equal representation length (" + bitLen + "!=" + ((BinaryEncodedIndividual) parents[i]).getBitStringRepresentation().getLength());
	}
	return bitLen;
    }

    private boolean kidsSatisfyConstraints(ClassifierIndividual[] kids, GAParameterSet params) {
	ClassifierIndividualFactory fact = (ClassifierIndividualFactory) params.getIndividualsFactory();
	for (int i = 0; i < kids.length; i++)
	    if (!fact.valid(kids[i]))
		return false;
	return true;
    }

    private ClassifierIndividual[] createKidsFromEncoding(GAParameterSet params, BitString c1, BitString c2) {
	ClassifierIndividual[] kids = new ClassifierIndividual[getRequiredNumberOfParents()];
	kids[0] = (ClassifierIndividual) params.getIndividualsFactory().createSpecificIndividual(c1, params);
	kids[1] = (ClassifierIndividual) params.getIndividualsFactory().createSpecificIndividual(c2, params);
	return kids;
    }

}