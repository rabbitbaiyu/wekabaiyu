package mat.unical.it.learner.engine.geneticAlgorithm.utils;


import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import mat.unical.it.learner.engine.basic.Category;
import mat.unical.it.learner.engine.basic.Configuration;
import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;
import mat.unical.it.learner.engine.basic.Sign;
import mat.unical.it.learner.engine.basic.TermConjunction;
import mat.unical.it.learner.engine.exception.RunExperimentException;
import mat.unical.it.learner.engine.geneticAlgorithm.ClassifierIndividual;
import mat.unical.it.learner.engine.geneticAlgorithm.ClassifierIndividualFactory;
import mat.unical.it.learner.engine.geneticAlgorithm.ReducedClassifierIndividual;
import mat.unical.it.learner.engine.geneticAlgorithm.ReducedClassifierIndividualFactory;
import mat.unical.it.learner.engine.rulesGeneration.ClassificationDetails;

import org.jaga.definitions.GAParameterSet;
import org.jaga.definitions.Individual;
import org.jaga.util.BitString;

public class GAUtil {

    /**
     * Convert a bitString to a list of Discriminative Terms. Assume that a
     * index correspondence exists between the bits in the bitString and the
     * terms of the vocabulary.
     * 
     * @param termsCode the input bitString
     * @param reducedVocabulary the running vocabulary. It may represent either
     *        the wole running vocabulary or a part of it (the positive Pos and
     *        the negative subsets Neg). It matches with the whole running
     *        vocabulary if the Pos and Neg are the same.
     * @return the List of terms set to 1 in the bitstring
     */
    public static List<DiscriminativeTerm> extractTerms(BitString termsCode, List<DiscriminativeTerm> reducedVocabulary) {

	List<DiscriminativeTerm> terms = new LinkedList<DiscriminativeTerm>();

	if (termsCode != null) {
	    // get all the terms in the reduced vocabulary whose index is set to
	    // 1
	    // in the bitString
	    for (int i = 0; i < termsCode.getLength(); i++) {
		if (termsCode.get(i)) {
		    terms.add(reducedVocabulary.get(i));
		}
	    }
	}
	return terms;
    }

    /**
     * Build the classification set, starting from a set of positive and
     * negative terms representing a classifier.
     * 
     * @param posDiscrTerms the positive terms of the running classifier
     * @param negDiscrTerms the negative terms of the running classifier
     * @return the classification set. It consists of all the documents of the
     *         training set, containing at least a term in posDiscrTerms and no
     *         terms in negDiscrTerms.
     */
    public static DocumentSet buildAc(List<DiscriminativeTerm> posDiscrTerms, List<DiscriminativeTerm> negDiscrTerms) {

	Vector<DocumentSet> posDocuments = new Vector<DocumentSet>();
	Vector<DocumentSet> negDocuments = new Vector<DocumentSet>();

	// get all document set of positive terms
	Iterator<DiscriminativeTerm> iter = posDiscrTerms.iterator();
	while (iter.hasNext()) {
	    DiscriminativeTerm dt = iter.next();
	    posDocuments.add(dt.getDocumentSet());
	}

	// get all document set of negative terms
	iter = negDiscrTerms.iterator();
	while (iter.hasNext()) {
	    DiscriminativeTerm dt = iter.next();
	    negDocuments.add(dt.getDocumentSet());
	}

	DocumentSet ac = null;
	try {
	    DocumentSet posDocSet = DocumentSet.makeUnion(posDocuments);
	    DocumentSet negDocSet = DocumentSet.makeUnion(negDocuments);
	    if (negDocSet == null) {
		negDocSet = new DocumentSet(posDocSet.getMaxSize());
	    }
	    // obtain ac as the complement of the set of positive documents
	    // minus the set of negative ones
	    ac = posDocSet.complementOf(negDocSet);
	} catch (RunExperimentException e) {
	    e.printStackTrace();
	    return null;
	} catch (Exception e) {
	    // NB: va in eccezione quando non vengono selezionati termini
	    // positivi
	    // e.printStackTrace();
	    return null;
	}

	return ac;
    }

