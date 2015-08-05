
package weka.classifiers.lazy;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.lazy.IB1;
import weka.classifiers.trees.Id3;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
//import knn.entrytester.ValueComparator;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.converters.ConverterUtils.DataSource;

//import knn_learning.entrytester.ValueComparator;

/**
 * @author baiyu
 *
 */
public class IB1_Evlution extends Classifier implements UpdateableClassifier, TechnicalInformationHandler {

	/**
	 * 
	 */
	private Instances m_Train;
	private static final long serialVersionUID = 1L;

	/** The minimum values for numeric attributes. */
	private double[] m_MinArray;

	/** The maximum values for numeric attributes. */
	private double[] m_MaxArray;

	/** The maximum values for numeric attributes. */
	// public Instances[] ClassInstances;
	
	private double[][] class_min_Array;

	private double[][] class_max_Array;
	/** split the instances by the class value. */
	private Instances[] splitData;
	/** the instancevector by class label to get the best instance */

	private Instancevector insvec;
	
	/**  Population number */
	private int N=30;
	
	private  Instancevector [] population;

	Random random = new Random();

	/**
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] argv) throws Exception {

		String filepath = "C:/Users/baiyu/Desktop/weka-src/src/main/java/data/diabetes2.arff";
		//String filepath = "/data/diabetes2.arff";

		IB1_Evlution ibev = new IB1_Evlution();
		Instances ins = ibev.getinstance(filepath);
		ins.setClassIndex(ins.numAttributes() - 1);
		ibev.buildClassifier(ins);
		// ibev.printinstancebyclass();
		ibev.randominstance();
		ibev.printinstancerandom();
		// System.out.println(ibev.getinstance(filepath));

		// ibev.test();
		// ibev.min_max_init();

	}

	public Instances getM_Train() {
		return m_Train;
	}

	public void setM_Train(Instances m_Train) {
		this.m_Train = m_Train;
	}

	public double[] getM_MinArray() {
		return m_MinArray;
	}

	public void setM_MinArray(double[] m_MinArray) {
		this.m_MinArray = m_MinArray;
	}

	public double[] getM_MaxArray() {
		return m_MaxArray;
	}

	public void setM_MaxArray(double[] m_MaxArray) {
		this.m_MaxArray = m_MaxArray;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void min_max_init() {

		System.out.println("number of attributtes  " + m_Train.numAttributes());
		// m_Train.attribute(index)
		Enumeration emuattri = m_Train.enumerateAttributes();
		System.out.println("min_max_init");
		while (emuattri.hasMoreElements()) {
			Attribute attri = (Attribute) emuattri.nextElement();
			System.out.print(attri.name() + "\t");
			// System.out.println(in + "*****");
			// splitData[(int) in.classValue()].add(in);
			// splitData[0].add(in);
		}
		System.out.println();

		// System.out.println("min_max_init");
		m_MinArray = new double[m_Train.numAttributes()];
		m_MaxArray = new double[m_Train.numAttributes()];
		for (int i = 0; i < m_Train.numAttributes(); i++) {
			m_MinArray[i] = m_MaxArray[i] = Double.NaN;
		}

	}

	Instances getinstance(String s) throws Exception {

		DataSource source = new DataSource(s);
		Instances data = source.getDataSet();
		// System.out.println(data);
		System.out.println("**************");
		return data;
	}

	@Override
	public void buildClassifier(Instances instances) throws Exception {

		// can classifier handle the data?
		getCapabilities().testWithFail(instances);

		// remove instances with missing class
		instances = new Instances(instances);
		instances.deleteWithMissingClass();

		m_Train = new Instances(instances, 0, instances.numInstances());

		// m_Train.setClassIndex(m_Train.numAttributes() - 1);
		System.out.println("number of attibutes is " + m_Train.numAttributes());
		// int numattribute = m_Train.numAttributes();
		int numdclass = m_Train.numClasses();
		insvec = new Instancevector(numdclass);
		population = new Instancevector[N];
		class_min_Array = new double[numdclass][2];
		class_max_Array = new double[numdclass][2];
		System.out.println("number of class is" + numdclass);

		splitData = new Instances[numdclass];
		for (int j = 0; j < numdclass; j++) {
			splitData[j] = new Instances(m_Train, m_Train.numInstances());
		}

		Enumeration enu = m_Train.enumerateInstances();

		while (enu.hasMoreElements()) {
			Instance in = (Instance) enu.nextElement();
			// System.out.println(in + "*****");
			splitData[(int) in.classValue()].add(in);
			// splitData[0].add(in);
		}

		for (int j = 0; j < numdclass; j++) {
			System.out.println(splitData[j]);
			System.out.println("_______________________________________________");
		}

		min_max_init();
		// System.out.println("#######");
		for (int j = 0; j < numdclass; j++) {
			System.out.println(splitData[j]);
			System.out.println("_______________________________________________");
			Enumeration enuinstances = splitData[j].enumerateInstances();
			while (enuinstances.hasMoreElements()) {
				updateMinMax((Instance) enuinstances.nextElement());
				// System.out.println("#######");
			}
			class_min_Array[j] = getM_MinArray();
			class_max_Array[j] = getM_MaxArray();
			min_max_init();
		}

		/*
		 * Enumeration enu1 = m_Train.enumerateInstances(); while
		 * (enu1.hasMoreElements()) { updateMinMax((Instance)
		 * enu1.nextElement()); //System.out.println("#######"); }
		 */

