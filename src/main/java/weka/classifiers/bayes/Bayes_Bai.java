/**
 * 
 */
package weka.classifiers.bayes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.WeightedInstancesHandler;
import weka.core.Capabilities.Capability;
import weka.estimators.DiscreteEstimator;
import weka.estimators.Estimator;
import weka.estimators.KernelEstimator;
import weka.estimators.NormalEstimator;

/**
 * @author rabbitbaiyu
 *
 */
public class Bayes_Bai extends Classifier 
implements OptionHandler, WeightedInstancesHandler, 
TechnicalInformationHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8659732314201638962L;
	  protected Estimator [][] m_Distributions;

	  /** The class estimator. */
	  protected Estimator m_ClassDistribution;
	  
	  protected boolean m_UseKernelEstimator = false;

	  /**
	   * Whether to use discretization than normal distribution
	   * for numeric attributes
	   */
	  protected boolean m_UseDiscretization = false;

	  /** The number of classes (or 1 for numeric class) */
	  protected int m_NumClasses;

	  /**
	   * The dataset header for the purposes of printing out a semi-intelligible 
	   * model 
	   */
	  protected Instances m_Instances;
	  
	  protected static final double DEFAULT_NUM_PRECISION = 0.01;

	  /**
	   * The discretization filter.
	   */
	  protected weka.filters.supervised.attribute.Discretize m_Disc = null;

	  protected boolean m_displayModelInOldFormat = false;


	/**
	 * @param args
	 * @throws Exception 
	 */
	
	  public static void main(String [] argv) throws Exception {
		    
		  String fileName = "F:/Program Files/weka/data/breast-cancer.arff";
			FileReader frData = new FileReader(fileName);
			Instances m_instances = new Instances(frData);
			System.out.println(m_instances);
			m_instances.setClassIndex(m_instances.numAttributes()-1);
			int i = m_instances.numClasses();
			System.out.println("numclass == "+i);
			Bayes_Bai by = new Bayes_Bai();
			by.buildClassifier(m_instances);
			Instance in = m_instances.firstInstance();
			System.out.println("-------------");
			System.out.println(by);
			System.out.println("--------------");
			in.setClassMissing();
			//by.classifyInstance(in);
			
		    //runClassifier(new Bayes_Bai(), argv);
		  }
	
	
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		// TODO Auto-generated method stub
		bayerslearning bayl = new bayerslearning();
		ArrayList<bayersinstance> ar = new ArrayList<bayersinstance>();
		
		 bayersinstance by = new bayersinstance(); 
		 by.setWeather("sunny"); 
		 by.settemperature("cool");

		for (int i = 1; i < 10; i++) {
			double j1 = (float) Math.random();
			double j2 = Math.random();
			double j3 = Math.random();

			bayersinstance bay = new bayersinstance();
			bay.setId(i);
			if (j1 < 0.3) {
				bay.setWeather("sunny");
			} else if (j1 >= 0.3 && j1 <= 0.7) {
				bay.setWeather("cloudy");
			} else {
				bay.setWeather("rainy");
			}

			if (j2 < 0.3) {
				bay.settemperature("cool");
			} else if (j2 >= 0.3 && j2 <= 0.7) {
				bay.settemperature("mid");
			} else {
				bay.settemperature("hot");
			}

			if (j3 < 0.5) {
				bay.setPlay("yes");
			}

			else {
				bay.setPlay("no");
			}

			ar.add(bay);

		}
		for (bayersinstance bi : ar) {

			System.out.println("id==" + bi.getId() + "  sunny="
					+ bi.getWeather() + "  tempture==" + bi.gettemperature()
					+ "  paly==" + bi.getPlay());

		}
		
		int flag = bayl.bayerslearner(ar, by);
		if (flag ==0){
			System.out.println("no");

		}
		
		else{
			System.out.println("yes");

		}
		
		

	}*/
	
/*public int bayerslearner(ArrayList<bayersinstance> ar, bayersinstance bayin) {
        
		int flag =0;
		String weather = bayin.getWeather();
		String tem = bayin.gettemperature();
		
		System.out.println("the weather is  "+weather);
		System.out.println("the temperature is "+tem);

		
		double yes = 0;
		double no = 0;
		double yes_weather=0;
		double yes_temperature=0;
		double no_weather=0;
		double no_temperature=0;
		
		for (bayersinstance bi : ar) {

			if (bi.getPlay().equalsIgnoreCase("yes")) {
				yes++;
				if(bi.getWeather().equalsIgnoreCase(weather)){
					yes_weather++;
				}
				if(bi.gettemperature().equalsIgnoreCase(tem)){
					yes_temperature++;
				}
				
			}
			if (bi.getPlay().equalsIgnoreCase("no")){
				no++;
				if(bi.getWeather().equalsIgnoreCase(weather)){
					no_weather++;
				}
				if(bi.gettemperature().equalsIgnoreCase(tem)){
					no_temperature++;
				}
			}

		}
		
		double p_yes = yes/(yes+no);
		double p_no = no/(yes+no);
		double P_weather_yes = yes_weather/yes;
		double P_temperature_yes = yes_temperature/yes;
		double P_weather_no = no_weather/no;
		double P_temperature_no = no_temperature/no;
		double p_weather_tempreture_yes=P_weather_yes*P_temperature_yes*p_yes;
		double p_weather_tempreture_no = P_weather_no*P_temperature_no*p_no;
		if(p_weather_tempreture_yes>p_weather_tempreture_no){
			  flag = 1;		  
		}
		else{
			 flag = 0;
		}
		System.out.println("count of yes is	"+yes);
		System.out.println("count of no is	"+no);
		System.out.println("count of yes_"+weather+"	is	"+yes_weather);
		System.out.println("count of yes_"+tem+"	is	"+yes_temperature);
		System.out.println("count of no_"+weather+"	is	"+no_weather);
		System.out.println("count of no_"+tem+"	is	"+no_temperature);
		System.out.println("the p_yes is	"+p_yes);
		System.out.println("the p_no is		"+p_no);
		System.out.println("the P_"+weather+"_yes is	"+P_weather_yes);
		System.out.println("the P_"+tem+"_yes is	"+P_temperature_yes);
		System.out.println("the P_"+weather+"_no is	"+P_weather_no);
		System.out.println("the P_"+tem+"_no is	"+P_temperature_no);
		System.out.println("the p_"+weather+"_"+tem+"_yes is	"+p_weather_tempreture_yes);
		System.out.println("the p_"+weather+"_"+tem+"_no is	"+p_weather_tempreture_no);
		System.out.println("the flag is	"+flag);
				
		return flag;
		

	}*/

