/**
 * 
 */
package weka;

import java.util.ArrayList;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * @author rabbitbaiyu
 *
 */
public class evolutionlearning {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 * @throws Exception
	 */
	/**
	 * @param args
	 * @throws Exception
	 */
	double min_a = Double.POSITIVE_INFINITY;
	double max_a = Double.NEGATIVE_INFINITY;
	double min_b = Double.POSITIVE_INFINITY;
	double max_b = Double.NEGATIVE_INFINITY;

	public double getMax_a() {
		return max_a;
	}

	public void setMax_a(double max_a) {
		this.max_a = max_a;
	}

	public double getMin_a() {
		return min_a;
	}

	public void setMin_a(double min_a) {
		this.min_a = min_a;
	}

	public double getMax_b() {
		return max_b;
	}

	public void setMax_b(double max_b) {
		this.max_b = max_b;
	}

	public double getMin_b() {
		return min_b;
	}

	public void setMin_b(double min_b) {
		this.min_b = min_b;
	}

	public static void main(String[] args) throws Exception {

		String filepath = "../weka/data/evolution.arff";
		evolutionlearning el = new evolutionlearning();
		Instances data = el.getinstance(filepath);
		el.get_a_b_Range(data);
		ArrayList<factornode> alf = el.get_Initialization();
		el.get_evolution(alf, data);
		// System.out.println(al);

	}

	Instances getinstance(String s) throws Exception {

		DataSource source = new DataSource(s);
		Instances data = source.getDataSet();
		System.out.println(data);
		System.out.println("**************");
		return data;
	}

	void get_a_b_Range(Instances data) throws Exception {
		// ArrayList al = new ArrayList();

		for (int i = 0; i < data.numInstances(); i++) {
			for (int j = i + 1; j < data.numInstances(); j++) {
				Instance insi = data.instance(i);
				Instance insj = data.instance(j);
				double xi = insi.value(1);
				double yi = insi.value(2);
				double xj = insj.value(1);
				double yj = insj.value(2);
				System.out.println("node i:	x=" + xi + "	y=" + yi);
				System.out.println("node j:	x=" + xj + "	y=" + yj);
				double a = (yj - yi) / (xj - xi);
				double b = (yi * xj - yj * xi) / (xj - xi);

				if (a > max_a) {
					max_a = a;
				}
				if (a < min_a) {
					min_a = a;
				}
				if (b > max_b) {
					max_b = b;
				}
				if (b < min_b) {
					min_b = b;
				}

				System.out.println("a=	" + a);
				System.out.println("max_a =" + max_a + "	min_a=" + min_a);
				System.out.println("b=	" + b);
				System.out.println("max_b =" + max_b + "	min_b=" + min_b);

			}

		}
		setMax_a(max_a);
		setMin_a(min_a);
		setMax_b(max_b);
		setMin_b(min_b);

	}

	ArrayList<factornode> get_Initialization() {

		ArrayList<factornode> alf = new ArrayList<factornode>();

		for (int i = 0; i < 5; i++) {
			double a = (Math.random()) * (max_a - min_a) + min_a;
			double b = -1;
			double c = (Math.random()) * (max_b - min_b) + min_b;
			factornode fn = new factornode();
			fn.setA(a);
			fn.setB(b);
			fn.setC(c);
			alf.add(fn);

		}

		for (factornode i : alf) {
			System.out.println("	a=	" + i.getA() + "	b=	" + i.getB() + "	c=	"
					+ i.getC());
		}

		return alf;
	}

	double fitness(Instances data, factornode i) {

		double dot_line_siatance = 0;
		double sum_distance = 0;
		for (int k = 0; k < data.numInstances(); k++) {
			Instance ik = data.instance(k);
			double kx = ik.value(1);
			double ky = ik.value(2);
			dot_line_siatance = Math.abs((i.getA() * kx + i.getB() * ky + i
					.getC())
					/ Math.sqrt(i.getA() * i.getA() + i.getB() * i.getB()));
			sum_distance = sum_distance + dot_line_siatance;
			System.out.println("a=" + i.getA() + "	b=" + i.getB() + "	c="
					+ i.getC() + "	x=" + kx + "	y=" + ky
					+ "	dot_line_siatance=" + dot_line_siatance);
		}

		return sum_distance;
	}

	void selectNextGenaration(factornode fn, ArrayList alf) {

		alf.clear();
		alf.add(fn);
		for (int i = 0; i < 4; i++) {
			factornode fn1 = new factornode();
			double an = (Math.random()) * (max_a - min_a) + min_a;
			double bn = -1;
			double cn = (Math.random()) * (max_b - min_b) + min_b;
			fn1.setA(an);
			fn1.setB(bn);
			fn1.setC(cn);
			alf.add(fn1);
		}

	}

	void get_evolution(ArrayList<factornode> alf, Instances data) {

		int loopcount = 0;
		double min_distance = Double.POSITIVE_INFINITY;
		double a = 0;
		double b = 0;
		double c = 0;
		while (loopcount < 100) {
			for (factornode i : alf) {
				double dot_line_siatance = 0;
				double sum_distance = 0;
				sum_distance = fitness(data, i);
				System.out.println("sum_distance = " + sum_distance);
				if (sum_distance < min_distance) {
					min_distance = sum_distance;
					a = i.getA();
					b = i.getB();
					c = i.getC();
				}

			}

			System.out.println("min_distance = " + min_distance + "	a = " + a
					+ "	b = " + b + "	c= " + c);
			factornode fn = new factornode();
			fn.setA(a);
			fn.setB(b);
			fn.setC(c);
			fn.setMin_distance(min_distance);
			selectNextGenaration(fn, alf);

			System.out.println("------------------------------------");
			for (factornode i : alf) {

				System.out.println("	a=	" + i.getA() + "	b=	" + i.getB()
						+ "	c=	" + i.getC());
			}
			System.out.println("-------------------------------------");

			loopcount++;
		}

		System.out.println("a===" + a);
		System.out.println("b===" + b);
		System.out.println("c===" + c);
		System.out.println("min_distance===" + min_distance);
		// System.out.println("yubai===" + min_distance);

	}

}