		System.out.println("------------------------min_array----------------------------");
		print_array(class_min_Array);
		System.out.println("------------------------max_array----------------------------");
		print_array(class_max_Array);

		// TODO Auto-generated method stub

	}

	public void print_array(double[][] class_min_Array) {
		for (int j = 0; j < class_min_Array.length; j++) {

			for (int k = 0; k < m_Train.numAttributes() - 1; k++) {

				System.out.print(class_min_Array[j][k] + "\t");
			}

			System.out.println();

		}

	}

	public void get_class(Instances ins) {
		int classnum = ins.numClasses();

	}

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

	private void updateMinMax(Instance instance) {

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
	}

	@Override
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result;

		result = new TechnicalInformation(Type.ARTICLE);
		result.setValue(Field.AUTHOR, "D. Aha and D. Kibler");
		result.setValue(Field.YEAR, "1991");
		result.setValue(Field.TITLE, "Instance-based  baiyu learning algorithms");
		result.setValue(Field.JOURNAL, "Machine Learning");
		result.setValue(Field.VOLUME, "6");
		result.setValue(Field.PAGES, "37-66");

		return result;
	}

	@Override
	public void updateClassifier(Instance instance) throws Exception {

		if (m_Train.equalHeaders(instance.dataset()) == false) {
			throw new Exception("Incompatible instance types");
		}
		if (instance.classIsMissing()) {
			return;
		}
		m_Train.add(instance);
		updateMinMax(instance);

		// TODO Auto-generated method stub

	}

	public double classifyInstance(Instance instance) throws Exception {

		if (m_Train.numInstances() == 0) {
			throw new Exception("No training instances!");
		}

		double distance, minDistance = Double.MAX_VALUE, classValue = 0;
		updateMinMax(instance);
		Enumeration enu = m_Train.enumerateInstances();
		while (enu.hasMoreElements()) {
			Instance trainInstance = (Instance) enu.nextElement();
			if (!trainInstance.classIsMissing()) {
				distance = distance(instance, trainInstance);
				if (distance < minDistance) {
					minDistance = distance;
					classValue = trainInstance.classValue();
				}
			}
		}

		return classValue;
	}

	private double distance(Instance first, Instance second) {

		double diff = 0, diffsqa, distance = 0;

		for (int i = 0; i < m_Train.numAttributes(); i++) {
			if (i == m_Train.classIndex()) {
				continue;
			}
			if (m_Train.attribute(i).isNominal()) {

				// If attribute is nominal
				if (first.isMissing(i) || second.isMissing(i) || ((int) first.value(i) != (int) second.value(i))) {
					distance += 1;
				}
			} else {

				// If attribute is numeric
				if (first.isMissing(i) || second.isMissing(i)) {
					/*
					 * if (first.isMissing(i) && second.isMissing(i)) { diff =
					 * 1; } else { if (second.isMissing(i)) { diff =
					 * norm(first.value(i), i); } else { diff =
					 * norm(second.value(i), i); } if (diff < 0.5) { diff = 1.0
					 * - diff; } }
					 */
				} else {
					// diff = norm(first.value(i), i) - norm(second.value(i),
					// i);
					diff = norm(first.value(i), i) - norm(second.value(i), i);
					// diff = java.lang.Math.pow(diffsqa,2);
				}
				distance += diff * diff;
			}
		}

		return distance;
	}

	public String globalInfo() {

		return "Nearest-neighbour classifier. Uses normalized Euclidean distance to "
				+ "find the training instance closest to the given test instance, and predicts "
				+ "the same class as this training instance. If multiple instances have "
				+ "the same (smallest) distance to the test instance, the first one found is " + "used.\n\n"
				+ "For more information, see \n\n" + getTechnicalInformation().toString();
	}

	private double norm(double x, int i) {

		if (Double.isNaN(m_MinArray[i]) || Utils.eq(m_MaxArray[i], m_MinArray[i])) {
			return 0;
		} else {
			return (x - m_MinArray[i]) / (m_MaxArray[i] - m_MinArray[i]);
		}
	}

	private void randominstance() {

		for (int j = 0; j < m_Train.numClasses(); j++) {
			Instances ins = splitData[j];
			int inSelectNumOne = random.nextInt(ins.numInstances());// 闅忔満閫夋嫨绗竴涓牱渚嬬储寮�
																	// int
																	// inSelectNumTwo
																	// =
																	// random.nextInt(instances.numInstances());
			Instance instanceSelectOne = ins.instance(inSelectNumOne);
			insvec.getInsv()[j] = instanceSelectOne;
			System.out.println("***********************");
			System.out.println(instanceSelectOne);
			System.out.println("***********************");

		}

	}

	private void printinstancebyclass() {
		for (int j = 0; j < m_Train.numClasses(); j++) {
			// System.out.println("@@@@@@@@@@@@@@@@@@@");
			System.out.println(splitData[j]);
			// System.out.println("@@@@@@@@@@@@@@@@@@@");
		}

	}

	private void printinstancerandom() {
		for (int j = 0; j < m_Train.numClasses(); j++) {
			System.out.println("-----------------------------");
			System.out.println(insvec.getInsv()[j]);
			// System.out.println("@@@@@@@@@@@@@@@@@@@");
		}

	}

}

class Instancevector {
	// private int num;
	private Instance[] insv;

	Instancevector(int number) {
		insv = new Instance[number];
	}

	public Instance[] getInsv() {
		return insv;
	}

	public void setInsv(Instance[] insv) {
		this.insv = insv;
	}

};