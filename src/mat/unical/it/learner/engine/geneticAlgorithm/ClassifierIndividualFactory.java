package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.definitions.*;
import org.jaga.individualRepresentation.greycodedNumbers.RangeConstraint;
import org.jaga.util.*;

public class ClassifierIndividualFactory implements IndividualsFactory {

    private int individualSize = 1;

    private double decimalScale = 1000000; // 10^6 i.e., 6 digits

    private int precision = 64; // bits

    private RangeConstraint[] constraints = new RangeConstraint[] { null };

    public ClassifierIndividualFactory() {}

    public ClassifierIndividualFactory(int varsPerIndividual, int decPrecision, int representationLen) {
	setIndividualSize(varsPerIndividual);
	setDecimalScale(decPrecision);
	setPrecision(representationLen);
    }

    public double getDecimalScale() {
	// double scal = Math.log(decimalScale) / Math.log(10); // i.e. log(10,
	// scale)
	// return (byte) scal;
	return decimalScale;
    }

    public void setDecimalScale(int val) {
	if (val < 0 || 20 < val)
	    throw new IllegalArgumentException("Decimal scale (" + val + ") not supported, use 10^0 - 10^20");
	decimalScale = Math.pow(10, val);
    }

    public int getIndividualSize() {
	return individualSize;
    }

    public void setIndividualSize(int size) {
	RangeConstraint[] newConstraints = new RangeConstraint[size];

	for (int i = 0; i < Math.min(size, this.individualSize); i++)
	    newConstraints[i] = this.constraints[i];

	for (int i = Math.min(size, this.individualSize); i < size; i++)
	    newConstraints[i] = null;

	this.individualSize = size;
	this.constraints = newConstraints;
    }

    /**
     * Gets the constraint for individuals' variable with specified index.
     * 
     * @param variableIndex the index of the variable inside the individuals to
     *        which the constraint applies.
     * 
     * @return the constraint vor variables at the specified index.
     */
    public RangeConstraint getConstraint(int variableIndex) {
	return constraints[variableIndex];
    }

    /**
     * Sets the constraints for individuals' variable at the specified index.
     * 
     * @param variableIndex index of the variable to which the consteraint
     *        applies.
     * @param constraint the constraint.
     */
    public void setConstraint(int variableIndex, RangeConstraint constraint) {
	constraints[variableIndex] = constraint;
    }

    public int getPrecision() {
	return precision;
    }

    public void setPrecision(int val) {
	precision = val;
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
    public boolean valid(ClassifierIndividual indiv) {

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
	// Create a default individual:
	ClassifierIndividual indiv = new ClassifierIndividual(individualSize, decimalScale, precision);

	// If some value is out of range, set it in the middle of range:
	for (int valInd = 0; valInd < individualSize; valInd++) {
	    double[] minmax = getAllowedRange(valInd);
	    if (indiv.getDoubleValue(valInd) < minmax[0] || minmax[1] < indiv.getDoubleValue(valInd)) {
		double val = 0.5 * minmax[0] + 0.5 * minmax[1];
		indiv.setDoubleValue(valInd, val);
	    }
	}

	return indiv;
    }

    // public Individual createEmptyIndividual(GAParameterSet params) {
    // // Create a default individual:
    // ClassifierIndividual indiv = new ClassifierIndividual(individualSize,
    // decimalScale, precision);
    //
    // // If some value is out of range, set it in the middle of range:
    // // for (int valInd = 0; valInd < individualSize; valInd++) {
    // // indiv.setDoubleValue(valInd, 0.0);
    // // }
    //
    // return indiv;
    // }

    public Individual createRandomIndividual(GAParameterSet params) {
	// create an individual:
	ClassifierIndividual indiv = new ClassifierIndividual(individualSize, decimalScale, precision);
//	indiv.toString();
	// set all values to uniformly distributed random values:
	for (int valInd = 0; valInd < individualSize; valInd++) {
	    double[] minmax = getAllowedRange(valInd);
	    double val = params.getRandomGenerator().nextDouble(minmax[0], minmax[1]);
	    indiv.setDoubleValue(valInd, val);
	}
	return indiv;
    }

    public Individual createSpecificIndividual(Object init, GAParameterSet params) {

	if (null == init)
	    throw new NullPointerException("Initialisation value for NDecimalsIndividual my not be null");

	if (init instanceof ClassifierIndividual)
	    return createSpecificIndividual((ClassifierIndividual) init);

	if (init instanceof BitString)
	    return createSpecificIndividual((BitString) init);

	if (init instanceof BitString)
	    return createSpecificIndividual((BitString) init);

	if (init instanceof double[])
	    return createSpecificIndividual((double[]) init);

	throw new ClassCastException("Initialisation value for NDecimalsIndividual " + "must be of type BitString or Double (but is " + init.getClass() + ")");
    }

    public Individual createSpecificIndividual(ClassifierIndividual initVal) {
	ClassifierIndividual indiv = new ClassifierIndividual(individualSize, decimalScale, precision);
	indiv.setBitStringRepresentation(initVal.getBitStringRepresentation());
	return indiv;
    }

    public Individual createSpecificIndividual(BitString initVal) {
	ClassifierIndividual indiv = new ClassifierIndividual(individualSize, decimalScale, precision);
	indiv.setBitStringRepresentation(initVal);
	return indiv;
    }

    public Individual createSpecificIndividual(double[] initVal) {
	ClassifierIndividual indiv = new ClassifierIndividual(individualSize, decimalScale, precision);
	for (int i = 0; i < individualSize; i++) {
	    indiv.setDoubleValue(i, initVal[i]);
	}
	return indiv;
    }

    private double[] getAllowedRange(int varInd) throws IllegalArgumentException {

	// use these if no constraint applies:
	final double veryLargePositive = 0.99 * (Long.MAX_VALUE / decimalScale);
	final double veryLargeNegative = 0.99 * (Long.MIN_VALUE / decimalScale);

	// get constraint:
	RangeConstraint constr = constraints[varInd];

	// return range:
	if (null == constr)
	    return new double[] { veryLargeNegative, veryLargePositive };

	return new double[] { Math.max(constr.getMinValue(), veryLargeNegative), Math.min(constr.getMaxValue(), veryLargePositive) };
    }

}