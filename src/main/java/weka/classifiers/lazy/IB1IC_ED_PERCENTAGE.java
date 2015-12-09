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

import java.util.Enumeration;

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
public class IB1IC_ED_PERCENTAGE extends Classifier implements UpdateableClassifier, TechnicalInformationHandler {

	/** for serialization */
	static final long serialVersionUID = -6152184127304895851L;

	/** The training instances used for classification. */
	private Instances m_Train;

	/** The minimum values for numeric attributes. */
	private double[] m_MinArray;

	/** The maximum values for numeric attributes. */
	private double[] m_MaxArray;
	private IBk_Copy ibkic;
	
	private double percentage;
	
	private double factor;
	
	private int idnumber=20;

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
	
	
	
	 private double distance(Instance first, Instance second) {
		    
		    double diff, distance = 0;

		    for(int i = 0; i < m_Train.numAttributes(); i++) { 
		      if (i == m_Train.classIndex()) {
			continue;
		      }
		      if (m_Train.attribute(i).isNominal()) {

			// If attribute is nominal
			if (first.isMissing(i) || second.isMissing(i) ||
			    ((int)first.value(i) != (int)second.value(i))) {
			  distance += 1;
			}
		      } else {
			
			// If attribute is numeric
			if (first.isMissing(i) || second.isMissing(i)){
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
	 
	 
	  private double norm(double x,int i) {

		    if (Double.isNaN(m_MinArray[i])
			|| Utils.eq(m_MaxArray[i], m_MinArray[i])) {
		      return 0;
		    } else {
		      return (x - m_MinArray[i]) / (m_MaxArray[i] - m_MinArray[i]);
		    }
		  }
	
	
	  
	  private void updateMinMax(Instance instance) {
		    
		    for (int j = 0;j < m_Train.numAttributes(); j++) {
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
		m_MinArray = new double [m_Train.numAttributes()];
	    m_MaxArray = new double [m_Train.numAttributes()];
	    for (int i = 0; i < m_Train.numAttributes(); i++) {
	      m_MinArray[i] = m_MaxArray[i] = Double.NaN;
	    }
	    Enumeration enu = m_Train.enumerateInstances();
	    while (enu.hasMoreElements()) {
	      updateMinMax((Instance) enu.nextElement());
	    }
	    
	    ibkic = new  IBk_Copy();
	    
	    Instances m_Train1 = new Instances(instances, 0, instances.numInstances());
	    
	    m_Train1.stratify(2);
	    Instances train =  m_Train1.trainCV(2, 0);
	    //System.out.println("train	"+train);
	    
	    //System.out.println("------------------------------");
	    //Instances test1 =  m_Train1.trainCV(2, 2);
	    // System.out.println("------------------------------");
	    
	    Instances test =  m_Train1.trainCV(2, 1);
	    //System.out.println("test	"+test);
	    // System.out.println("------------------------------");
	    // System.out.println("test1	"+test);
	   
	    
	    ibkic.buildClassifier(train);  
	    //ibkic.setAttrfactor(1);
	   /* double right = 0;
	    for(int k=0;k<instances.numInstances();k++){	    	
	    	Instance in = instances.instance(k);
	    	double[] classresult = ibkic.distributionForInstance(in);
	    	for(int n =0;n<classresult.length;n++){
	    		 //System.out.println("n ==	"+n+"kn=="+classresult[n]);
	    	}
	    	int index = 0;
	    	double possibility = 0;
	    	for(int m=0;m<classresult.length;m++){
	    		if(classresult[m]>possibility){
	    			index=m;
	    			possibility =classresult[m]; 
	    		}
	    	}
	    	double realvalue = in.classValue();
	    	//System.out.println("index ==	"+index+"	realValue ==	"+realvalue);
	    	if(index == realvalue ){
	    		right++;
	    		//System.out.println("right ==	"+right);
	    	}
	    }*/
	    //double arrc = fitness(m_Train);
	    
	    
	    //ibkic.setAttrfactor(0.5);
	  	//System.out.println(+fitness(test));
	  		
	  	//ibkic.setAttrfactor(0.7);
	  	//System.out.println(fitness(test));
	    double [] fit = new double[idnumber];
	    
	    double numattri = m_Train.numAttributes();
		//System.out.println("-------numattri		"+numattri);
		numattri = Math.sqrt(numattri);
		//System.out.println("------- after sqrt  "+numattri);
		//double radom = Math.random();
		//radom = (numattri-1)*radom+1;
	    
	      
		for (int m = 1; m < idnumber; m++) {
			//ibkic.setAttrfactor(m);
			double radom = Math.random();
			radom = (numattri-1)*radom+1;
			double randomfactor = Math.random();
			randomfactor = 99*randomfactor+1;
			//double m1 = (m*0.1);
			//System.out.println("radom		"+radom);
			//System.out.println(m);
			//System.out.println(m1);
			ibkic.setThreshold(radom);
			ibkic.setAttrfactor(randomfactor);
			fit[m] = fitness(test);
			//System.out.println("fitness	"		+fit[m]);
			//System.out.println(radom+"	"+randomfactor+"	"+fit[m]);
			System.out.println(fit[m]);
		}
		
	    //ibkic.setAttrfactor(1);
	    //double ar = fitness(m_Train);
	    
	    //System.out.println("***********************************"+ar);
	    //ibkic.setAttrfactor(3);
	    //ar = fitness(m_Train);
	    //System.out.println("***********************************"+ar);
	    
	    
/*	    for(int k=1;k<fit.length;k++){
	    	//System.out.println("fit ==	"+k+"   "+fit[k]);
	    	  System.out.println(fit[k]);
	    }*/
	    
	  
	    
	    //System.out.println("total right ==	"+right);
	    //System.out.println("number of instance ==	"+instances.numInstances());
	    
	    //double ar = right/instances.numInstances();
	    
	    //System.out.println("arrc ==	"+arrc);
	    
	    
	    

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
	public double[] distributionForInstance(Instance instance) throws Exception {
		
		//factor = 5;
		//ibkic.setAttrfactor(factor);
		
		return ibkic.distributionForInstance(instance);
		
	
	}

	/**
	 * Returns a description of this classifier.
	 *
	 * @return a description of this classifier as a string.
	 */



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

	/**
	 * Main method for testing this class.
	 *
	 * @param argv
	 *            should contain command line arguments for evaluation (see
	 *            Evaluation).
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
			
		 //runClassifier(new IB1IC_ED_PERCENTAGE(), argv);
		 
		 
		 
		 String filepath ="F:/系统备份/weka-src/data/56Data/anneal.arff";
		 //String filepath ="F:/系统备份/weka-src/data/weather.nominal.arff";
		 IB1IC_ED_PERCENTAGE ib2 = new IB1IC_ED_PERCENTAGE(); 
		 Instances ins =ib2.getinstance(filepath); 
		 Instance inc2 = ins.instance(2);
		 //System.out.println(inc2); 
		 //Instance inc1 = ins.instance(1);
		 //System.out.println(inc3); 
		 // System.out.println(ins);
		 //System.out.println("----------");
		 ins.setClassIndex(ins.numAttributes() - 1); 
		 //System.out.println(ins);
		 //ins.add(inc2); 
		 //System.out.println("---------- instance begin");
		 //System.out.println(ins); 
		 //System.out.println("---------- instance end"); 
		 ib2.buildClassifier(ins); 
		 //double distance = ib2.distance(inc2, inc3); 
		 //System.out.println("distance=="+distance); 
		 //ib2.buildClassifier(ins);
		 // ib2.buildClassifier(ins);  
		 //ib2.classifyInstance(inc2);
		 //System.out.println("---------- classify instance3 ");
		 //System.out.println(inc1); 
		 //System.out.println("---------- instance end"); 
		 //double dis = ib2.classifyInstance(inc1);
		 //System.out.println("hello world	"+dis);
		 //System.out.println();		  
		//System.out.println("hello world	"); 
		// System.out.println(dis);
		// remove instances with missing class
		// ibev.buildClassifier(ins);
		// System.out.println("begin classify " );
		// ibev.classifyInstance(ins.lastInstance());
		// System.out.println("end classify " );
	
		 
	
	
	}
	
	public double  fitness(Instances ins) throws Exception {
		double right = 0;
	    for(int k=0;k<ins.numInstances();k++){	    	
	    	Instance in = ins.instance(k);
	    	//System.out.println("classify instance ==	"+in);
	    	double[] classresult = ibkic.distributionForInstance(in);
	    	for(int n =0;n<classresult.length;n++){
	    		 //System.out.println("n ==	"+n+"kn=="+classresult[n]);
	    	}
	    	int index = 0;
	    	double possibility = 0;
	    	for(int m=0;m<classresult.length;m++){
	    		if(classresult[m]>possibility){
	    			index=m;
	    			possibility =classresult[m]; 
	    		}
	    	}
	    	double realvalue = in.classValue();
	    	//System.out.println("index ==	"+index+"	realValue ==	"+realvalue);
	    	if(index == realvalue ){
	    		right++;
	    		//System.out.println("right ==	"+right);
	    	}
	    }
	    
	    //System.out.println("total right ==	"+right);
	    //System.out.println("number of instance ==	"+ins.numInstances());
	    
	    double ar = right/ins.numInstances();
	    
	    //System.out.println("ar ==	"+ar);
	    return ar;
	}


}
