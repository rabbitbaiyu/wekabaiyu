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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;

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
public class IB1IC_facor_threshold extends Classifier implements UpdateableClassifier, TechnicalInformationHandler {

	/** for serialization */
	static final long serialVersionUID = -6152184127304895851L;

	/** The training instances used for classification. */
	private Instances m_Train;

	/** The minimum values for numeric attributes. */
	private double[] m_MinArray;

	/** The maximum values for numeric attributes. */
	private double[] m_MaxArray;
	
	private IBk_Copy ibkic;
	
	private int idnumber=10;

	private int Maxgen=50;
	
	//private double randomfactor;
	
	private double CR = 0.5;
	
	private int numberoffactor = 2;
	
	private double maxfactor = 10;
	
	private double minfactor = 1;
	
	private double final_a ;
	
	private double final_b ;
	
	
	
	
	
	
	private Random random = new Random();

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
	    
	    //Instances m_Train1 = new Instances(instances, 0, instances.numInstances());
	    
	    m_Train.stratify(10);
	    Instances train =  m_Train.trainCV(10, 0);
	    //System.out.println("train	"+train);
	    
	    //System.out.println("------------------------------");
	    //Instances test1 =  m_Train1.trainCV(2, 2);
	    // System.out.println("------------------------------");
	    
	    Instances test =  m_Train.testCV(10, 0);
	    //System.out.println("test	"+test);
	    // System.out.println("------------------------------");
	    // System.out.println("test1	"+test);
	   
	    //System.out.println("111");
	    ibkic.buildClassifier(train);  
	    //System.out.println("222");
	    
	    
	    Instancevector [] population;
	    //System.out.println("population.length	"+population.length);
	    population = randominstance();
	    //System.out.println("222");
	   
	    //System.out.println("population.length	"+population.length);
	    //printpopulation(population);
	    
	    
	    double fitness [] = null;
	    
	    //fitness   = caculate_fittness(population,test);
	    //printpopulation(population);
	    //print_fittness(fitness);
	    int count=0;
	    
	    
	    for(int i = 0;i<Maxgen;i++){
			//System.out.println("------------begin population-----------------");
			//printpopulation(population);
			//System.out.println("------------end population-----------------");
			fitness   = caculate_fittness(population,test);
			//System.out.println("333");
			//print_fittness(fitness);
			//System.out.println("count	" + count);
			int label = choose_the_best(fitness);
			System.out.println("label	" + label+"		"+fitness[label]);
			Instancevector [] newpopulation = evolution(population,label);
			//System.out.println("444");
			//System.out.println("------------begin population-----------------");
			//printpopulation(population);
			//System.out.println("------------end population-----------------");
			//System.out.println("------------begin new population-----------------");
			//printpopulation(newpopulation);
			//System.out.println("------------end new  population-----------------");
			Instancevector [] crosspopulation = cross(population,newpopulation);
			//System.out.println("555");
			//System.out.println("------------begin crosspopulation population-----------------");
			//printpopulation(crosspopulation);
			//System.out.println("------------end   crosspopulation  population-----------------");
			double cross_fitness [] = caculate_fittness(crosspopulation,test);
			//System.out.println("666");
			//System.out.println("------------fitness-----------------");
			//print_fittness(fitness);
			//System.out.println("------------crossfitness-----------------");
			//print_fittness(cross_fitness);
			Instancevector [] evlutionpopulation =  evlutionpopulation(population,crosspopulation,fitness,cross_fitness);
			//System.out.println("777");
			population = evlutionpopulation;		
			//System.out.println("------------begin evlutionpopulation-----------------");
			//printpopulation(evlutionpopulation);
			//System.out.println("------------end evlutionpopulation-----------------");
			//int label = choose_the_best(fitness);
			//bestinsvec = population[label];		
			//System.out.println("------------bestinstance-----------------");
			//printinstancerandom(bestinsvec);
			//System.out.println("bestfitness		"+	i +"	"+fitness[label]);
			//System.out.println(fitness[label]);
			//Instancevector [] newpopulation = evolution(population,label);
			//Instancevector [] crosspopulation = cross(population,newpopulation);
			//System.out.println("------------begin population-----------------");
			//printpopulation(population);
			//System.out.println("------------end population-----------------");
			//System.out.println("------------begin newpopulation-----------------");
			//printpopulation(newpopulation);
			//System.out.println("------------end newpopulation-----------------");
			//System.out.println("------------begin crosspopulation-----------------");
			//printpopulation(crosspopulation);
			//System.out.println("------------end crosspopulation-----------------");
			//double cross_fitness [] = caculate_fittness(crosspopulation);
			//print_fittness(cross_fitness);
			//Instancevector [] evlutionpopulation =  evlutionpopulation(population,crosspopulation,fitness,cross_fitness);
			//population = evlutionpopulation;
			count++;

					
		}
	    
