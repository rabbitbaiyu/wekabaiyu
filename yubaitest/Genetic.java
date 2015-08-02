package weka.Mygenetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import weka.core.Instance;
import weka.core.Instances;

/**
 * @author ZhiChao Wang
 *
 */
public class Genetic {

	public Instances getInstances() {
		return instances;
	}

	public void setInstances(Instances instances) {
		this.instances = instances;
	}

	// private int speciesNum=50;//种群数量
	private double[] finness;// 适应度
	private static int generationNum = 200;// 进化代数
	private double crossPro = 0.25;// 交叉概率
	private double mutationPro = 0.02;// 变异概率
	Random random = new Random();
	// instanceOpe instanceope = new instanceOpe();
	int attSelectNum;

	Instances instances;

	public double fitness(Instance instance, Instances instances) {// 求当前样例和其他样例的距离
		double sum = 0;
		double result = 0;
		for (int i = 0; i < instances.numInstances(); i++) {

			for (int j = 0; j < instance.numAttributes(); j++) {
				sum += Math.pow(instance.value(j)
						- instances.instance(i).value(j), 2);
			}
			result += sum;
		}
		return result / instances.numInstances();
	}

	public void Genetic() {

	}

	/**
	 * Select Method
	 */
	public void select(Instance[] instance) {

		double sumFitness = 0;
		double[] instanceFitness = new double[instances.numInstances()];
		double[] instancePro = new double[instances.numInstances()];

		for (int i = 0; i < instances.numInstances(); i++) {// 计算适应度
			instanceFitness[i] = fitness(instance[i], instances);
			sumFitness += instanceFitness[i];// 计算所有使用度总和
		}
		for (int i = 0; i < instances.numInstances(); i++) {// 计算适用度概率
			instancePro[i] += instanceFitness[i] / sumFitness;
		}

		double wheelBorder[] = new double[instances.numInstances()];
		for (int i = 0; i < instances.numInstances(); i++) {// 计算轮盘赌边界
			for (int j = 0; j < i; j++) {
				wheelBorder[i] += instancePro[i];
			}
		}

		Random random = new Random();
		Instance[] tmpInstance = new Instance[instances.numInstances()];
		for (int i = 0; i < instances.numInstances(); i++) {

			double pointer = random.nextDouble();
			for (int j = 0; j < wheelBorder.length; j++) {
				if (pointer > wheelBorder[j] && pointer < wheelBorder[j + 1]) {
					tmpInstance[i] = instance[j];// 临时保存在tmpInstance
				}
			}
		}
		instance = tmpInstance; // 把选出来的样本重新赋给instance

	}

	/**
	 * Cross Method
	 */
	public void cross(Instance[] instance) {

		for (int i = 0; i < instances.numInstances(); i++) {
			if (crossPro > random.nextDouble()) {
				int inSelectNumOne = random.nextInt(instances.numInstances());// 随机选择第一个样例索引
				int inSelectNumTwo = random.nextInt(instances.numInstances());
				Instance instanceSelectOne = instance[inSelectNumOne];// 选择第一个样例
				Instance instanceSelectTwo = instance[inSelectNumOne];
				String strSelectOne = encode(instanceSelectOne);// 把第一个样例随机属性进行编码
				int attSelectNumOne = attSelectNum;// 临时保存一下随机属性索引
				String strSelectTwo = encode(instanceSelectTwo);// 把第一个样例随机属性进行编码
				int attSelectNumTwo = attSelectNum;
				int crossPosition = random.nextInt(strSelectOne.length());// 随机选择交叉位置
				// 交叉操作(多点交叉)
				String newStrOne = strSelectOne.substring(0, crossPosition - 1)
						+ strSelectTwo.substring(crossPosition);
				String newStrTwo = strSelectTwo.substring(0, crossPosition - 1)
						+ strSelectOne.substring(crossPosition);
				int intDecodeOne = decode(newStrOne);// 解码
				instance[inSelectNumOne]
						.setValue(attSelectNumOne, intDecodeOne);// 解码之后赋值
				int intDecodeTwo = decode(newStrTwo);
				instance[inSelectNumOne]
						.setValue(attSelectNumTwo, intDecodeTwo);// 解码之后赋值
			}
		}
	}

	/**
	 * Mutation Method
	 */
	public void mutation(Instance[] instance) {

		for (int i = 0; i < instances.numInstances(); i++) {
			if (mutationPro > random.nextDouble()) {
				int inSelectNum = random.nextInt(instances.numInstances());// 随机样例序列
				Instance instanceSelect = instance[inSelectNum];// 随机选择一个样例
				String attString = encode(instanceSelect);
				int mutPosition = random.nextInt(attString.length());// 随机选择一个交叉位置
				char ch = attString.charAt(mutPosition);// 选出一个字符来0 或 1
				if (ch == '0') {
					ch = '1';
				} else {
					ch = '0';
				}
				attString.replace(attString.charAt(mutPosition), ch);// 用新的字符串代替原来的
				instance[i].setValue(attSelectNum, decode(attString));// 给样例重新赋属性值
			}

		}
	}

	/**
	 * Encode Method
	 */
	public String encode(Instance instance) {// 选出一个样例的属性进行编码

		attSelectNum = random.nextInt(instance.numAttributes());// 随即选择0~属性长度的位置

		double getAttValue = instance.value(attSelectNum);// 根据随即属性索引得到属性值

		String str = Integer.toBinaryString((int) getAttValue); // 把该属性转换为二进制

		return str;
	}

	/**
	 * Decode Method
	 */
	public int decode(String str) {// 把String-里面包含二进制 转换成十进制
		return Integer.parseInt(str, 2);

	}

	public static void main(String args[]) {

		Genetic genetic = new Genetic();

		try {
			File file = new File("../weka/data/contact-lenses.arff");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			Instances instances = new Instances(br);
			genetic.setInstances(instances);
			System.out.println(instances);
			Instance[] instance = new Instance[instances.numInstances()];
			for (int i = 0; i < instances.numInstances(); i++) {

				instance[i] = instances.instance(i);
				System.out.println("----------");
				System.out.println(instance[i]);
			}
			
			
			
			System.out.println("----------");
			System.out.println(instance);

			for (int i = 0; i < generationNum; i++) {
				genetic.select(instance);// 选择
				//genetic.cross(instance);// 交叉
				genetic.mutation(instance);// 变异

				// 1 J48 j48 = new J48();使用分类器进行分类,每一代得到一个模型
				// 2 利用这些模型在测试机上进行测试，得到一个正确率数组
				// 3 选出使正确率最高的那个模型对应的Instance[]
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
