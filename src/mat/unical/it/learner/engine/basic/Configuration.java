package mat.unical.it.learner.engine.basic;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Represents a set of discriminative terms for a category. It consists of two
 * subsets of terms (positive and negative) and value of precision, recall and
 * F-measure are given.
 */
public class Configuration implements Comparable<Object>, java.lang.Cloneable, java.io.Serializable {

	/**
	 * The category name
	 */
	private Category category;

	/**
	 * The F-measure value
	 */
	private double Fmeasure;

	/**
	 * The precision value
	 */
	private double precision;

	/**
	 * The recall value
	 */
	private double recall;

	private long learning_time = 0;

	/**
	 * The list of positive terms
	 */
	private TreeSet<TermConjunction> positiveTerms;

	/**
	 * The list of negative terms
	 */
	private TreeSet<TermConjunction> negativeTerms;

	/**
	 * Create a new Configuration for <code>category</code>.
	 * 
	 * @param category
	 *            the category for which to create an empty configuration
	 */
	public Configuration(Category category) {
		this(category, 0);
	}

	/**
	 * Create a new Configuration, using the given parameters
	 * 
	 * @param category
	 *            the category for which creating a new Configuration
	 * @param termSet
	 *            the initial set of terms
	 * @param Fvalue
	 *            the initial Fvalue
	 */
	public Configuration(Category category, double Fvalue) {
		this.category = category;
		positiveTerms = new TreeSet<TermConjunction>();
		negativeTerms = new TreeSet<TermConjunction>();
		Fmeasure = Fvalue;
		precision = 0;
		recall = 0;
	}

	/**
	 * Get the learning time
	 * 
	 * @return learning_time
	 */
	public long getLearning_time() {

		return learning_time;
	}

	/**
	 * Set the learning time
	 * 
	 * @param learning_time
	 *            the learning time to set.
	 */
	public void setLearning_time(long learning_time) {

		this.learning_time = learning_time;
	}

	/**
	 * Return the term set of this configuration
	 * 
	 * @return terms
	 */
	public TreeSet<TermConjunction> getTerms(Sign sign) {

		if (sign.equals(Sign.POSITIVE))
			return positiveTerms;
		else if (sign.equals(Sign.NEGATIVE))
			return negativeTerms;
		else {
			TreeSet<TermConjunction> allTerms = new TreeSet<TermConjunction>();
			allTerms.addAll(positiveTerms);
			allTerms.addAll(negativeTerms);
			return allTerms;
		}

	}

	public Iterator<TermConjunction> getIteratorOnTerms(Sign sign) {

		if (sign.equals(Sign.POSITIVE))
			return positiveTerms.iterator();
		else if (sign.equals(Sign.NEGATIVE))
			return negativeTerms.iterator();
		else {
			TreeSet<TermConjunction> allTerms = new TreeSet<TermConjunction>();
			allTerms.addAll(positiveTerms);
			allTerms.addAll(negativeTerms);
			return allTerms.iterator();
		}

	}

	/**
	 * Return true if this configuration doesn't contain any term
	 * 
	 * @return a boolean indicating wheter the term set is empty
	 */
	public boolean isEmpty() {
		return (getPositiveTerms().isEmpty() && getNegativeTerms().isEmpty());
	}

	/**
	 * Return the number of terms in the configuration
	 * 
	 * @return the number of terms in the configuration.
	 */
	public int getSize() {
		return getPositiveTerms().size() + getNegativeTerms().size();
	}

	/**
	 * Add a new term in this configuration, with its sign
	 * 
	 * @param term
	 * @param value
	 */
	public void addTerm(TermConjunction term) {
		if (term.getSign().equals(Sign.POSITIVE)) {
			positiveTerms.add(term);
		} else if (term.getSign().equals(Sign.NEGATIVE)) {
			negativeTerms.add(term);
		} else {
			System.err.println("ERROR : cannot add a term without sign");

		}
	}

	/**
	 * set the Fvalue of this configuration
	 * 
	 * @param function
	 *            the fvalue to set
	 */
	public void setFmeasure(double function) {
		Fmeasure = function;
	}

	/**
	 * Get the Fvalue of this configuration
	 * 
	 * @return the Fvalue of this configuration
	 */
	public double getFmeasure() {
		return Fmeasure;
	}

