import java.util.ArrayList;
import java.util.Random;

class Cluster{
	
	private double center[] = new double[2];
	private double oldCenter[] = new double[2];
	private ArrayList<Element> elements = new ArrayList<Element>();
	private int category[] = {0, 0};
	private double TP = 0, FP = 0;
	
	public void init(double center[]){
		this.center[0] = center[0];
		this.center[1] = center[1];
		this.oldCenter[0] = 100;
		this.oldCenter[1] = 100;
		elements.clear();
	}
	
	public void init(){
		oldCenter[0] = center[0];
		oldCenter[1] = center[1];
		elements.clear();
	}
	
	public void calculateCenter(){
		center[0] = 0;
		center[1] = 0;
		
		for (Element element : elements){
			center[0] += element.getValue(0);
			center[1] += element.getValue(1);
		}
		center[0] /= elements.size();
		center[1] /= elements.size();
	}
	
	public int calculatePartPurity(){
		
		for (Element element : elements){
			category[(element.getCategory() + 1)/2] += 1;
		}
		if (category[0] > category[1]) {
			TP = category[0];
			FP = category[1];
			return category[0];
		}
		TP = category[1];
		FP = category[0];
		return category[1];
	}
	
	public double fmeasure(int cat1FN, int cat2FN){
		int a = 1;
		return ((1+a)/(1/precision() + a/recall(cat1FN, cat2FN)));
	}
	
	public double precision(){
		return TP/(TP+FP);
	}
	
	public double recall(int cat1FN, int cat2FN){
		int FN = 0;
		if (category[0] > category[1]) FN = cat2FN - category[1];
		else FN = cat1FN - category[0];
		return TP/(TP+FN);
	}
	
	
	
	public int[] FN(){
		int fn[] = new int[2];
		fn[0] = 0;
		fn[1] = 0;
		if (category[0] > category[1]) fn[1] = category[1];
		else fn[0] = category[0];
		return fn;
		
	}
	
	public void addElement(Element element){
		elements.add(element);
	}
	
	public double[] getCenter(){
		return center;
	}
	
	public boolean stop(double e){
		double x = oldCenter[0] - center[0];
		double y = oldCenter[1] - center[1];
		return (Math.sqrt(x*x + y*y) <= e);
	}
	
	
}

public class KMeans{
	
	private Cluster clusters[];
	private Element elements[];
	private int k;
	private Random random = new Random();
	private double e = 0.0001;
	private double purity = 0, fmeasure;
	private static int totalCategoryCount[] = new int[2];
	
	public KMeans(Element elements[], int k){
		totalCategoryCount[0] = 0;
		totalCategoryCount[1] = 0;
		this.elements = elements;
		this.k = k;
		clusters = new Cluster[k];
		for (int i = 0; i < elements.length; i++){
			totalCategoryCount[(elements[i].getCategory()+1)/2] += 1;
		}
	}

	public void run() {
	
		int RUNS = 0;
		initCenters();
		while(!stop()){
			updateClusters();
			RUNS += 1;
		}
		purity = purity();
		fmeasure = fmeasure();
	}
	
	public void initCenters(){
		//init randomly based on 'k' elements' position
		ArrayList<Integer> usedIndexes = new ArrayList<Integer>();
		double center[] = new double[2];
		int index;
		for (int i = 0; i < k; i++){
			clusters[i] = new Cluster();
			index = random.nextInt(elements.length);
			while(usedIndexes.contains(index)){
				index = random.nextInt(usedIndexes.size());
			}
			usedIndexes.add(index);
			center[0] = elements[index].getValue(0);
			center[1] = elements[index].getValue(1);
			clusters[i].init(center);
		}
	}
	
	public void updateClusters(){
		
		double pos[] = new double[2];
		
		for (int i = 0; i < k; i++){
			clusters[i].init();
		}	
		
		for (Element element : elements){
			pos[0] = element.getValue(0);
			pos[1] = element.getValue(1);
			getMinDistanceCluster(pos).addElement(element);
		}	
		
		for (int i = 0; i < k; i++){
			clusters[i].calculateCenter();
		}	
	}
	
	public Cluster getMinDistanceCluster(double pos[]){
		
		int minIndex = -1;
		double minDistance = 0, currentDistance = 0;
		double x = 0, y = 0;
		
		for (int i = 0; i < k; i++){
			x = clusters[i].getCenter()[0] - pos[0];
			y = clusters[i].getCenter()[1] - pos[1];
			currentDistance = Math.sqrt(x*x + y*y);
			if (currentDistance < minDistance || i == 0){
				minDistance = currentDistance;
				minIndex = i;
			}
		}
		return clusters[minIndex];
	}
	
	public boolean stop(){
		for (Cluster cluster : clusters){
			if (!cluster.stop(e)) return false;
		}
		return true;
	}
	
	public double purity(){
		double purity = 0;
		for (Cluster cluster : clusters){
			purity += cluster.calculatePartPurity();
		}
		return purity/elements.length;
	}
	
	public double fmeasure(){
		int cat1FN = 0, cat2FN = 0, fn[];
		double fmeasure = 0;
		for (Cluster cluster : clusters){
			fn = cluster.FN();
			cat1FN += fn[0];
			cat2FN += fn[1];
		}
		for (Cluster cluster : clusters){
			fmeasure += cluster.fmeasure(cat1FN, cat2FN);
		}
		//System.out.println(cat1FN + " " + cat2FN);
		return fmeasure;
	}
	
	public double getPurity(){
		return purity();
	}
	
	public static int[] getTotalCategoryCount(){
		return totalCategoryCount;
	}

	public void print(){
		System.out.println("K = " + k + ", Purity = " + purity + ", F-measure = " + fmeasure);
		System.out.println("----CENTERS----");
		for (Cluster cluster : clusters){
			System.out.println("[" + cluster.getCenter()[0] + ", " + cluster.getCenter()[1] + "]");
		}
		System.out.println();
	}

}
