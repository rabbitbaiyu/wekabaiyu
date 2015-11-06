
package weka.classifiers.lazy;

import java.io.FileReader;
import java.io.Serializable;
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

	//private Instancevector insvec;

	/** Population number */
	private int N = 1000;

	double randomfactor;
	
	private int Maxgen = 300;
	
	private double CR = 0.6;
	private Instancevector bestinsvec;

	Random random = new Random();

	/**
	 * @param args
	 * @throws Exceptionb
	 */

	public static void main(String[] argv) throws Exception {

		//String filepath = "C:/Users/baiyu/Desktop/weka-src/src/main/java/data/diabetes.arff";
		String filepath = "/Users/rabbitbaiyu/git/wekabaiyu/src/main/java/data/diabetes.arff";
		IB1_Evlution ibev = new IB1_Evlution();
		Instances ins = ibev.getinstance(filepath);
		ins.setClassIndex(ins.numAttributes() - 1);		
		ibev.buildClassifier(ins);
		System.out.println("begin classify	" );
		ibev.classifyInstance(ins.lastInstance());
		System.out.println("end   classify	" );
		//runClassifier(new IB1_Evlution(), argv);


	}

	private double[] caculate_fittness(Instancevector[] population) {
		//double[] fittness = new double[N];
		double percentage;
		double[] fittness = new double[N];
		for (int i = 0; i < N; i++) {
			percentage = caculate(population[i]);
			fittness[i] = percentage;
		}
		
		return fittness;


	}

	private void print_fittness(double[] fittness) {
		for (int i = 0; i < fittness.length; i++) {
			System.out.println("fitness		"+i+ "\t"+fittness[i]);
		}

	}
	
	
	
	private Instancevector  instancevectoradd(Instancevector first,Instancevector second,double factor){
		
		Instancevector result = new Instancevector(m_Train.numClasses());
		for(int i =0;i<m_Train.numClasses();i++){
			result.getInsv()[i] = instanceadd(first.getInsv()[i],second.getInsv()[i],factor);
		}
			
		return result;
	}
	
	private Instancevector  instancevectorsub(Instancevector first,Instancevector second,double factor){
		
		Instancevector result = new Instancevector(m_Train.numClasses());
		for(int i =0;i<m_Train.numClasses();i++){
			result.getInsv()[i] = instancesub(first.getInsv()[i],second.getInsv()[i],factor);
		}
			
		return result;
	}
	
	
	private int choose_the_best(double[] fittness){
		
		double bestpercentage = -1;
		int	   bestlabel = -1;
		for (int i = 0; i < N; i++) {
			
			if (fittness[i] > bestpercentage) {
				bestpercentage = fittness[i];
				bestlabel = i;
			}
			//System.out.println("fittness	"+i+"\t" + fittness[i]);
			//System.out.println("bestpercentage " + bestpercentage);
		}
		//System.out.println("-------------bestpercentage--------------" + bestpercentage);
		//System.out.println("bestlabel	" + bestlabel);

		return bestlabel;
		
	}

	public double[][] getClass_min_Array() {
		return class_min_Array;
	}

	public void setClass_min_Array(double[][] class_min_Array) {
		this.class_min_Array = class_min_Array;
	}

	public double[][] getClass_max_Array() {
		return class_max_Array;
	}

	public void setClass_max_Array(double[][] class_max_Array) {
		this.class_max_Array = class_max_Array;
	}

	public Instances[] getSplitData() {
		return splitData;
	}

	public void setSplitData(Instances[] splitData) {
		this.splitData = splitData;
	}


	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
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
		//System.out.println(m_Train);
		//randomfactor = 2*random.nextDouble()-1;
		/*System.out.println("-------------------------");
		System.out.println("first instance "+m_Train.firstInstance());
		System.out.println("-------------------------");
		System.out.println("last instance "+m_Train.lastInstance());*/
		//Instance crossinstance = instancecross(m_Train.firstInstance(),m_Train.lastInstance());
		//System.out.println("sub instance ");
		//System.out.println(crossinstance );
		

		m_MinArray = new double[m_Train.numAttributes()];
		m_MaxArray = new double[m_Train.numAttributes()];
		for (int i = 0; i < m_Train.numAttributes(); i++) {
			m_MinArray[i] = m_MaxArray[i] = Double.NaN;
		}
		Enumeration enu = m_Train.enumerateInstances();
		while (enu.hasMoreElements()) {
			updateMinMax((Instance) enu.nextElement());
		}

		// m_Train.setClassIndex(m_Train.numAttributes() - 1);
		System.out.println("number of attibutes is " + m_Train.numAttributes());
		// int numattribute = m_Train.numAttributes();
		int numdclass = m_Train.numClasses();
		// insvec = new Instancevector(numdclass);
		Instancevector [] population;
		class_min_Array = new double[numdclass][2];
		class_max_Array = new double[numdclass][2];
		System.out.println("number of class is	" + numdclass);

		splitData = new Instances[numdclass];
		for (int j = 0; j < numdclass; j++) {
			splitData[j] = new Instances(m_Train, m_Train.numInstances());
		}

		Enumeration enu1 = m_Train.enumerateInstances();

		while (enu1.hasMoreElements()) {
			Instance in = (Instance) enu1.nextElement();
			splitData[(int) in.classValue()].add(in);

		}

