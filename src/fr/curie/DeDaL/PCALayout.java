package fr.curie.DeDaL;

import java.util.Random;

import vdaoengine.analysis.PCAMethod;
import vdaoengine.analysis.elmap.ElmapAlgorithm;
import vdaoengine.analysis.elmap.ElmapProjection;
import vdaoengine.data.VDataSet;
import vdaoengine.data.VDataTable;
import vdaoengine.data.io.VDatReadWrite;
import vdaoengine.utils.VSimpleProcedures;

public class PCALayout {
	
	public VDataSet dataset = null;
	public float geneProjections[][] = null; // This is used to make the layout
	public float attributeContributions[][] = null; // This is not needed for layout but just in case
	public float explainedVariation[] = null;
	public int pcNumber;
	
	public String settings = "#ALG8\n"+
			"algtype = epoch\n\n"+
			"grid.type = simplerectangular\n"+
			"grid.size1 = 10\n"+
			"grid.size2 = 10\n\n"+
			"init.stretch1 = 1\n"+
			"init.stretch2 = 1\n"+
			"init.stretch3 = 1\n\n"+
			"epoch.id = 1\n"+
			"epoch.minimize = true\n"+
			"epoch.ep = 1\n"+
			"epoch.rp = 250\n"+
			"epoch.eps = 0.01\n"+
			"epoch.epsSLAU = 0.01\n"+
			"epoch.numiter = 3\n"+
			"epoch.numiterSLAU = 1000\n\n"+
			"epoch.id = 2\n"+
			"epoch.minimize = true\n"+
			"epoch.numiter = 10\n"+
			"epoch.ep = 0.001\n"+
			"epoch.rp = 20\n"+
			"epoch.extrapolate = 3\n\n";
	
	public static void main(String[] args) {
		int numberOfPoints = 100; // 100 points
		float matrix[][] = new float[numberOfPoints][10]; // 10 dimensions
		Random r = new Random();
		for(int i=0;i<matrix.length;i++)
			for(int j=0;j<matrix[0].length;j++)
				matrix[i][j] = r.nextFloat();
		// Let us add some missing values
		matrix[10][3] = Float.NaN;
		matrix[15][0] = Float.NaN;		
		matrix[51][5] = Float.NaN;				
		
		PCALayout pca = new PCALayout();
		pca.makeDataSet(matrix);
		//pca.loadDataSet("c:/datas/test/iris1.txt");
		//pca.dataset = VSimpleProcedures.SimplyPreparedDataset(VDatReadWrite.LoadFromVDatFile("c:/datas/test/wine.dat"),-1);
		pca.computePCA();
		System.out.println("Variance explained by PC1 = "+pca.explainedVariation[0]);
		System.out.println("Variance explained by PC2 = "+pca.explainedVariation[1]);		
		
		pca.computeNonlinearPCALayout();
		System.out.println("\nVariance explained by nonlinear pca = "+pca.explainedVariation[0]);
		
		System.out.println("POINT\tX\tY");
		for(int i=0;i<pca.geneProjections.length;i++)
			System.out.println(i+"\t"+pca.geneProjections[i][0]+"\t"+pca.geneProjections[i][1]);
	}
	
	public void makeDataSet(float matrix[][]){
		dataset = new VDataSet();
		dataset.coordCount = matrix[0].length;
		dataset.pointCount = matrix.length;
		dataset.massif = matrix.clone();
		// hasGaps is set to true to warn that there are some missing values
		dataset.hasGaps = true;
		
		dataset.calcStatistics();
	}
	
	// this can load a dataset from simple tab-delimited text file with numbers and a header
	public void loadDataSet(String fn){
		VDataTable vt = VDatReadWrite.LoadFromSimpleDatFile(fn, true, "\t");
		for(int i=0;i<vt.colCount;i++)
			vt.fieldTypes[i] = vt.NUMERICAL;
		dataset = VSimpleProcedures.SimplyPreparedDataset(vt, -1);
	}
	
	
public void computePCA(){
		
		PCAMethod pca = new PCAMethod();
		pca.setDataSet(dataset);
		pca.calcBasis(pcNumber);

		attributeContributions = new float[dataset.coordCount][pcNumber];
		for(int j=0;j<pcNumber;j++) for(int i=0;i<dataset.coordCount;i++) attributeContributions[i][j] = (float)pca.getBasis().basis[j][i];
		
		VDataSet vdprojected = pca.getProjectedDataset();
		geneProjections = vdprojected.massif;
		
		explainedVariation = new float[pcNumber];
		double disp[] = pca.calcDispersionsRelative(dataset.simpleStatistics.totalDispersion*dataset.simpleStatistics.totalDispersion);
		for (int i=0; i<pcNumber;i++){
		explainedVariation[i] = (float)disp[i];
		}
}
	public void computeNonlinearPCALayout(){
		  ElmapProjection elmap = new ElmapProjection();
	      ElmapAlgorithm ela = new ElmapAlgorithm();
	      ela.setData(dataset);
	      ela.readIniFileStr(settings,8);
	      ela.computeElasticGrid();
	      
	      //ela.grid.saveToFile("c:/datas/test/iris1.vem", "iris1");
	      //ela.grid.saveToFile("c:/datas/test/wine.vem", "wine");
	      
	      elmap.setElmap(ela);
	      VDataSet vd = elmap.getProjectedDataset();
	      geneProjections = vd.massif;
	      
	      explainedVariation = new float[1];
	      explainedVariation[0] = 1-elmap.calculateMSEToProjection(dataset)/(dataset.simpleStatistics.totalDispersion*dataset.simpleStatistics.totalDispersion);
	      
	}

}
	