import java.util.ArrayList;

public abstract class Classifier {
	abstract public double run(ArrayList<Element> trainData, ArrayList<Element> testData);
}