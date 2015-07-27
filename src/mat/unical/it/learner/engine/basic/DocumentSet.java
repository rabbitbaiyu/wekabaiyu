package mat.unical.it.learner.engine.basic;


import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import mat.unical.it.learner.engine.exception.RunExperimentException;

public class DocumentSet implements Comparable<Object>, Serializable {

	/**
	 * the max dimension
	 */
	private int maxSize = 0;

	/**
	 * The real dimension.
	 */
//	private long bitCardinality = 0;

	/**
	 * the set of document integer identifiers
	 */
	private BitSet documents = null;

	/**
	 * Create a new DocumentSet with a given max dimension
	 * 
	 * @param dim
	 */
	public DocumentSet(int dim) {
		documents = new BitSet(dim);
		this.maxSize = dim;
		// this.setCardinality();
	}

	/**
	 * Create a new DocumentSet by setting an initial document set.
	 * 
	 * @param t
	 *            the term to set
	 * @param initialSet
	 *            the initial set of documents
	 */
	public DocumentSet(Vector<Integer> initialIndexesSet) {

		this(initialIndexesSet.size(), initialIndexesSet);
	}

	/**
	 * Create a new DocumentSet
	 * 
	 * @param t
	 * @param dim
	 * @param initialSet
	 */
	public DocumentSet(int dim, List<Integer> initialIndexesSet) {

		this.documents = new BitSet(dim);

		this.maxSize = dim;

		if (dim == initialIndexesSet.size()) {
			documents.set(0, this.maxSize);
		} else
			for (int i = 0; i < initialIndexesSet.size(); i++) {
				documents.set(initialIndexesSet.get(i));
			}
		
//		this.bitCardinality = documents.cardinality();
	}

	/**
	 * Modify this DocumentSet by copying from another one
	 * 
	 * @param set
	 * @return
	 * @throws Exception
	 */
	public DocumentSet copyOf(DocumentSet set) {

		if (this.maxSize != set.maxSize) {
			return null;
		}
		this.documents = (BitSet) set.documents.clone();
//		this.bitCardinality = this.documents.cardinality();

		return this;
	}

	/**
	 * Return the DocumentSet max size
	 * 
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Get the cardinality
	 * 
	 * @return the cardinality
	 */
	public int getCardinality() {

		return documents.cardinality();
	}

	/**
	 * Calculate the cardinality
	 */
//	public void setCardinality() {
//		bitCardinality = documents.cardinality();
//	}

	/**
	 * @return the document identifiers
	 */

	public BitSet getDocuments() {
		return documents;
	}

	/**
	 * Add a new document identifier in the document set
	 */
	public void addElement(int docIndex) {
		// if (this.getCardinality() == this.getMaxSize()) {
		// throw new IndexOutOfBoundsException("Error while adding document " +
		// docIndex + ": DocumentSet has cardinality = " + this.getCardinality()
		// + " and maxSize = " + this.getMaxSize());
		// }
		this.documents.set(docIndex);
		// bitCardinality = this.documents.cardinality();
	}

	/**
	 * @return a boolean indicating whether the DocumentSet is empty
	 */
	public boolean isEmpty() {
		return (this.getCardinality() == 0);
	}

	/**
	 * Costruisce l'intersezione di due DocumentSet *ordinati* (in maniera
	 * efficiente, sfruttando l'ordinamento dei set)
	 * 
	 * @param secondSet
	 *            Il DocumentSet con cui fare l'intersezione
	 * @return un nuovo DocumentSet ottenuto per intersezione.
	 */
	public DocumentSet intersectionOf(DocumentSet secondSet) {

		if (this.getMaxSize() != secondSet.getMaxSize()) {
			System.out.println("first ds size: " + this.maxSize
					+ " second ds size: " + secondSet.maxSize);
			return null;
		}
		
		DocumentSet intersectionSet = new DocumentSet(this.maxSize);
		intersectionSet.copyOf(this);
		intersectionSet.documents.and(secondSet.documents);

//		intersectionSet.bitCardinality = intersectionSet.documents.cardinality();
		// DTSelector.log.debug("intersect " +
		// intersectionSet.getCardinality());
		return intersectionSet;
	}
	
	

	/**
	 * Costruisce il complemento di due DocumentSet *ordinati*
	 * 
	 * @param secondSet
	 *            Il DocumentSet da sottrarre a quello chiamante.
	 * @return il DocumentSet ottenuto sottraendo al DS chiamante quello passato
	 *         come parametro
	 */
	public DocumentSet complementOf(DocumentSet secondSet) {

		if (this.getMaxSize() != secondSet.getMaxSize()) {
			System.out.println("first ds size: " + this.maxSize
					+ " second ds size: " + secondSet.maxSize);
			return null;
		}

		DocumentSet complementSet = new DocumentSet(this.getMaxSize());
		complementSet.copyOf(this);
		complementSet.documents.andNot(secondSet.getDocuments());
//		complementSet.bitCardinality = complementSet.documents.cardinality();
		return complementSet;
	}

