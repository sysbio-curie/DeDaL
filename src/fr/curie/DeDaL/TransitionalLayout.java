package fr.curie.DeDaL;


import java.awt.Dimension;

import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.lang.Object;
import java.awt.Component;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.write.* ;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.EdgeBendVisualProperty;
import org.cytoscape.view.presentation.property.values.Bend;
import org.cytoscape.view.presentation.property.values.LineType;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.undo.AbstractCyEdit;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;

import java.text.DecimalFormat;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class TransitionalLayout extends AbstractCyAction {
	//public static  Double p;
	private static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	public CyAppAdapter adapter;
private Map<String,List<Double>> currNetMap;
private Map<String,List<Double>> netMap2;
private String currNetName = null;
private static TransitionalLayout instance;
private UndoSlider undoSlider;
public SliderPCA slider;
private DedalMethods dMet = new DedalMethods();
	public TransitionalLayout(CySwingAppAdapter adapter){
		// Add a menu item
		super("Layout morphing",
				adapter.getCyApplicationManager(),
				"network",
				adapter.getCyNetworkViewManager());
		this.adapter = adapter;
		setPreferredMenu("Layout.DeDaL");
instance=this;
//System.out.println("transitionalL");

	}
	
	public static TransitionalLayout getInstance(){return instance;}


	

	public void actionPerformed(ActionEvent g) {
		
		final CyApplicationManager manager = adapter.getCyApplicationManager();
		
	 CyNetworkManager netmanager = adapter.getCyNetworkManager();
		
		
		dMet.setCenter(manager);
		dMet.makeMap(manager);
		CyNetworkView currNetworkView = dMet.networkView;		
		CyNetwork currNetwork =  dMet.network;
		dMet.copyNetwork(adapter, currNetwork);
		//System.out.println("curr net Map " + currNetwork);
		currNetMap=dMet.netMap;
		currNetName=dMet.nameNetwork;
		
		
		
		
		//final CyNetworkReader reader = readermanager.getReader(arg0, arg1)
	

		//System.out.println("LIST 1 " );
	dMet.getNetworkList(adapter);
	
	
	ArrayList<String> choice = new ArrayList<String>();
	int l=0;
	ArrayList<CyNetwork> allNets =  dMet.netlist;
	//System.out.println("currNetName" + currNetName);
	for (String net : dMet.netNames){
		//System.out.println("net" + net);
		
		if (!(net.equals(currNetName))){
		
		choice.add(net);
		
		l++;
	}}
		
	 
	    //String input = (String) JOptionPane.showInputDialog(null, "Second network...","Choose the second network", JOptionPane.QUESTION_MESSAGE, null, choice, choice[0]); 
	   // System.out.println(input);	
	//System.out.println("CHOICE" + choice);
		
	PreSliderDialog d = new PreSliderDialog(new JFrame(),"Select second network",true);
	d.setDialogData(choice);
	d.setVisible(true);
	String sel = d.mysel;
	//System.out.println("s.mysel : " + d.mysel);
	
	if (d.mysel==null){currNetworkView.fitContent();}else{
	for (CyNetwork net: dMet.netlist){
			String name =net.getRow(net).get("name", String.class);
			if(name.equals(d.mysel)){manager.setCurrentNetwork(net);}
			//System.out.println("selecetd network " + net);
		}
	
	CyNetworkView currNetwork2View = manager.getCurrentNetworkView();
		currNetwork2View.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION, 0.0);
		currNetwork2View.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION, 0.0);
	if (d.alignButton.isSelected()){
		
		dMet.AllRotRef(manager,currNetMap);
		Map<String,List<Double>> alignMap=dMet.alignMap;
		//manager.setCurrentNetwork(currNetwork);
		netMap2=alignMap;
	}else{
		
		CyNetwork network2 = manager.getCurrentNetwork();
		List<CyNode> nodes2 = network2.getNodeList();
		CyNetworkView networkView2 = manager.getCurrentNetworkView();
		dMet.setCenter(manager);
		dMet.makeMap(manager);
		netMap2 = dMet.netMap;
		//Map<String, List<Double>> adPcaNetMap = new HashMap<String, List<Double>>();
	
	networkView2.fitContent();
		
		
	
	
	}

	//System.out.println("LIST2 " );
	
	dMet.getNetworkList(adapter);
	ArrayList<CyNetwork> newList= dMet.netlist;
newList.removeAll(allNets);
//System.out.println("NEW LIST" + newList);
for (CyNetwork cyNet: newList){
	manager.setCurrentNetwork(cyNet);
	//System.out.println(cyNet);
}
	 //manager.reset();
	//dMet.getNetworkList(adapter);
	/*for (CyNetwork newNet: dMet.netlist){
		System.out.println(" newNet" + newNet);
	for (CyNetwork net : allNets){
		System.out.println(" net" + net);
		if (newNet==net){}else if (newNet.equals(net)){}else{
			manager.setCurrentNetwork(newNet);
			System.out.println(" curr" + newNet);
		}
	}
	}*/
	
		//manager.setCurrentNetwork(currNetwork);
		
		CyNetwork network = manager.getCurrentNetwork();
	
		network.getRow(network).set(CyNetwork.NAME, "Morphed Network Layout");
		
		//System.out.println("current network doLayout" +  currNetwork);
		UndoSupport undo = adapter.getUndoSupport();
		undoSlider= new UndoSlider();
		undo.postEdit(undoSlider);
		//slider\
		doMyLayout (0.50);
		
		
		slider= new SliderPCA();
        slider.createAndShowGUI();
       
	
    	//
    	
		
		currNetworkView.updateView();
		
		currNetworkView.fitContent();
		
	}
	
	}
	
