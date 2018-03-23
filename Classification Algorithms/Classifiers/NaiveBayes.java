import java.util.ArrayList;

public class NaiveBayes extends Classifier{
	
	private double meanW0[], meanW1[];
	private double varianceW0[], varianceW1[];
	private double pW0, pW1; //probabilities for each category
	private double w0Counter, w1Counter;
	
	public double run(ArrayList<Element> trainData, ArrayList<Element> testData) {
		
		double accuracy = 0;
		meanW0 = new double[2];
		varianceW0 = new double[2];
		meanW1 = new double[2];
		varianceW1 = new double[2];
		pW0 = 0;
		pW1 = 0;
		w0Counter = 0;
		w1Counter = 0;
		
		calculateMean(trainData);
		calculateVariance(trainData);
		
		int predict;
	
		for (Element test : testData){
			predict = winner(test);
			if (predict == test.getCategory()) accuracy += 1;
		}
		
		return accuracy/testData.size();
	}
	
	public void calculateMean(ArrayList<Element> trainData){
		
		for(Element element : trainData){
		
			if (element.getCategory() == 0) {
				w0Counter += 1;
				meanW0[0] += element.getValue(0);
				meanW0[1] += element.getValue(1);
			}
			else {
				w1Counter += 1;
				meanW1[0] += element.getValue(0);
				meanW1[1] += element.getValue(1);
			}
		}
		
		meanW0[0] /= w0Counter;
		meanW0[1] /= w0Counter;
		meanW1[0] /= w1Counter;
		meanW1[1] /= w1Counter;
		pW0 = w0Counter / trainData.size();
		pW1 = w1Counter / trainData.size();
	}
	
	public void calculateVariance(ArrayList<Element> trainData){
		
		for (Element element : trainData){
			
			if (element.getCategory() == 0){
				varianceW0[0] += (meanW0[0] - element.getValue(0))*(meanW0[0] - element.getValue(0));
				varianceW0[1] += (meanW0[1] - element.getValue(1))*(meanW0[1] - element.getValue(1));
			}
			else{
				varianceW1[0] += (meanW1[0] - element.getValue(0))*(meanW1[0] - element.getValue(0));
				varianceW1[1] += (meanW1[1] - element.getValue(1))*(meanW1[1] - element.getValue(1));
			}
		}
		
		varianceW0[0] /= w0Counter;
		varianceW0[1] /= w0Counter;
		varianceW1[0] /= w1Counter;
		varianceW1[1] /= w1Counter;
	}
	
	public int winner(Element element){
		
		double arg00 = gaussian(element.getValue(0), meanW0[0], varianceW0[0]);
		double arg01 = gaussian(element.getValue(1), meanW0[1], varianceW0[1]);
		double arg10 = gaussian(element.getValue(0), meanW1[0], varianceW1[0]);
		double arg11 = gaussian(element.getValue(1), meanW1[1], varianceW1[1]);
		double pXW0 = arg00 * arg01;
		double pXW1 = arg10 * arg11;
		
		if (pXW0/pXW1 > pW1/pW0) {
			
			return 0;
		}
		return 1;
	}
	
	public double gaussian(double x, double mean, double variance){
		return (1/(Math.sqrt(2*Math.PI*variance))*Math.exp(-(x-mean)*(x-mean)/(2*variance)));
	}
}