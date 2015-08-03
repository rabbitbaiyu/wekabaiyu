
package weka.classifiers.lazy;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.lazy.IB1;
import weka.classifiers.trees.Id3;
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

//import knn_learning.entrytester.ValueComparator;

/**
 * @author baiyu
 *
 */
public class IB1_Bai extends Classifier 
implements UpdateableClassifier, TechnicalInformationHandler{

	/**
	 * 
	 */
	private Instances m_Train;
	private static final long serialVersionUID = 1L;

	/** The minimum values for numeric attributes. */
	private double[] m_MinArray;

	/** The maximum values for numeric attributes. */
	private double[] m_MaxArray;

	/**
	 * @param args
	 * @throws Exception
	 */
	
	  public static void main(String [] argv) {
		    runClassifier(new IB1_Bai(), argv);
		  }
	
	/*public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String fileName = "F:/Program Files/weka/data/contact-lenses.arff";
		FileReader frData = new FileReader(fileName);
		Instances m_instances = new Instances(frData);
		System.out.println(m_instances);
		m_instances.setClassIndex(m_instances.numAttributes()-1);
	    IB1 ib1 = new IB1();
	    Id3 id3 = new Id3();
	    ib1.buildClassifier(m_instances);
	    Instance ins = m_instances.instance(1);
	    double cl = ib1.classifyInstance(ins);
	    System.out.println(cl);
	    System.out.println("-----------");
	    id3.buildClassifier(m_instances);
	    System.out.println("-----------end");
	    System.out.println(ib1);
	    System.out.println("-----------end");
	    System.out.println(id3);
	    Instances ins1;
	    Instance in;
	    Id3 id31 = new Id3();
	    
	    Classifier cls;
	    
		ArrayList<instance> ar = new ArrayList<instance>();
		// ArrayList ar1 = new ArrayList();
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		int i = 1;

		while (i <= 100) {
			int m, n;
			m = i;
			n = i + 20;
			int flag = Math.random() > 0.5 ? 1 : 0;
			instance in = new instance();
			in.setId(i);
			in.setAttribute1(m);
			in.setAttribute2(n);
			in.setFlag(flag);
			ar.add(in);
			i++;

		}

		System.out.println("print the original instances-----------begin");

		for (instance k : ar) {

			System.out.println(k.getId() + "\t" + k.getAttribute1() + "\t"
					+ k.getAttribute2() + "\t" + k.getFlag());

		}

		System.out.println("print the original instances-----------end");

		System.out.println("input the number you want to perdict");
		Scanner sc = new Scanner(System.in);
		System.out.println("input the first attribute：");
		int at1 = sc.nextInt();
		System.out.println("input the second attribute:");
		int at2 = sc.nextInt();
		System.out.println("你的信息如下：");
		System.out.println("first：" + at1 + "\t" + "second：" + at2);

		for (instance k : ar) {
			double distance;
			int kat1 = k.getAttribute1();
			int kat2 = k.getAttribute2();
			distance = Math.sqrt(java.lang.Math.pow((kat1 - at1), 2)
					+ java.lang.Math.pow((kat2 - at2), 2));
			map.put(k.getId(), distance);

		}
		
		for(int i =0;i<	m_instances.numInstances();i++){
			Instance instance = m_instances.instance(i);
			
		}
		System.out.println("input the distance to every exist instance-----------begin");
		for (Entry<Integer, Double> entry : map.entrySet()) {

			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue());

		}
		System.out.println("input the distance to every exist instance-----------end");

		List<Map.Entry<Integer, Double>> list = new ArrayList<Entry<Integer, Double>>();
		list.addAll(map.entrySet());
		entrytester.ValueComparator vc = new ValueComparator();
		Collections.sort(list, vc);

		System.out.println("sort by the distance-----------begin");
		for (Iterator<Entry<Integer, Double>> it = list.iterator(); it
				.hasNext();) {
			System.out.println(it.next());

		}
		
		
		System.out.println("sort by the distance-----------end");
		Entry<Integer, Double> en = list.get(0);
		int findindex = en.getKey();
		System.out.println("-------------index " +findindex +"-------------- ");
		instance in1 = ar.get(findindex);
		int value1 = in1.getAttribute1();
		int value2 = in1.getAttribute2();
		
		System.out.println("The 1 nearest neighbor is the " +findindex +"th one ");
		System.out.println("The first value for the node is "+value1);
		System.out.println("The second value for the node is "+value2);
		
		

	}*/

	@Override
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
		
		// TODO Auto-generated method stub
		
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
	    TechnicalInformation 	result;
	    
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
		    
		    double diff=0 ,diffsqa, distance = 0;

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
/*			  if (first.isMissing(i) && second.isMissing(i)) {
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
			  }*/
			} else {
			  //diff = norm(first.value(i), i) - norm(second.value(i), i);
				 diff = norm(first.value(i), i) - norm(second.value(i), i);
				//diff = java.lang.Math.pow(diffsqa,2);
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
		      + "the same (smallest) distance to the test instance, the first one found is "
		      + "used.\n\n"
		      + "For more information, see \n\n"
		      + getTechnicalInformation().toString();
		  }
	  
	  private double norm(double x,int i) {

		    if (Double.isNaN(m_MinArray[i])
			|| Utils.eq(m_MaxArray[i], m_MinArray[i])) {
		      return 0;
		    } else {
		      return (x - m_MinArray[i]) / (m_MaxArray[i] - m_MinArray[i]);
		    }
		  }
	  
	  

	  
	


}
