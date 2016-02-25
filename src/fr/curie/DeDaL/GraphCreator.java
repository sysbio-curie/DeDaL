package fr.curie.DeDaL;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.StringTokenizer;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

public class GraphCreator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Graph CreateGraphFromCyNetwork(CyNetwork network){
		Graph graph = new Graph();
		for(CyNode node : network.getNodeList()){
			String nodename = getNodeName(node, network);
			graph.getCreateNode(nodename);
		}
		for(CyEdge edge : network.getEdgeList()){
			CyNode source = edge.getSource();
			CyNode target = edge.getTarget();
			String source_name = getNodeName(source, network);
			String target_name = getNodeName(target, network);
			Edge e = new Edge();
			e.Id = source_name+"_"+target_name;
			e.Node1 = graph.getNode(source_name);
			e.Node2 = graph.getNode(target_name);
			graph.addEdge(e);
		}
		return graph;
	}
	
	public static String getNodeName(CyNode node, CyNetwork network){
		String name = "";
		CyRow row = network.getRow(node);		
		Map<String,Object> values = row.getAllValues();
		name = network.getRow(node).get("name", String.class);
		return name;
	}
	
	public static Graph loadFromSif(String fn){
		Graph graph = new Graph();
		int k=0;
		try{
			
			LineNumberReader lr = new LineNumberReader(new FileReader(fn));
			String s = null;
			while((s=lr.readLine())!=null){
				k++;
				try{
				StringTokenizer st = new StringTokenizer(s,"\t");
				String interactor1 = st.nextToken();
				if(st.hasMoreTokens()){
				String interactionType = st.nextToken();
				String interactor2 = st.nextToken();
				Node n1 = graph.getCreateNode(interactor1);
				Node n2 = graph.getCreateNode(interactor2);
				Edge e = graph.getCreateEdge(n1.Id+"_"+n2.Id);
				e.Node1 = n1;
				e.Node2 = n2;
				e.setAttributeValueUnique("InteractionType", interactionType, Attribute.ATTRIBUTE_TYPE_STRING);
				}
				}catch(Exception e1){
					System.out.println("ERROR in loading SIF: line "+k+">"+s);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return graph;
	}

	

}
