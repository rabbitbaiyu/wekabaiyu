package mat.unical.it.learner.engine.basic;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Rappresenta un Termine. Esso puo' essere semplice oppure composto, cioè
 * unione di due termini.
 */
@SuppressWarnings("unchecked")
public class DiscriminativeTerm implements Comparable, Serializable {

	private int index;

	private String termValue = null;

	private int length = 0;

	private String type = "";

	private Integer ontoId = null;

	private DocumentSet documentSet = null;

	private Double scoreValue = null;

	public Double getScoreValue() {
		return scoreValue;
	}

	public void setScoreValue(Double scoringFuntion) {
		this.scoreValue = scoringFuntion;
	}

	/**
	 * Costruttore. Consente di costruire termini semplici
	 * 
	 * @param i
	 */
	public DiscriminativeTerm(int i) {
		index = i;
	}

	public DiscriminativeTerm(String n, int i) {
		setTermValue(n);
		index = i;
		setType("ngram");
		calculateLength();
		setOntoId(null);
		// calculatePrefix();
	}

	/**
	 * Costruttore. Consente di costruire termini semplici con un nome
	 * 
	 * @param i
	 * @param n
	 */
	public DiscriminativeTerm(int i, String n, String type, Integer ontoId,
			DocumentSet documentSet) {
		index = i;
		setTermValue(n);
		setType(type);
		calculateLength();
		setOntoId(ontoId);
		this.documentSet = documentSet;
		// calculatePrefix();
	}

	/**
	 * Costruttore. Consente di costruire termini semplici con un nome
	 * 
	 * @param i
	 * @param n
	 */
	public DiscriminativeTerm(int i, String n, String type, int length,
			Integer ontoId, DocumentSet documentSet) {
		index = i;
		setTermValue(n);
		setLength(length);
		setType(type);
		calculateLength();
		setOntoId(ontoId);
		this.documentSet = documentSet;
		// calculatePrefix();
	}

	/**
	 * Costruttore di copia
	 * 
	 * @param t
	 *            Il termine da copiare
	 */
	public DiscriminativeTerm(DiscriminativeTerm t) {
		this.setType(t.getType());
		this.setTermValue(t.getTermValue());
		this.setLength(t.getLength());
		this.setIndex(t.getIndex());
		this.setOntoId(t.getOntoId());
		this.setDocumentSet(t.getDocumentSet());

	}

	/**
	 * @param length2
	 */
	private void setLength(int length2) {
		length = length2;
	}

	/**
	 * @param i
	 */
	private void calculateLength() {
		int count = 0;
		if (termValue != null && !termValue.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(termValue, " ");
			while (tokenizer.hasMoreElements()) {
				count++;
				tokenizer.nextElement();
			}
		}
		length = count;
	}

	/**
	 * @return Il primo elemento della coppia
	 */
	public int getIndex() {
		return index;
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
	 * @param documentSet
	 *            the documentSet to set
	 */
	public void setDocumentSet(DocumentSet documentSet) {

		this.documentSet = documentSet;
	}

	public void setTermValue(String string) {
		String copy = "";
		if (string != null) {
			if (!string.startsWith("\"")) {
				copy += "\"";
			}
			copy += string;
			if (!string.endsWith("\"")) {
				copy += "\"";
			}
		}
		termValue = copy;
	}

	/**
	 * Setta il primo indice del termine
	 * 
	 * @param i
	 *            L'indice da settare
	 */
	public void setIndex(int i) {
		index = i;
	}

	/**
	 * Restituisce true se il termine ha entrambi gli indici non nulli
	 * 
	 * @return
	 */
	public boolean isValid() {
		return this.getIndex() != 0;

	}

	/**
	 * Restituisce una versione in stringa del termine
	 */
	public String toString() {
		return "t(" + this.getZeroIndex() + "," + this.getIndex() + ")";
	}

	/**
	 * @return
	 */
	private String getZeroIndex() {

		return 0 + "";
	}

	public Object clone() {
		return new DiscriminativeTerm(this);
	}

	/**
	 * Restituisce una versione in stringa del termine
	 */
	public String toString2() {
		return "t(" + this.getZeroIndex() + "," + this.getIndex() + "): "
				+ this.getZeroString() + " " + this.getTermValue();
	}

	/**
	 * @return
	 */
	private String getZeroString() {

		return "";
	}

	/**
	 * Get the termValue
	 * 
	 * @return the termValue
	 */
	public String getTermValue() {
		return this.termValue;
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
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public String getTermToString() {

		String rank = "low";

		StringBuffer string = new StringBuffer();
		string.append(this.getType() + "(" + this.getIndex() + ","
				+ this.getTermValue());
		if (this.isConcept()) {
			string.append("," + this.getOntoId());
		}
		string.append("," + rank + ")");

		return string.toString();
	}

	/**
	 * @return
	 */
	private boolean isConcept() {

		if (this.getType().equalsIgnoreCase(Constants.CONCEPT_TYPE))
			return true;
		return false;
	}

	/**
	 * @return Returns the ontoId.
	 */
	public Integer getOntoId() {
		return ontoId;
	}

	/**
	 * @param ontoId
	 *            The ontoId to set.
	 */
	public void setOntoId(Integer ontoId) {
		this.ontoId = ontoId;
	}

	/**
	 * Restituisce true se i termini hanno gli stessi indici
	 * 
	 * @param t
	 *            Il termine di confronto
	 * @return true se i termini hanno gli stessi indici, false altrimenti
	 */
	public boolean equals(Object t) {
		return this.compareTo(t) == 0;
	}

	private static int compareOntologyId(Integer ontoId1, Integer ontoId2) {

		int value = 0;
		if (ontoId1 == null && ontoId2 == null) {
			return value;
		}
		try {
			if (ontoId1.intValue() < ontoId2.intValue()) {
				value = -1;
			} else if (ontoId1.intValue() > ontoId2.intValue()) {
				value = 1;
			}
		} catch (NullPointerException e) {
			value = (ontoId1 == null ? -1 : 1);

		}
		return value;
	}

	private static int compareType(String termType1, String termType2) {

		int value = 0;
		if (termType1 == null && termType2 == null) {
			return value;
		}
		try {
			value = termType1.compareTo(termType2);
		} catch (NullPointerException e) {
			value = (termType1 == null ? -1 : 1);

		}
		return value;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object t) {

		if (!(t instanceof DiscriminativeTerm)) {
			return -1;
		}

		DiscriminativeTerm term = (DiscriminativeTerm) t;

		if (!this.getTermValue().equals(term.getTermValue())) {
			return this.getTermValue().compareTo(term.getTermValue());
		}
		if (compareType(this.getType(), term.getType()) != 0) {
			return compareType(this.getType(), term.getType());
		}
		if (compareOntologyId(this.getOntoId(), term.getOntoId()) != 0) {
			return compareOntologyId(this.getOntoId(), term.getOntoId());
		}
		if (this.getLength() < term.getLength()) {
			return -1;
		} else if (this.getLength() > term.getLength()) {
			return 1;
		}
		return 0;
	}

}