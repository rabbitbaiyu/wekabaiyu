package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.selection.AbsoluteFitness;



public class ClassifierFitness extends AbsoluteFitness {

	private int truePositives = 0;
	private int trueNegatives = 0;
	private int falsePositives = 0;
	private int falseNegatives = 0;
	private double precision = 0.;
	private double recall = 0.;

	public ClassifierFitness(int truePositive, int trueNegative,
							 int falsePositive, int falseNegative,
							 double precision, double recall,
							 double fitnessValue) {
		super(fitnessValue);
		this.truePositives = truePositive;
		this.trueNegatives = trueNegative;
		this.falsePositives = falsePositive;
		this.falseNegatives = falseNegative;
		this.precision = precision;
		this.recall = recall;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append(getValue());
		s.append(" (TP=");
		s.append(this.truePositives);
		s.append(", TN=");
		s.append(this.trueNegatives);
		s.append(", FP=");
		s.append(this.falsePositives);
		s.append(", FN=");
		s.append(this.falseNegatives);
		s.append(", Sens=");
		s.append(this.precision);
		s.append(", Spec=");
		s.append(this.recall);
		s.append(", Quality=");
		s.append(this.precision + this.recall);
		s.append(")");
		return s.toString();
	}

	public int getFalseNegatives() {
		return falseNegatives;
	}

	public int getFalsePositives() {
		return falsePositives;
	}

	public int getTrueNegatives() {
		return trueNegatives;
	}

	public int getTruePositives() {
		return truePositives;
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