	/**
	 * Indicate whether a term belong to this configuration.
	 * 
	 * @param t
	 *            The term to look for in thic configuration
	 * @return a boolean indicating whether a term belong to this configuration
	 */
	public boolean contains(TermConjunction tc) {

		return getTerms(tc.getSign()).contains(tc);
	}

	/**
	 * @return the negative terms in this configuration
	 */
	public Vector<TermConjunction> getNegativeTerms() {

		return new Vector<TermConjunction>(negativeTerms);
	}

	/**
	 * @return the negative terms in this configuration in string representation
	 */
	public String getNegativeTermsInString() {

		StringBuffer negTerms = new StringBuffer("");

		for (Iterator<TermConjunction> it = getIteratorOnTerms(Sign.NEGATIVE); it
				.hasNext();) {
			TermConjunction term = (TermConjunction) it.next();
			negTerms.append(term.toString() + "\n");
		}
		return negTerms + "";
	}

	/**
	 * @return the positive terms in this configuration
	 */
	public Vector<TermConjunction> getPositiveTerms() {

		return new Vector<TermConjunction>(positiveTerms);
	}

	/**
	 * @return the positive terms in this configuration in string representation
	 */
	public String getPositiveTermsInString() {

		StringBuffer posTerms = new StringBuffer("");

		for (Iterator<TermConjunction> it = getIteratorOnTerms(Sign.POSITIVE); it
				.hasNext();) {
			TermConjunction term = (TermConjunction) it.next();
			posTerms.append(term.toString() + "\n");
		}
		return posTerms + "";
	}

	/**
	 * @return the string representation of this configuration
	 */
	public String toString() {
		StringBuffer string = new StringBuffer();
		string.append(setToString(this.getTerms(Sign.UNDEFINED)));
//		string.append("\nIntersezione ");
//		string.append("negativa");

		string.append("\nFmeasure " + this.getFmeasure() + "\n");
		return string.toString();
	}

	/**
	 * @param selectedTerms
	 * @return
	 */
	private static String setToString(TreeSet<TermConjunction> set) {
		StringBuffer s = new StringBuffer();
		s.append("{");
		for (Iterator<TermConjunction> it = set.iterator(); it.hasNext();) {
			TermConjunction termConj = (TermConjunction) it.next();
			s.append(termConj.toString() + termConj.getSign());

			if (it.hasNext()) {
				s.append(", ");
			}
		}
		s.append("}");

		return s.toString();
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {

		return category;
	}

	public boolean containsTermConjunctionAsPositive(
			TermConjunction termConjunction) {

		return getPositiveTerms().contains(termConjunction);

	}

	public boolean containsTermConjunctionAsNegative(
			TermConjunction termConjunction) {

		return getNegativeTerms().contains(termConjunction);

	}


	/**
	 * @param conjunction
	 */
	public void addTermConjunctionAsPositive(TermConjunction conjunction) {

		conjunction.setSign(Sign.POSITIVE);
		positiveTerms.add(conjunction);

	}

	/**
	 * @param conjunction
	 */
	public void addTermConjunctionAsNegative(TermConjunction conjunction) {

		conjunction.setSign(Sign.NEGATIVE);
		negativeTerms.add(conjunction);

	}

	public int compareTo(Object arg0) {

		if (!(arg0 instanceof Configuration)) {
			return -1;
		}
		Configuration c = (Configuration) arg0;
		if (!this.getCategory().getCategoryName().equals(
				c.getCategory().getCategoryName())) {
			return -1;
		}
		if (this.getFmeasure() > c.getFmeasure()) {
			return 1;
		}
		if (this.getFmeasure() == c.getFmeasure()) {
			return 0;

		}
		return -1;

	}

	// public static void main(String[] a) {
	// Configuration c = new Configuration(new Category("aaa", 1));
	// Configuration c1 = new Configuration(new Category("aaa", 1));
	//
	// Configuration[] cc = new Configuration[2];
	// cc[0] = c1;
	// cc[1] = c;
	//
	// c.setFmeasure(2);
	// c1.setFmeasure(3);
	//
	// Arrays.sort(cc);
	// System.out.println(cc[0]);
	// System.out.println(cc[1]);
	// }

	public void setCategory(Category category) {
		this.category = category;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

}
