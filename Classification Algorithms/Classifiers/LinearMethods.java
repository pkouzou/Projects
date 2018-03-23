class LinearMethods{
	
	public static Matrix transpose(Matrix matrix) {
	    Matrix transposedMatrix = new Matrix(matrix.getNcols(), matrix.getNrows());
	    for (int i=0;i<matrix.getNrows();i++) {
	        for (int j=0;j<matrix.getNcols();j++) {
	            transposedMatrix.setValueAt(j, i, matrix.getValueAt(i, j));
	        }
	    }
	    return transposedMatrix;
	} 
	
	public static double determinant(Matrix matrix){
	   	    if (matrix.size() == 1) {
		return matrix.getValueAt(0, 0);
	    }
	    if (matrix.size()==2) {
	        return (matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1)) - 
	                         ( matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0));
	    }
	    double sum = 0.0;
	    for (int i=0; i<matrix.getNcols(); i++) {
	        sum += changeSign(i) * matrix.getValueAt(0, i) * determinant(createSubMatrix(matrix, 0, i));
	    }
	    return sum;
	} 
	
	public static Matrix createSubMatrix(Matrix matrix, int excluding_row, int excluding_col) {
	    Matrix mat = new Matrix(matrix.getNrows()-1, matrix.getNcols()-1);
	    int r = -1;
	    for (int i=0;i<matrix.getNrows();i++) {
	        if (i==excluding_row)
	            continue;
	            r++;
	            int c = -1;
	        for (int j=0;j<matrix.getNcols();j++) {
	            if (j==excluding_col)
	                continue;
	            mat.setValueAt(r, ++c, matrix.getValueAt(i, j));
	        }
	    }
	    return mat;
	} 
	
	public static Matrix cofactor(Matrix matrix){
	    Matrix mat = new Matrix(matrix.getNrows(), matrix.getNcols());
	    for (int i=0;i<matrix.getNrows();i++) {
	        for (int j=0; j<matrix.getNcols();j++) {
	            mat.setValueAt(i, j, changeSign(i) * changeSign(j) * 
	                             determinant(createSubMatrix(matrix, i, j)));
	        }
	    }
	    
	    return mat;
	}
	
	public static int changeSign(int i){
		if (i % 2 == 0) return 1;
		return -1;
	}
	
	public static Matrix inverse(Matrix matrix) {
	    return (transpose(cofactor(matrix)).multiplyByConstant(1.0/determinant(matrix)));
	}
	
	public static Matrix multiplyMatrices(Matrix first, Matrix second){
		
		Matrix result = new Matrix(first.getNrows(), second.getNcols());

		for (int i = 0; i < first.getNrows(); i++) { 
		    for (int j = 0; j < second.getNcols(); j++) { 
		        for (int k = 0; k < first.getNcols(); k++) { 
		        	result.setValueAt(i, j, result.getValueAt(i, j) + first.getValueAt(i, k) * second.getValueAt(k, j));
		        }
		    }
		}
		
		return result;
	}
}
