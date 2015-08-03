package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.util.BitString;

public class ReducedClassifierIndividual extends ClassifierIndividual {

    private int positiveTermsSize = -1;

    private int negativeTermsSize = -1;

    public ReducedClassifierIndividual(int posTSize, int negTSize, double decimalscale) {
	super(posTSize + negTSize, decimalscale, 1);
	this.positiveTermsSize = posTSize;
	this.negativeTermsSize = negTSize;
    }

    public String oldToString() {
	final int size = getSize();
	StringBuffer s = new StringBuffer("{size=");
	s.append(size);
	s.append("; repr=");
	s.append(getBitStringRepresentation().toString());
	s.append("; vals=(");
	for (int i = 0; i < size; i++) {
	    s.append(getDoubleValue(i));
	    if (i < size - 1)
		s.append(", ");
	    else
		s.append(")");
	}
	if (null == getFitness()) {
	    s.append("; fitness-unknown}");
	}
	else {
	    s.append("; fit=");
	    s.append(getFitness().toString());
	    s.append("}");
	}
	return s.toString();
    }

    public String toString() {
	StringBuffer s = new StringBuffer("{");
	s.append("Indiv=(");
	s.append(getBitStringRepresentation().toString());
	s.append(")");

	if (null == getFitness()) {
	    s.append("; fitness-unknown}");
	}
	else {
	    s.append("; fit=");
	    s.append(getFitness().toString());
	    s.append("}");
	}
	return s.toString();
    }

    public BitString getBitCode(int valueIndex) {
	if (valueIndex < 0 || valueIndex > getSize())
	    throw new IndexOutOfBoundsException("value index is " + valueIndex + ", but must be in [0, " + getSize() + "]");

	BitString valRep = getBitStringRepresentation().substring(valueIndex * getPositiveTermsSize(), getPositiveTermsSize() + valueIndex * getNegativeTermsSize());
	return valRep;
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
