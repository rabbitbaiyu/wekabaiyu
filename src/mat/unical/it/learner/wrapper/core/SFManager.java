package mat.unical.it.learner.wrapper.core;

public class SFManager {

    public static final int CHI = 0;

    public static final int IG = 1;

    public static final int ODDS = 2;

    public static final int SF4 = 3;

    public static final int SF5 = 4;

    public static final int SF6 = 5;

    private static String[] scoringFunctions = new String[] { "Chi Square", "Information Gain", "Odds Ratio" };

    public static String[] getScoringFunctions() {
	return scoringFunctions;
    }

    public static int getScoringFunctionInternalIndex(String sfName) {
	// Chi square
	if (sfName.equals(scoringFunctions[0]))
	    return CHI;
	// Information gain
	if (sfName.equals(scoringFunctions[1]))
	    return IG;

	if (sfName.equals(scoringFunctions[2]))
	    return ODDS;

	return -1;
    }

    public static double computesFunctionValue(int A, int B, int C, int D, int numOfDOcs, int type) {
	switch (type) {
	    case ODDS:
		return calculateOR(A, B, C, D, numOfDOcs);
	    case IG:
		return calculateIG(A, B, C, D, numOfDOcs);
	    case CHI:
		return calculateChi_Square(A, B, C, D, numOfDOcs);
	    case SF5:
		return calculateIGMod(A, B, C, D, numOfDOcs);
	    default:
		return -1.0;
	}
    }

    private static double calculateChi_Square(int A, int B, int C, int D, int numOfDOcs) {
	double score = 0;
	// Veronica
	if (A != 0) {

	    score = numOfDOcs * Math.pow(A * D - B * C, 2);
	    score = score / ((A + B) * (A + C));

	    if (C + D != 0) {
		score = score / (C + D);
	    }
	    if (B + D != 0) {
		score = score / (B + D);
	    }
	    if (score < 0) {
		System.out.println("error");
	    }

	}
	return score;
    }

    private static double calculateIG(int A, int B, int C, int D, int numOfDOcs) {

	double score = 0;
	double N = numOfDOcs;

	if (A != 0) {
	    double tempFirst = (A + B) * (A + C);
	    score = (A / N) * log(2, (A * N) / (tempFirst));
	    /*******************************************************************
	     * questo permette di evitare 2 casi in cui la funzione seguente
	     * risulterebbe indefinita C/N log[ C * N / (C+D)*(A+C)] SE C + D =
	     * 0 (=> C = D = 0) si imposta C = 1 (=> C + D = 1)
	     */
	    double c = C;
	    if (c == 0) {
		c = 1;
	    }
	    double tempSecond = (A + c) * (c + D);
	    score += c / N * log(2, (c * N) / (tempSecond));
	}
	return score;
    }

    private static double calculateIGMod(int A, int B, int C, int D, int numOfDOcs) {

	double score = 0;
	double N = numOfDOcs;

	if (A != 0) {
	    double tempA = (A + C) * (A + B);
	    score = (A / N) * log(2, (A * N) / (tempA));
	}

	if (C != 0) {
	    double tempC = (A + C) * (C + D);
	    score += (C / N) * log(2, (C * N) / (tempC));
	}

	// I fattori B e D sono stati aggiunti in un secondo momento
	if (B != 0) {
	    double tempB = (B + D) * (A + B);
	    score += (B / N) * log(2, (B * N) / (tempB));
	}

	if (D != 0) {
	    double tempD = (B + D) * (C + D);
	    score += (D / N) * log(2, (D * N) / (tempD));
	}
	return score;
    }

    private static double calculateOR(int A, int B, int C, int D, int numOfDOcs) {
	double score = 0;

	if (A != 0) {
	    score = (numOfDOcs - B) * A;
	    if (numOfDOcs - A != 0) {
		score = (double) score / (numOfDOcs - A);
	    }
	    if (B != 0) {
		score = score / B;
	    }
	    /**
	     * we dont want to deal with infinity, t.B == 0 for many terms
	     */
	    if (score == 0) {
		// System.out.println(t.stem + " " + t.A + " " + t.B);
	    }
	}
	return score;
    }

//    private static double calculateIGcomplete(int termA, int termB, int termC, int termD, int numOfDOcs) {
//
//	double score = 0;
//	double N = numOfDOcs;
//
//	int A = (termA > 0 ? termA : 1);
//	double tempFirst = (A + termB) * (A + termC);
//	score = (A / N) * log(2, (A * N) / (tempFirst));
//
//	int C = (termC > 0 ? termC : 1);
//
//	double tempSecond = (termA + C) * (C + termD);
//	score += C / N * log(2, (C * N) / (tempSecond));
//
//	int B = (termB > 0 ? termB : 1);
//
//	double tempThird = (termA + B) * (B + termD);
//	score += B / N * log(2, (B * N) / (tempThird));
//
//	int D = (termD > 0 ? termD : 1);
//
//	double tempForth = (D + termB) * (termC + D);
//	score += D / N * log(2, (D * N) / (tempForth));
//
//	return score;
//    }
//
//    private static double calculateIGcompleteWithSign(int A, int B, int C, int D, int numOfDOcs) {
//
//	return Math.signum(A * D - B * C) * calculateIGMod(A, B, C, D, numOfDOcs);
//    }
//
//    private static double calculateORlog(int A, int B, int C, int D, int numOfDOcs) {
//
//	double or = calculateOR(A, B, C, D, numOfDOcs);
//
//	if (or == 0) {
//	    return Double.MIN_VALUE;
//	}
//	return log(2, or);
//    }

    private static double log(int b, double x) {

	double log = Math.log(x) / Math.log(b);
	return log;
    }

//    public static void main(String[] args) {
//	int[] A = new int[] { 1, 1, 2, 1, 3, 1, 2, 1, 2, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 14, 1, 1, 9, 5, 1, 1, 1, 1, 1, 1, 10, 1, 1, 5, 2, 2, 2, 1, 1, 1, 3 };
//	int[] B = new int[] { 0, 0, 3, 0, 0, 0, 2, 0, 0, 1, 0, 2, 0, 0, 0, 1, 0, 0, 0, 11, 0, 0, 4, 0, 0, 0, 0, 2, 0, 0, 7, 0, 0, 1, 1, 0, 1, 2, 0, 0, 0 };
//	int[] C = new int[] { 16, 16, 15, 16, 14, 16, 15, 16, 15, 16, 16, 14, 16, 16, 16, 16, 16, 16, 16, 3, 16, 16, 8, 12, 16, 16, 16, 16, 16, 16, 7, 16, 16, 12, 15, 15, 15, 16, 16, 16, 14 };
//	int[] D = new int[] { 12, 12, 9, 12, 12, 12, 10, 12, 12, 11, 12, 10, 12, 12, 12, 11, 12, 12, 12, 1, 12, 12, 8, 12, 12, 12, 12, 10, 12, 12, 5, 12, 12, 11, 11, 12, 11, 10, 12, 12, 12 };
//
//	for (int i = 0; i < D.length; i++) {
//	    int numOfDOcs = A[i] + B[i] + C[i] + D[i];
//	    System.out.println(calculateIG(A[i], B[i], C[i], D[i], numOfDOcs));
//	}
//
//	// System.out.println("CHI= "+ calculateChi_Square(A, B, C, D,
//	// numOfDOcs));
//	// System.out.println("    I_GAIN= " + calculateIG(A, B, C, D,
//	// numOfDOcs));
//	// System.out.println("       O_R= " + calculateOR(A, B, C, D,
//	// numOfDOcs));
//    }
}
