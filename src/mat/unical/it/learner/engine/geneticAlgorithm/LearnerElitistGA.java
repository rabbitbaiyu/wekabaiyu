package mat.unical.it.learner.engine.geneticAlgorithm;


import java.util.Arrays;

import mat.unical.it.learner.engine.geneticAlgorithm.masterAlgorithm.SimpleGA;

import org.jaga.definitions.GAParameterSet;
import org.jaga.definitions.GAResult;
import org.jaga.definitions.Individual;
import org.jaga.definitions.IndividualsFactory;
import org.jaga.definitions.Population;
import org.jaga.selection.AbsoluteFitnessIndividualComparator;
import org.jaga.util.FittestIndividualResult;

public class LearnerElitistGA extends SimpleGA {

    private double eliteProportion = 0.2;// propotion

    private double badProportion = 0.05;
    
    private GAParameterSet parameters = null;

    public LearnerElitistGA() {}

    public LearnerElitistGA(double eliteProportion) {
	setEliteProportion(eliteProportion);
    }

    public LearnerElitistGA(double eliteProportion, double badProportion) {
	setEliteProportion(eliteProportion);
	setBadProportion(badProportion);
    }

    public LearnerElitistGA(GAParameterSet parameters, double eliteProportion) {
	setParameters(parameters);
	setEliteProportion(eliteProportion);
    }

    public LearnerElitistGA(GAParameterSet parameters, double eliteProportion, double badProportion) {
	setParameters(parameters);
	setEliteProportion(eliteProportion);
	setBadProportion(badProportion);
    }

    public GAParameterSet getParameters() {
        return parameters;
    }

    public void setParameters(GAParameterSet parameters) {
        this.parameters = parameters;
    }
    
    public double getEliteProportion() {
	return this.eliteProportion;
    }

    public void setEliteProportion(double eliteProportion) {
	if (eliteProportion < 0 || 1 < eliteProportion)
	    throw new IllegalArgumentException("Elite proportion must be in [0, 1]");
	this.eliteProportion = eliteProportion;
    }

    public double getBadProportion() {
	return this.badProportion;
    }

    public void setBadProportion(double badProportion) {
	if (badProportion < 0 || 1 < badProportion)
	    throw new IllegalArgumentException("Bad proportion must be in [0, 1]");
	this.badProportion = badProportion;
    }

    protected Population generateNextPopulation(Population oldPop, int age, GAResult result, GAParameterSet params) {

	FittestIndividualResult res = (FittestIndividualResult) result;
	Population newPop = createEmptyPopulation(params);
	final IndividualsFactory fact = params.getIndividualsFactory();

	// Cut bad:

	Individual[] pop = oldPop.getAllMembers();
	Arrays.sort(pop, new AbsoluteFitnessIndividualComparator());
	int cutSize = (int) ((double) pop.length * (1.0 - badProportion));
	Individual[] cutPop = new Individual[cutSize];
	System.arraycopy(pop, pop.length - cutSize, cutPop, 0, cutSize);
	// the population we want is cutPop
	// Copy elite:

	int eliteSize = (int) ((double) params.getPopulationSize() * getEliteProportion());
	int p = cutSize - 1;
	while (newPop.getSize() < eliteSize) {
	    Individual kid = fact.createSpecificIndividual(cutPop[p], params);
	    kid.setFitness(cutPop[p].getFitness());
	    newPop.add(kid);
	    if (--p < 0)
		p = cutSize - 1;
	}

	// Copy rest:
	
	while (newPop.getSize() < params.getPopulationSize()) {

	    Individual[] parents = selectForReproduction(oldPop, age, params);
	    notifySelectedForReproduction(parents, oldPop, age, result, params);

	    Individual[] children = haveSex(parents, params);
	    for (int i = 0; i < children.length; i++) {
		if (null != children[i].getFitness())
		    continue;
		
		updateIndividualFitness(children[i], oldPop, age, params);
		
		if (children[i].getFitness().isBetter(res.getBestFitness()))
		    res.setFittestIndividual(children[i]);
	    }

	    notifyReproduced(children, parents, oldPop, age, result, params);
	    newPop.addAll(children);
	}

	return newPop;
    }
    
    public GAResult exec() {
	if (null == getParameters())
	    throw new IllegalStateException("ReusableSimpleGA.exec() was called without " + "previously initialising the parameter set");
	return exec(getParameters());
    }
}
