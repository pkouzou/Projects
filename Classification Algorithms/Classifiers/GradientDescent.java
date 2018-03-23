import java.util.ArrayList;

public class GradientDescent extends Classifier{
	
	private double w[];
	private double diff = 0.0001;
	private double E[];
	private double n;

	public double run(ArrayList<Element> trainData, ArrayList<Element> testData) {
		
		w = new double[3];
		E = new double[2]; //E[0] for Eold, E[1] for Enew
		n = 0.1;
		train(trainData);
		
		return test(testData)/testData.size();
	}
	
	public void init(ArrayList<Element> trainData){
		w[0] = 1;
		w[1] = 1;
		w[2] = 1;
		E[0] = 0;
		E[1] = 0;
		
		calculateE(0, trainData);
	}
	
	public void train(ArrayList<Element> trainData){
		int iterations = 0;
		init(trainData);
		
		double w_[] = new double[3];
		while(Math.abs(E[1] - E[0]) > diff && iterations < 2000){
			
			if (E[1] != 0) E[0] = E[1];
			
			w_[0] = 0;
			w_[1] = 0;
			w_[2] = 0;
			for (Element element : trainData){
				double delta = sigmoid(a(element)) - element.getCategory();
				double val = delta*sigmoid(a(element))*(1-sigmoid(a(element)));
				
				w_[0] += val;
				w_[1] += val*element.getValue(0);
				w_[2] += val*element.getValue(1);
			}
			
			w[0] -= n*w_[0];
			w[1] -= n*w_[1];
			w[2] -= n*w_[2];
			
			calculateE(1, trainData);
			iterations += 1;
		}
	}
	
public double test(ArrayList<Element> testData){
		
		double found = 0;
		int predict = 0;
		
		for(Element element : testData){

			double val = w[0] + w[1]*element.getValue(0) + w[2]*element.getValue(1);
			
			if (val < 0.5) predict = 0;
			else predict = 1;
			
			if (predict == element.getCategory()) found += 1;
		}

		return found;
	}
	
	public void calculateE(int index, ArrayList<Element> trainData){
		
		E[index] = 0;
		
		for (Element element : trainData){
			double delta = sigmoid(a(element)) - element.getCategory();
			
			E[index] += delta*delta;
		}
		
		E[index] /= 2;
	}
	
	public double a(Element element){
		return w[0] + w[1]*element.getValue(0) + w[2]*element.getValue(1);
	}
	
	public double sigmoid(double a){
		
		double l = 1.0;
		
		return 1.0 / (1 + Math.exp(-l*a));
	}
}