	    //print_fittness(fitness);
	    //printpopulation(population);
	    
	    int label = choose_the_best(fitness);
		//System.out.println("label	" + label+"		"+fitness[label]);
		//System.out.println("label	" + label+"		"+population[label]);
		final_a = population[label].getInsv()[0];
		final_b = population[label].getInsv()[1];
		
		
		
		
		//System.out.println("final_a	" + final_a+"		");
		//System.out.println("final_b	" + final_b+"		");
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	/*    for(int k =0;k<idnumber;k++){
	    	
	    	System.out.println("----random population");
	    	printinstancerandom(population[k]);
	    }*/
	    //printinstancerandom();
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
	    //double [] fit = new double[idnumber];
	    
	    //double numattri = m_Train.numAttributes();
		//System.out.println("-------numattri		"+numattri);
		//numattri = Math.sqrt(numattri);
		//System.out.println("------- after sqrt  "+numattri);
		//double radom = Math.random();
		//radom = (numattri-1)*radom+1;
	    
	      
/*		for (int m = 1; m < idnumber; m++) {
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
		}*/
		
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
	
	

	
	private void printpopulation(Instancevector[] population) {

		for (int k = 0; k < idnumber; k++) {
			System.out.println("-----------------------------");
			Instancevector insvec = population[k];
			double [] insarray = insvec.getInsv();
			for (int j = 0; j < numberoffactor; j++) {
				System.out.println(insarray[j]);
			}
		}

	}
	
	
	private void print_fittness(double[] fittness) {
		for (int i = 0; i < fittness.length; i++) {
			System.out.println("fitness		"+i+ "\t"+fittness[i]);
		}

	}
	
	
	
	private int choose_the_best(double[] fittness){
		
		double bestpercentage = -1;
		int	   bestlabel = -1;
		for (int i = 0; i < idnumber; i++) {
			
			if (fittness[i] > bestpercentage) {
				bestpercentage = fittness[i];
				bestlabel = i;
			}
			//System.out.println("fittness	"+i+"\t" + fittness[i]);
			//System.out.println("bestpercentage " + bestpercentage);
		}
		//System.out.println("-------------bestpercentage--------------" + bestpercentage);
		//System.out.println("bestlabel	" + bestlabel);
		
		//System.out.println("bestlabel	" + bestlabel+"		"+fittness[bestlabel]);
		return bestlabel;
		
		
	}

	private void printinstancerandom(Instancevector ins) {
		for (int j = 0; j < numberoffactor; j++) {
			System.out.println("-----------------------------");
			System.out.println(ins.getInsv()[j]);

		}

	}

	
	
