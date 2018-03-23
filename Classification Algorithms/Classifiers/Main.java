import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Element{
	
	private int category; //0, 1
	private double values[]; //values.size == 2
	
	Element(int category, int valuesLength){
		values = new double[valuesLength];	
		this.category = category;
	}
	
	public void setValue(int index, double value){
		values[index] = value;
	}
	
	public int getCategory(){
		return category;
	}
	
	public double getValue(int index){
		return values[index];
	}
}

public class Main {
	
	private static int RUNS = 10;
	private ArrayList<String> dataList;
	private ArrayList<Element> trainData;
	private ArrayList<Element> testData;
	private int subsetSize;
	private Random rand;
	private Element elements[];
	private Classifier naiveBayes, leastSquares, gradientDescent;
	
	public void init(){
		dataList = new ArrayList<String>();
		trainData = new ArrayList<Element>();
		testData = new ArrayList<Element>();
		rand = new Random();
		createDataSet("clouds.dat");
		runKNN();
		naiveBayes = new NaiveBayes();
		System.out.println("Classifier: NaiveBayes, Accuracy: " + crossValidation(naiveBayes));
		leastSquares = new LeastSquares();
		System.out.println("Classifier: LeastSquares, Accuracy: " + crossValidation(leastSquares));
		gradientDescent = new GradientDescent();
		System.out.println("Classifier: GradientDescent, Accuracy: " + crossValidation(gradientDescent));
	}
	
	public void createDataSet(String dataSetName){
		writeFileToList(dataSetName);
		shuffleList(dataList);
		createElements();
	}
	
	public void writeFileToList(String fileName){
		
		File file = new File("res/" + fileName);
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       dataList.add(line.trim().replaceAll(" +", " ").replace("\t", " "));
		    }
		    br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shuffleList(ArrayList<String> list){
		
		int numberOfShuffles = list.size();
		int index1 = 0, index2 = 0;
		String temp = "";
		
		for (int i = 0; i < numberOfShuffles; i++){
			index1 = rand.nextInt(list.size());
			index2 = rand.nextInt(list.size());
			temp = list.get(index1);
			list.set(index1, list.get(index2));
			list.set(index2, temp);
		}
	}

	public void runKNN(){
		
		double accuracy = 0;
		double maxAccuracy = -1;
		int k = -1;
		for (int i = 1; i < 10; i++){
			accuracy = crossValidation(new KNN(i));
			if (accuracy > maxAccuracy){
				maxAccuracy = accuracy;
				k = i;
			}
		}
		System.out.println("Classifier: KNN(" + k  + "), Accuracy: " + accuracy);
	}
	
	public double crossValidation(Classifier clf){
		
		int testStart = 0;
		double accuracy = 0;
		subsetSize = elements.length/RUNS;

		for (int i = 0; i < RUNS; i++){
		
			setTrainData(testStart);
			setTestData(testStart);
		
			accuracy += clf.run(trainData, testData);
			testStart += subsetSize;
		}
		
		accuracy /= RUNS;
		return accuracy;
	}
	
	public void setTrainData(int testStart){
		
		trainData.clear();
		for (int i = 0; i < testStart; i++){
			trainData.add(elements[i]);
		}
		for (int i = testStart + subsetSize; i < elements.length; i++){
			trainData.add(elements[i]);
		}
	}
	
	public void setTestData(int testStart){
		
		testData.clear();
		for (int i = testStart; i < testStart + subsetSize; i++){
			testData.add(elements[i]);
		}
	}
	
	public void createElements(){
		
		int category;
		elements = new Element[dataList.size()];
	
		for (int i = 0; i < dataList.size(); i++){
			String splitted[] = dataList.get(i).split(" ");
			category = Integer.parseInt(splitted[2]);
			elements[i] = new Element(category, 2);
			elements[i].setValue(0, Double.parseDouble(splitted[0]));
			elements[i].setValue(1, Double.parseDouble(splitted[1]));
		}
	}
	
	public static void main(String args[]){
		Homework1 hw1 = new Homework1();
		hw1.init();
	}
}