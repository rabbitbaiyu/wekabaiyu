package mat.unical.it.learner.wrapper.core;


import java.util.Comparator;

import mat.unical.it.learner.engine.basic.DiscriminativeTerm;

public class ScoringFunctionTermsComparator  implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		
		if( (o1 instanceof DiscriminativeTerm) && (o2 instanceof DiscriminativeTerm)){
			DiscriminativeTerm t1 = (DiscriminativeTerm)o1;
			DiscriminativeTerm t2 = (DiscriminativeTerm)o2;
			
			if(t1.getScoreValue().compareTo(t2.getScoreValue()) == 0){
				return t1.getTermValue().compareTo(t2.getTermValue());
			}
			return 1-t1.getScoreValue().compareTo(t2.getScoreValue());
		}
		return 0;
	}

}