@Override
public TechnicalInformation getTechnicalInformation() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void buildClassifier(Instances instances) throws Exception {
	
	


    // can classifier handle the data?
    getCapabilities().testWithFail(instances);

    // remove instances with missing class
    instances = new Instances(instances);
    instances.deleteWithMissingClass();

    m_NumClasses = instances.numClasses();

    // Copy the instances
    m_Instances = new Instances(instances);

    // Discretize instances if required
    if (m_UseDiscretization) {
      m_Disc = new weka.filters.supervised.attribute.Discretize();
      m_Disc.setInputFormat(m_Instances);
      m_Instances = weka.filters.Filter.useFilter(m_Instances, m_Disc);
    } else {
      m_Disc = null;
    }

    // Reserve space for the distributions
    m_Distributions = new Estimator[m_Instances.numAttributes() - 1]
      [m_Instances.numClasses()];
    m_ClassDistribution = new DiscreteEstimator(m_Instances.numClasses(), 
                                                true);
    int attIndex = 0;
    Enumeration enu = m_Instances.enumerateAttributes();
    while (enu.hasMoreElements()) {
      Attribute attribute = (Attribute) enu.nextElement();

      // If the attribute is numeric, determine the estimator 
      // numeric precision from differences between adjacent values
      double numPrecision = DEFAULT_NUM_PRECISION;
      if (attribute.type() == Attribute.NUMERIC) {
	m_Instances.sort(attribute);
	if ((m_Instances.numInstances() > 0)
	    && !m_Instances.instance(0).isMissing(attribute)) {
	  double lastVal = m_Instances.instance(0).value(attribute);
	  double currentVal, deltaSum = 0;
	  int distinct = 0;
	  for (int i = 1; i < m_Instances.numInstances(); i++) {
	    Instance currentInst = m_Instances.instance(i);
	    if (currentInst.isMissing(attribute)) {
	      break;
	    }
	    currentVal = currentInst.value(attribute);
	    if (currentVal != lastVal) {
	      deltaSum += currentVal - lastVal;
	      lastVal = currentVal;
	      distinct++;
	    }
	  }
	  if (distinct > 0) {
	    numPrecision = deltaSum / distinct;
	  }
	}
      }


      for (int j = 0; j < m_Instances.numClasses(); j++) {
	switch (attribute.type()) {
	case Attribute.NUMERIC: 
	  if (m_UseKernelEstimator) {
	    m_Distributions[attIndex][j] = 
	      new KernelEstimator(numPrecision);
	  } else {
	    m_Distributions[attIndex][j] = 
	      new NormalEstimator(numPrecision);
	  }
	  break;
	case Attribute.NOMINAL:
	  m_Distributions[attIndex][j] = 
	    new DiscreteEstimator(attribute.numValues(), true);
	  break;
	default:
	  throw new Exception("Attribute type unknown to NaiveBayes");
	}
      }
      attIndex++;
    }

    // Compute counts
    Enumeration enumInsts = m_Instances.enumerateInstances();
    while (enumInsts.hasMoreElements()) {
      Instance instance = 
	(Instance) enumInsts.nextElement();
      updateClassifier(instance);
    }

    // Save space
    m_Instances = new Instances(m_Instances, 0);
	
	// TODO Auto-generated method stub
	
}


public void updateClassifier(Instance instance) throws Exception {

    if (!instance.classIsMissing()) {
      Enumeration enumAtts = m_Instances.enumerateAttributes();
      int attIndex = 0;
      while (enumAtts.hasMoreElements()) {
	Attribute attribute = (Attribute) enumAtts.nextElement();
	if (!instance.isMissing(attribute)) {
	  m_Distributions[attIndex][(int)instance.classValue()].
            addValue(instance.value(attribute), instance.weight());
	}
	attIndex++;
      }
      m_ClassDistribution.addValue(instance.classValue(),
                                   instance.weight());
    }
  }


public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attributes
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    // instances
    result.setMinimumNumberInstances(0);

    return result;
  }



}
