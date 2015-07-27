package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.individualRepresentation.greycodedNumbers.NNumbersGreycodedIndivudual;
import org.jaga.util.*;

public class ClassifierIndividual extends NNumbersGreycodedIndivudual {

    private double decimalScale = 1000000.0; // 10^6 i.e., 6 digits

    public ClassifierIndividual(int size, double decimalScale, int precision) {
	super(size, precision);
	this.decimalScale = decimalScale;
    }

    public double getDecimalScale() {
	return decimalScale;
    }

    public double getDoubleValue(int valueIndex) {
	BitString clear = getClearBitCode(valueIndex);
	double f = 1.0;
	double val = 0.0;
	for (int i = getPrecision() - 1; i >= 1; i--, f *= 2.0)
	    val += (clear.get(i) ? 1.0 : 0.0) * f;
	if (clear.get(0))
	    val = -val;
	val /= decimalScale;

	// System.out.println("getDoubleValue");
	// System.out.println("valueIndex: " + valueIndex);
	// System.out.println("val: " + val + ", clear: " + clear);
	// System.out.println("Representation: " +
	// getBitStringRepresentation());
	return val;
    }

    public void setDoubleValue(int valueIndex, double value) {
	double setVal = Math.rint(value * decimalScale);
	if (setVal >= Long.MAX_VALUE || setVal <= Long.MIN_VALUE)
	    throw new IllegalArgumentException("Absolute decimal value too big");
	BitString clear = new BitString(getPrecision());
	clear.set(0, setVal < 0);
	setVal = Math.abs(setVal);
	double f = 1.0;
	for (int i = getPrecision() - 1; i >= 1; i--, f *= 2.0)
	    clear.set(i, (setVal % (2.0 * f) >= f));
	setClearBitCode(valueIndex, clear);
	// System.out.println("valueIndex: " + valueIndex);
	// System.out.println("Value: " + value);
	// System.out.println("setVal: " + setVal);
	// System.out.println("BitString: " + clear);
	// System.out.println("Representation: " +
	// getBitStringRepresentation());
    }

    public String toString() {
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
//
//    public BitString getBitCode(int valueIndex) {
//	if (valueIndex < 0 || valueIndex > getSize())
//	    throw new IndexOutOfBoundsException("value index is " + valueIndex + ", but must be in [0, " + getSize() + "]");
//	BitString valRep = getBitStringRepresentation().substring(valueIndex * getPrecision(), (valueIndex + 1) * getPrecision());
//	return valRep;
//    }


    public BitString getBitCode(int valueIndex) {
	if (valueIndex < 0 || valueIndex > getSize())
	    throw new IndexOutOfBoundsException("value index is " + valueIndex + ", but must be in [0, " + getSize() + "]");
	BitString valRep = getBitStringRepresentation().substring((valueIndex * getPrecision() * getSize())/2, ((valueIndex + 1) * getPrecision() * getSize())/2);
	return valRep;
    }

}