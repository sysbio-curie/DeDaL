package fr.curie.DeDaL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import vdaoengine.data.VDataSet;
import vdaoengine.data.VDataTable;
import vdaoengine.data.io.VDatReadWrite;
import vdaoengine.utils.VSimpleProcedures;

public class DeDaLCommandLine {
	
	String datafilename = null;
	String network_sif_format = null;
	String output_network_filename = null;
	String eigenVectorMatrix_filename = null;
	boolean writeEVMasText = false;
	float network_smoothing_factor = Float.NaN;
	float threshold_missing = 0.1f;
	boolean doDoubleCentering = false;
	boolean loadEVMMatrix = false;
	boolean saveEVMMatrix = false;
	int PC1 = -1;
	int PC2 = -1;
	float scale = 1000f;
	float node_radius_for_repulse = -1f;
	boolean linearize_paths = false;

	public static void main(String[] args) {
		try{

			DeDaLCommandLine ddl = new DeDaLCommandLine();			
			
			if(args.length==0){
				System.out.println("DeDaL Command Line Usage: \n"
						+ "java -jar DeDaL.jar fr.curie.DeDaL.DeDaLCommandLine -network <filename.sif> -data <data.txt> -out <filename.xgmml> [options]\n"
						+ "\n"
						+ "Options:\n"
						+ "-dc : apply double centering for data pre-processing"
						+ "-ns <smoothing_factor> : if specified, network smoothing is applied with smoothing_factor coefficient of smoothing (0 - no smoothing,1 - extreme smoothing), a new file with smoothed data will be created automatically\n"
						+ "-maxmissing <threshold>: maximum ratio of the number of missing values in a row (from [0;1])\n"
						+ "-evm <filename.dat> :  works if -ns option is specified, allows to load pre-computed eigen vector decomposition of the network Laplacian (if file does not exist then it is computed and saved)\n"
						+ "-pc1 <integer> : first dimension of PCA analysis to choose for DDL\n"
						+ "-pc2 <integer> : second dimension of PCA analysis to choose for DDL\n"
						+ "(if neither pc1 or pc2 are specified then non-linear PCA is applied to compute DDL)\n"
						+ "-scale <float> : scale factor for the layout ("+ddl.scale+" by default)\n"
						+ "-linearizepaths : straigthen linear paths in the layout\n"
						+ "-repulsenodes <node_radius> : apply node repulsion algorithm to improve the lyout readability\n");
			}else{
			for(int i=0;i<args.length;i++){
				if(args[i].equals("-network")) ddl.network_sif_format = args[i+1];
				if(args[i].equals("-data")) ddl.datafilename = args[i+1];
				if(args[i].equals("-out")) ddl.output_network_filename = args[i+1];
				if(args[i].equals("-dc")) ddl.doDoubleCentering = true;
				if(args[i].equals("-ns")) { ddl.network_smoothing_factor = 1-Float.parseFloat(args[i+1]); }
				if(args[i].equals("-maxmissing")) ddl.threshold_missing = Float.parseFloat(args[i+1]);
				if(args[i].equals("-evm")) { ddl.eigenVectorMatrix_filename = args[i+1]; }
				if(args[i].equals("-evmtxt")) { ddl.writeEVMasText = true; }
				//if(args[i].equals("-saveevm")) { ddl.eigenVectorMatrix_filename = args[i+1]; ddl.saveEVMMatrix = true; }
				if(args[i].equals("-pc1")) ddl.PC1 = Integer.parseInt(args[i+1]);
				if(args[i].equals("-pc2")) ddl.PC2 = Integer.parseInt(args[i+1]);
				if(args[i].equals("-scale")) ddl.scale = Float.parseFloat(args[i+1]);
				if(args[i].equals("-linearizepaths")) ddl.linearize_paths = true;
				if(args[i].equals("-repulsenodes")) ddl.node_radius_for_repulse = Float.parseFloat(args[i+1]);
			}
			
			ddl.doTheJob();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void doTheJob() throws Exception{
		Graph gr = GraphCreator.loadFromSif(network_sif_format);
		System.out.println("Number of nodes: "+gr.Nodes.size());
		System.out.println("Number of edges: "+gr.Edges.size());

		VDataTable vt = VDatReadWrite.LoadFromSimpleDatFile(datafilename, true, "\t");
		for(int i=1;i<vt.colCount;i++) vt.fieldTypes[i] = vt.NUMERICAL;
		vt.makePrimaryHash(vt.fieldNames[0]);

		Vector<Integer> lineNumbers = new Vector<Integer>();
		for(int i=0;i<gr.Nodes.size();i++){
			String node = gr.Nodes.get(i).Id;
			int k = -1;
			if(vt.tableHashPrimary.get(node)!=null){
				k = vt.tableHashPrimary.get(node).get(0);
				lineNumbers.add(k);
			}
		}
		
		vt = VSimpleProcedures.selectRows(vt, lineNumbers);
		System.out.println("Selected rows: "+vt.rowCount);

		
		if(doDoubleCentering){
			float dat[][] = new float[vt.rowCount][vt.colCount-1];
			dat = VSimpleProcedures.SimplyPreparedDatasetWithoutNormalization(vt, -1).massif;
			dat = PreprocessingProcedures.doubleCenterMatrix(dat);
			for(int i=0;i<vt.rowCount;i++)for(int j=1;j<vt.colCount;j++) vt.stringTable[i][j]=""+dat[i][j-1];
		}
		
		if(!Float.isNaN(network_smoothing_factor)){
			double v[][] = null;
			if((new File(eigenVectorMatrix_filename)).exists()){
				ObjectInputStream ios = new ObjectInputStream(new FileInputStream(eigenVectorMatrix_filename));
				short vs[][] = null;
				vs = (short[][])ios.readObject();
				v = new double[vs.length][vs[0].length];
				for(int i=0;i<vs.length;i++)for(int j=0;j<vs[0].length;j++) v[i][j] = (float)vs[i][j]/1000f;
			}else{
				int am[][] = gr.GetAjacencyMatrix(gr);
				int L[][] = gr.GraphLaplacian(am);
				Vector<Double> eigenValues = new Vector<Double>();
				Date tm1 = new Date();
				v = PreprocessingProcedures.LaplacianEigenVectors(L,eigenValues);
				if(writeEVMasText){
					FileWriter fw = new FileWriter(eigenVectorMatrix_filename+".txt");
					fw.write("NAME\t"); for(int i=0;i<v[0].length;i++) fw.write("E"+i+"\t"); fw.write("\n");
					for(int i=0;i<v.length;i++){
						fw.write(gr.Nodes.get(i).Id+"\t");
						for(int j=0;j<v[i].length;j++){
							DecimalFormat df = new DecimalFormat("#.##");
							String s = df.format(v[i][j]);
							fw.write(s+"\t"); 
						}
						fw.write("\n");
					}
					fw.close();
				}
				v = PreprocessingProcedures.TransposeMatrix(v);
				System.out.println("=================================================");
				System.out.println("Time to compute eigenvectors "+(new Date().getTime()-tm1.getTime()));
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(eigenVectorMatrix_filename));
				short vs[][] = new short[v.length][v[0].length];
				for(int i=0;i<v.length;i++)for(int j=0;j<v[0].length;j++) vs[i][j] = (short)(v[i][j]*1000);
				oos.writeObject(vs);
			}
			float nsf = network_smoothing_factor;
			
			Date tm = new Date();
			VDataTable vt1 = PreprocessingProcedures.NetworkSmoothing(vt, gr, nsf, v);
			System.out.println("=================================================");
			System.out.println("Time to smooth table: "+(new Date().getTime()-tm.getTime())+"\n");
			for(int i=0;i<vt1.colCount;i++) vt1.fieldNames[i] = vt1.fieldNames[i]+"_s"+nsf;
			VDatReadWrite.saveToVDatFile(vt1, datafilename+nsf+".dat");
			VDatReadWrite.saveToSimpleDatFile(vt1, datafilename+nsf+".txt", true);
			vt = vt1;
		}

		PCALayout pcal = new PCALayout();
		if((PC1<0)||(PC2<0)){
			Date tm2 = new Date();
			VDataSet vd = VSimpleProcedures.SimplyPreparedDatasetWithoutNormalization(vt, -1);
			pcal.makeDataSet(vd.massif);
			pcal.computeNonlinearPCALayout();
			System.out.println("=================================================");
			System.out.println("Time to compute principal manifold: "+(new Date().getTime()-tm2.getTime()));
			System.out.println("\nVariance explained by nonlinear pca = "+pcal.explainedVariation[0]);
			for(int i=0;i<vt.rowCount;i++){
				String name = vt.stringTable[i][0];
				Node n = gr.getNode(name);
				n.x = pcal.geneProjections[i][0]*scale;
				n.y = pcal.geneProjections[i][1]*scale;
			}
		}else{
			Date tm2 = new Date();
			VDataSet vd = VSimpleProcedures.SimplyPreparedDatasetWithoutNormalization(vt, -1);
			pcal.makeDataSet(vd.massif);
			pcal.pcNumber = Math.min(10, vt.colCount-1);
			pcal.computePCA();
			System.out.println("=================================================");
			System.out.println("Time to compute "+pcal.pcNumber+" PCAs: "+(new Date().getTime()-tm2.getTime()));
			System.out.println("Variance explained by PC1 = "+pcal.explainedVariation[0]);
			System.out.println("Variance explained by PC2 = "+pcal.explainedVariation[1]);		
			for(int i=0;i<vt.rowCount;i++){
				String name = vt.stringTable[i][0];
				Node n = gr.getNode(name);
				n.x = pcal.geneProjections[i][PC1-1]*scale;
				n.y = pcal.geneProjections[i][PC2-1]*scale;
			}
		}
		
		gr.saveAsCytoscapeXGMML(output_network_filename);

	}

}
