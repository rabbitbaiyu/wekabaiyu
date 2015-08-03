package mat.unical.it.learner.engine.geneticAlgorithm;

public class GARepAlgoTypes {

    public static final int UNIFORM_XOVER_WITH_MUTATION = 0;

    public static final int TWO_POINT_XOVER_WIH_MUTATION = 1;

    public static final int BINARY_XOVER_WITH_MUTATION = 2;

    private static String[] reproductionAlgoritms = new String[] { 
	"Uniform xOver", 
	"Two point xOver", 
	"Binary xOver", 
	};

    public static String[] getRepAlgoritms() {
	return reproductionAlgoritms;
    }

    public static void setReproductionAlgoritms(String[] reproductionAlgoritms) {
	GARepAlgoTypes.reproductionAlgoritms = reproductionAlgoritms;
    }
}
