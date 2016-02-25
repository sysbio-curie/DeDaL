package fr.curie.DeDaL;


import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Object;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.undo.UndoSupport;

import vdaoengine.data.VDataTable;
import vdaoengine.data.io.VDatReadWrite;

import java.text.DecimalFormat;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

///////////////////>>>>DEDAL 2014<<<<<<//////////////////////////
//Cytoscape 3.0 plug-in
// Here is the code performing Data Driven Layout:PCA and nlPM
//it rescale the coordinates to fit better the screan
// nodes with missing values follow their neighbours
///////////////////////////////////////////////////////////////////

public class MenuAction extends AbstractCyAction {
	public CyAppAdapter adapter;
	public Map<String,List<Double>> pcaMap;
	public Map<String,List<Double>> startMap;
	private static MenuAction instance;
	private DedalMethods dMet = new DedalMethods();
	public CyApplicationManager manager;
	public CyNetwork mynetwork;
	private CyNetworkView networkView;
	private UndoDDL undoDDL;
	private Stats stats;
public MenuAction(CySwingAppAdapter adapter){
// Add a menu item
	super("Data-Driven Layout ",
			adapter.getCyApplicationManager(),
			"network",
			adapter.getCyNetworkViewManager());
	this.adapter = adapter;
	setPreferredMenu("Layout.DeDaL");
	instance=this;

}
public static MenuAction getInstance(){return instance;}


	public void actionPerformed(ActionEvent e) {
		
		long start = System.currentTimeMillis( );
		manager = adapter.getCyApplicationManager();
		//final CyRootNetworkManager rootmanager = adapter.getCyRootNetworkManager();
		//final CyNetworkManager netmanager = adapter.getCyNetworkManager();
		//final CyNetworkReader reader = readermanager.getReader(arg0, arg1)
		networkView = manager.getCurrentNetworkView();
		final CyNetwork network = networkView.getModel();

	mynetwork = manager.getCurrentNetwork();
		CyTable mytable = mynetwork.getDefaultNodeTable();

	
		dMet.makeMap(manager);
		startMap=dMet.netMap;
		
		
		//general network description
		
		Double height = networkView.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT );
		//System.out.println("height= "+height);
		Double width = networkView.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH );
		//System.out.println("width= "+ width);
		Double scale = networkView.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR );
		//System.out.println("scale= "+ scale);
		networkView.setVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR,0.5 );
	scale = networkView.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR );

		//System.out.println("scale= "+ scale);
