public class Matrix {

    private int nrows;
    private int ncols;
    private double[][] data;

    public Matrix(double[][] dat) {
        this.data = dat;
        this.nrows = dat.length;
        this.ncols = dat[0].length;
        
        for (int i = 0; i < nrows; i++){
        	for(int j = 0; j < ncols; j++){
        		data[i][j] = dat[i][j];
        	}
        }
    }

    public Matrix(int nrow, int ncol) {
        this.nrows = nrow;
        this.ncols = ncol;
        data = new double[nrow][ncol];
    }
    
    public int getNrows(){
    	return nrows;
    }
    
    public int getNcols(){
    	return ncols;
    }
    
    public double setValueAt(int i, int j, double val){
    	return data[i][j] = val;
    }
    
    public double getValueAt(int i, int j){
    	return data[i][j];
    }
    
    public boolean isSquare(){
    	return nrows == ncols;
    }
    
    public int size(){
    	return nrows;
    }
    
    public void print(){
    	for (int i = 0; i < nrows; i++){
    		for (int j = 0; j < ncols; j++){
    			System.out.print(data[i][j] + " ");
    		}
    		System.out.println(" ");
    	}
    }
    
    
    public Matrix multiplyByConstant(double c){
    	
    	Matrix m = new Matrix(nrows, ncols);
    	
    	for(int i = 0; i < nrows; i++){
    		for (int j = 0; j < ncols; j++){
    			m.setValueAt(i, j, data[i][j]*c);
    		}
    	}
    	
    	return m;
    }
    
    
}