/*		for (int j = 0; j < numdclass; j++) {
			System.out.println(splitData[j]);
			System.out.println("_______________________________________________");
		}*/
		population = randominstance();
		//printinstancerandom();
		
		for(int i = 0;i<Maxgen;i++){
			//System.out.println("------------begin population-----------------");
			//printpopulation(population);
			//System.out.println("------------end population-----------------");
			double fitness [] = caculate_fittness(population);
			//print_fittness(fitness);
			int label = choose_the_best(fitness);
			bestinsvec = population[label];		
			//System.out.println("------------bestinstance-----------------");
			//printinstancerandom(bestinsvec);
			//System.out.println("bestfitness		"+	i +"	"+fitness[label]);
			//System.out.println(fitness[label]);
			Instancevector [] newpopulation = evolution(population,label);
			Instancevector [] crosspopulation = cross(population,newpopulation);
			//System.out.println("------------begin population-----------------");
			//printpopulation(population);
			//System.out.println("------------end population-----------------");
			//System.out.println("------------begin newpopulation-----------------");
			//printpopulation(newpopulation);
			//System.out.println("------------end newpopulation-----------------");
			//System.out.println("------------begin crosspopulation-----------------");
			//printpopulation(crosspopulation);
			//System.out.println("------------end crosspopulation-----------------");
			double cross_fitness [] = caculate_fittness(crosspopulation);
			//print_fittness(cross_fitness);
			Instancevector [] evlutionpopulation =  evlutionpopulation(population,crosspopulation,fitness,cross_fitness);
			population = evlutionpopulation;

					
		}
	
		

		// TODO Auto-generated method stub

	}
	
	
	private Instancevector[] cross(Instancevector[] population,Instancevector[] newpopulation){
		//int randominti;
		//int randomintj;
		Instancevector[] crosspopulation = new Instancevector[N];
		//Instance [] instancearray;
		//Instancevector[] population = new Instancevector[N];
		
		//choose_the_best();
		//Instancevector bestinsvec = population[bestlabel];	
		
		
		for(int i = 0;i<N;i++){
			Instancevector insvector = new Instancevector(m_Train.numClasses());
			for (int j =0;j<m_Train.numClasses();j++){
				//instancearray = crosspopulation[i].getInsv();
				//crosspopulation[i].getInsv()[j] = instancecross(population[i].getInsv()[j],newpopulation[i].getInsv()[j]);
				insvector.getInsv()[j] = instancecross(population[i].getInsv()[j],newpopulation[i].getInsv()[j]);
			}
			crosspopulation[i] = insvector;
			 /*randomfactor = 2*random.nextDouble()-1;
			 //randomfactor = 1;
		
			 randominti = random.nextInt(N);
			 
			 
			 System.out.println("------------bestlabel--------------------"+randominti);
			 printinstancerandom(bestinsvec);
			 System.out.println("------------randominti--------------------"+randominti);
			 printinstancerandom(population[randominti]);
			 insvecti = instancevectoradd(bestinsvec,population[randominti],randomfactor);
			 
	
			 System.out.println("------------addresult---------------------");
			 printinstancerandom(insvecti);
			 
			 randomintj = random.nextInt(N);
			 System.out.println("------------randomintj--------------------"+randomintj);
			 printinstancerandom(population[randomintj]);
			 insvectestj = instancevectorsub(insvecti,population[randomintj],randomfactor);
			 System.out.println("------------subresult---------------------");
			 printinstancerandom(insvectestj);
			 newpopulation[i] = insvectestj;
			*/
		}
		
		return crosspopulation;
/*		System.out.println("------------bestinstance-----------------");
		printinstancerandom(bestinsvec);
		randominti = random.nextInt(N);
		System.out.println("------------randominti--------------------"+randominti);
		printinstancerandom(population[randominti]);
		insvecti = instancevectoradd(bestinsvec,population[randominti]);
		System.out.println("------------addresult---------------------");
		printinstancerandom(insvecti);
		randomintj = random.nextInt(N);
		System.out.println("------------randomintj---------------------"+randomintj);
		printinstancerandom(population[randomintj]);
		System.out.println("------------subresult---------------------");
		insvectestj = instancevectorsub(insvecti,population[randomintj]);
		printinstancerandom(insvectestj);*/
		
	}
	
	private Instancevector[] evlutionpopulation(Instancevector[] population,Instancevector[] crosspopulation,double [] fitness,double [] crossfitness){
		Instancevector[] evlutionpopulation = new Instancevector[N];
/*		System.out.println("------------population--------------------");
		printpopulation(population); 
		System.out.println("------------fitness--------------------");
		print_fittness(fitness);
		System.out.println("------------crosspopulation--------------------");
		printpopulation(crosspopulation); 
		System.out.println("------------crossfitness--------------------");
		print_fittness(crossfitness);*/

		
		
		for(int i =0;i<N;i++){
			if(fitness[i]<=crossfitness[i]){
				evlutionpopulation[i] = crosspopulation[i];
			}
			
			else{
				evlutionpopulation[i] = population[i];
			}
		}
		//System.out.println("------------evlutionpopulation--------------------");
		//printpopulation(evlutionpopulation); 
		return evlutionpopulation;
		
	
		
	}

	private Instancevector[] evolution(Instancevector[] population,int bestlabel){
		int randominti;
		int randomintj;
		Instancevector[] newpopulation = new Instancevector[N];
		//Instancevector[] population = new Instancevector[N];
		
		//choose_the_best();
		Instancevector bestinsvec = population[bestlabel];	
		Instancevector insvecti;
		Instancevector insvectestj;
		
		for(int i = 0;i<N;i++){
			 randomfactor = 2*random.nextDouble()-1;
			 //randomfactor = 1;
		
			 randominti = random.nextInt(N);
			 
			 
			 //System.out.println("------------bestlabel--------------------"+randominti);
			 //printinstancerandom(bestinsvec);
			// System.out.println("------------randominti--------------------"+randominti);
			 //printinstancerandom(population[randominti]);
			 insvecti = instancevectoradd(bestinsvec,population[randominti],randomfactor);
			 
	
			 //System.out.println("------------addresult---------------------");
			 //printinstancerandom(insvecti);
			 
			 randomintj = random.nextInt(N);
			 //System.out.println("------------randomintj--------------------"+randomintj);
			 //printinstancerandom(population[randomintj]);
			 insvectestj = instancevectorsub(insvecti,population[randomintj],randomfactor);
			 //System.out.println("------------subresult---------------------");
			 //printinstancerandom(insvectestj);
			 newpopulation[i] = insvectestj;
			
		}
		
		return newpopulation;
/*		System.out.println("------------bestinstance-----------------");
		printinstancerandom(bestinsvec);
		randominti = random.nextInt(N);
		System.out.println("------------randominti--------------------"+randominti);
		printinstancerandom(population[randominti]);
		insvecti = instancevectoradd(bestinsvec,population[randominti]);
		System.out.println("------------addresult---------------------");
		printinstancerandom(insvecti);
		randomintj = random.nextInt(N);
		System.out.println("------------randomintj---------------------"+randomintj);
		printinstancerandom(population[randomintj]);
		System.out.println("------------subresult---------------------");
		insvectestj = instancevectorsub(insvecti,population[randomintj]);
		printinstancerandom(insvectestj);*/
		
	}
	
	
	private Instance instanceadd(Instance first,Instance second,double factor){
		
		Instance ins = new Instance(first);
		double addresult;

		double diff, distance = 0;

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
					if (first.isMissing(i) && second.isMissing(i)) {
						diff = 1;
					} else {
						if (second.isMissing(i)) {
							diff = norm(first.value(i), i);
						} else {
							diff = norm(second.value(i), i);
						}
						if (diff < 0.5) {
							diff = 1.0 - diff;
						}
					}
				} else {
					//diff = norm(first.value(i), i) - norm(second.value(i), i);
					addresult = (first.value(i)+second.value(i))*factor;
					ins.setValue(i, addresult);
					
				}
				//distance += diff * diff;
			}
		}		
		return ins;
	}
	
	
	private Instance instancesub(Instance first,Instance second,double factor){
		
		Instance ins = new Instance(first);
		double addresult;

		double diff, distance = 0;

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
					if (first.isMissing(i) && second.isMissing(i)) {
						diff = 1;
					} else {
						if (second.isMissing(i)) {
							diff = norm(first.value(i), i);
						} else {
							diff = norm(second.value(i), i);
						}
						if (diff < 0.5) {
							diff = 1.0 - diff;
						}
					}
				} else {
					//diff = norm(first.value(i), i) - norm(second.value(i), i);
					addresult = (first.value(i)-second.value(i))*factor;
					ins.setValue(i, addresult);
					
				}
				//distance += diff * diff;
			}
		}		
		return ins;
	}
	