//Double scale=0.55;
		
		
		//all nodes of the network
		List<CyNode> nodes = mynetwork.getNodeList();
		//System.out.println(nodes);
		
		//Create a set with all column names
		/*Set<String> set_attributes = new HashSet<String>();
		for (CyNode node : mynetwork.getNodeList()) {
			CyRow row = mynetwork.getRow(node);
			Map<String,Object> values = row.getAllValues();
			
			for (String col: values.keySet()){
				
					set_attributes.add(col);
					//System.out.println(col);
				
			}
			
		}
		*/
		//test
		
		Collection<CyColumn> colList  = mytable.getColumns() 	;
		for (CyColumn c : colList){
			Class<?> type = c.getType();
			String  cname = c.getName();
			
		}
		
		
		
			//A list with all column names
			ArrayList<String> myList = new ArrayList<String>();
			for (CyColumn c : colList){
				Class<?> type = c.getType();
				String ntype = type.getName();
				String  cname = c.getName();
				//System.out.println(cname + " : "+ type);
				//System.out.println("ntype" + ntype);
				
				if (ntype.equals("java.lang.String")||ntype.equals("java.lang.Boolean") ||ntype.equals("java.lang.Long")|| cname.equals("selected"))
					
			
					{}
					else{
				
					myList.add(cname);
					//System.out.println(col);
				
					}}
			
			//a window for the user to select columns needed in analysis
			SelectColumnsDialog d = new SelectColumnsDialog(new JFrame(),"Choose data and DeDaL algorithm",true);
			d.setDialogData(myList);
			d.setVisible(true);

			
		//re-creation of attributes matrix
		float [][] matrix = new float [nodes.size()] [d.myselList.size()] ; 
		Map<String,Integer> map_index = new HashMap<String,Integer>();
		int ct = 0;
		for (String att : d.myselList){
			map_index.put(att,ct);
			ct++;
		}
		//System.out.println(map_index);
		//System.out.println(d.myselList);
		int ct2=0;
		for (CyNode node : mynetwork.getNodeList()) {
			CyRow row = mynetwork.getRow(node);
			Map<String,Object> values = row.getAllValues();
			String myname = mynetwork.getRow(node).get("name", String.class);
			//System.out.println(myname);
			//System.out.println(ct2);
			for (String col: values.keySet()){
				if (map_index.containsKey(col)){
					int index2=map_index.get(col);
					//System.out.println(index2);
					

					Object value = mynetwork.getRow(node).getAllValues().get(col);
					//System.out.print(value);
					
					//check if attributes are numerical values 
					if (value instanceof String||value instanceof Boolean){
						JOptionPane.showMessageDialog(null, "Columns must contain only numerical values","ERROR", JOptionPane.WARNING_MESSAGE);
						
					}
					
					//missing values are replaced by 0
					// ????? MUST BE REPLACED BY NaN????
					Float fvalue = 0f;
					if (value == null){
						fvalue=Float.NaN;
						//fvalue=0f;
					}else{
						if(value instanceof Integer)
							fvalue = ((Integer)value).floatValue();
						else 
						if(value instanceof Double)	
							fvalue = ((Double) value).floatValue();
						else
						if(value instanceof Float)
							fvalue = ((Float) value).floatValue();;
					}
					//System.out.print(fvalue);


					//System.out.print(ct2);
					//System.out.print(index2);
					//System.out.print(" ");

					// FINAL MATRIX
					//System.out.println(fvalue);
					matrix[ct2][index2]=fvalue; }
				

			}
			ct2++;
		}

		
		//PreprocessingProcedures.SaveMatrixToFile(matrix, "c:/datas/dedal/test/temp_matrixinit.txt");



		/*Matrix newmatrix = new Matrix(matrix);
		System.out.println(newmatrix);
		float[][] redmatrix = newmatrix.removeRowsWithValue(0.2);

		for (int i = 0; i < redmatrix[0].length; i++) {
			for (int j = 0; j < redmatrix.length; j++) {
				System.out.print(redmatrix[i][j] + " ");
			}
			System.out.print("\n");
		}*/	

		

		// delete row that have too much missing values
		float[][] data = matrix;
		double treshold= 0.2;
		ArrayList<Integer> delnodes = new  ArrayList<Integer>();
		
		/* Use an array list to track of the rows we're going to want to 
	               keep...arraylist makes it easy to grow dynamically so we don't 
	               need to know up front how many rows we're keeping */
		List<float[]> rowsToKeep = new ArrayList<float[]>(data.length);
		
		//System.out.print("length data" + data.length);
		List<CyNode> mynewnodelist = mynetwork.getNodeList();
		int nnode = 0;
		int nnode2=0;
		int nrow = 0;
		for(float[] row : data)
		{
			/* If you download Apache Commons, it has built-in array search
	                      methods so you don't have to write your own */



			boolean found = false;
			double sum0=0;
			
			//System.out.println(nnode);
			for(float testValue : row)
			{
				/* Using == to compares doubles is generally a bad idea 
	                               since they can be represented slightly off their actual
	                               value in memory */

				if(Float.isNaN(testValue)){
				//if(testValue == 0){
					sum0++;
				}
			
				double rate=sum0/data[0].length;	

				if(rate>treshold)	
				{//System.out.println(nnode);
					found = true;
					//System.out.println("found"+nnode);
					//System.out.println("found"+nnode2);
					delnodes.add(nnode);
					
					mynewnodelist.remove(nnode2);
					nnode2=nnode2-1;
					//System.out.println(mynewnodelist);
					break;
				}
			}

			/* if we didn't find our value in the current row, 
	                      that must mean its a row we keep */
			if(!found)
			{
			
				rowsToKeep.add(row);
				
				nrow++;
				//System.out.println(row);
			}nnode++;
			nnode2++;
			
		}
		//System.out.println(delnodes);
		/* now that we know what rows we want to keep, make our 
	               new 2D array with only those rows */
		data = new float[rowsToKeep.size()][];
		for(int i=0; i < rowsToKeep.size(); i++)
		{
			data[i] = rowsToKeep.get(i);

		}
		
		//PreprocessingProcedures.SaveMatrixToFile(data, "c:/datas/dedal/test/temp_matrixfilt.txt");
		
		//System.out.print("length data" + data.length);
		/*for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				System.out.print(data[i][j] + " ");
			}
			System.out.print("\n");
		}*/

		int numberOfPoints = mynewnodelist.size();	
