package fr.curie.DeDaL;

import java.util.ArrayList;
import java.util.List;


public class Matrix
{
    public float[][] data;
    
    
    public Matrix(float[][] data)
    {
        /*int r= data.length;
        int c= data[0].length;
        this.data= new float[r][c];
        for(int i = 0; i < r; i++) {
            for(int j = 0; j < c; j++) {
                    this.data[i][j] = data[i][j];
            }
        }*/
    }

    /* convenience method for getting a 
       string representation of matrix */
    /*  public String toString()
    {
    	StringBuilder sb = new StringBuilder(1024);
    	for(float[] row : this.data)
    	{
    		for(float val : row)
    		{
    			sb.append(val);
    			sb.append(" ");
    		}
    		sb.append("\n");
    	}

    	return(sb.toString());
    }*/

    public float[][] removeRowsWithValue(double treshold)
    {
            /* Use an array list to track of the rows we're going to want to 
               keep...arraylist makes it easy to grow dynamically so we don't 
               need to know up front how many rows we're keeping */
    	List<float[]> rowsToKeep = new ArrayList<float[]>(data.length);
    	
    	
    	//int nnode = 0;
    	for(float[] row : data)
    	{
    		/* If you download Apache Commons, it has built-in array search
                      methods so you don't have to write your own */
    		boolean found = false;
    		double sum0=0;
    		for(float testValue : row)
    		{
                            /* Using == to compares doubles is generally a bad idea 
                               since they can be represented slightly off their actual
                               value in memory */
    			if(testValue == 0){
    			sum0++;
    			}
    			double rate=sum0/data[0].length;	
    				
    			if(rate>treshold)	
    			{
    				found = true;
    				
    				break;
    			}
    		}

                    /* if we didn't find our value in the current row, 
                      that must mean its a row we keep */
    		if(!found)
    		{
    			rowsToKeep.add(row);
    			//System.out.println(row);
    		}
    	}

            /* now that we know what rows we want to keep, make our 
               new 2D array with only those rows */
    	data = new float[rowsToKeep.size()][];
    	for(int i=0; i < rowsToKeep.size(); i++)
    	{
    		data[i] = rowsToKeep.get(i);
    		
    	}
    	return data;
    }
    
   /* public static void main(String[] args)
    {
    	float[][] test = { {1, 2, 3, 4, 5, 6, 7, 8, 9},
    						{6, 2, 7, 2, 9, 6, 8, 10, 5},
    						{2, 6, 4, 7, 8, 4, 3, 2, 5},
    						{9, 8, 7, 5, 9, 7, 4, 1, 10},
    						{5, 3, 6, 8, 2, 7, 3, 7, 2} };

            //make the original array and print it out      	
    	Matrix m = new Matrix(test);
    	System.out.println(m);

            //remove rows with the value "10" and then reprint the array
    	m.removeRowsWithValue(10);
    	System.out.println(m);
    }*/
    
}