    /*
     * FIXME: il metodo dovrebbe essere reso piu' sicuro per il caso di un
     * ReducedClassifier individual. Infatti, il metodo accetta in input
     * (individual, NEG, 0) e (individuale, POS, 1), mentre le uniche
     * combinazioni che non producono risultati sbagliati sono (individual, POS,
     * 0) e (individuale, NEG, 1)
     */
    /**
     * Convert the subset <i>index</i> of the classifier <i>individual</i> to a
     * list of Discriminative Terms.
     * 
     * @param individual the individual classifier
     * @param reducedVocabulary the running vocabulary
     * @param index
     * @return
     */
    public static List<DiscriminativeTerm> extractTerms(Individual individual, List<DiscriminativeTerm> reducedVocabulary, int index) {
	BitString termIndividual = null;

	if (individual instanceof ReducedClassifierIndividual) {
	    ReducedClassifierIndividual indiv = (ReducedClassifierIndividual) individual;
	    termIndividual = indiv.getBitCode(index);
	}
	else if (individual instanceof ClassifierIndividual) {
	    ClassifierIndividual indiv = (ClassifierIndividual) individual;
	    termIndividual = indiv.getBitCode(index);
	}

	List<DiscriminativeTerm> discriminativeTerms = GAUtil.extractTerms(termIndividual, reducedVocabulary);
	return discriminativeTerms;
    }

    /**
     * Generate the category <i>configuration</i>, containing the given terms.
     * 
     * @param posDiscrTerms the positive discriminative terms to add to the
     *        configuration
     * @param negDiscrTerms the negative discriminative terms to add to the
     *        configuration
     * @param category the category for which to build the configuration
     * @return the category <i>configuration</i>, containing all the given
     *         terms.
     */
    public static Configuration buildsConfiguration(List<DiscriminativeTerm> posDiscrTerms, List<DiscriminativeTerm> negDiscrTerms, Category category) {
	Configuration termConfig = new Configuration(category);

	Iterator<DiscriminativeTerm> iter = negDiscrTerms.iterator();
	while (iter.hasNext()) {
	    TermConjunction termConj = new TermConjunction();
	    DiscriminativeTerm negDTerm = iter.next();
	    termConj.addTerm(negDTerm);
	    termConj.setSign(Sign.NEGATIVE);
	    termConfig.addTerm(termConj);
	}

	iter = posDiscrTerms.iterator();
	while (iter.hasNext()) {
	    TermConjunction termConj = new TermConjunction();
	    DiscriminativeTerm posDTerm = iter.next();
	    termConj.addTerm(posDTerm);
	    termConj.setSign(Sign.POSITIVE);
	    // Aggiungo il termine positivo solo se non esiste gi� come
	    // negativo
	    if (!termConfig.contains(termConj)) {
		termConfig.addTerm(termConj);
	    }
	}
//	System.out.println("CLASSIFICATORE OTTENUTO\n" + "POS TERMS:" + termConfig.getPositiveTerms().size() + "\nNEG TERMS:" + termConfig.getNegativeTerms().size() + "\n");
	return termConfig;
    }

