package mat.unical.it.learner.engine.basic;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class TermConjunction implements Comparable<Object>, java.lang.Cloneable, java.io.Serializable {

    /** The set of discriminative terms which the conjunction is composed by.*/
    TreeSet<DiscriminativeTerm> terms;

    /** The conjunction length */
    private int length;

    /** The related set of documents */
    private DocumentSet documentSet = null;
    
    private Sign sign = null;

    
    /**
     * 
     * Create a new TermConjunction
     * @param term
     */
    public TermConjunction(DiscriminativeTerm term, Sign sign) {
	
	terms = new TreeSet<DiscriminativeTerm>();
	terms.add(term);

	if (term.getDocumentSet() != null) {
	    documentSet = (DocumentSet) term.getDocumentSet();
	}
	length = 1;
	this.sign = sign;
    }

    
    /**
     * 
     * Create a new TermConjunction
     * @param tc
     */
    public TermConjunction(TermConjunction tc) {
	terms = new TreeSet<DiscriminativeTerm>();
	for (Iterator<DiscriminativeTerm> it = tc.terms.iterator(); it.hasNext();) {
	    terms.add(new DiscriminativeTerm(it.next()));
	}
	if (tc.getDocumentSet() != null) {
	    documentSet = (DocumentSet) tc.getDocumentSet();
	}
	length = tc.length;
	this.sign = tc.sign;
    }

    /**
     * 
     * Create a new TermConjunction
     */
    public TermConjunction() {
	terms = new TreeSet<DiscriminativeTerm>();
	length = 0;
	sign = Sign.UNDEFINED;
    }

    
    /**
     * 
     * @param dt
     */
    public void addTerm(DiscriminativeTerm dt) {
	if (terms == null) {
	    terms = new TreeSet<DiscriminativeTerm>();
	}
	terms.add(new DiscriminativeTerm(dt));
	if (documentSet == null && dt.getDocumentSet() != null) {
	    documentSet = (DocumentSet) dt.getDocumentSet();
	}
	else {
	    documentSet = documentSet.intersectionOf(dt.getDocumentSet());
	}
	length++;
    }



    
    /**
     * 
     */
    public boolean equals(Object o) {

	if (o instanceof TermConjunction) {
	    boolean write = false;
	    
	    TermConjunction tc = (TermConjunction) o;
	    

	    if (this.length != tc.length)
		return false;

	    Iterator<DiscriminativeTerm> it = terms.iterator();
	    Iterator<DiscriminativeTerm> it2 = tc.terms.iterator();
	    while (it.hasNext()) {
		DiscriminativeTerm dt = (DiscriminativeTerm) it.next();
		DiscriminativeTerm dt2 = (DiscriminativeTerm) it2.next();
		if (!dt.getTermValue().equals(dt2.getTermValue())) {
		    
		    if (write) {
			System.out.println("dt" + " -- " + dt.getTermToString() + "   ****   " + dt2.getTermToString() );
		    }
		    return false;
		}
	    }
	    return true;
	}
	return false;
    }

    
    
    public Sign getSign() {
        return sign;
    }


    public void setSign(Sign sign) {
        this.sign = sign;
    }


    /**
     * Get the documentSet
     * 
     * @return the documentSet
     */
    public DocumentSet getDocumentSet() {

	return this.documentSet;
    }

    /**
     * Set the documentSet
     * 
     * @param documentSet the documentSet to set
     */
    public void setDocumentSet(DocumentSet documentSet) {

	this.documentSet = documentSet;
    }

    
    
    //TODO: rendere indipendente dal metodo toString
    public int compareTo(Object obj) {


	if (obj instanceof TermConjunction) 
	    return (this.toString().compareTo(((TermConjunction) obj).toString()));
	return -1;
    }

    /**
     * @return
     */
    public boolean isValid() {

	for (Iterator<DiscriminativeTerm> it = terms.iterator(); it.hasNext();) {
	    DiscriminativeTerm dt = it.next();
	    if (!dt.isValid()) {
		return false;
	    }
	}
	return true;
    }

    /**
     * FIXME: attualmente stampa la versione lunga della congiunzione 
     * [es. onegram(....)twogram(....)]
     * 
     * @return
     */
    public String toString() {

	StringBuffer strBuff = new StringBuffer();

	for (Iterator<DiscriminativeTerm> it = terms.iterator(); it.hasNext();) {
	    DiscriminativeTerm dt = it.next();
	    strBuff.append(dt.getTermToString());
	}
	return strBuff.toString();
    }

    /**
     * Get the length
     * 
     * @return the length
     */
    public int getLength() {

	return this.length;
    }

    /**
     * Set the length
     * 
     * @param length the length to set
     */
    public void setLength(int length) {

	this.length = length;
    }

    /**
     * Get the terms
     * 
     * @return the terms
     */
    public Set<DiscriminativeTerm> getTerms() {

	return this.terms;
    }

//    /**
//     * Set the terms
//     * 
//     * @param terms the terms to set
//     */
//    public void setTerms(Set<DiscriminativeTerm> terms) {
//
//	for (Iterator<DiscriminativeTerm> it = terms.iterator(); it.hasNext();) {
//	    this.addTerm(it.next());
//	}
//    }

    
    /**
     * @return
     */
    public String getStringRepresentation() {

	StringBuffer strBuff = new StringBuffer();

	for (Iterator<DiscriminativeTerm> it = terms.iterator(); it.hasNext();) {
	    DiscriminativeTerm dt = it.next();
	    String tv = dt.getTermValue();
	    String tvNoQuotes = tv.replace("\"", "");
	    strBuff.append(tvNoQuotes);

	    if (it.hasNext()) {
		strBuff.append("_");
	    }
	}
	return "\"" + strBuff + "\"";

    }

    /**
     * @param dt
     * @return
     */
    public boolean contains(DiscriminativeTerm dt) {

	for (Iterator<DiscriminativeTerm> it = terms.iterator(); it.hasNext();) {
	    DiscriminativeTerm dtTemp = it.next();
	    if (dtTemp.equals(dt)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * 
     * @return
     */
    public String shortRepresentation() {

	StringBuffer st = new StringBuffer();

	for (Iterator<DiscriminativeTerm> it = terms.iterator(); it.hasNext();) {
	    DiscriminativeTerm dtTemp = it.next();
	    st.append(dtTemp.getTermValue() + " ");
	}
	return st.toString();
    }

}
