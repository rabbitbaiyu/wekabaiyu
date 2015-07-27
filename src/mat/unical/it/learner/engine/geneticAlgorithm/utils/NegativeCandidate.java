package mat.unical.it.learner.engine.geneticAlgorithm.utils;

import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;

public class NegativeCandidate extends DiscriminativeTerm implements Comparable {

    
    public int score = 0;
    
    public NegativeCandidate(int i) {
	super(i);
    }

    public NegativeCandidate(int i, String n, String type, Integer ontoId, DocumentSet documentSet) {
	super(i, n, type, ontoId, documentSet);
    }

    public NegativeCandidate(int i, String n, String type, int length, Integer ontoId, DocumentSet documentSet) {
	super(i, n, type, length, ontoId, documentSet);
    }

    public NegativeCandidate(DiscriminativeTerm t) {
	super(t);
    
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    public int compareTo(Object o) {
	
	if (o instanceof NegativeCandidate) {
	    return ((NegativeCandidate)o).getScore() - this.getScore();
	}
	return -1;
    }
    
    
}
