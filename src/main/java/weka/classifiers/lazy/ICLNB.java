/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    IB1.java
 *    Copyright (C) 1999 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.lazy;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.converters.ConverterUtils.DataSource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <!-- globalinfo-start --> Nearest-neighbour classifier. Uses normalized
 * Euclidean distance to find the training instance closest to the given test
 * instance, and predicts the same class as this training instance. If multiple
 * instances have the same (smallest) distance to the test instance, the first
 * one found is used.<br/>
 * <br/>
 * For more information, see <br/>
 * <br/>
 * D. Aha, D. Kibler (1991). Instance-based learning algorithms. Machine
 * Learning. 6:37-66.
 * <p/>
 * <!-- globalinfo-end -->
 * 
 * <!-- technical-bibtex-start --> BibTeX:
 * 
 * <pre>
 * &#64;article{Aha1991,
 *    author = {D. Aha and D. Kibler},
 *    journal = {Machine Learning},
 *    pages = {37-66},
 *    title = {Instance-based learning algorithms},
 *    volume = {6},
 *    year = {1991}
 * }
 * </pre>
 * <p/>
 * <!-- technical-bibtex-end -->
 *
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 *  -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console
 * </pre>
 * 
 * <!-- options-end -->
 *
 * @author Stuart Inglis (singlis@cs.waikato.ac.nz)
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 5525 $
 */
public class ICLNB extends Classifier implements UpdateableClassifier, TechnicalInformationHandler {

	/** for serialization */
	static final long serialVersionUID = -6152184127304895851L;

	/** The training instances used for classification. */
	private Instances m_Train;

	/** The minimum values for numeric attributes. */
	//private double[] m_MinArray;

	/** The maximum values for numeric attributes. */
	//private double[] m_MaxArray;

