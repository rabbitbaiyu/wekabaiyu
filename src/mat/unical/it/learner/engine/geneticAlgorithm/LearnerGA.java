package mat.unical.it.learner.engine.geneticAlgorithm;

import org.jaga.definitions.*;
import org.jaga.masterAlgorithm.SimpleGA;

public class LearnerGA extends SimpleGA {

    private GAParameterSet parameters = null;

    public LearnerGA() {
	super();
    }

    public LearnerGA(GAParameterSet parameters) {
	super();
	this.parameters = parameters;
    }

    public GAResult exec() {
	if (null == this.parameters)
	    throw new IllegalStateException("ReusableSimpleGA.exec() was called without " + "previously initialising the parameter set");
	return exec(this.parameters);
    }
}