    /**
     * Remove from the given configuration all the ineffective negative terms. A
     * negative term is ineffective if its intesection with positive terms is
     * empty (since it has no effect on classification).
     * 
     * @param configuration the GA output configuration
     * @return a new configuration containing all positive terms in
     *         configuration and the sole negative terms whose intersection with
     *         positive ones is not empty
     */
    public static Configuration cleanConfiguration(Configuration configuration) {

	Configuration cleanCopy = new Configuration(configuration.getCategory());

	// add to cleanCopy all the positive terms
	Vector<DocumentSet> posTermDocSets = new Vector<DocumentSet>();
	for (int i = 0; i < configuration.getPositiveTerms().size(); i++) {
	    TermConjunction tconj = configuration.getPositiveTerms().get(i);
	    cleanCopy.addTermConjunctionAsPositive(tconj);
	    posTermDocSets.add(tconj.getDocumentSet());
	}

	DocumentSet thetaPlus = null;
	try {
	    thetaPlus = DocumentSet.makeUnion(posTermDocSets);
	    // add to cleanCopy all the negative terms whose intersection with
	    // thetaPlus is not empty
	    for (int i = 0; i < configuration.getNegativeTerms().size(); i++) {
		TermConjunction tconj = configuration.getNegativeTerms().get(i);
		if (!tconj.getDocumentSet().disjointWith(thetaPlus)) {
		    cleanCopy.addTermConjunctionAsNegative(tconj);
		}
	    }
	} catch (RunExperimentException e) {
	    e.printStackTrace();
	    System.out.println("Positive terms : " + configuration.getPositiveTerms().size() + "\tnegative terms : " + configuration.getNegativeTerms().size());
	    return configuration;
	}
//	System.out.println("----- " + (configuration.getNegativeTerms().size() - cleanCopy.getNegativeTerms().size()) + " NEGATIVE TERMS REMOVED\n");
//	System.out.println("CLASSIFICATORE OTTENUTO\n" + "POS TERMS:" + cleanCopy.getPositiveTerms().size() + "\nNEG TERMS:" + cleanCopy.getNegativeTerms().size() + "\n");
	return cleanCopy;
    }

    /**
     * 
     * @param posVocabulary
     * @param negVocabulary
     * @param configuration
     * @param params
     * @return
     */
    public static ClassifierIndividual buildsIndividual(List<DiscriminativeTerm> posVocabulary, List<DiscriminativeTerm> negVocabulary, Configuration configuration, GAParameterSet params) {
	// Lista dei termini positivi selezionati dall'ottimizzatore
	List<TermConjunction> posTerm = configuration.getPositiveTerms();
	// Lista dei termini negativi selezionati dall'ottimizzatore
	List<TermConjunction> negTerm = configuration.getNegativeTerms();

	ReducedClassifierIndividualFactory individualFactory = (ReducedClassifierIndividualFactory) params.getIndividualsFactory();
	ReducedClassifierIndividual indiv = new ReducedClassifierIndividual(individualFactory.getPositiveTermsSize(), individualFactory.getNegativeTermsSize(), individualFactory.getDecimalScale());

	BitString posIndivBitString = indiv.getBitCode(0);

	Iterator<TermConjunction> iter = posTerm.iterator();

	// printReducedVocabulary(negVocabulary);
	while (iter.hasNext()) {
	    TermConjunction tc = iter.next();
	    Iterator<DiscriminativeTerm> it = tc.getTerms().iterator();
	    DiscriminativeTerm dt = it.next();
	    int index = posVocabulary.indexOf(dt);
	    posIndivBitString.set(index, true);
	}

	BitString negIndivBitString = indiv.getBitCode(1);

	iter = negTerm.iterator();
	while (iter.hasNext()) {
	    TermConjunction tc = iter.next();
	    Iterator<DiscriminativeTerm> it = tc.getTerms().iterator();
	    DiscriminativeTerm dt = it.next();
	    int index = negVocabulary.indexOf(dt);
	    negIndivBitString.set(index, true);
	}

	BitString result = new BitString(individualFactory.getIndividualSize());
	result.set(0, individualFactory.getPositiveTermsSize(), posIndivBitString);
	result.set(individualFactory.getPositiveTermsSize(), individualFactory.getPositiveTermsSize() + individualFactory.getNegativeTermsSize(), negIndivBitString);

	indiv.setBitStringRepresentation(result);

//	List<DiscriminativeTerm> posDiscrTerms = GAUtil.extractTerms(indiv, ((ReducedClassifierFitnessEvaluator) params.getFitnessEvaluationAlgorithm()).getPositiveReducedVocabulary(), 0);
//	List<DiscriminativeTerm> negDiscrTerms = GAUtil.extractTerms(indiv, ((ReducedClassifierFitnessEvaluator) params.getFitnessEvaluationAlgorithm()).getNegativeReducedVocabulary(), 1);
//
//	Configuration fittestConfig = GAUtil.buildsConfiguration(posDiscrTerms, negDiscrTerms, ReducedLearnerGAMainClass.getCategory());
//	System.out.println(fittestConfig.toString());
	return indiv;
    }