	private Instancevector[] evlutionpopulation(Instancevector[] population,Instancevector[] crosspopulation,double [] fitness,double [] crossfitness){
		Instancevector[] evlutionpopulation = new Instancevector[idnumber];
/*		System.out.println("------------population--------------------");
		printpopulation(population); 
		System.out.println("------------fitness--------------------");
		print_fittness(fitness);
		System.out.println("------------crosspopulation--------------------");
		printpopulation(crosspopulation); 
		System.out.println("------------crossfitness--------------------");
		print_fittness(crossfitness);*/

		
		
		for(int i =0;i<idnumber;i++){
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
		Instancevector[] newpopulation = new Instancevector[idnumber];
		//Instancevector[] population = new Instancevector[N];
		
		//choose_the_best();
		Instancevector bestinsvec = population[bestlabel];	
		Instancevector insvecti;
		Instancevector insvectestj;
		
		for(int i = 0;i<idnumber;i++){
			
			 double randomfactor = 1;
			 //System.out.println("------------randomfactor		"+randomfactor);
			 //randomfactor = 1;		
			 randominti = random.nextInt(idnumber);	
			 while(randominti == bestlabel){
				 randominti = random.nextInt(idnumber);			 
			 }
			 //System.out.println("------------bestinstance--------------------");
			 //printinstancerandom(bestinsvec);
			 //System.out.println("------------randominti			"+randominti);
			 //System.out.println("------------randominti--------------------");
			 //printinstancerandom(population[randominti]);
			 //insvecti = instancevectoradd(bestinsvec,population[randominti],randomfactor);
			 //System.out.println("------------addresult---------------------");
			 //printinstancerandom(insvecti);		 
			 randomintj = random.nextInt(idnumber);
			 while(randominti == randomintj||bestlabel == randomintj){
				 randomintj = random.nextInt(idnumber);			 
			 }
		
			 
			//System.out.println("------------bestlabel		"+bestlabel);
			//System.out.println("------------randominti		"+randominti);
			//System.out.println("------------randomintj--------------------");
			//System.out.println("------------randomintj		"+randomintj);
			 //System.out.println("------------randomintj--------------------");
			 //printinstancerandom(population[randomintj]);
			 insvectestj = instancevectorsub(population[randominti],population[randomintj],randomfactor);
			 //System.out.println("------------subresult---------------------");
			 //printinstancerandom(insvectestj);
			 insvecti = instancevectoradd(bestinsvec,insvectestj,1);
			 //System.out.println("------------total result---------------------");
			 //printinstancerandom(insvecti);
			 newpopulation[i] = insvecti;			
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
	
	
	
	private Instancevector[] cross(Instancevector[] population,Instancevector[] newpopulation){
		//int randominti;
		//int randomintj;
		Instancevector[] crosspopulation = new Instancevector[idnumber];
		//Instance [] instancearray;
		//Instancevector[] population = new Instancevector[N];
		
		//choose_the_best();
		//Instancevector bestinsvec = population[bestlabel];	
		
		
		for(int i = 0;i<idnumber;i++){
			Instancevector insvector = new Instancevector(numberoffactor);
			Random rand = new Random();
			int flag = rand.nextInt(2);
			//System.out.println("------------flag--------------------"+flag);
			for (int j =0;j<numberoffactor;j++){
				//instancearray = crosspopulation[i].getInsv();
				//crosspopulation[i].getInsv()[j] = instancecross(population[i].getInsv()[j],newpopulation[i].getInsv()[j]);
				double danomdouble = Math.random();
				double first = population[i].getInsv()[j];
				double second = newpopulation[i].getInsv()[j];
				if((danomdouble<=CR)||(flag==j)){
					insvector.getInsv()[j] = second; 
				}
				else //if((danomdouble>CR)&&(flag!=j))
					
				{
					insvector.getInsv()[j] = first; 
				}
				
				//double first = population[i].getInsv()[j];
				//double second = newpopulation[i].getInsv()[j];
				//insvector.getInsv()[j] = instancecross(population[i].getInsv()[j],newpopulation[i].getInsv()[j]);
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
	
	
private double instancecross(double first,double second){
		
		
	
	    double  crossvalue =0; 
	    double danomdouble = Math.random();
	    if(danomdouble>=CR)
	    {
	    	crossvalue = first;
	    }
	    else
	    	crossvalue = second;
		//int numAttributes = m_Train.numAttributes();
		//int dimension = random.nextInt();
		
		//double attributeValue = 0;

		//double diff, distance = 0;
		//Instance ins = new Instance(first);

		/*for (int i = 0; i < m_Train.numAttributes(); i++) {
			
			
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
		}*/		
		return crossvalue;
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
		//ibkic.setAttrfactor(a);
		
		ibkic.setThreshold(final_a);
		ibkic.setAttrfactor(final_b);
		//System.out.println("***********************"+final_a);
		//System.out.println("***********************"+final_b);
		
		
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

	
	private Instancevector[] randominstance() {
		Instancevector[] population = new Instancevector[idnumber];
		Instancevector insvec;
/*		for (int k = 0; k < idnumber; k++) {
			insvec = new Instancevector(m_Train.numClasses());
			for (int j = 0; j < m_Train.numClasses(); j++) {
				// random = new Random();
				Instances ins = splitData[j];
				int inSelectNumOne = random.nextInt(ins.numInstances());
				Instance instanceSelectOne = ins.instance(inSelectNumOne);
				insvec.getInsv()[j] = instanceSelectOne;
				System.out.println("***********************");
				System.out.println("intSelectNumOne		" + inSelectNumOne);
				System.out.println("instanceSelectOne		" + instanceSelectOne);
				System.out.println("***********************");
			}
			population[k] = insvec;

		}
		*/
		
		double numattri = m_Train.numAttributes();
			      
		for (int m = 0; m < idnumber; m++) {
			//ibkic.setAttrfactor(m);
			insvec = new Instancevector(numberoffactor);
			double radom = Math.random();
			radom = (numattri-1)*radom+1;
			// radom [1,numattri]
			double randomfactor = Math.random();
			// radomfactor [n of copied instances]
			randomfactor = (maxfactor-minfactor)*randomfactor+minfactor;
			//population[m][0]= radom;
			insvec.getInsv()[0] = radom;
			insvec.getInsv()[1] = randomfactor;
			population[m] = insvec;
			//double m1 = (m*0.1);
			//System.out.println("radom		"+radom);
			//System.out.println(m);
			//System.out.println(m1);
			//ibkic.setThreshold(radom);
			//ibkic.setAttrfactor(randomfactor);
			//fit[m] = fitness(test);
			//System.out.println("fitness	"		+fit[m]);
			//System.out.println(radom+"	"+randomfactor+"	"+fit[m]);
			//System.out.println(fit[m]);
		}
		
		return population;

	}
	
	
	
	private double [] caculate_fittness(Instancevector[] population,Instances ins) throws Exception{
		
		    double [] fitness = new double[idnumber];
		    		      
			for (int m = 0; m < idnumber; m++) {
				
				Instancevector insver = population[m];
				double [] vector = insver.getInsv();
				double radom = vector[0];
				double randomfactor = vector[1];
				
				//ibkic.setAttrfactor(m);
				//insvec = new Instancevector(2);
				//double radom = Math.random();
				//radom = (numattri-1)*radom+1;
				//double randomfactor = Math.random();
				//randomfactor = 49*randomfactor+1;
				//population[m][0]= radom;
				//insvec.getInsv()[0] = radom;
				//insvec.getInsv()[1] = randomfactor;
				//population[m] = insvec;
				//double m1 = (m*0.1);
				//System.out.println("radom		"+radom);
				//System.out.println(m);
				//System.out.println(m1);
				ibkic.setThreshold(radom);
				ibkic.setAttrfactor(randomfactor);
				fitness[m] = fitness(ins);
				//System.out.println("fitness	"		+fit[m]);
				//System.out.println(radom+"	"+randomfactor+"	"+fitness[m]);
				//System.out.println(fitness[m]);
			}
		     
		    
		    return fitness;
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
	
	
	
	private Instancevector  instancevectoradd(Instancevector first,Instancevector second,double factor){
		
		Instancevector result = new Instancevector(numberoffactor);
	/*	for(int i =0;i<m_Train.numClasses();i++){
			result.getInsv()[i] = instanceadd(first.getInsv()[i],second.getInsv()[i],factor);
		}*/
		double firsta = first.getInsv()[0];
		double firstb = first.getInsv()[1];
		double seconda =second.getInsv()[0];
		double secondb =second.getInsv()[1];
		double resulta = (firsta+seconda)*factor;
		double resultb = (firstb+secondb)*factor;
		result.getInsv()[0] = resulta;
		result.getInsv()[1] = resultb;	
		return result;
	}
	
	
	
	private Instancevector  instancevectorsub(Instancevector first,Instancevector second,double factor){
		
		Instancevector result = new Instancevector(numberoffactor);
	/*	for(int i =0;i<m_Train.numClasses();i++){
			result.getInsv()[i] = instanceadd(first.getInsv()[i],second.getInsv()[i],factor);
		}*/
		double firsta = first.getInsv()[0];
		double firstb = first.getInsv()[1];
		double seconda =second.getInsv()[0];
		double secondb =second.getInsv()[1];
		double resulta = (firsta-seconda)*factor;
		double resultb = (firstb-secondb)*factor;
		result.getInsv()[0] = resulta;
		result.getInsv()[1] = resultb;	
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 5525 $");
	}

	Instances getinstance(String s) throws Exception {

		DataSource source = new DataSource(s);
		Instances data = source.getDataSet();
		// System.out.println(data);
		//System.out.println("**************");
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
			
		 //runClassifier(new IB1IC_facor_threshold(), argv);
		 
		 
		 
		 String filepath ="F:/系统备份/weka-src/data/56Data/iris.arff";
		 //String filepath ="F:/系统备份/weka-src/data/weather.nominal.arff";
		 IB1IC_facor_threshold ib2 = new IB1IC_facor_threshold(); 
		 Instances ins =ib2.getinstance(filepath); 
		 Instance inc2 = ins.instance(2);
		 //System.out.println(inc2); 
		 //Instance inc1 = ins.instance(1);
		 //System.out.println(inc3); 
		 // System.out.println(ins);
		 //System.out.println("----------");
		 ins.setClassIndex(ins.numAttributes() - 1); 
		 ib2.buildClassifier(ins); 
		 //ib2.distributionForInstance(inc2);
		 
		 
/*		 ins.stratify(10);
		 
			
		 Instances train =  ins.trainCV(10, 0);
		 
		 Instances test =  ins.testCV(10, 0);
		 
		 
		 System.out.println("train	"+train.numInstances());
		 System.out.println("test	"+test.numInstances());
		 
		 System.out.println("train	"+train);
		 System.out.println("test	"+test);*/
		 
		 
		 
		    
		    //System.out.println("------------------------------");
		    //Instances test1 =  m_Train1.trainCV(2, 2);
		    // System.out.println("------------------------------");
		    
		 //Instances test =  ins.testCV(2, 1);
		 
		 //System.out.println("test	"+test);
		 //System.out.println(ins);
		 //ins.add(inc2); 
		 //System.out.println("---------- instance begin");
		 //System.out.println(ins); 
		 //System.out.println("---------- instance end"); 
		
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
	
	
	
	
	
	private class Instancevector implements Serializable {
	
		private static final long serialVersionUID = -3468720922109550007L;
		// private int num;
		
		//private static final long serialVersionUID ;
		private double[] insv;

		Instancevector(int number) {
			insv = new double[number];
		}

		public double [] getInsv() {
			return insv;
		}

		public void setInsv(double [] insv) {
			this.insv = insv;
		}

	}
	
	
	
	


}
