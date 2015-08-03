package mat.unical.it.learner.engine.geneticAlgorithm;

import java.util.HashMap;

import org.jaga.definitions.FitnessEvaluationAlgorithm;
import org.jaga.definitions.GAParameterSet;
import org.jaga.definitions.IndividualsFactory;
import org.jaga.definitions.RandomGenerator;
import org.jaga.definitions.ReproductionAlgorithm;
import org.jaga.definitions.SelectionAlgorithm;
import org.jaga.selection.RouletteWheelSelection;
import org.jaga.util.DefaultRandomGenerator;

public class LearnerParameterSet implements GAParameterSet {

    private HashMap<String, Object> params = null;

    public static double xOverRate = 0.65;

    public static double mutationRate = 0.001;

    public LearnerParameterSet() {
	setDefaultvalues();
    }

    public String toString() {
	StringBuffer s = new StringBuffer("\n individualsFactory:         ");
	s.append(params.get("individualsFactory").getClass().getName());

	s.append("\n populationSize:             ");
	s.append(((Integer) params.get("populationSize")).intValue());

	s.append("\n maxGenerationNumber:        ");
	s.append(((Integer) params.get("maxGenerationNumber")).intValue());

	s.append("\n reproductionAlgorithm:      ");
	s.append(params.get("reproductionAlgorithm").getClass().getName());

	s.append("\n selectionAlgorithm:         ");
	s.append(params.get("selectionAlgorithm").getClass().getName());

	s.append("\n fitnessEvaluationAlgorithm: ");
	s.append(params.get("fitnessEvaluationAlgorithm").getClass().getName());

	s.append("\n maxBadReproductionAttempts: ");
	s.append(((Integer) params.get("maxBadReproductionAttempts")).intValue());

	s.append("\n randomGenerator:            ");
	s.append(params.get("randomGenerator").getClass().getName());

	s.append("\n useMainAlgorithmHooks:      ");
	s.append(((Boolean) params.get("useMainAlgorithmHooks")).booleanValue());

	s.append("\n}");
	return s.toString();
    }

    public void setDefaultvalues() {
	params = new HashMap<String, Object>();

	IndividualsFactory indFac = new ClassifierIndividualFactory();
	params.put("individualsFactory", indFac);

	Integer popSize = new Integer(100);
	params.put("populationSize", popSize);

	Integer maxPop = new Integer(500);
	params.put("maxGenerationNumber", maxPop);

//	System.out.println("XOVER RATE: " + xOverRate);
	ReproductionAlgorithm reprAlg = new ClassifierUniformXOverWithMutation(xOverRate, mutationRate);

	params.put("reproductionAlgorithm", reprAlg);

	SelectionAlgorithm selAlg = new RouletteWheelSelection();
	params.put("selectionAlgorithm", selAlg);

	FitnessEvaluationAlgorithm fitAlg = new ClassifierFitnessEvaluator();
	params.put("fitnessEvaluationAlgorithm", fitAlg);

	Integer mAtt = new Integer(15);
	params.put("maxBadReproductionAttempts", mAtt);

	RandomGenerator rnd = new DefaultRandomGenerator(System.currentTimeMillis());
	params.put("randomGenerator", rnd);

	Boolean useHook = new Boolean(true);
	params.put("useMainAlgorithmHooks", useHook);
    }

    protected Object fetchParameter(String paramName) {
	Object val = params.get(paramName);
	if (null == val)
	    throw new IllegalStateException("Trying to fetch a non-existing parameter \"" + paramName + "\"");
	return val;
    }

    public IndividualsFactory getIndividualsFactory() {
	Object val = fetchParameter("individualsFactory");
	return (IndividualsFactory) val;
    }

    public void setIndividualsFactory(IndividualsFactory val) {
	if (null == val)
	    throw new NullPointerException("GA paremeter value may not be null");
	params.put("individualsFactory", val);
    }

    public int getPopulationSize() {
	Object val = fetchParameter("populationSize");
	return ((Integer) val).intValue();
    }

    public void setPopulationSize(int val) {
	if (0 > val)
	    throw new NullPointerException("GA paremeter value may not be negative");
	Integer popSize = new Integer(val);
	params.put("populationSize", popSize);
//	System.out.println("POP SIZE: " + popSize);
    }

    public int getMaxGenerationNumber() {
	Object val = fetchParameter("maxGenerationNumber");
	return ((Integer) val).intValue();
    }

    public void setMaxGenerationNumber(int val) {
	if (0 > val)
	    throw new NullPointerException("GA paremeter value may not be negative");
	Integer maxPop = new Integer(val);
	params.put("maxGenerationNumber", maxPop);
    }

    public ReproductionAlgorithm getReproductionAlgorithm() {
	Object val = fetchParameter("reproductionAlgorithm");
	return (ReproductionAlgorithm) val;
    }

    public void setReproductionAlgorithm(ReproductionAlgorithm val) {
	if (null == val)
	    throw new NullPointerException("GA paremeter value may not be null");
	params.put("reproductionAlgorithm", val);
    }

    public SelectionAlgorithm getSelectionAlgorithm() {
	Object val = fetchParameter("selectionAlgorithm");
	return (SelectionAlgorithm) val;
    }

    public void setSelectionAlgorithm(SelectionAlgorithm val) {
	if (null == val)
	    throw new NullPointerException("GA paremeter value may not be null");
	params.put("selectionAlgorithm", val);
    }

    public FitnessEvaluationAlgorithm getFitnessEvaluationAlgorithm() {
	Object val = fetchParameter("fitnessEvaluationAlgorithm");
	return (FitnessEvaluationAlgorithm) val;
    }

    public void setFitnessEvaluationAlgorithm(FitnessEvaluationAlgorithm val) {
	if (null == val)
	    throw new NullPointerException("GA paremeter value may not be null");
	params.put("fitnessEvaluationAlgorithm", val);
    }

    public int getMaxBadReproductionAttempts() {
	Object val = fetchParameter("maxBadReproductionAttempts");
	return ((Integer) val).intValue();
    }

    public void setMaxBadReproductionAttempts(int val) {
	if (0 > val)
	    throw new NullPointerException("GA paremeter value may not be negative");
	Integer mAtt = new Integer(val);
	params.put("maxBadReproductionAttempts", mAtt);
    }

    public RandomGenerator getRandomGenerator() {
	Object val = fetchParameter("randomGenerator");
	return (RandomGenerator) val;
    }

    public void setRandomGenerator(RandomGenerator val) {
	if (null == val)
	    throw new NullPointerException("GA paremeter value may not be null");
	params.put("randomGenerator", val);
    }

    public boolean getUseMainAlgorithmHooks() {
	Object val = fetchParameter("useMainAlgorithmHooks");
	return ((Boolean) val).booleanValue();
    }

    public void setUseMainAlgorithmHooks(boolean val) {
	Boolean useHook = new Boolean(val);
	params.put("useMainAlgorithmHooks", useHook);
    }
}