	/**
	 * Returns a string describing classifier
	 * 
	 * @return a description suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {

		return "Nearest-neighbour classifier. Uses normalized Euclidean distance to "
				+ "find the training instance closest to the given test instance, and predicts "
				+ "the same class as this training instance. If multiple instances have "
				+ "the same (smallest) distance to the test instance, the first one found is " + "used.\n\n"
				+ "For more information, see \n\n" + getTechnicalInformation().toString();
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing detailed
	 * information about the technical background of this class, e.g., paper
	 * reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result;

		result = new TechnicalInformation(Type.ARTICLE);
		result.setValue(Field.AUTHOR, "D. Aha and D. Kibler");
		result.setValue(Field.YEAR, "1991");
		result.setValue(Field.TITLE, "Instance-based learning algorithms");
		result.setValue(Field.JOURNAL, "Machine Learning");
		result.setValue(Field.VOLUME, "6");
		result.setValue(Field.PAGES, "37-66");

		return result;
	}

	/**
	 * Returns default capabilities of the classifier.
	 *
	 * @return the capabilities of this classifier
	 */
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);
		result.enable(Capability.NUMERIC_ATTRIBUTES);
		result.enable(Capability.DATE_ATTRIBUTES);
		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);

		// instances
		result.setMinimumNumberInstances(0);

		return result;
	}

	/**
	 * Generates the classifier.
	 *
	 * @param instances
	 *            set of instances serving as training data
	 * @throws Exception
	 *             if the classifier has not been generated successfully
	 */
	public void buildClassifier(Instances instances) throws Exception {

		// can classifier handle the data?
		getCapabilities().testWithFail(instances);

		// remove instances with missing class
		instances = new Instances(instances);
		instances.deleteWithMissingClass();

		m_Train = new Instances(instances, 0, instances.numInstances());


	}

	/**
	 * Updates the classifier.
	 *
	 * @param instance
	 *            the instance to be put into the classifier
	 * @throws Exception
	 *             if the instance could not be included successfully
	 */
	public void updateClassifier(Instance instance) throws Exception {

		if (m_Train.equalHeaders(instance.dataset()) == false) {
			throw new Exception("Incompatible instance types");
		}
		if (instance.classIsMissing()) {
			return;
		}
		m_Train.add(instance);
		//updateMinMax(instance);
	}

	/**
	 * Classifies the given test instance.
	 *
	 * @param instance
	 *            the instance to be classified
	 * @return the predicted class for the instance
	 * @throws Exception
	 *             if the instance can't be classified
	 */
	public double classifyInstance(Instance instance) throws Exception {

		Instances instances = new Instances(m_Train);
		instances.deleteWithMissingClass();

		Instances newm_Train = new Instances(instances, 0, instances.numInstances());

		if (m_Train.numInstances() == 0) {
			throw new Exception("No training instances!");
		}

		double distance, minDistance = Double.MAX_VALUE, classValue = 0;
		//updateMinMax(instance);

		NaiveBayes nb = new NaiveBayes();

		//System.out.println("number of instances		" + newm_Train.numInstances());
		//System.out.println("-------" + newm_Train.instance(0));
		//System.out.println("-------classvalue" + newm_Train.instance(0).classValue());

		int length = newm_Train.numInstances();
		ArrayList<Integer> similar = new ArrayList ();
		int count = 0;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		Enumeration enu = newm_Train.enumerateInstances();
		while (enu.hasMoreElements()) {
			Instance trainInstance = (Instance) enu.nextElement();
			if (!trainInstance.classIsMissing()) {
				int instancesimilar = distance(instance, trainInstance);
				//similar.add(instancesimilar);
				map.put(count, instancesimilar);
				count++;
			}
		}
		
		
		
/*		while (enu.hasMoreElements()) {
			Instance trainInstance = (Instance) enu.nextElement();
			if (!trainInstance.classIsMissing()) {
				int instancesimilar = distance(instance, trainInstance);
				similar.add(instancesimilar);
				count++;
			}
		}
*/
		//System.out.println(map);
		//System.out.println("%%%%%%%%%%");
		
		List<Map.Entry<Integer,Integer>> list=new ArrayList<>();
		list.addAll(map.entrySet());
		ICLNB.ValueComparator vc=new ValueComparator();
		Collections.sort(list,vc);
/*		for(Iterator<Map.Entry<Integer,Integer>> it=list.iterator();it.hasNext();)
		{
			//System.out.println(it.next());
		}*/
		int kvalue = 10;

		
		
		
		
/*		for(int k = 0;k<length;k++){
			
			System.out.println(map);
			
			//System.out.println("%%%%%%%%%%");
			
		}*/
		
		
		

/*		for (int k = 0; k < similar.length; k++) {
			System.out.println("similar of instance " + k + "	is  " + similar[k]);
		}*/
		Instances newm_Train1 = new Instances(instances, 0, 0); 
		for (int k = 0; k < kvalue; k++) {
			//int countnum = (int) similar.get(k);
			// Instance ins = m_Train.instance(k);
			int countnum  = list.get(k).getValue();
			int insnumber = list.get(k).getKey();
			//System.out.println("countnum "+countnum+"  insnumber   "+insnumber);
			//Instance ins = m_Train.instance(insnumber);
			for (int m = 0; m < countnum+1; m++) {
				newm_Train1.add(newm_Train.instance(insnumber));
			}
		}

		System.out.println("number of instances		" + newm_Train1.numInstances());
		
/*		for (int k = 0; k < newm_Train.numInstances(); k++) {
			System.out.println("-------");
			System.out.println("instance " + k + "	" + newm_Train.instance(k));
			System.out.println("-------");
		}*/

		/*
		 * for (int k = 0; k < distances.length; k++) {
		 * System.out.println("-------"); System.out.println("distance of "+k+
		 * "	"+distances[k]); System.out.println("-------"); }
		 */
		nb.buildClassifier(newm_Train1);
		double possibility = -1;
		double classvalue = 0;
		double[] dis = nb.distributionForInstance(instance);
		for (int m = 0; m < dis.length; m++) {
			//System.out.println("-------" + m);
			//System.out.println("------dis[m]-" + dis[m]);
			//System.out.println("----possibility-before--" + possibility);
			if (dis[m] > possibility) {
				possibility = dis[m];
				classvalue = m;
			}
			//System.out.println("---possibility-after----" + possibility);
			//System.out.println("---classvalue----" + classvalue);

		}
		// double[] distribution = makeDistribution(neighbours, distances);
		// return dis;

		return classvalue;
	}

	/**
	 * Returns a description of this classifier.
	 *
	 * @return a description of this classifier as a string.
	 */

	private int distance(Instance first, Instance second) {

		//double diff;
		int similar = 0;

		for (int i = 0; i < m_Train.numAttributes(); i++) {
			if (i == m_Train.classIndex()) {
				continue;
			}
			if (m_Train.attribute(i).isNominal()) {



				if (((int) first.value(i) == (int) second.value(i))) {
					similar += 1;
				}

			}

		}

		return similar;
	}

	public String toString() {

		return ("IB1 classifier");
	}

	/**
	 * Calculates the distance between two instances
	 *
	 * @param first
	 *            the first instance
	 * @param second
	 *            the second instance
	 * @return the distance between the two given instances
	 */

	/**
	 * Normalizes a given value of a numeric attribute.
	 *
	 * @param x
	 *            the value to be normalized
	 * @param i
	 *            the attribute's index
	 * @return the normalized value
	 */

	/**
	 * Updates the minimum and maximum values for all the attributes based on a
	 * new instance.
	 *
	 * @param instance
	 *            the new instance
	 */
