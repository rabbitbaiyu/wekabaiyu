package mat.unical.it.learner.engine.rulesGeneration;

import mat.unical.it.learner.engine.basic.DocumentSet;
import mat.unical.it.learner.engine.exception.RunExperimentException;

public class ClassificationDetails {


	/**
	 * Calcola il valore della precisione per la classificazione relativa alla
	 * categoria concept
	 * 
	 * @param concept
	 * @param Tc
	 * @param Ac
	 */
	public double calculateP(DocumentSet Tc, DocumentSet Ac, DocumentSet TPc) {

		double precision = 0;

		if (Ac != null && Ac.getCardinality() != 0) {
			precision = (double) TPc.getCardinality() / Ac.getCardinality();
		}
		return precision;
	}

	public double calculateR(DocumentSet Tc, DocumentSet Ac, DocumentSet TPc) {

		double recall = 0;

		if (Tc != null && Tc.getCardinality() != 0) {
			recall = (double) TPc.getCardinality() / Tc.getCardinality();
		}

		return recall;
	}


	/**
	 * @param AcSize
	 * @param TPcSize
	 * @param TcSize
	 * @param alphaWeight
	 * @return
	 * @throws RunExperimentException
	 */
	public static double calculate_F(int AcSize, int TPcSize, int TcSize,
			double alphaWeight) throws RunExperimentException {

		double precision;
		double function = 0;
		if ((AcSize != 0) && (TcSize != 0)) {
			precision = (double) TPcSize / AcSize;
			double recall = (double) TPcSize / TcSize;
			if (precision != 0 || recall != 0) {

				function = precision
						* recall
						/ ((1 - alphaWeight) * precision + alphaWeight * recall);

				if (Double.isInfinite(function) || Double.isNaN(function)
						|| function > 1 || function < 0) {
					throw new RunExperimentException("F out of bounds "
							+ function);
				}

			}
		}
		return function;

	}

}
