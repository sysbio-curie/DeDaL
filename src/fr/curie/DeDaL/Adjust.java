package fr.curie.DeDaL;

import java.awt.event.ActionEvent;
import java.lang.Object;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.EdgeBendVisualProperty;
import org.cytoscape.view.presentation.property.values.Bend;
import org.cytoscape.view.presentation.property.values.LineType;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;

import java.text.DecimalFormat;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Adjust extends AbstractCyAction {
	public CyAppAdapter adapter;
	private DedalMethods dMet = new DedalMethods();
	private CyApplicationManager manager;
	private UndoAlign undoAlign;
	public CyNetworkManager netmanager;
	private static Adjust instance;
	private Map<String, List<Double>> refNetMap;
	public ArrayList<CyNetwork> adnetList;
	public List<HashMap<String, List<Double>>> listMap;
	private 	Double scale1;
	public Adjust(CySwingAppAdapter adapter){
		// Add a menu item
		super("Layout aligning",
				adapter.getCyApplicationManager(),
				"network",
				adapter.getCyNetworkViewManager());
		this.adapter = adapter;
		setPreferredMenu("Layout.DeDaL");
		instance=this;
	}
	public static Adjust getInstance(){return instance;}

	public void actionPerformed(ActionEvent f) {

		manager = adapter.getCyApplicationManager();

		dMet.getNetworkList(adapter);

		//getting the reference network from user
		SelectNetworksDialog d = new SelectNetworksDialog(new JFrame(),"Options",true);
		d.setDialogData(dMet.netNames);
		d.setDialogData2(dMet.netNames);
		d.setVisible(true);
		//System.out.println("d.myselNet" + d.myselNet);
		if (d.myselList.size()!=1){
			
		}
		else{
			String mycurrnet = null;
			for (CyNetwork net:dMet.netlist){
				String name = net.getRow(net).get("name", String.class);
				System.out.println("sel network" +name );
				System.out.println("sel List" +d.myselList );
				for (String refnetSel : d.myselList){
					if (name.equals(refnetSel))

					{

						System.out.println("match ref net" + net);
						mycurrnet = name;
						manager.setCurrentNetwork(net);

					}

				}}


			CyNetworkView refnetworkView = manager.getCurrentNetworkView();
			Double z = refnetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Z_LOCATION);
			//System.out.println("z " + z);
			Double width = refnetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH);
			Double height = refnetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT);
			Double size = refnetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_SIZE);
			//System.out.println("width  " + width + " height "+ height + " size " + size);
			//refnetworkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION, 0.0);
			//refnetworkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION, 0.0);
			//refnetworkView.fitContent();
			//Double x = refnetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION);
			//Double y = refnetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION);
			dMet.setCenter(manager);
			
		
			dMet.makeMap(manager);
			refNetMap = dMet.netMap;
			refnetworkView.fitContent();


			//get the networks selected by user
			adnetList = new ArrayList<CyNetwork>();
			for (CyNetwork net:dMet.netlist){
				String name =net.getRow(net).get("name", String.class);
				//System.out.println("d.myselNet" + d.myselNet);
				//System.out.println("name"+name);
				for (String selNetItem:d.myselNet){
					if ((name.equals(selNetItem))&&(!(name.equals(mycurrnet))))


					{
						//System.out.println("name match "+name);
						adnetList.add(net);
					}

				}
			}
			if (adnetList.size()==0){
				
			}
			else{//adjust network after network to the ref network
				//
				//System.out.println("network  "+a);
				getStart();
				
				doAlign();


				//applyStart();

				


			}
			
		}
		UndoSupport undo = adapter.getUndoSupport();
		undoAlign = new UndoAlign();
		undo.postEdit(undoAlign);
	}

	public void doAlign(){
		for (CyNetwork a : adnetList){
			manager.setCurrentNetwork(a);
			dMet.setCenter(manager);

			dMet.AllRotRef(manager, refNetMap);
			Map<String,List<Double>> mapXY = dMet.alignMap;

			dMet.applyXYMap (mapXY,  a,  manager );
		}}
	public void getStart(){
		listMap = new ArrayList<HashMap<String, List<Double>>>();
		for (CyNetwork a : adnetList){
			//System.out.println("network  "+a);
			manager.setCurrentNetwork(a);
			dMet.makeMap(manager);
			HashMap<String, List<Double>> currMap= dMet.netMap;
			listMap.add(currMap);

			//dMet.applyXYMap (mapXY,  a,  manager );
		}
	}	
	public void applyStart(){
		int n=0;
		for (CyNetwork a : adnetList){
			System.out.println("n "+n);
			System.out.println("network  "+a);
			manager.setCurrentNetwork(a);
			HashMap<String, List<Double>> xyMap=listMap.get(n);
			dMet.applyXYMap(xyMap, a, manager);
			n++;
		}
		System.out.println("END");
		
	}
}


