package fr.curie.DeDaL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.create.CloneNetworkTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;


public  class DedalMethods {
	public ArrayList<CyNetwork> netlist;
	public ArrayList<String> netNames;
	public HashMap<String,List<Double>> netMap;
	public Map<Double, List<Integer>> minDistMap;
	public CyNetworkView networkView;
	public CyNetwork network;
	public CyNetworkView network2View;

	public String nameNetwork;
	public String alignName;
	public View<CyNode> nodeView;
	public HashMap<String,List<Double>> alignMap;
	public Double xNew;
	public Double yNew;
	private Stats stats;
	//CyNetwork mynetwork = manager.getCurrentNetwork();
	//CyTable mytable = mynetwork.getDefaultNodeTable();

	//getting the list of networks in the open session

	public void getNetworkList(CyAppAdapter adapter){

		netlist	= new ArrayList<CyNetwork>();
		netNames = new  ArrayList<String>();
		CyNetworkManager netmanager = adapter.getCyNetworkManager();	
		Set<CyNetwork> setnet = netmanager.getNetworkSet();
		//System.out.println("NETWORKS :"+setnet);
		//creating list of networks out of the set and list of networks' names


		for (CyNetwork net:setnet){
			//System.out.println(net);
			String name =net.getRow(net).get("name", String.class);
			if (!(name.equals("null"))){

				netlist.add(net);
				//System.out.println("sel net" + name);
				netNames.add(name);
			}
		}

	}

