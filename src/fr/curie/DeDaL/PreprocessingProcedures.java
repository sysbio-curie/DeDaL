package fr.curie.DeDaL;

import java.io.FileWriter;
import java.util.Vector;

import vdaoengine.data.VDataSet;
import vdaoengine.data.VDataTable;
import vdaoengine.utils.VSimpleProcedures;
import vdaoengine.utils.VVectorCalc;

public class PreprocessingProcedures {
public static double vv[][] = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static float[][] doubleCenterMatrix(float matrix[][]){
		float res[][] = matrix.clone();
		
		int n = matrix.length;
		int m = matrix[0].length;
		float sumPerRow[] = new float[n];
		int countInRow[] = new int[n];
		float sumPerColumn[] = new float[m];
		int countInColumn[] = new int[m];
		int countTotal = 0;
		float totalSum = 0f;
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++)if(!Float.isNaN(matrix[i][j])){
				totalSum+=matrix[i][j];
				sumPerRow[i]+=matrix[i][j];
				countInRow[i]++;
				countTotal++;
			}
		}
		for(int j=0;j<m;j++){
			for(int i=0;i<n;i++)if(!Float.isNaN(matrix[i][j])){
				sumPerColumn[j]+=matrix[i][j];
				countInColumn[j]++;
			}
		}
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				res[i][j] = matrix[i][j]-1/(float)countInRow[i]*sumPerRow[i]-1/(float)countInColumn[j]*sumPerColumn[j]+1/countTotal*totalSum;
			}
		}
		
		return res;
	}
	
	public static float sumOfAllMatrixElements(float matrix[][], boolean absvalue){
		float sum = 0f;
		for(int i=0;i<matrix.length;i++)
			for(int j=0;j<matrix[0].length;j++){
				if(!absvalue)
					sum+=matrix[i][j];
				else
					sum+=Math.abs(matrix[i][j]);
			}
		return sum;
	}
	
	public static VDataTable NetworkSmoothing(VDataTable vt, Graph graph, float smoothingFactor){
		return NetworkSmoothing(vt, graph, smoothingFactor, null);
	}
	
	  public static VDataTable NetworkSmoothing(VDataTable vt, Graph graph, float smoothingFactor, double EVM[][]){
		  VDataSet vd = VSimpleProcedures.SimplyPreparedDatasetWithoutNormalization(vt, -1);
		  float mat[][] = TransposeMatrix(vd.massif);
		  //float mat[][] = vd.massif;
		  Vector<String> names = new Vector<String>();
		  for(int i=0;i<vt.rowCount;i++)
		  	  names.add(vt.stringTable[i][0]);
		  /*for(int i=0;i<graph.Nodes.size();i++){
			  names.add(graph.Nodes.get(i).Id);
		  }*/
		  float matrix[][] = NetworkSmoothing(graph, names, mat, smoothingFactor, EVM);
		  //System.out.println("Matrix dimensions: "+matrix.length+"\t"+matrix[0].length);
		  
		  vt.makePrimaryHash(vt.fieldNames[0]);
		  VDataTable vt1 = new VDataTable();
		  vt1.copyHeader(vt);
		  vt1.colCount = vt.colCount;
		  //vt1.rowCount = graph.Nodes.size();
		  vt1.rowCount = vt.rowCount;
		  vt1.stringTable = new String[vt1.rowCount][vt1.colCount];
		  
		  //for(int i=0;i<graph.Nodes.size();i++){
		  for(int i=0;i<vt.rowCount;i++){
			  String name = vt.stringTable[i][0];
			  vt1.stringTable[i][0] = name;
			  int n=0;
			  for(int k=0;k<vt1.colCount;k++)
				  if(vt1.fieldTypes[k]==vt1.NUMERICAL){
					  vt1.stringTable[i][k] = ""+matrix[n][i];
					  n++;
				  }
		  }
		  return vt1;
	  }
	  
	  public static float[][] TransposeMatrix(float matrix[][]){
		  float res[][] = new float[matrix[0].length][matrix.length];
		  for(int i=0;i<matrix.length;i++)
			  for(int j=0;j<matrix[0].length;j++)
				  res[j][i] = matrix[i][j];
		  return res;
	  }
	  public static double[][] TransposeMatrix(double matrix[][]){
		  double res[][] = new double[matrix[0].length][matrix.length];
		  for(int i=0;i<matrix.length;i++)
			  for(int j=0;j<matrix[0].length;j++)
				  res[j][i] = matrix[i][j];
		  return res;
	  }
	  public static float[][] NetworkSmoothing(Graph graph, Vector<String> names, float matrix[][], float smoothingFactor){
		  return NetworkSmoothing(graph, names, matrix, smoothingFactor, null);
	  }
	
	  public static float[][] NetworkSmoothing(Graph graph, Vector<String> names, float matrix[][], float smoothingFactor, double EVM[][]){
		  float mat[][] = new float[matrix.length][names.size()];
		  System.out.println("\nSmoothing factor="+smoothingFactor);
		  
		  int numberOfConnectedComponents = Graph.ConnectedComponents(graph).size();
		  
		  int numVars = (numberOfConnectedComponents+2)+(int)(smoothingFactor*(graph.Nodes.size()-(numberOfConnectedComponents+2))+0.5f);
		  if(numVars>graph.Nodes.size())
			  numVars = graph.Nodes.size();
		  
		  System.out.println("Number of axes = "+numVars);
		  
		  double v[][] = null;
		  if(EVM==null){
			  int L[][] = GraphLaplacian(GetAjacencyMatrix(graph));
			  Vector<Double> eigenValues = new Vector<Double>();
			  v = LaplacianEigenVectors(L,eigenValues);
			  vv=v;
			  v = TransposeMatrix(v);
			  graph.calcNodesInOut();
		  }else{
			  v = EVM;
			 
		  }
		  
		  for(int i=0;i<matrix.length;i++){
			  float vector[] = matrix[i];
			  //System.out.println("===================  FIELD "+(i+1)+"  ==============");
			  vector = ProjectVectorOnGraph(graph, names, vector);
			  //System.out.println("Projected vector length: "+vector.length);
			  vector = ProjectVectorOnFirstComponentsOfABasis(vector,v,numVars,0);
			  for(int k=0;k<vector.length;k++){
				  int k1 = names.indexOf(graph.Nodes.get(k).Id);
				  if(k1>=0)
					  mat[i][k1] = vector[k];
			  }
		  }
		  
		  return mat;
		 
	  }
	  
	  public static double[][] LaplacianEigenVectors(int Laplacian[][], Vector<Double> eigenValues){
		  double mat[][] = new double[Laplacian.length][Laplacian[0].length];
		  for(int i=0;i<Laplacian.length;i++)
			  for(int j=0;j<Laplacian[0].length;j++){
				  mat[i][j] = Laplacian[i][j];
			  }
		  mat = VSimpleProcedures.CalcEigenVectors(mat, eigenValues);
		  return mat;
	  }
	  
	  public static float[] ProjectVectorOnGraph(Graph graph, Vector<String> names, float vec[]){
		  float res[] = new float[graph.Nodes.size()];
		  for(int i=0;i<res.length;i++) res[i] = Float.NaN;
		  int numNonAssigned = 1;
		  //try{
			  
		  //FileWriter fw = new FileWriter("c:/datas/dedal/test/temp.txt");
		  
		  //try{
		  //fw.write("vec length="+vec.length+"\n");
		  //fw.write("names length="+names.size()+"\n");
		  for(int i=0;i<names.size();i++){
			  String s = names.get(i);
			  if(graph.getNode(s)!=null){
				  int k = graph.Nodes.indexOf(graph.getNode(s));
				  //fw.write("Node "+s+" "+k+"\n");
				  res[k] = vec[i];
			  }
		  }
		  while(numNonAssigned>0){
			  numNonAssigned = 0;
			  for(int i=0;i<res.length;i++) 
				  if(Float.isNaN(res[i]))
					  numNonAssigned++;
			  for(int i=0;i<res.length;i++) 
				  if(Float.isNaN(res[i])){
					  Vector<String> nameNeigh = new Vector<String>();
					  for(Edge e: graph.Nodes.get(i).incomingEdges)
						  nameNeigh.add(e.Node1.Id);
					  for(Edge e: graph.Nodes.get(i).outcomingEdges)
						  nameNeigh.add(e.Node2.Id);
					  if(nameNeigh.size()==0){
						  res[i] = 0;
					  }else{
						  float v = 0f;
						  int num = 0;
						  for(String s: nameNeigh){
							  int k1 = graph.Nodes.indexOf(graph.getNode(s));
							  if(!Float.isNaN(res[k1])){
								  v+=res[k1];
								  num++;
							  }
						  }
							  if(num>0){
								  res[i] = v/(float)num;
								  //System.out.println(graph.Nodes.get(i).Id+" restored to "+res[i]);
							  }						  
					  }
				  }
		  }
		  /*}catch(Exception e1){
			  e1.printStackTrace();
			  fw.write(e1.getMessage());
			  fw.close();
		  }

		  fw.close();

		  }catch(Exception e){
			  e.printStackTrace();
		  }*/
		  return res;
	  }
	  
	public static float[] ProjectVectorOnFirstComponentsOfABasis(float vector[], double basis[][], int numberOfFirstVectors, int startFrom){
		float basisf[][] = new float[basis.length][basis[0].length];
		for(int i=0;i<basis.length;i++)
			for(int j=0;j<basis[0].length;j++)
				basisf[i][j] = (float)basis[i][j];
		return ProjectVectorOnFirstComponentsOfABasis(vector,basisf,numberOfFirstVectors,startFrom);
	}
	  
	public static float[] GetEigenSpectrum(float vector[], float basis[][]){
		float res[] = new float[basis.length];
		float normBasis[][] = NormalizeBasis(basis);
		for(int i=0;i<res.length;i++){
			res[i] = VVectorCalc.ScalarMult(vector, normBasis[i]);
		}
		return res;
	}
	
	public static float[][] NormalizeBasis(float matrix[][]){
		float res[][] = new float[matrix.length][matrix[0].length];
		for(int i=0;i<matrix.length;i++){
			float v[] = matrix[i];
			VVectorCalc.Normalize(v);
			for(int j=0;j<v.length;j++)
				res[i][j] = v[j];
		}
		return res;
	}
	
	
	public static float[] ProjectVectorOnFirstComponentsOfABasis(float vector[], float basis[][], int numberOfFirstVectors, int startFrom){
		float res[] = new float[vector.length];
		float projs[] = new float[numberOfFirstVectors];
		float nbasis[][] = NormalizeBasis(basis);
		for(int i=0;i<numberOfFirstVectors;i++){
			projs[i] = VVectorCalc.ScalarMultGap(vector, nbasis[i]);
		}
		for(int i=startFrom;i<numberOfFirstVectors;i++){
			res = VVectorCalc.Add_(res, VVectorCalc.Mult_(nbasis[i], projs[i]));
		}
		return res;
	}
	
	  public static int[][] GetAjacencyMatrix(Graph gr){
		  int am[][] = new int[gr.Nodes.size()][gr.Nodes.size()];
		  for(Edge e: gr.Edges){
			  int i = gr.Nodes.indexOf(e.Node1);
			  int j = gr.Nodes.indexOf(e.Node2);
			  am[i][j] = 1;
			  am[j][i] = 1;
		  }
		  return am;
	  }
	  
	  public static int[][] GraphLaplacian(int adjacencyMatrix[][]){
		  int L[][] = new int[adjacencyMatrix.length][adjacencyMatrix.length];
		  int connectivity[] = new int[adjacencyMatrix.length];
		  for(int i=0;i<adjacencyMatrix.length;i++){
			  for(int j=0;j<adjacencyMatrix.length;j++)
				  connectivity[i]+=adjacencyMatrix[i][j];
		  }
		  for(int i=0;i<L.length;i++)
			  for(int j=0;j<L.length;j++){
				  if(i==j)
					  L[i][j] = connectivity[i];
				  else
					  L[i][j] = -adjacencyMatrix[i][j];
			  }
		  return L;
	  }
	  
	  public static void SaveMatrixToFile(float data[][], String fn){
		  try{
			  FileWriter fw = new FileWriter(fn);
			  for(int i=0;i<data.length;i++){
				  for(int j=0;j<data[0].length;j++)
					  fw.write(data[i][j]+"\t");
				  fw.write("\n");
			  }
			  fw.close();
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  
	  public static void SaveNamedMatrixToFile(Vector<String> names, float data[][], String fn){
		  try{
			  FileWriter fw = new FileWriter(fn);
			  for(int i=0;i<data.length;i++){
				  fw.write(names.get(i)+"\t");
				  for(int j=0;j<data[0].length;j++)
					  fw.write(data[i][j]+"\t");
				  fw.write("\n");
			  }
			  fw.close();
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  
	  public static void SaveStringListToFile(Vector<String> names, String fn){
		  try{
			  FileWriter fw = new FileWriter(fn);
			  for(int i=0;i<names.size();i++){
				  fw.write(names.get(i)+"\n");
			  }
			  fw.close();
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	

}