private Instance instancecross(Instance first,Instance second){
		
		
		int numAttributes = m_Train.numAttributes();
		int dimension = random.nextInt(numAttributes);
		
		double attributeValue = 0;

		double diff, distance = 0;
		Instance ins = new Instance(first);

		for (int i = 0; i < m_Train.numAttributes(); i++) {
			
			
			double danomdouble = Math.random();
			
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
					if (first.isMissing(i) && second.isMissing(i)) {
						diff = 1;
					} else {
						if (second.isMissing(i)) {
							diff = norm(first.value(i), i);
						} else {
							diff = norm(second.value(i), i);
						}
						if (diff < 0.5) {
							diff = 1.0 - diff;
						}
					}
				} else {

					
					if((danomdouble<=CR )||(i == dimension)){
						attributeValue = second.value(i);
					}
					else if((danomdouble>CR)&&(i !=dimension)){
						attributeValue = first.value(i);
					}
					ins.setValue(i, attributeValue);
					//addresult = first.value(i);
				}
				//distance += diff * diff;
			}
		}		
		return ins;
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

		double distance, minDistance = Double.MAX_VALUE, classValue = -1;
		
		updateMinMax(instance);
		Enumeration enu = m_Train.enumerateInstances();
		System.out.println(instance);
/*		while (enu.hasMoreElements()) {
			Instance trainInstance = (Instance) enu.nextElement();
			if (!trainInstance.classIsMissing()) {
				distance = distance(instance, trainInstance);
				if (distance < minDistance) {
					minDistance = distance;
					classValue = trainInstance.classValue();
				}
			}
		}*/
		
		Instance[] insarray = bestinsvec.getInsv();
		for (int j = 0; j < m_Train.numClasses(); j++) {
			// System.out.println(insarray[j]);
			Instance insindividual = new Instance(insarray[j]);
			insindividual.setDataset(m_Train);
			//insindividual.setClassValue(-1);
			//System.out.println("k ==" + k + "  " + "j == " + j);
			//System.out.println("insn"+j+"=="+insarray[j]);

			//System.out.println("insindividual ==" + insindividual);
			// System.out.println(ins.classValue());
			//System.out.println("insm_Train ==" + inst);
			// System.out.println(inst.classValue());
			distance = distance(insindividual, instance);
			//System.out.println("distance ==" + distance);
			if (distance < minDistance) {
				minDistance = distance;
				classValue =insindividual.classValue() ;
				//System.out.println("classValue="+classValue+"  j="+j);
			}
			
			//System.out.println("mindistance ==" + minDistance);
			//System.out.println("index ==" + index);
		}
		//System.out.println("classValue="+classValue);
		
		return classValue;
	}

	private double distance(Instance first, Instance second) {

		double diff, distance = 0;

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
					if (first.isMissing(i) && second.isMissing(i)) {
						diff = 1;
					} else {
						if (second.isMissing(i)) {
							diff = norm(first.value(i), i);
						} else {
							diff = norm(second.value(i), i);
						}
						if (diff < 0.5) {
							diff = 1.0 - diff;
						}
					}
				} else {
					diff = norm(first.value(i), i) - norm(second.value(i), i);
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

	private Instancevector[] randominstance() {
		Instancevector[] population = new Instancevector[N];
		Instancevector insvec;
		for (int k = 0; k < N; k++) {
			insvec = new Instancevector(m_Train.numClasses());
			for (int j = 0; j < m_Train.numClasses(); j++) {
				// random = new Random();
				Instances ins = splitData[j];
				int inSelectNumOne = random.nextInt(ins.numInstances());
				Instance instanceSelectOne = ins.instance(inSelectNumOne);
				insvec.getInsv()[j] = instanceSelectOne;
/*				System.out.println("***********************");
				System.out.println("intSelectNumOne		" + inSelectNumOne);
				System.out.println("instanceSelectOne		" + instanceSelectOne);
				System.out.println("***********************");*/
			}
			population[k] = insvec;

		}
		
		return population;

	}

	private void printinstancebyclass() {
		for (int j = 0; j < m_Train.numClasses(); j++) {
			// System.out.println("@@@@@@@@@@@@@@@@@@@");
			System.out.println(splitData[j]);
			// System.out.println("@@@@@@@@@@@@@@@@@@@");
		}

	}

	private void printpopulation(Instancevector[] population) {

		for (int k = 0; k < N; k++) {
			System.out.println("-----------------------------");
			Instancevector insvec = population[k];
			Instance[] insarray = insvec.getInsv();
			for (int j = 0; j < m_Train.numClasses(); j++) {
				System.out.println(insarray[j]);
			}
		}

	}



	private void printinstancerandom(Instancevector ins) {
		for (int j = 0; j < m_Train.numClasses(); j++) {
			System.out.println("-----------------------------");
			System.out.println(ins.getInsv()[j]);

		}

	}

	private double caculate(Instancevector insvecinput) {

		double[] distancearray = new double[m_Train.numInstances()];
		double[] classlebel = new double[m_Train.numInstances()];
		double minDistance = Double.MAX_VALUE;
		double distance;
		int index = -1;
		// insvec = population[0];
		//insvec = insvecinput;
		Instance[] insarray = insvecinput.getInsv();
		for (int k = 0; k < m_Train.numInstances(); k++) {
			Instance inst = new Instance(m_Train.instance(k));
			for (int j = 0; j < m_Train.numClasses(); j++) {
				// System.out.println(insarray[j]);
				Instance insindividual = new Instance(insarray[j]);
				//System.out.println("k ==" + k + "  " + "j == " + j);
				// System.out.println("insnj =="+insarray[j]);

				//System.out.println("insindividual ==" + insindividual);
				// System.out.println(ins.classValue());
				//System.out.println("insm_Train ==" + inst);
				// System.out.println(inst.classValue());
				distance = distance(insindividual, inst);
				//System.out.println("distance ==" + distance);
				if (distance < minDistance) {
					minDistance = distance;
					index = j;
				}
				//System.out.println("mindistance ==" + minDistance);
				//System.out.println("index ==" + index);
			}

			classlebel[k] = index;
			distancearray[k] = minDistance;
			minDistance = Double.MAX_VALUE;
			index = -1;

		}

/*		for (int k = 0; k < m_Train.numInstances(); k++) {
			System.out.print(classlebel[k] + "\t");
		}
		System.out.println();
		for (int k = 0; k < m_Train.numInstances(); k++) {
			System.out.print(distancearray[k] + "\t");
		}*/
		System.out.println();
		double right = 0;
		double wrong = 0;
		double percent = 0;
		for (int k = 0; k < m_Train.numInstances(); k++) {
			//System.out.println("classlabel	" + classlebel[k]+"	instancelabel	"+m_Train.instance(k).classValue());
			//System.out.println("instancelabel	" + m_Train.instance(k).classValue());
			if (m_Train.instance(k).classValue() == classlebel[k]) {
				right++;
			} else {
				wrong++;
			}
		}

		percent = right / (right + wrong);
		//System.out.println("percentage	==	" + percent);
		return percent;

	}
	
	
	private class Instancevector implements Serializable {
		// private int num;
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -812101155693927872L;
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

	}

}

/*class Instancevector {
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

}*/