	/**
	 * Costruisce l'unione di due DocumentSet
	 * 
	 * @param secondSet
	 *            Il DocumentSet da sottrarre a quello chiamante.
	 * @return il CopyOfDocumentSet ottenuto come unione del DS chiamante e di
	 *         quello passato come parametro
	 */
	public DocumentSet unionOf(DocumentSet secondSet) {

		if (this.getMaxSize() != secondSet.getMaxSize()) {
			System.out.println("first ds size: " + this.maxSize
					+ " second ds size: " + secondSet.maxSize);
			return null;
		}

		DocumentSet unionSet = new DocumentSet(this.getMaxSize());
		unionSet.copyOf(this);
		unionSet.documents.or(secondSet.documents);
//		unionSet.bitCardinality = unionSet.documents.cardinality();
		return unionSet;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object docset) {

		if (!(docset instanceof DocumentSet)) {
			return false;
		}
		DocumentSet docSet = (DocumentSet) docset;

		if (this.getMaxSize() != docSet.getMaxSize() || this.getCardinality() != docSet.getCardinality()) {
			return false;
		}
		if (!this.documents.equals(docSet.documents)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object docSet) {

		if (docSet instanceof DocumentSet
				&& ((DocumentSet) this).equals(docSet)) {
			return 0;
		}
		return -1;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {

		DocumentSet toReturn = new DocumentSet(this.getMaxSize());
//		toReturn.bitCardinality = this.bitCardinality;
		toReturn.documents = (BitSet) this.documents.clone();
		return toReturn;
	}

	public boolean disjointWith(DocumentSet ds) {

		return !this.documents.intersects(ds.documents);

	}

	/**
	 * @param positiveDS
	 * @return
	 * @throws RunExperimentException
	 */
	public static DocumentSet makeUnion(Vector<DocumentSet> docSets)
			throws RunExperimentException {

		BitSet set = new BitSet();

		for (int i = 0; i < docSets.size(); i++) {
			DocumentSet ds = docSets.get(i);
			BitSet elements = ds.getDocuments();
			set.or(elements);
		}
		try {
			if (docSets.isEmpty()) {
				return null;
			}
			DocumentSet toReturn = new DocumentSet(docSets.get(0).maxSize);
			toReturn.setDocuments(set);
//			toReturn.setCardinality();

			return toReturn;
		} catch (NullPointerException e) {
			throw new RunExperimentException(e.getMessage());
		}

	}

	/**
	 * Costruisce il set complementare di un document set *ordinato* rispetto al
	 * corpus anche esso *ordinato* (in maniera efficiente, sfruttando
	 * l'ordinamento dei set)
	 * 
	 * @param corpus
	 *            : L'universo rispetto al quale costruire il complementare del
	 *            documentSet su cui è chiamato il metodo
	 * @return un nuovo DocumentSet ottenuto come set complemetare.
	 */
	public DocumentSet getComplement(DocumentSet corpus) {
		if (this.getMaxSize() != corpus.getMaxSize()) {
			System.out.println("first ds size: " + this.maxSize
					+ " corpus size: " + corpus.maxSize);
			return null;
		}
		DocumentSet complementarySet = new DocumentSet(corpus.getMaxSize());
		complementarySet.copyOf(this);
		complementarySet.documents.flip(0, complementarySet.maxSize);
//		complementarySet.bitCardinality = complementarySet.documents.size();

		return complementarySet;
	}

	/**
	 * @param docs
	 */
	public void setDocuments(BitSet docs) {

		this.documents = docs;
//		setCardinality();

	}

	public String toString() {
		StringBuffer st = new StringBuffer("");
		for (int i = 0; i < documents.cardinality(); i++) {
			st.append(documents.nextSetBit(0) + " ");
		}
		return st.toString();
	}

	/**
	 * Create a DocumentSet, initializing it with a given.
	 * 
	 * 
	 * @param documents
	 * @param doc_correspondences
	 * @return
	 */
	public static DocumentSet createDocumentSet(
			Collection<Integer> documentsInSet,
			HashMap<Integer, Integer> wholeCollectionDocuments) {

		DocumentSet ds = new DocumentSet(wholeCollectionDocuments.size());

		if (ds == null) {
			System.out.println("ds == null");
		}
		try {

			for (Integer idDoc : documentsInSet) {

				if (wholeCollectionDocuments.get(idDoc) == null) {
					System.err.println(documentsInSet);
					System.err.println(idDoc + "-->doc null");
				}
				ds.addElement(wholeCollectionDocuments.get(idDoc));
			}

		} catch (NullPointerException npe) {
			// npe.printStackTrace();
			// throw new NullPointerException();
		}
		return ds;

	}

}