/*	private void updateMinMax(Instance instance) {

		for (int j = 0; j < m_Train.numAttributes(); j++) {
			if ((m_Train.attribute(j).isNumeric()) && (!instance.isMissing(j))) {
				if (Double.isNaN(m_MinArray[j])) {
					m_MinArray[j] = instance.value(j);
					m_MaxArray[j] = instance.value(j);
				} else {
					if (instance.value(j) < m_MinArray[j]) {
						m_MinArray[j] = instance.value(j);
					} else {
						if (instance.value(j) > m_MaxArray[j]) {
							m_MaxArray[j] = instance.value(j);
						}
					}
				}
			}
		}
	}*/

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 5525 $");
	}

	Instances getinstance(String s) throws Exception {

		DataSource source = new DataSource(s);
		Instances data = source.getDataSet();
		// System.out.println(data);
		System.out.println("**************");
		return data;
	}
	
	
	private static class ValueComparator implements Comparator<Map.Entry<Integer,Integer>>
	{
		public int compare(Map.Entry<Integer,Integer> m,Map.Entry<Integer,Integer> n)
		{
			return n.getValue()-m.getValue();
		}
	}
	

	/**
	 * Main method for testing this class.
	 *
	 * @param argv
	 *            should contain command line arguments for evaluation (see
	 *            Evaluation).
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
		runClassifier(new ICLNB(), argv);

		
		 /*String filepath ="F:/ϵͳ����/weka-src/data/56Data/iris.arff";
		 ICLNB ib2 = new ICLNB(); 
		 Instances ins =ib2.getinstance(filepath); 
		 Instance inc2 = ins.instance(114);
		 System.out.println("------inc2----");
		 System.out.println(inc2); 
		 System.out.println("------inc2----");
		 //Instance inc3 = ins.instance(93);
		 //System.out.println(inc3); // System.out.println(ins);
		 System.out.println("----------");
		 ins.setClassIndex(ins.numAttributes() - 1); 
		 System.out.println(ins);
		 //ins.add(inc2); System.out.println("---------- instance begin");
		 //System.out.println(ins); 
		 System.out.println("---------- instance end"); 
		 ib2.buildClassifier(ins); 
		 //int distance = ib2.distance(inc2, inc3); 
		 //System.out.println("distance=="+distance); 
		 //ib2.buildClassifier(ins);
		 // ib2.buildClassifier(ins);  
		 double result = ib2.classifyInstance(inc2);
		 System.out.println(result); 
		 System.out.println("---------- classify instance3 ");
		 //System.out.println(inc3); 
		 //System.out.println("---------- instance end"); 
		 //double dis = ib2.classifyInstance(inc3);
		 //System.out.println("hello world	"+dis);
		 
		 System.out.println();*/
		  
		//System.out.println("hello world	"); 
		// System.out.println(dis);
		// remove instances with missing class
		// ibev.buildClassifier(ins);
		// System.out.println("begin classify " );
		// ibev.classifyInstance(ins.lastInstance());
		// System.out.println("end classify " );
	}
}
