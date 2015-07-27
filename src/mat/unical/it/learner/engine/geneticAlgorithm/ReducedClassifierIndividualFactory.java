package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.definitions.*;
import org.jaga.individualRepresentation.greycodedNumbers.RangeConstraint;
import org.jaga.util.*;

public class ReducedClassifierIndividualFactory extends ClassifierIndividualFactory {

    public static boolean emptyNegativeTerms = true;
    public static boolean randomPositiveTerms = true;
    private int positiveTermsSize = -1;
    
    private int negativeTermsSize = -1;
    

    public ReducedClassifierIndividualFactory() {}

    public ReducedClassifierIndividualFactory(int positiveTermsSize, int negativeTermsSize) {
	setIndividualSize(positiveTermsSize + negativeTermsSize);
	setDecimalScale(0);
	setPrecision(1);
	setPositiveTermsSize(positiveTermsSize);
	setNegativeTermsSize(negativeTermsSize);
    }

    /**
     * Checks if the values of the specified individual are in the ranges
     * specified by constraints of this factory.
     * 
     * @param indiv some individual
     * 
     * @return <code>true</code> if all values encoded by this individual are
     *         inside the ranges specified by the constrains applicable to this
     *         factory; <code>false</code> otherwise.
     */
    public boolean valid(ReducedClassifierIndividual indiv) {

	// check if individual's settings match this factory:
	if (indiv.getDecimalScale() != this.getDecimalScale() || indiv.getSize() != this.getIndividualSize() || indiv.getPrecision() != this.getPrecision()) {
	    throw new IllegalArgumentException("The given individual (" + indiv + ") is likely not created by this factory.");
	}

	// check all values of the individual:
	for (int i = 0; i < indiv.getSize(); i++) {
	    double minmax[] = getAllowedRange(i);
	    double val = indiv.getDoubleValue(i);
	    if (val < minmax[0])
		return false;
	    if (val > minmax[1])
		return false;
	}
	return true;
    }

    public Individual createDefaultIndividual(GAParameterSet params) {
	System.out.println("DEFAULT CREATION");
	// Create a default individual:
	ReducedClassifierIndividual indiv =  new ReducedClassifierIndividual(positiveTermsSize, negativeTermsSize, getDecimalScale());

	// If some value is out of range, set it in the middle of range:
	for (int valInd = 0; valInd < getIndividualSize(); valInd++) {
	    double[] minmax = getAllowedRange(valInd);
	    if (indiv.getDoubleValue(valInd) < minmax[0] || minmax[1] < indiv.getDoubleValue(valInd)) {
		double val = 0.5 * minmax[0] + 0.5 * minmax[1];
		indiv.setDoubleValue(valInd, val);
	    }
	}
	return indiv;

    }

    public Individual createRandomIndividual(GAParameterSet params) {
	
	int size = getPositiveTermsSize();
	if (!emptyNegativeTerms) {
	    size = getIndividualSize();
	}
	// create an individual:
	ReducedClassifierIndividual indiv = new ReducedClassifierIndividual(positiveTermsSize, negativeTermsSize, getDecimalScale());
	
	if (randomPositiveTerms) {
        	// set all values to uniformly distributed random values:
        	for (int valInd = 0; valInd < size; valInd++) {
        	    double[] minmax = getAllowedRange(valInd);
        	    double val = params.getRandomGenerator().nextDouble(minmax[0], minmax[1]);
        	    indiv.setDoubleValue(valInd, val);
        	}
	}
	else {
		//get the positive terms part
		BitString terms = new BitString(this.getIndividualSize());
		for (int bit = 0; bit < positiveTermsSize; bit++) {
		    terms.set(bit, true);
		}
		indiv.setBitStringRepresentation(terms);  
	}

	
	return indiv;
    }

    
    public Individual createSpecificIndividual(Object init, GAParameterSet params) {
//	System.out.println("SPECIFIC CREATION");
	if (null == init)
	    throw new NullPointerException("Initialisation value for NDecimalsIndividual my not be null");

	if (init instanceof ReducedClassifierIndividual)
	    return createSpecificIndividual((ReducedClassifierIndividual) init);

	if (init instanceof BitString)
	    return createSpecificIndividual((BitString) init);

	if (init instanceof BitString)
	    return createSpecificIndividual((BitString) init);

	if (init instanceof double[])
	    return createSpecificIndividual((double[]) init);

	throw new ClassCastException("Initialisation value for NDecimalsIndividual " + "must be of type BitString or Double (but is " + init.getClass() + ")");
    }

    public Individual createSpecificIndividual(ReducedClassifierIndividual initVal) {
//	System.out.println("SPECIFIC CREATION (COPY)");
	ReducedClassifierIndividual indiv = new ReducedClassifierIndividual(positiveTermsSize, negativeTermsSize, getDecimalScale());
	indiv.setBitStringRepresentation(initVal.getBitStringRepresentation());
	return indiv;
    }

    public Individual createSpecificIndividual(BitString initVal) {
//	System.out.println("SPECIFIC CREATION (Bitstring)");
	ReducedClassifierIndividual indiv = new ReducedClassifierIndividual(positiveTermsSize, negativeTermsSize, getDecimalScale());
	indiv.setBitStringRepresentation(initVal);
	return indiv;
    }

    public Individual createSpecificIndividual(double[] initVal) {
//	System.out.println("SPECIFIC CREATION (vals)");
	ReducedClassifierIndividual indiv = new ReducedClassifierIndividual(positiveTermsSize, negativeTermsSize, getDecimalScale());
	for (int i = 0; i < getIndividualSize(); i++) {
	    indiv.setDoubleValue(i, initVal[i]);
	}
	return indiv;
    }

    private double[] getAllowedRange(int varInd) throws IllegalArgumentException {

	// use these if no constraint applies:
	final double veryLargePositive = 0.99 * (Long.MAX_VALUE / getDecimalScale());
	final double veryLargeNegative = 0.99 * (Long.MIN_VALUE / getDecimalScale());

	// get constraint:
	RangeConstraint constr = super.getConstraint(varInd);

	// return range:
	if (null == constr)
	    return new double[] { veryLargeNegative, veryLargePositive };

	return new double[] { Math.max(constr.getMinValue(), veryLargeNegative), Math.min(constr.getMaxValue(), veryLargePositive) };
    }

    public int getPositiveTermsSize() {
        return positiveTermsSize;
    }

    public void setPositiveTermsSize(int positiveTermsSize) {
        this.positiveTermsSize = positiveTermsSize;
    }

    public int getNegativeTermsSize() {
        return negativeTermsSize;
    }

    public void setNegativeTermsSize(int negativeTermsSize) {
        this.negativeTermsSize = negativeTermsSize;
    }

}