	public void AllRotRef(CyApplicationManager manager, Map<String,List<Double>> refNetMap){

		network2View = manager.getCurrentNetworkView();
		network = manager.getCurrentNetwork();
		List<CyNode> Nodes2 = network.getNodeList();
		//network2View.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION, 0.0);
		//network2View.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION, 0.0);
		network2View.fitContent();
		Double x = network2View.getVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION);
		Double y = network2View.getVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION);
		//System.out.println("center x" + x + "center y " + y);
		//Double z = network2View.getVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Z_LOCATION);
		//System.out.println("z " + z);
		//Double width = network2View.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH);
		//Double height = network2View.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT);
		//Double size = network2View.getVisualProperty(BasicVisualLexicon.NETWORK_SIZE);
		//System.out.println("width  " + width + " height "+ height + " size " + size);
		//network2View.setVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR, scale1 );
		//System.out.println("scale2= "+ scale);

		Double start = Double.POSITIVE_INFINITY;
		minDistMap = new HashMap<Double, List<Integer>>();
		List<Integer> startlist =new ArrayList<Integer>();
		startlist.add(1);
		startlist.add(1);
		minDistMap.put(start,startlist);

		//rotation
		for (int r=0;r<360; r++){

			//System.out.println("R" +r);
			//int r=0;
			Double eudistSum1 =0.0;	
			Double eudistSum2 = 0.0;
			Double eudistSum3 = 0.0;
			Double eudistSum4 = 0.0;



			for (CyNode pcaNode:Nodes2){

				//System.out.println("check1 " );
				String pcaName =network.getRow(pcaNode).get("name", String.class);


				View<CyNode> node2View= network2View.getNodeView(pcaNode) ;



				for (Map.Entry<String, List<Double>> entry : refNetMap.entrySet()) {
					String key = entry.getKey();

					if (key.equals(pcaName)){
						//System.out.println("KEY" +key);
						//System.out.println("name" + pcaName);
						List<Double> values = entry.getValue();
						Double xref = values.get(0);
						Double yref = values.get(1);
						//System.out.println("xref " + xref + " yref "+yref);
						View<CyNode> pcaNodeView=  network2View.getNodeView(pcaNode) ;

						Double xad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
						Double yad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
						//System.out.println("x " + xad + " y "+yad);

						// convert them to radians
						Double rRad = Math.toRadians(r);
						//m0 - no reflection
						Double rx = xad*Math.cos(rRad)-yad*Math.sin(rRad);
						Double ry = xad*Math.sin(rRad)+yad*Math.cos(rRad);	
						//System.out.println("noreflection "+ "x " + rx + " y "+ry);

						Double eudist1 =edist(xref,yref,rx,ry);
						//Double eudist1 = Math.sqrt(Math.pow(xref-rx,2)+Math.pow(yref-ry,2));
						//System.out.println("eudistnode" + eudist1);
						eudistSum1 = eudistSum1+eudist1;

						//mx - reflexion x
						Double rxmx = rx;
						Double rymx = -ry;
						Double eudist2 =edist(xref,yref,rxmx,rymx);
						//Double eudist2 = Math.sqrt(Math.pow(xref-rxmx,2)+Math.pow(yref-rymx,2));
						eudistSum2 = eudistSum2+eudist2;
						//System.out.println(" x reflection "+ "x " + rxmx + " y "+rymx);
						//mx - reflexion y
						Double rxmy = -rx;
						Double rymy = ry;
						Double eudist3 =edist(xref,yref,rxmy,rymy);
						//Double eudist3 = Math.sqrt(Math.pow(xref-rxmy,2)+Math.pow(yref-rymy,2));
						eudistSum3 = eudistSum3+eudist3;
						//System.out.println(" y reflection "+ "x " + rxmy + " y "+rymy);
						//mxy - center symmetry
						Double rxmxy = -rx;
						Double rymxy = -ry;
						Double eudist4 =edist(xref,yref,rxmxy,rymxy);
						//Double eudist4 = Math.sqrt(Math.pow(xref-rxmxy,2)+Math.pow(yref-rymxy,2));
						//System.out.println(" xy reflection "+ "x " + rxmxy + " y "+rymxy);
						eudistSum4 = eudistSum4+eudist4;
						//System.out.println("SUM1" +eudistSum1);
						//System.out.println("SUM2" +eudistSum2);
						//System.out.println("SUM3" +eudistSum3);
						//System.out.println("SUM4" +eudistSum4);




					}

				}//System.out.println("check2");
			}

			//System.out.println("SUM1" +eudistSum1);
			//System.out.println("R" +r);
			//System.out.println("SET" +minDistMap.keySet());

			for (Map.Entry<Double, List<Integer>> entry : minDistMap.entrySet()) {
				//System.out.println("check3");
				Double key = entry.getKey();
				//System.out.println("KEY" +key);
				//System.out.println("SUM1" +eudistSum1);
				//System.out.println("SUM2" +eudistSum2);
				//System.out.println("SUM3" +eudistSum3);
				//System.out.println("SUM4" +eudistSum4);
				if (eudistSum1<key){minDistMap.remove(key);
				List<Integer> list1 =new ArrayList<Integer>();
				list1.add(1);
				list1.add(r);
				minDistMap.put(eudistSum1,list1);
				//System.out.println("ENTRY" +minDistMap.entrySet());
				}}
			for (Map.Entry<Double, List<Integer>> entry : minDistMap.entrySet()) {
				Double key = entry.getKey();
				if (eudistSum2<key){minDistMap.remove(key);
				List<Integer> list2 =new ArrayList<Integer>();
				list2.add(2);
				list2.add(r);
				minDistMap.put(eudistSum2,list2);
				//System.out.println("ENTRY" +minDistMap.entrySet());
				}}
			for (Map.Entry<Double, List<Integer>> entry : minDistMap.entrySet()) {
				Double key = entry.getKey();
				if (eudistSum3<key){minDistMap.remove(key);
				List<Integer> list3 =new ArrayList<Integer>();
				list3.add(3);
				list3.add(r);
				minDistMap.put(eudistSum3,list3);
				//System.out.println("ENTRY" +minDistMap.entrySet());
				}}
			for (Map.Entry<Double, List<Integer>> entry : minDistMap.entrySet()) {
				Double key = entry.getKey();
				if (eudistSum4<key){minDistMap.remove(key);

				List<Integer> list4 =new ArrayList<Integer>();

				list4.add(4);
				list4.add(r);
				//System.out.println("LIST4"+list4);

				minDistMap.put(eudistSum4,list4);
				//System.out.println("ENTRY" +minDistMap.entrySet());
				}}


		}
		//System.out.println("check4");
		alignMap=new HashMap<String,List<Double>>();
		for (Map.Entry<Double, List<Integer>> entry : minDistMap.entrySet()) {

			Double key = entry.getKey();
			List<Integer> values = entry.getValue();

			//values.get(0);
			if (values.get(0)==1){
				// convert them to radians
				Double rRad = Math.toRadians(values.get(1));
				for (CyNode pcaNode:Nodes2){
					View<CyNode> pcaNodeView=  network2View.getNodeView(pcaNode) ;
					alignName = network.getRow(pcaNode).get("name", String.class);
					List<Double> XY = new ArrayList<Double>();
					Double xad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
					Double yad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
					//m0 - no reflection
					//System.out.println("no ref at " + values.get(1)+ "degrees");
					Double rx = xad*Math.cos(rRad)-yad*Math.sin(rRad);
					Double ry = xad*Math.sin(rRad)+yad*Math.cos(rRad);
					XY.add(rx);
					XY.add(ry);
					alignMap.put(alignName, XY);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, rx);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, ry);
				}
			}
			//System.out.println("Key = " + key);
			//System.out.println("Values = " + values);
			if (values.get(0)==2){
				// convert them to radians
				Double rRad = Math.toRadians(values.get(1));
				for (CyNode pcaNode:Nodes2){
					View<CyNode> pcaNodeView=  network2View.getNodeView(pcaNode) ;
					alignName = network.getRow(pcaNode).get("name", String.class);
					List<Double> XY = new ArrayList<Double>();
					Double xad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
					Double yad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
					//rotation
					Double rx = xad*Math.cos(rRad)-yad*Math.sin(rRad);
					Double ry = xad*Math.sin(rRad)+yad*Math.cos(rRad);
					//mx - reflexion x
					//System.out.println("ref x " + values.get(1)+ "degrees");
					Double rxmx = rx;
					Double rymx = -ry;
					XY.add(rxmx);
					XY.add(rymx);
					alignMap.put(alignName, XY);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, rxmx);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, rymx);
				}
			}

			if (values.get(0)==3){
				// convert them to radians
				Double rRad = Math.toRadians(values.get(1));
				for (CyNode pcaNode:Nodes2){
					View<CyNode> pcaNodeView=  network2View.getNodeView(pcaNode) ;
					alignName = network.getRow(pcaNode).get("name", String.class);
					List<Double> XY = new ArrayList<Double>();
					Double xad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
					Double yad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
					//rotation
					Double rx = xad*Math.cos(rRad)-yad*Math.sin(rRad);
					Double ry = xad*Math.sin(rRad)+yad*Math.cos(rRad);
					//mx - reflexion y
					//System.out.println("ref y " + values.get(1)+ "degrees");
					Double rxmy = -rx;
					Double rymy = ry;
					XY.add(rxmy);
					XY.add(rymy);
					alignMap.put(alignName, XY);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, rxmy);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, rymy);
				}
			}
			if (values.get(0)==4){
				// convert them to radians
				Double rRad = Math.toRadians(values.get(1));
				for (CyNode pcaNode:Nodes2){
					View<CyNode> pcaNodeView=  network2View.getNodeView(pcaNode) ;
					alignName = network.getRow(pcaNode).get("name", String.class);
					List<Double> XY = new ArrayList<Double>();
					Double xad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
					Double yad = pcaNodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
					//rotation
					Double rx = xad*Math.cos(rRad)-yad*Math.sin(rRad);
					Double ry = xad*Math.sin(rRad)+yad*Math.cos(rRad);
					//mxy - center symmetry
					//System.out.println("center symmetry " + values.get(1)+ "degrees");
					Double rxmxy = -rx;
					Double rymxy = -ry;
					XY.add(rxmxy);
					XY.add(rymxy);
					alignMap.put(alignName, XY);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, rxmxy);
					//pcaNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, rymxy);
				}
			}
		}


		network2View.updateView();
		network2View.fitContent();
		//System.out.println("alloRot END");
	}

	public void applyXYMap(Map<String,List<Double>> xyMap, CyNetwork currNetwork, CyApplicationManager manager){

		CyNetworkView currNetworkView2 = manager.getCurrentNetworkView();

		List<CyNode> currNodes = currNetwork.getNodeList();
		for (CyNode node:currNodes){


			String nName=currNetwork.getRow(node).get("name", String.class);

			for (Map.Entry<String, List<Double>> entry : xyMap.entrySet()) {
				String key = entry.getKey();

				if (key.equals(nName)){
					List<Double> values = entry.getValue();
					View<CyNode> currNodeView=  currNetworkView2.getNodeView(node) ;

					currNodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, values.get(0));
					currNodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, values.get(1));


				}

			}
		}
		currNetworkView2.updateView();
		currNetworkView2.fitContent();
	}
	public void makeMap (CyApplicationManager manager){

		netMap=new HashMap<String,List<Double>>();
		networkView = manager.getCurrentNetworkView();
		network = manager.getCurrentNetwork();
		nameNetwork = network.getRow(network).get("name", String.class);
		//System.out.println("curr net 2 one" + network);
		List<CyNode> nodes = network.getNodeList();
		//networkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION, 0.0);
		//networkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION, 0.0);


		for (CyNode node: nodes ){
			String name = network.getRow(node).get("name", String.class);
			nodeView= networkView.getNodeView(node) ;
			List<Double> XY = new ArrayList<Double>();
			Double xref = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			Double yref = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);

			XY.add(xref);
			XY.add(yref);
			netMap.put(name, XY);
		}
	}

	public void setCenter(CyApplicationManager manager){

		network = manager.getCurrentNetwork();
		networkView = manager.getCurrentNetworkView();
		List<CyNode> nodes = network.getNodeList();
		ArrayList<Double> xList = new ArrayList<Double>();
		ArrayList<Double> yList = new ArrayList<Double>();
		for (CyNode node: nodes ){
			String name = network.getRow(node).get("name", String.class);
			nodeView= networkView.getNodeView(node) ;

			Double xref = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			Double yref = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);

			//System.out.println("xref " + xref + "yref" + yref);
			xList.add(xref);
			yList.add(yref);

		}
		stats=new Stats(xList);
		Double xAv  = stats.average();
		stats=new Stats(yList);
		Double yAv  = stats.average();
		networkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION, xAv);
		networkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION, yAv);
		//System.out.println("center" + xAv + " "+ yAv);
		for (CyNode node: nodes ){
			nodeView= networkView.getNodeView(node) ;
			Double xref = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			Double yref = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,xref-xAv);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,yref-yAv);
			Double nx =  xref-xAv;
			Double ny= yref-yAv;

		}
	}
	public void computePoint (Double xref, Double yref, Double xcurr, Double ycurr, Double pourcentage){
		Double edist = edist(xref,yref,xcurr,ycurr);

		Double edistSq = sqEdist(xref,yref,xcurr,ycurr);

		Double z = pourcentage*pourcentage*edistSq; //*

		Double z1 = (1-pourcentage)*(1-pourcentage)*edistSq; //*
		Double f = -(2*xref-2*xcurr);//*
		Double h = -(Math.pow(xcurr,2)-Math.pow(xref,2)+Math.pow(ycurr,2)-Math.pow(yref,2)-z+z1);//*
		Double e = 2*yref- 2*ycurr;//*

		Double a =  Math.pow(e,2)+Math.pow(f,2);//*
		Double b = -2*Math.pow(e,2)*xcurr-2*e*f*ycurr+2*f*h;
		Double c = Math.pow(e,2)*Math.pow(xcurr,2) + h*h-2*e*h*ycurr+Math.pow(e,2)*Math.pow(ycurr,2)-Math.pow(e,2)*z;

		Double delta = b*b - 4*a*c;

		xNew=-b/ (2*a);
		yNew= (f*xNew + h )/e;
	}
	double edist(Double x1, Double y1, Double x2, Double y2){
		Double edist = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
		return edist;
	}
	double sqEdist(Double x1, Double y1, Double x2, Double y2){
		Double sqEdist = Math.pow(x1-x2,2)+Math.pow(y1-y2,2);
		return sqEdist;
	}

	public void outliers(CyApplicationManager manager,double p){
		
		network = manager.getCurrentNetwork();
		networkView = manager.getCurrentNetworkView();
		List<CyNode> nodes = network.getNodeList();

		Map<CyNode, Double> parDisMap= new HashMap<CyNode, Double>();
		List<Double> allNodesList = new ArrayList<Double>();
		//List<Double> nodeList2 = new ArrayList<Double>();
		for (CyNode node1 : nodes){
			//System.out.println("node1 : " + node1);
			List<Double> nodeList = new ArrayList<Double>();
			for (CyNode node2 : nodes){
//System.out.println( "node2 : "+ node2);

				if (node1!=node2){
					nodeView= networkView.getNodeView(node1) ;
					Double x1 = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
					Double y1 = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
					nodeView= networkView.getNodeView(node2) ;
					Double x2 = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
					Double y2 = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
					Double eudist=edist(x1,y1,x2,y2);
					nodeList.add(eudist);

				}}
			//System.out.println("nodeList" + nodeList);
			stats=new Stats(nodeList);
			parDisMap.put(node1, stats.average());
			for (Double n : nodeList){
				allNodesList.add(n);

			}


		}
		
		//System.out.println("AllnodesList" + allNodesList);
stats=new Stats(allNodesList);

Double m =stats.average();
Double s = stats.std();
//System.out.println("STD : "+ s);

for (Map.Entry<CyNode,Double> entry : parDisMap.entrySet()) {
	CyNode key = entry.getKey();
	Double av =entry.getValue();
	//System.out.println("AVERAGE : "+ m);
	//System.out.print("key : " + key + "value : " + av);
	Double condition=m+p*s;
	//System.out.println("condition : "+ condition );
	if (av>condition){
		
		CyNode outlier = key;
		nodeView= networkView.getNodeView(outlier) ;
		Double xo = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
		Double yo = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
		Double edist = edist(0.00,0.00,xo,yo);
		Double pourcentage = (s*2)/edist;
		computePoint (0.00,0.00, xo,yo, pourcentage);
		nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,xNew);
		nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,yNew);
	}


}

	}
	public void copyNetwork(CyAppAdapter adapter, CyNetwork networkToC){
		
		 TaskManager taskManager= adapter.getTaskManager();
		 CloneNetworkTaskFactory cloneFactory=adapter.get_CloneNetworkTaskFactory();
		 TaskIterator it=cloneFactory.createTaskIterator(networkToC); 

		//  CyNetworkView 	CyNetworkReader.buildCyNetworkView(CyNetwork network) 
		 taskManager.execute(it);
		 //System.out.println("LIST in Clone " );
		 getNetworkList(adapter);
		//System.out.println("clone" );
			
		
	}
}