package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafoNuovo;
	
	public Model() {
		
		dao = new ExtFlightDelaysDAO();
		idMap = new HashMap<Integer,Airport>();
		
	}
	
	public void creaGrafo (double distanza) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		dao.loadAllAirports(idMap);
		
		
		for(Adiacenza a : dao.getAdiacenze(idMap)) {
			
			if(grafo.containsVertex(a.getA1()) && grafo.containsVertex(a.getA2())) {
				
				DefaultWeightedEdge e = this.grafo.getEdge(a.getA1(), a.getA2());
				
				if(e==null) {
					Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
				} else {
					double pesoVecchio = this.grafo.getEdgeWeight(e);
					double pesoNuovo = (pesoVecchio + a.getPeso())/2;
					
					this.grafo.setEdgeWeight(e, pesoNuovo);
					
				}
				
			} else {
				
				if(!grafo.containsVertex(a.getA1())) {
					grafo.addVertex(a.getA1());
				}
				
				if(!grafo.containsVertex(a.getA2())) {
					grafo.addVertex(a.getA2());
				}
				
				Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
			}
			
		}
		
		grafoNuovo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			
			if(grafo.getEdgeWeight(e) >= distanza) {
				
				if(!grafoNuovo.vertexSet().contains(grafo.getEdgeSource(e))) {
					grafoNuovo.addVertex(grafo.getEdgeSource(e));
				}
				
				if(!grafoNuovo.vertexSet().contains(grafo.getEdgeTarget(e))) {
					grafoNuovo.addVertex(grafo.getEdgeTarget(e));
				}
				
				if(!grafoNuovo.edgeSet().contains(e)) {
					Graphs.addEdge(grafoNuovo, grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e));
				}
				
			}
			
		}
		
	}
	
	public String getInfoGrafo() {
		
		return "Grafo creato:\n"+" #VERTICI: "+grafoNuovo.vertexSet().size()+"\n #ARCHI: "+grafoNuovo.edgeSet().size()+"\n";
	}
	
	public String getArchiPeso() {
		
		String result = "";
		
		for(DefaultWeightedEdge e : this.grafoNuovo.edgeSet()) {
			
			result+="["+grafoNuovo.getEdgeSource(e).getAirportName()+" - "+grafoNuovo.getEdgeTarget(e).getAirportName()+" - WEIGHT: "+grafoNuovo.getEdgeWeight(e)+"\n";
			
		}
		
		return result;
	}
	
}