    public static ClassifierIndividual buildsIndividual(List<DiscriminativeTerm> vocabulary, Configuration configuration, GAParameterSet params) {
	// Lista dei termini positivi selezionati dall'ottimizzatore
	List<TermConjunction> posTerm = configuration.getPositiveTerms();
	// Lista dei termini negativi selezionati dall'ottimizzatore
	List<TermConjunction> negTerm = configuration.getNegativeTerms();

	ClassifierIndividualFactory individualFactory = (ClassifierIndividualFactory) params.getIndividualsFactory();
	ClassifierIndividual indiv = new ClassifierIndividual(individualFactory.getIndividualSize(), individualFactory.getDecimalScale(), individualFactory.getPrecision());

	BitString posIndivBitString = indiv.getBitCode(0);

	Iterator<TermConjunction> iter = posTerm.iterator();
	while (iter.hasNext()) {
	    TermConjunction tc = iter.next();
	    Iterator<DiscriminativeTerm> it = tc.getTerms().iterator();
	    DiscriminativeTerm dt = it.next();
	    int index = vocabulary.indexOf(dt);
	    posIndivBitString.set(index, true);
	}

	BitString negIndivBitString = indiv.getBitCode(1);

	iter = negTerm.iterator();
	while (iter.hasNext()) {
	    TermConjunction tc = iter.next();
	    Iterator<DiscriminativeTerm> it = tc.getTerms().iterator();
	    DiscriminativeTerm dt = it.next();
	    int index = vocabulary.indexOf(dt);
	    negIndivBitString.set(index, true);
	}

	BitString result = new BitString(individualFactory.getIndividualSize());
	result.set(0, (individualFactory.getPrecision() * individualFactory.getIndividualSize()) / 2, posIndivBitString);
	result.set((individualFactory.getPrecision() * individualFactory.getIndividualSize()) / 2, individualFactory.getPrecision() * individualFactory.getIndividualSize(), negIndivBitString);
	indiv.setBitStringRepresentation(result);
	// System.out.println("bestDTIndividual: " +
	// indiv.getBitStringRepresentation());
	// System.out.println("********** START REDUCED VOCABULARY
	// ***************");
	// printReducedVocabulary(vocabulary);
	// System.out.println("********** STOP REDUCED VOCABULARY
	// ***************");
	// System.out.println("********** START + TERMS ***************");
	// printReducedVocabulary(posTerm);
	// System.out.println("********** STOP - TERMS ***************");
	// System.out.println("********** START - TERMS***************");
	// printReducedVocabulary(negTerm);
	// System.out.println("********** STOP - TERMS ***************");
	return indiv;
    }

//    public static void printReducedVocabulary(List reducedV) {
//	Iterator iter = reducedV.iterator();
//	while (iter.hasNext()) {
//	    Object dt = iter.next();
//	    if (dt instanceof DiscriminativeTerm) {
//		System.out.println("term: " + ((DiscriminativeTerm) dt).getTermValue());
//	    }
//	    else if (dt instanceof TermConjunction) {
//		Iterator<DiscriminativeTerm> it = ((TermConjunction) dt).getTerms().iterator();
//		DiscriminativeTerm dt1 = it.next();
//		System.out.println("term: " + ((TermConjunction) dt).getSign() + " " + dt1.getTermValue());
//	    }
//	}

//    }

