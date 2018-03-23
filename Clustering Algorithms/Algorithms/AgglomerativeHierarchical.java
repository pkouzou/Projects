import java.util.ArrayList;
import java.util.HashMap;

class DistanceArray{
	
	private ArrayList<double[]> distances;
	
	public DistanceArray(Element elements[]){
		int counter = 0;
		distances = new ArrayList<double[]>();
		for (int i = 0; i < elements.length; i++){
			distances.add(new double[elements.length-i-1]);
			counter = 0;
			for (int j = i+1; j < elements.length; j++){
				distances.get(i)[counter] = elements[i].distance(elements[j]);
				counter += 1;
			}
		}
	}
	
	public double get(int i, int j){
		if (i == j || i >= distances.size() || j >= distances.size()) return -1;
		if (i > j){
			int temp = i;
			i = j;
			j = temp;
		}
		return distances.get(i)[j-i-1];
	}
	
	public void setDistance(int i, int j, double distance){
		
		if (i > j){
			int temp = i;
			i = j;
			j = temp;
		}
		distances.get(i)[j-i-1] = distance;
	}
	
	public void print(){
		for (int i = 0; i < distances.size(); i++){
			for (int k = 0; k < i; k++) System.out.print("   ");
			for (int j = 0; j < distances.get(i).length; j++){
				System.out.print(" " + distances.get(i)[j]);
			}
			System.out.println();
		}
	}
}

class AHCluster{
	private Element element;
	private AHCluster containedCluster;
	private double center[] = new double[2];
	private int index;
	private int categoryCount[] = new int[2];
	
	public AHCluster(Element element, int index){
		this.element = element;
		this.index = index;
		categoryCount[0] = 0;
		categoryCount[1] = 0;
		center[0] = element.getValue(0);
		center[1] = element.getValue(1);
		containedCluster = null;
		categoryCount[(element.getCategory()+1)/2] += 1;
	}
	
	public void merge(AHCluster cluster2){
		containedCluster = cluster2;
		addCategoryCount(cluster2.getCategoryCount());
		center[0] = (center[0] + cluster2.getCenter()[0])/2;
		center[1] = (center[1] + cluster2.getCenter()[1])/2;
	}
	
	public double distance(AHCluster cluster2){
		double x = center[0] - cluster2.getCenter()[0];
		double y = center[1] - cluster2.getCenter()[1];
		return Math.sqrt(x*x + y*y);
	}
	
	public void addCategoryCount(int categoryCount[]){
		this.categoryCount[0] += categoryCount[0];
		this.categoryCount[1] += categoryCount[1];
	}
	
	public double[] getCenter(){
		return center;
	}
	
	public int[] getCategoryCount(){
		return categoryCount;
	}
	
	public int getTotalCategoryCount(){
		return categoryCount[0] + categoryCount[1];
	}
	
	public AHCluster getContainedCluster(){
		return containedCluster;
	}
	
	public int getIndex(){
		return index;
	}
}

public class AgglomerativeHierarchical {

	private HashMap<Integer, AHCluster> ahClusters;
	private DistanceArray distances;
	private int minIndexes[] = new int[2];
	
	public void run(Element elements[]){
		createClusters(elements);
		distances = new DistanceArray(elements);
		
		while (ahClusters.size() > 1){
			calculateMinDistance();
			ahClusters.get(minIndexes[0]).merge(ahClusters.get(minIndexes[1]));
			ahClusters.remove(minIndexes[1]);
			calculateNewDistances();
		}
		
		for (int key : ahClusters.keySet()){
			System.out.println(ahClusters.get(key).getCategoryCount()[0] + " " + ahClusters.get(key).getCategoryCount()[1]);
		}
	}
	
	public void createClusters(Element elements[]){
		ahClusters = new HashMap<Integer, AHCluster>(elements.length);
	
		for (int i = 0; i < elements.length; i++){
			ahClusters.put(i, new AHCluster(elements[i], i));
		}
	}
	
	public void calculateMinDistance(){
		double minDistance = -1;
		double distance;
		ArrayList<Integer> keys = new ArrayList<Integer>(ahClusters.keySet());
		minIndexes[0] = -1;
		minIndexes[1] = -1;
		for (int i = 0; i < keys.size(); i++){
			for (int j = i+1; j < keys.size(); j++){
				distance = distances.get(ahClusters.get(keys.get(i)).getIndex(), ahClusters.get(keys.get(j)).getIndex());
				if (distance < minDistance || minDistance == -1){
					minDistance = distance;
					minIndexes[0] = keys.get(i);
					minIndexes[1] = keys.get(j);
				}
			}
		}
	}
	
	public void calculateNewDistances(){
		for (int key : ahClusters.keySet()){
			if (key == minIndexes[0]) continue;
			distances.setDistance(ahClusters.get(minIndexes[0]).getIndex(), ahClusters.get(key).getIndex(), ahClusters.get(minIndexes[0]).distance(ahClusters.get(key)));
		}
	}
}
