package fr.curie.DeDaL;


import java.util.List;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.undo.AbstractCyEdit;
import org.cytoscape.work.undo.UndoSupport;


public class UndoDDL extends AbstractCyEdit {



	private DedalMethods dMet = new DedalMethods ();
	private Map<String,List<Double>> startMap = MenuAction.getInstance().startMap;
	private Map<String,List<Double>> pcaMap = MenuAction.getInstance().pcaMap;
	private CyNetwork mynetwork = MenuAction.getInstance().mynetwork;
	private CyApplicationManager manager = MenuAction.getInstance().manager;
	
	public UndoDDL() {
		super("Data Driven Layout");


		
	}
	
	public void redo() {
	
		dMet.applyXYMap(pcaMap, mynetwork,  manager);	
	//MenuAction.getInstance().applyMyMap();
		
	}
	public void undo() {
		//System.out.println(mynetwork);
		
		dMet.applyXYMap(startMap, mynetwork,  manager);
	}
} 