//System.out.println("check1");
		// ============================ PREPROCESSING DATA ===================================
		//double center data
		if (d.dcenter.isSelected()){//center data code
			data = PreprocessingProcedures.doubleCenterMatrix(data);
		}
		
		//network-smooth data
		if(d.nsmoothing.isSelected()){
			
			double v[][] = null;
			//System.out.println("v start: ");
			//System.out.println(v);
			if (d.file!=null){
				System.out.print("have the eigen vect file");
				ObjectInputStream ios;
				try {
					ios = new ObjectInputStream(new FileInputStream(d.file));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				short vs[][] = null;
				try {
					ios = new ObjectInputStream(new FileInputStream(d.file));
					vs = (short[][])ios.readObject();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Wrong file format","ERROR", JOptionPane.WARNING_MESSAGE);

					e1.printStackTrace();
				}
				v = new double[vs.length][vs[0].length];
				for(int i=0;i<vs.length;i++)for(int j=0;j<vs[0].length;j++) v[i][j] = (float)vs[i][j]/1000f;
			
				
			}
			
			CyNetwork thisnetwork = manager.getCurrentNetwork();
			Graph graph = GraphCreator.CreateGraphFromCyNetwork(thisnetwork);

			//int am[][] = graph.GetAjacencyMatrix(graph);
			//int L[][] = graph.GraphLaplacian(am);
			//Vector<Double> eigenValues = new Vector<Double>();
			//Date tm1 = new Date();
			//v = PreprocessingProcedures.LaplacianEigenVectors(L,eigenValues);
			
			
			
			float smoothingFactor = 1-(float)d.smoothingvalue.getValue()/100f;
			Vector<String> names = new Vector<String>();
			for(CyNode node: mynewnodelist)
				names.add(GraphCreator.getNodeName(node, thisnetwork));
			//System.out.println("v end: ");
			//System.out.println(v);
			data = PreprocessingProcedures.NetworkSmoothing(graph, names, data, smoothingFactor,v);
			//PreprocessingProcedures.SaveMatrixToFile(data, "c:/datas/dedal/test/temp_ns1.txt");
			data = PreprocessingProcedures.TransposeMatrix(data);
			//PreprocessingProcedures.SaveMatrixToFile(data, "c:/datas/dedal/test/temp_ns2.txt");
			//PreprocessingProcedures.SaveStringListToFile(names, "c:/datas/dedal/test/temp_ns2_names.txt");
			v = PreprocessingProcedures.vv;
			if(d.saveEigenVectors){	
				System.out.print("exporting eigen vect file");
				FileWriter fw;
				try {
					fw = new FileWriter(d.fs+".txt");
					fw.write("NAME\t"); for(int i=0;i<v[0].length;i++) fw.write("E"+i+"\t"); fw.write("\n");
					for(int i=0;i<v.length;i++){
						fw.write(graph.Nodes.get(i).Id+"\t");
						for(int j=0;j<v[i].length;j++){
							DecimalFormat df = new DecimalFormat("#.##");
							String s = df.format(v[i][j]);
							fw.write(s+"\t"); 
						}
						fw.write("\n");
					}
					fw.close();
					v = PreprocessingProcedures.TransposeMatrix(v);
					
					ObjectOutputStream oos;
					try {
						oos = new ObjectOutputStream(new FileOutputStream(d.fs));
						short vs[][] = new short[v.length][v[0].length];
						for(int i=0;i<v.length;i++)for(int j=0;j<v[0].length;j++) vs[i][j] = (short)(v[i][j]*1000);
						oos.writeObject(vs);
					
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Name your file","ERROR", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				}
			
				
			}}else{
					if (d.file!=null){
						JOptionPane.showMessageDialog(null, "Check option 'Network smoothing' in order to use the file","ERROR", JOptionPane.WARNING_MESSAGE);

					}
					if(d.saveEigenVectors){
						JOptionPane.showMessageDialog(null, "Check option 'Network smoothing' in order to compute and save eigen vectors","ERROR", JOptionPane.WARNING_MESSAGE);

					}
			
			}
				
		// saving preprocessed data
		
		
		
		
		if(d.savePreprocessedData){
			try{
			CyNetwork thisnetwork = manager.getCurrentNetwork();
			JFileChooser fc = new JFileChooser();
			int ret = fc.showDialog((new SelectColumnsDialog()), "Select");
			if (ret == JFileChooser.APPROVE_OPTION) {
				File f = fc.getSelectedFile();
				FileWriter fw = new FileWriter(f);
				fw.write("NODE\t");
				CyRow row = thisnetwork.getRow(thisnetwork.getNodeList().get(0));				
				Map<String,Object> values = row.getAllValues();				
				for (String col: values.keySet()){
					if (map_index.containsKey(col)){
						fw.write(col+"\t");
					}
				}
				fw.write("\n");
				int nodecount = 0;
				for(CyNode node: mynewnodelist){
					fw.write(GraphCreator.getNodeName(node, network)+"\t");
					row = thisnetwork.getRow(node);				
					values = row.getAllValues();				
					for (String col: values.keySet()){
						if (map_index.containsKey(col)){
							int index2=map_index.get(col);
							fw.write(data[nodecount][index2]+"\t");
						}
					}
					nodecount++;
					fw.write("\n");
				}
				fw.close();
				VDataTable vt = VDatReadWrite.LoadFromSimpleDatFile(f.getAbsolutePath(), true, "\t");
				for(int i=1;i<vt.colCount;i++) vt.fieldTypes[i] = vt.NUMERICAL;
				VDatReadWrite.saveToVDatFile(vt, f.getAbsolutePath()+".dat"); 
			}
			}catch(Exception exc){
				exc.printStackTrace();
			}
		}
		// ==================================================================================
	
		//
		//System.out.println("check2");		
		//PCA analysis on matrix without missing values
		
			ArrayList<Double> arrayListX = new ArrayList<Double>();
			ArrayList<Double> arrayListY = new ArrayList<Double>();
		if (d.pcaButton.isSelected()){
		
			
		PCALayout pca = new PCALayout();
		if (data[0].length<10){pca.pcNumber = data[0].length;}
		else {pca.pcNumber = 10;}
		//System.out.print("pcNumber= "+ pca.pcNumber);
		//System.out.print("data[0].length= "+ data[0].length);
		pca.makeDataSet(data);
		pca.computePCA();
		
		
		
		
	
		int a2 =0;
		int a3 =0;
		int a4 =0;
		int a5 =0;
		int a6 =0;
		int a7 =0;
		int a8 =0;
		int a9 =0;
		//check only 2 PC
		int index = 0;
		int pcCount =0;
		if (d.pc1.isSelected()){index++; pcCount=1;}
		if (d.pc2.isSelected()){index++;pcCount=2;}
		if (d.pc3.isSelected()){index++;pcCount=3;}
		if (d.pc4.isSelected()){index++;pcCount=4;}
		if (d.pc5.isSelected()){index++;pcCount=5;}
		if (d.pc6.isSelected()){index++;pcCount=6;}
		if (d.pc7.isSelected()){index++;pcCount=7;}
		if (d.pc8.isSelected()){index++;pcCount=8;}
		if (d.pc9.isSelected()){index++;pcCount=9;}
		if (d.pc10.isSelected()){index++;pcCount=10;}
		//System.out.println(index);
		if (index!=2){
			JOptionPane.showMessageDialog(null, "Choose exacly 2 principal components","ERROR", JOptionPane.WARNING_MESSAGE);
			networkView.fitContent();
			}
		else if (data[0].length<2){
			networkView.fitContent();
		}
		else if (pcCount>data[0].length){
			JOptionPane.showMessageDialog(null, "You can compute as many principal components as colums in your data set, not more!","ERROR", JOptionPane.WARNING_MESSAGE);
			networkView.fitContent();
			}
		else
		{
		//System.out.println("POINT\tX\tY");
		for(int i=0;i<numberOfPoints;i++){
			

			
	
			
			if (d.pc1.isSelected()){
				
					double x = pca.geneProjections[i][0];
					arrayListX.add(x);
			}
			if (d.pc2.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a2==1)){
					a2=1;
					double x = pca.geneProjections[i][1];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][1];
				arrayListY.add(y);}
			}
			if (d.pc3.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a3==1)){
					a3=1;
					double x = pca.geneProjections[i][2];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][2];
				arrayListY.add(y);}
			}
			if (d.pc4.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a4==1)){
					
					a4=1;
					double x = pca.geneProjections[i][3];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][3];
				arrayListY.add(y);}
			}
			if (d.pc5.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a5==1)){
					a5=1;
					double x = pca.geneProjections[i][4];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][4];
				arrayListY.add(y);}
			}
			if (d.pc6.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a6==1)){
					a6=1;
					double x = pca.geneProjections[i][5];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][5];
				arrayListY.add(y);}
			}
			if (d.pc7.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a7==1)){
					a7=1;
					double x = pca.geneProjections[i][6];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][6];
				arrayListY.add(y);}
			}
			if (d.pc8.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a8==1)){
					a8=1;
					double x = pca.geneProjections[i][7];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][7];
				arrayListY.add(y);}
			}
			if (d.pc9.isSelected()){
				if((i==0 && arrayListX.size()<1) ||  ((i>0)&& a9==1)){
					a9=1;
					double x = pca.geneProjections[i][8];
					arrayListX.add(x);}
				else {double y = pca.geneProjections[i][8];
				arrayListY.add(y);}
			}
			if (d.pc10.isSelected()){
				double y = pca.geneProjections[i][9];
				arrayListY.add(y);
			}
			
		
		
		}
		
		//System.out.println("check3");	
		String sortie ="";
		sortie += " PC\t% of variance";
		 int b=0;
		
		for (double i : pca.explainedVariation ){
			b++;
			 //System.out.println(i);
			DecimalFormat df = new DecimalFormat("#.00");
			String c = df.format(i*100);
			//i = (double)Math.round(i * 1000) / 1000;
			sortie+= "\n PC " + b + "\t" + c + "%";}
		
	JTextArea aireSortie = new JTextArea (7,10);
	aireSortie.setText (sortie);
	
	JOptionPane.showMessageDialog(null,aireSortie,"Report", JOptionPane.INFORMATION_MESSAGE);
	
		//JOptionPane.showMessageDialog(null,,"REPORT", JOptionPane.INFORMATION_MESSAGE);
		}
		
		}
		else if (d.nlpcaButton.isSelected()){
			PCALayout nlpca = new PCALayout();
			nlpca.makeDataSet(data);
			nlpca.computeNonlinearPCALayout();
			for(int i=0;i<numberOfPoints;i++){
			double x = nlpca.geneProjections[i][0];
			arrayListX.add(x);
			double y = nlpca.geneProjections[i][1];
			arrayListY.add(y);
			}
			
			
			String sortie ="";
		
			 
				double pofv = nlpca.explainedVariation[0];
				DecimalFormat df = new DecimalFormat("#.00");
				String c = df.format(pofv*100);
				//i = (double)Math.round(i * 1000) / 1000;
				sortie+= "\n Explained variation \t" + c + "%";
			
		JTextArea aireSortie = new JTextArea (7,10);
		aireSortie.setText (sortie);
		
		JOptionPane.showMessageDialog(null,aireSortie,"Rapport", JOptionPane.INFORMATION_MESSAGE);
			
	}
		ArrayList<Double> arrayListF = new ArrayList<Double>();
		//System.out.println("check4");	

		Double maxX = Collections.max(arrayListX);
		Double minX = Collections.min(arrayListX);
		//System.out.println("max X= "+ maxX + "minX= "+ minX);

		Double sumX = maxX +Math.abs(minX);
		//System.out.println(sumX);


		Double factorX =(width/(scale))/sumX;
		//System.out.println("factorX= " + factorX);
		arrayListF.add(factorX);

		Double maxY = Collections.max(arrayListY);
		Double minY = Collections.min(arrayListY);
		//System.out.println("max Y= "+ maxY+"minY= "+ minY);


		Double sumY = maxY +Math.abs(minY);
		//System.out.println(sumY);

		Double factorY =(height/(scale))/sumY;
		//System.out.println("factorY= " + factorY);
		//System.out.println("check5");	
		arrayListF.add(factorY);
		Double factor = Collections.min(arrayListF);
		//System.out.println("X" + arrayListX);
		//System.out.println("Y"+ arrayListY);
	
		
		//System.out.println(numberOfPoints);
		//System.out.println("check5A");
		for(int i=0;i<numberOfPoints;i++){
			//System.out.println(mynewnodelist);
			CyNode node = mynewnodelist.get(i);
			View<CyNode> nodeViewf =  networkView.getNodeView(node) ;
			//System.out.println("check5b");
			//System.out.println(arrayListX.get(i));
			//System.out.println( arrayListY.get(i));
			double x = arrayListX.get(i) *factor;
			double y = arrayListY.get(i)*factor;
			//System.out.println(y);
			//System.out.println(x);

			nodeViewf.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeViewf.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
		}
		///>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICICICICICI???
		//System.out.println("check6");
	dMet.outliers(manager,1.5);
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
		for(int i=0;i<2;i++){
		for (int node : delnodes)
		{		
			CyNode delnode = nodes.get(node);
			//String delnodename = mynetwork.getRow(delnode).get("name", String.class);
			//System.out.println("empty node name: " + delnodename);
			List<CyNode> neighbors = mynetwork.getNeighborList(delnode, CyEdge.Type.ANY);
			ArrayList<Double> nx = new  ArrayList<Double>();
			ArrayList<Double> ny = new  ArrayList<Double>();
			for (CyNode neighnode : neighbors)
			 {
				View<CyNode> nodeViewN =  networkView.getNodeView(neighnode) ;
				String mynameneig = mynetwork.getRow(neighnode).get("name", String.class);
				//System.out.println("neighbor: " + mynameneig);
				Double xD = nodeViewN.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				Double yD = nodeViewN.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				nx.add(xD);
				ny.add(yD);
		}
			//System.out.println("check7");

			View<CyNode> nodeViewD =  networkView.getNodeView(delnode) ;
			stats=new Stats(nx);
			Double avx = stats.average();
		
			//System.out.println(nx + "mean = "+ avx);
			nodeViewD.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, avx);
			stats=new Stats(ny);
			Double avy = stats.average();
			//System.out.println("check8");

			nodeViewD.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, avy);
			//System.out.println(ny + "mean = "+ avy);

			
		}
		}
		//System.out.println("check9");

		CyNetwork mynetwork2 = manager.getCurrentNetwork();
		ArrayList<Double> allEdgel = new  ArrayList<Double>();
		for (CyNode dnode : mynetwork2.getNodeList()){ 
			//System.out.println(dnode);
			//String mynamednode = mynetwork.getRow(dnode).get("name", String.class);
			//System.out.println(mynamednode);
			View<CyNode> nodeViewDist =  networkView.getNodeView(dnode);
			Double xnDist = nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			Double ynDist = nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			List<CyNode> neighborsDist = mynetwork2.getNeighborList(dnode, CyEdge.Type.ANY);
			//System.out.println(neighborsDist);
			ArrayList<Double> edgel = new  ArrayList<Double>();
			
			for (CyNode neighnodeDist : neighborsDist){
				
				View<CyNode> nodeViewDistneig =  networkView.getNodeView(neighnodeDist) ;
				Double xneDist = nodeViewDistneig.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				Double yneDist = nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				Double edist = dMet.edist(xnDist, ynDist, xneDist, yneDist);
				//Double edist = Math.sqrt(Math.pow(xnDist-xneDist,2)+Math.pow(ynDist-yneDist,2));
				edgel.add(edist);
				allEdgel.add(edist);
			}
			
			//System.out.println("edges lengths: "+edgel);
			if (edgel.isEmpty()){}
			else{int minIndex = edgel.indexOf(Collections.min(edgel));
			Double smedge = edgel.get(minIndex);
			//System.out.println("smallest edge: "+smedge);
			Double tresh =nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_SIZE)*0.6;
			if (smedge<tresh){
				
			ArrayList<Double>  listr = new ArrayList<Double>();
			Double Max = 3.0;
			Double Min = 1.0;
			Double random = Min + (int)(Math.random() * ((Max - Min) + 1))*1.5*nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
			listr.add(random);
			Double Max2 = -1.0;
			Double Min2 = -3.0;
			Double random2 = Min2 + (int)(Math.random() * ((Max2 - Min2) + 1))*1.5*nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
			listr.add(random2);
			
			Random r = new Random();
			Double rr = listr.get(r.nextInt(listr.size()));
			
			nodeViewDist.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, xnDist+rr);
			//System.out.println("old location "+xnDist +"deplacement" + rr);
			nodeViewDist.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, ynDist+rr);
			//System.out.println("old location "+ynDist +"deplacement" + rr);
			}
			
			
			
			
			}
		}
		//System.out.println("check8");

		stats = new Stats(allEdgel);
		Double disAv = stats.average();
		for (CyNode dnode : mynetwork2.getNodeList()){ 
			View<CyNode> nodeViewDist =  networkView.getNodeView(dnode);
			Double xnDist = nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			Double ynDist = nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			List<CyNode> neighborsDist = mynetwork2.getNeighborList(dnode, CyEdge.Type.ANY);
			//System.out.println(neighborsDist);
			ArrayList<Double> edgel = new  ArrayList<Double>();
			
			for (CyNode neighnodeDist : neighborsDist){
				
				View<CyNode> nodeViewDistneig =  networkView.getNodeView(neighnodeDist) ;
				Double xneDist = nodeViewDistneig.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				Double yneDist = nodeViewDist.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				Double edist =dMet.edist(xnDist, ynDist, xneDist, yneDist);
				//Double edist = Math.sqrt(Math.pow(xnDist-xneDist,2)+Math.pow(ynDist-yneDist,2));
				edgel.add(edist);
				
			}
			
			//System.out.println("edges lengths: "+edgel);
			/*if (edgel.isEmpty()){}else{
				int maxIndex = edgel.indexOf(Collections.max(edgel));
				Double lngedge = edgel.get(maxIndex);
				if (lngedge > 5*disAv){
					nodeViewDist.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, xnDist);
					//System.out.println("old location "+xnDist +"deplacement" + rr);
					nodeViewDist.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, ynDist);
				}
				}*/
		}
			
		
			
			
		
		
		
		//networkView.setVisualProperty(BasicVisualLexicon.NETWORK_WIDTH,1000.0 );
		//double scaleFactor = 130;
		//networkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION, 0.0);
		//networkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION, 0.0);

		//networkView.setVisualProperty(BasicVisualLexicon.EDGE_BEND,EdgeBendVisualProperty.DEFAULT_EDGE_BEND);
//miraculous!!!!!!!!!!
		//System.out.println("check9");
		networkView.updateView();
		networkView.fitContent();
		
		
		dMet.makeMap(manager);
		pcaMap = dMet.netMap;
		networkView.updateView();
		networkView.fitContent();
		//applyStartMap();
		//dMet.applyXYMap(startMap, mynetwork,  manager);
		UndoSupport undo = adapter.getUndoSupport();
		undoDDL= new UndoDDL();
		undo.postEdit(undoDDL);

		
		/*for (Map.Entry<String, List<Double>> entry : startMap.entrySet()) {
			String key = entry.getKey();
			List<Double> values =entry.getValue();
			System.out.println("KEY" +key);
		
			System.out.println("Values " + values.get(0) + " " + values.get(1));
		
			}*/
	
		 long end = System.currentTimeMillis( );
         long diff = end - start;
         System.out.println("Difference is : " + diff);
	
}
}
