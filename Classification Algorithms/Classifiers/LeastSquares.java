import java.util.ArrayList;

public class LeastSquares extends Classifier{
	
	private double X[][];
	private double Y[][];
	private double w[][];
	private int d = 2;

	public double run(ArrayList<Element> trainData, ArrayList<Element> testData) {
		
		X = new double[trainData.size()][d+1];
		Y = new double[trainData.size()][1];
		w = new double[d+1][1];
		train(trainData);
		
		return test(testData)/testData.size();
	}
	
	public void train(ArrayList<Element> trainData){
		Element element;
		
		for (int i = 0; i < trainData.size(); i++){
			element = trainData.get(i);
			X[i][0] = 1;
			X[i][1] = element.getValue(0);
			X[i][2] = element.getValue(1);
			Y[i][0] = element.getCategory();
		}
	
		Matrix m = LinearMethods.multiplyMatrices(pseudoInverse(X), new Matrix(Y));
		
		for (int i = 0; i < m.getNrows(); i++){
			w[i][0] = m.getValueAt(i, 0);
		}
	}
	
	public double test(ArrayList<Element> testData){
		
		double found = 0;
		int predict = 0;
		
		for(Element element : testData){

			double val = w[0][0] + w[1][0]*element.getValue(0) + w[2][0]*element.getValue(1);
			
			if (val < 0.5) predict = 0;
			else predict = 1;
			
			if (predict == element.getCategory()) found += 1;
		}

		return found;
	}
	
	public Matrix pseudoInverse(double X[][]){
		
		Matrix transposed = LinearMethods.transpose(new Matrix(X));
		Matrix m = LinearMethods.multiplyMatrices(transposed, new Matrix(X));
		Matrix inversed = LinearMethods.inverse(m);
		m = LinearMethods.multiplyMatrices(inversed, transposed);
		return m;
	}
}