package mat.unical.it.learner.engine.geneticAlgorithm.drivenInitialization;


import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;

import org.jaga.util.BitString;

public class DocumentClassifierBuilder {

    public static List<BitString>  buildClassifiersFromDocuments(List<DiscriminativeTerm> reducedVocabulary, DocumentSet Tc){
	
	
	HashMap<Integer, BitString> documentClassifier = new HashMap<Integer, BitString>();
	int bitStringLen = reducedVocabulary.size();
	
	for ( int i = 0; i < reducedVocabulary.size(); i++) {
	    DiscriminativeTerm dt = reducedVocabulary.get(i);
	    BitSet documents = dt.getDocumentSet().getDocuments();
	    
	    for(int d = 0; d < documents.size(); d++) {
		if (!Tc.getDocuments().get(d)) 
		    continue;
		if (documents.get(d) ) {
		    if (documentClassifier.get(d) == null) {
			documentClassifier.put(d, new BitString(bitStringLen));
		    }
		    documentClassifier.get(d).set(i, true);
		}
	    }
	}
	
	int doublelen = bitStringLen*2;
	List<BitString> classifIndividuals = new LinkedList<BitString>();
	for (Iterator<Integer> it = documentClassifier.keySet().iterator(); it.hasNext(); ) {
	    BitString posClass = documentClassifier.get(it.next());
	    
	    
	    BitString classifier = new BitString(doublelen);
//	  System.out.println("------>classifier.len=" + classifier.getLength());  
	    for (int i = 0; i < bitStringLen; i++) {
		classifier.set(i, posClass.get(i));
		classifier.set(i + bitStringLen, !posClass.get(i));
	    }
	    classifIndividuals.add(classifier);
	}
	    
	return classifIndividuals;
	
	
    }
    
}
