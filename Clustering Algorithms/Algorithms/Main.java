import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Element{
	
	private int category; //-1, 1
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
	
	public double distance(Element element){
		double x = values[0] - element.getValue(0);
		double y = values[1] - element.getValue(1);
		return Math.sqrt(x*x + y*y);
	}
}

public class Main {
	private static final int RUNS = 10;
	private Element elements[];
	private ArrayList<String> dataList;
	private KMeans kmeans;
	private int k[] = {2, 3, 4};
	private Random rand = new Random();
	
	
	public void init(){
		dataList = new ArrayList<String>();
		createDataSet("moonandsun.dat");
		AgglomerativeHierarchical ah = new AgglomerativeHierarchical();
		
		//ah.run(elements);
		runKMeans();
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
	
	public void createElements(){
		
		int category;
		elements = new Element[dataList.size()];
	
		for (int i = 0; i < dataList.size(); i++){
			String splitted[] = dataList.get(i).split(" ");
			category = Integer.parseInt(splitted[0]);
			elements[i] = new Element(category, 2);
			elements[i].setValue(0, Double.parseDouble(splitted[1]));
			elements[i].setValue(1, Double.parseDouble(splitted[2]));
		}
	}
	
	public void runKMeans(){
		KMeans allKMeans[] = new KMeans[RUNS];
		double maxPurity = -1;
		int maxPurityIndex = -1;
		for(int K : k){
			maxPurity = -1;
			for (int i = 0; i < RUNS; i++){
				allKMeans[i] = new KMeans(elements, K);
				allKMeans[i].run();
				if (allKMeans[i].getPurity() > maxPurity){
					maxPurity = allKMeans[i].getPurity();
					maxPurityIndex = i;
				}
			}
			allKMeans[maxPurityIndex].print();
		}
	}
	
	public static void main(String args[]){
		Homework2 hw2 = new Homework2();
		hw2.init();
	}
	
}
