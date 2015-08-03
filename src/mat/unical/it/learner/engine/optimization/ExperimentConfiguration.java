package mat.unical.it.learner.engine.optimization;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mat.unical.it.learner.engine.basic.DiscriminativeTerm;
import mat.unical.it.learner.engine.basic.DocumentSet;

public class ExperimentConfiguration {

	private HashMap<Integer, Integer> trainingSetDocumentAssociations = null;

	private List<DiscriminativeTerm> reducedVocabulary = null;

	private DocumentSet trainingSet = null;

	private Map<String, DocumentSet> categoryTrainingSet = null;

	private Map<String, DocumentSet> categoryPreClassificationSet = null;

	public Map<String, DocumentSet> getCategoryPreClassificationSet() {
		return categoryPreClassificationSet;
	}

	public void setCategoryPreClassificationSet(
			Map<String, DocumentSet> categoryPreClassificationSet) {
		this.categoryPreClassificationSet = categoryPreClassificationSet;
	}

	public Map<String, DocumentSet> getCategoryTrainingSet() {
		return categoryTrainingSet;
	}

	public void setCategoryTrainingSet(
			Map<String, DocumentSet> categoryTrainingSet) {
		this.categoryTrainingSet = categoryTrainingSet;
	}

	public DocumentSet getTrainingSet() {
		return trainingSet;
	}

	public void setTrainingSet(DocumentSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	public List<DiscriminativeTerm> getReducedVocabulary() {
		return reducedVocabulary;
	}

	public void setReducedVocabulary(List<DiscriminativeTerm> reducedVocabulary) {
		this.reducedVocabulary = reducedVocabulary;
	}

	public ExperimentConfiguration(List<Integer> trainingSetDocumentList,
			Hashtable<String, Vector<Integer>> tcs,
			Hashtable<String, Vector<Integer>> acs) {

		try {
			// Collection<Integer> trainingSetDocumentList =
			// trainingSetDocumentAssociations.keySet();

			// if (trainingSetDocumentAssociations == null) {
			// System.err.println("trainingSetDocumentAssociations  null");
			// }
			// trainingSet =
			// DocumentSet.createDocumentSet(trainingSetDocumentList,
			// trainingSetDocumentAssociations);

			trainingSet = new DocumentSet(trainingSetDocumentList.size(),
					trainingSetDocumentList);
			// this.reducedVocabulary = reducedVocabulary;

			categoryTrainingSet = new HashMap<String, DocumentSet>();
			categoryPreClassificationSet = new HashMap<String, DocumentSet>();

			for (Enumeration<String> en = tcs.keys(); en.hasMoreElements();) {
				String category = en.nextElement();

				if (tcs.get(category) == null) {
					System.err.println(category + "tc null");
				}
				if (acs.get(category) == null) {
					// System.err.println(category + "ac null");
				}

				DocumentSet ds = new DocumentSet(
						trainingSetDocumentList.size(), tcs.get(category));
				categoryTrainingSet.put(category, ds);

				Vector<Integer> ac = null;
				if ((ac = acs.get(category)) == null) {
					ac = new Vector<Integer>();
				}
				categoryPreClassificationSet.put(category, new DocumentSet(
						trainingSetDocumentList.size(), ac));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public DocumentSet getCategoryTc(String category) {

		return categoryTrainingSet.get(category);
	}

	public DocumentSet getCategoryAc(String category) {

		DocumentSet Ac = categoryPreClassificationSet.get(category);

		if (Ac == null) {
			return new DocumentSet(this.trainingSet.getMaxSize());
		}
		return Ac;
	}

	public HashMap<Integer, Integer> getTrainingSetDocumentAssociations() {
		return trainingSetDocumentAssociations;
	}

	public void setTrainingSetDocumentAssociations(
			HashMap<Integer, Integer> trainingSetDocumentAssociations) {
		this.trainingSetDocumentAssociations = trainingSetDocumentAssociations;
	}

}
