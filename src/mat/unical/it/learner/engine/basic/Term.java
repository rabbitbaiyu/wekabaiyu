package mat.unical.it.learner.engine.basic;


import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Term implements Comparable {

    /** The stem sequence representing the term */
    String stem;

    /** The type of term */
    String type;

    /** The ontology identifier of a term (null if term type is not concept) */
    Integer ontologyId = null;

    /** The length of the term */
    int length = 0;

    /** The set of documents where the term appears */
    public String distribution = "";

    int A, B, C, D;

    /** The score of this term by suitable filtering function */
    double score = 0;

    /**
     * The average distance of the term in the documents of a category (0<=
     * ad <=1)
     */
    double ad = 0; // 

    /** The frequency of the term in the documents of of a category */
    int noApperance = 0;

    /**
     * The average frequency of the term in the documents of a category (0<=
     * ad <=1)
     */
    double avgnoApps = 0;


    String listDocument ="";
    
    /**
     * Create a new Term
     * 
     * @param termStem
     * @param length
     * @param type
     * @param ontologyId
     */
    public Term(String termStem, int length, String type, Integer ontologyId) {

	this.stem = termStem;
	this.length = length;
	this.ontologyId = ontologyId;
	this.type = type;
	this.A = 0;
	this.B = 0;
	this.C = 0;
	this.D = 0;
    }



    /**
     * Get the ontologyId
     * 
     * @return the ontologyId
     */
    public Integer getOntologyId() {

	return this.ontologyId;
    }

    /**
     * Set the ontologyId
     * 
     * @param ontologyId the ontologyId to set
     */
    public void setOntologyId(Integer ontologyId) {

	this.ontologyId = ontologyId;
    }

    /**
     * sort the terms by score first then sort by lexical order of their
     * names if there is a tie
     */
    public int compareTo(Object obj) {

	Term t = (Term) obj;
//	System.out.println("comparing " + this.stem + "  and " + t.stem);
	if (t.score == score) {
	    return stem.compareTo(t.stem);
	}
	else if (t.score > score) {
	    return 1;
	}
	else {
	    return 0;
	}
    }

    // it returns the type of this term (e.g. onegram, twogram)
    /**
     * Get the type of term
     * 
     * @return the type
     */
    public String getType() {

	return this.type;
    }

    /**
     * Set the type
     * 
     * @param type the type to set
     */
    public void setType(String type) {

	this.type = type;
    }

    public double getAvgnoApps() {
	return avgnoApps;
    }

    public void setAvgnoApps(double avgnoApps) {
	this.avgnoApps = avgnoApps;
    }

    /**
     * @return Returns the score.
     */
    public double getScore() {

	return score;
    }

    /**
     * @param score The score to set.
     */
    public void setScore(double score) {

	this.score = score;
    }

    /**
     * @return Returns the stem.
     */
    public String getStem() {

	return stem;
    }

    /**
     * @param stem The stem to set.
     */
    public void setStem(String stem) {

	this.stem = stem;
    }


    /**
     * @return Returns the a.
     */
    public int A() {

	return A;
    }

    /**
     * @param a The a to set.
     */
    public void setA(int a) {

	A = a;
    }

    /**
     * @return Returns the b.
     */
    public int B() {

	return B;
    }

    /**
     * @param b The b to set.
     */
    public void setB(int b) {

	B = b;
    }

    /**
     * @return Returns the c.
     */
    public int C() {

	return C;
    }

    /**
     * @param c The c to set.
     */
    public void setC(int c) {

	C = c;
    }

    /**
     * @return Returns the d.
     */
    public int D() {

	return D;
    }

    /**
     * @param d The d to set.
     */
    public void setD(int d) {

	D = d;
    }

    /**
     * @param ad2
     */
    public void setAd(double ad2) {

	ad = ad2;

    }

    /**
     * @param noApps
     */
    public void setNoApperance(int noApps) {

	noApperance = noApps;

    }

    /**
     * @return
     */
    public double getNoApperance() {
	return noApperance;
    }

    /**
     * @return
     */
    public double getAd() {
	return ad;
    }

    // Lexicographical compare
    public int compare(Term t2) {

	return this.getStem().compareToIgnoreCase(t2.getStem());

    }

    public int getLength() {

	return length;

    }

    public void setDistribution(Set termDistribution) {

	//	for (int i = 0; i < termDistribution.size(); i++) {
	//	    distribution += termDistribution.get(i) + " ";
	//	}
	for (Iterator i = termDistribution.iterator(); i.hasNext();) {
	    distribution += i.next() + " ";
	}

    }
    
    public void addDistribution(String docId) {
	    distribution += docId + " ";
    }
    
    public void setDistribution(String documentIdList) {
	    distribution += documentIdList;
    }
    public void increaseA() {
	A = A + 1;
    }

    public void increaseB() {
	B = B + 1;
    }

    public void increaseC() {
	C = C + 1;
    }

    public void increaseD() {
	D = D + 1;
    }



    public String getDistribution() {
	// TODO Auto-generated method stub
	return distribution;
    }



    public String getListDocument() {
        return listDocument;
    }



    public void setListDocument(String listDocument) {
        this.listDocument = listDocument;
    }

}
