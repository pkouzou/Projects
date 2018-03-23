import java.util.ArrayList;

public class KNN extends Classifier{
	
	private int k = 0;
	
	KNN(int k){
		this.k = k;
	}
	
	public double run(ArrayList<Element> trainData, ArrayList<Element> testData){
		
		double accuracy = 0;
		double distances[][] = new double[trainData.size()][2];
		int categoryCount[] = {0, 0};
		int predict = 0;
		
		for (Element test : testData){
			
			categoryCount[0] = 0;
			categoryCount[1] = 0;
			
			for (int i = 0; i < trainData.size(); i++){
				distances[i][0] = getDistance(test, trainData.get(i));
				distances[i][1] = trainData.get(i).getCategory();
			}
			
			java.util.Arrays.sort(distances, new java.util.Comparator<double[]>() {
			    public int compare(double[] a, double[] b) {
			        return Double.compare(a[0], b[0]);
			    }
			});
			
			for (int i = 0; i < k; i++){
				categoryCount[(int)distances[i][1]] += 1;
				if (categoryCount[0] < categoryCount[1]) predict = 1;
				else predict = 0;
			}
			
			if (predict == test.getCategory()) accuracy += 1;
		}
		return accuracy/testData.size();
		
	}
	
	public double getDistance(Element el1, Element el2){
		double diff1 = el1.getValue(0) - el2.getValue(0);
		double diff2 = el1.getValue(1) - el2.getValue(1);
		
		return Math.sqrt(diff1*diff1 + diff2*diff2);
	}
}