    /**
     * Select the subset of <code>limit</code> term as negative candidates for
     * GA execution. A term is a negative candidates if it some constraints are
     * verified. First of all, it has to cover any document out of
     * <code>Tc</code> (the category training set). Then it has to show an high
     * score, computed as the number of documents that it covers in thetaPlus
     * (the set of documents containing any term of the positive reduced
     * vocabulary <code>posReducedVocabulary</code>). It
     * <code>applyDisjointConstraint</code> is true, this constraint is enforced
     * by selecting only the terms that have no intersection with
     * <code>Tc</code>
     * 
     * @param reducedVocabulary
     * @param posReducedVocabulary
     * @param Tc
     * @param limit
     * @param applyDisjointConstraint
     * @return
     */
    public static List<DiscriminativeTerm> extractNegativeCandidatesWithLimit(List<DiscriminativeTerm> reducedVocabulary, 
    		List<DiscriminativeTerm> posReducedVocabulary, DocumentSet Tc, int limit, boolean applyDisjointConstraint) {
	// computes the set of documents containing any positive term
	Vector<DocumentSet> positiveDocumentSet = new Vector<DocumentSet>();
	Iterator<DiscriminativeTerm> it = posReducedVocabulary.iterator();
	while (it.hasNext()) {
	    DiscriminativeTerm dt = it.next();
	    positiveDocumentSet.add(dt.getDocumentSet());
	}

	DocumentSet thetaPlus = null;
	try {
	    thetaPlus = DocumentSet.makeUnion(positiveDocumentSet);

	} catch (RunExperimentException e) {
	    e.printStackTrace();
	}

	// compute thetaPlusComplementTc (the set of documents containing any
	// positive term and not belonging to category c)
	DocumentSet thetaPlusComplementTc = thetaPlus.complementOf(Tc);

	List<DiscriminativeTerm> negativeCandidates = new Vector<DiscriminativeTerm>();
	it = reducedVocabulary.iterator();
	while (it.hasNext()) {
	    NegativeCandidate negCandidate = new NegativeCandidate(it.next());
	    // condition 1: terms do not appear in any document of category c
	    // (if applying disjoint constraint)
	    if (applyDisjointConstraint && !negCandidate.getDocumentSet().disjointWith(Tc))
		continue;
	    DocumentSet intersection = negCandidate.getDocumentSet().intersectionOf(thetaPlusComplementTc);
	    // condition 2: terms appear in any positive document!
	    if (!intersection.isEmpty()) {
		// set to this term its score, computed as the size of the
		// intersection with thetaPlusComplementTc
		negCandidate.setScore(intersection.getCardinality());
		negativeCandidates.add(negCandidate);
	    }
	}
	Collections.sort(negativeCandidates);

	if (limit == -1) {
	    limit = negativeCandidates.size();
	}

	// extract the subset of negative candidates
	List<DiscriminativeTerm> negReducedVocabulary = null;
	if (negativeCandidates.size() > limit) {
	    negReducedVocabulary = negativeCandidates.subList(0, limit);
	}
	else {
	    negReducedVocabulary = negativeCandidates;
	}
	return negReducedVocabulary;
    }

    // FIXME: la configurazione ha gi� i termini positivi e negativi
    /**
     * Compute the performances values of the configuration
     * <code>fittestConfiguration</code>.
     * 
     * @param fittestConfig the configuration for which computing performance
     *        values (precision, recall, f-measure)
     * @param posDiscrTerms the positive terms
     * @param negDiscrTerms the negative terms
     * @param tc the training set for this category
     */
    public static void setPerformanceValues(Configuration fittestConfig, List<DiscriminativeTerm> posDiscrTerms, List<DiscriminativeTerm> negDiscrTerms, DocumentSet tc) {

	double p = 0;
	double r = 0;
	try {
	    DocumentSet ac = buildAc(posDiscrTerms, negDiscrTerms);
	    DocumentSet tpc = tc.intersectionOf(ac);
	    
	    p = new ClassificationDetails().calculateP(tc, ac, tpc);
	    r = new ClassificationDetails().calculateR(tc, ac, tpc);
	} catch (NullPointerException e) {

	} finally {
	    fittestConfig.setPrecision(p);
	    fittestConfig.setRecall(r);
	}
    }
    
}