//public int getSliderValue(){return slider.getValue();}
//public void setSliderValue (int value){slider.setValue(value);}
public void doMyLayout (double pourcentage){
undoSlider.setPourcentage(pourcentage);
	//System.out.println("LIST in do Layout " );
	//dMet.getNetworkList(adapter);
	final CyApplicationManager manager = adapter.getCyApplicationManager();

	final CyNetworkManager netmanager = adapter.getCyNetworkManager();
	
	CyNetworkView currNetworkView = manager.getCurrentNetworkView();
	//System.out.println(" do layout p " + pourcentage);

	CyNetwork currNetwork = manager.getCurrentNetwork();
	
	
	//CyTable myreftable = myrefnetwork.getDefaultNodeTable();
	List<CyNode> currNodes = currNetwork.getNodeList();

	   
			for (CyNode pcaNode:currNodes){
				
				String pcaName = currNetwork.getRow(pcaNode).get("name", String.class);
				//System.out.println("name  " + pcaName);
			for (Map.Entry<String, List<Double>> pcaEntry : netMap2.entrySet()) {
				
				String pcaKey = pcaEntry.getKey();
				
				
				// to be given by user p%
				
				for (Map.Entry<String, List<Double>> currEntry : currNetMap.entrySet()) {
					String currKey = currEntry.getKey();
					//System.out.println("pcaKey " + pcaKey);
					//System.out.println("currKey " + currKey);
					if ((pcaKey.equals(currKey)) &&  (pcaKey.equals(pcaName))) {
						//System.out.println("pcaKey " + pcaKey);
						//System.out.println("currKey " + currKey);
						List<Double> pcaValues = pcaEntry.getValue();
						Double xpca = pcaValues.get(0);
						Double ypca = pcaValues.get(1);
						List<Double> currValues = currEntry.getValue();
						Double xcurr = currValues.get(0);
						Double ycurr = currValues.get(1);
						dMet.computePoint (xpca, ypca, xcurr, ycurr, pourcentage);
						
						
						Double xNew=dMet.xNew;
						Double yNew=dMet.yNew;
					
						
						
						View<CyNode> pcaNodeView=  currNetworkView.getNodeView(pcaNode) ;	
						
						
							
							pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, xNew);
							pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, yNew);
							
							
							
						
					}
				}
			}
			
			}
			currNetworkView.updateView();
			currNetworkView.fitContent();
			//AbstractCyEdit myCyEdit = new UndoSlider();
			
			
			//undo.postEdit(myCyEdit);
}
	
}





