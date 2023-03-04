import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/* Sąlyga:
 * Duotas kelių sąrašas: miestas, miestas, kelio savininkas, kaina.
 * Rasti pigiausią kelionės iš vieno
 * duoto miesto į kitą duotą miestą maršrutą ir jo kainą
 * (kainą sudaro tik važiavimo keliu kaina,
 * be to, savininkas duoda 50% nuolaidą, jei atvažiuota jam priklausančiu keliu).
 * (grafo realizacija paremta kaimynystės sąrašais)
 */

public class Main {

	public static void main(String[] args) {
		
		
		String string = null;
		String delimSpace =" ";
		
		
		//Generate separate graphs for owners
		Set<String> ownerSet = new HashSet<>();
		Map<String, Graph> ownerMap = new HashMap<>();
		Graph ownerGraph = null;
		
		//General graph
		Graph graph = new Graph(); 
		
		//SETTING UP NODES
		File file = new File("C:\\Users\\gabrc\\eclipse-workspace\\ADS #4\\src\\data.txt");
		Scanner scanner;
		try {
			
			scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				
				string = scanner.nextLine();
				String[] stringArr = string.split(delimSpace);
				
				
				//OWNER VARIABLES
				//Check if owner exist
				if(CheckIfOwnerExist(ownerSet, stringArr[2])) {
					
					//Find graph in map that owner owns
					ownerGraph = findOwnerGraph(ownerMap, stringArr[2]);
					
				}
				else {
					//Add owner to set
					ownerSet.add(stringArr[2]);
					//Create new graph
					ownerGraph = new Graph();
					//Create new map entry
					ownerMap.put(stringArr[2], ownerGraph);
					
				}
				
				AddGraphNodes(graph,stringArr[0], stringArr[1], stringArr[3]);
				AddGraphNodes(ownerGraph, stringArr[0], stringArr[1], stringArr[3]);
			}
			scanner.close();
				
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
		
		String cityName1 = "Vilnius";
		String cityName2 = "Klaipeda";
		
		List<Node> CheapestGraphPath = ReturnCheapestPath(graph, cityName1, cityName2);
		if(CheapestGraphPath == null) {
			System.out.println("Road does not exist.");
			System.exit(0);
		}
		
		int CheapestGraphPrice = ReturnCheapestPrice(graph, cityName1, cityName2);

		int cheapestOwnerPrice = Integer.MAX_VALUE;
		String cheapestOwner = null;
		
		//Iterate through all ownerGraphs
		for( String ownerName : ownerSet) {
			
			//Find corresponding graph
			ownerGraph = findOwnerGraph(ownerMap, ownerName);
			
			//Check if path exist
			List<Node> cheapestPath = ReturnCheapestPath(ownerGraph, cityName1, cityName2);
			
			//If path exist find price
			if(cheapestPath != null) {
				
				//find cheapest price
				int price = ReturnCheapestPrice(ownerGraph, cityName1, cityName2) / 2;
				if( price < cheapestOwnerPrice) {
					cheapestOwnerPrice = price;
					cheapestOwner = ownerName;
				}
			}
				
		}
		
		//Compare with cheapest Owner price with regular graph
		if( cheapestOwnerPrice < CheapestGraphPrice) {
			//Return owner path
			Graph bestGraph = findOwnerGraph(ownerMap, cheapestOwner);
			System.out.println("Cheapest path is owned by one owner: " + cheapestOwner);
			System.out.print("Cheapest path: ");
			PrintPath(ReturnCheapestPath(bestGraph,cityName1, cityName2), cityName2);
			//Return owner price
			System.out.println("Cheapest path price: " + cheapestOwnerPrice);
		}
		else {
			System.out.println("Cheapest path has multiple owners.");
			System.out.print("Cheapest path: ");
			PrintPath(ReturnCheapestPath(graph, cityName1, cityName2), cityName2);
			System.out.println("Cheapest path price: " +ReturnCheapestPrice(graph, cityName1, cityName2));
		}
	
	}

	public static boolean CheckIfGraphContainsString(Set <Node> set, String string) {
		for( Node node : set) {
			if(node.getCity().equals(string)) {
				return true;
			}
		}
		return false;	
	}
	
	public static Node ReturnNodeWithString(Set <Node> set, String string) {
		
		for(Node node : set) {
			if(node.getCity().equals(string)) {
				return node;
			}
		}
		return null;
	}
	
	//Dijkstra's algorithm
	public static Graph calculateCheapestPathFromSource(Graph graph, Node source) {
		
		source.setTotalPrice(0);
		
		Set<Node> settledNodes = new HashSet<>();
		Set<Node> unsettledNodes = new HashSet<>();
		
		unsettledNodes.add(source);
		
		//continue while there's unsettled nodes
		while( unsettledNodes.size() != 0) { 
			
			//Finding the lowest distance unsettled node
			Node currentNode = getLowestPriceNode(unsettledNodes);
			//Removing from unsettled list because it will be checked
			unsettledNodes.remove(currentNode);
			
			//Iterating through all adjacent nodes
			for( Entry <Node, Integer> adjacencyPair:
				currentNode.GetAdjacentNodes().entrySet())
			{
				
				Node adjacentNode = adjacencyPair.getKey();
				Integer edgeWeight = adjacencyPair.getValue();
				
				if(!settledNodes.contains(adjacentNode)) {
					CalculateMinimumPrice(adjacentNode, edgeWeight, currentNode);
					unsettledNodes.add(adjacentNode);
				}
			}
			
			settledNodes.add(currentNode);	
		}
		return graph;
	}
	
	public static Node getLowestPriceNode(Set<Node> unsettledNodes) {
		Node lowestTotalPriceNode = null;
		int lowestTotalPrice = Integer.MAX_VALUE;

		for(Node node: unsettledNodes) {
			
			int nodeTotalPrice = node.getTotalPrice();
			if( nodeTotalPrice < lowestTotalPrice) {
				
				lowestTotalPrice = nodeTotalPrice;
				lowestTotalPriceNode = node;
			}
			
		}
		
		return lowestTotalPriceNode;
	}
		
	public static void CalculateMinimumPrice(Node evaluationNode, Integer edgeWeight, Node sourceNode) {
		Integer sourceTotalPrice = sourceNode.getTotalPrice();
		 //if new minimum price is smaller
		if( sourceTotalPrice + edgeWeight < evaluationNode.getTotalPrice())
		{
			evaluationNode.setTotalPrice(sourceTotalPrice + edgeWeight);
			LinkedList<Node> cheapestPath = new LinkedList<>(sourceNode.getCheapestPath());
			cheapestPath.add(sourceNode);
			evaluationNode.setCheapestPath(cheapestPath);	
		}
	}


	public static Boolean CheckIfOwnerExist(Set <String> ownerSet, String ownerName) {
		for(String string : ownerSet)
		{
			if(string.equals(ownerName)) 
			{
				return true;
			}
		}
		return false;
	}
	
	public static Graph findOwnerGraph(Map <String, Graph> ownerMap, String ownerName) {
		
		Graph ownerGraph;
		for(Map.Entry<String, Graph> entry : ownerMap.entrySet()) {
			
			if(entry.getKey().equals(ownerName)) {
				ownerGraph = entry.getValue();
				return ownerGraph;
			}
		}
		return null;
	}
	
	public static void AddGraphNodes(Graph graph, String city1, String city2, String price) {
		
		Node node1 = null, node2 = null;
		
		//Check first city
		//Check if graph contains string
		if(CheckIfGraphContainsString(graph.GetNodes(), city1)) {
			node1 = ReturnNodeWithString(graph.GetNodes(),city1);
		}
		else {
			//Create new node
			node1 = new Node(city1);
			graph.AddNode(node1);
		}
		
		//Check second city
		if(CheckIfGraphContainsString(graph.GetNodes(), city2)) {
			//Then node already exists
			node2 = ReturnNodeWithString(graph.GetNodes(), city2);
		}
		else {
			node2 = new Node(city2);
			graph.AddNode(node2);
		}
		
		//Add relationship to nodes for both
		int lineWeight = Integer.parseInt(price);
		node1.addDestination(node2, lineWeight);
		node2.addDestination(node1, lineWeight);
	}
	
	public static List<Node> ReturnCheapestPath(Graph graph, String cityName1, String cityName2) {
		Node firstCity = ReturnNodeWithString(graph.GetNodes(), cityName1);
		Node secondCity = ReturnNodeWithString(graph.GetNodes(), cityName2);
		//Check if cities exist
		if(firstCity == null || secondCity == null) {
			return null;
		}
		else{
			graph = calculateCheapestPathFromSource(graph, firstCity);
			return secondCity.getCheapestPath();
		}
	}
	
	public static int ReturnCheapestPrice(Graph graph, String cityName1, String cityName2) {
		Node firstCity = ReturnNodeWithString(graph.GetNodes(), cityName1);
		Node secondCity = ReturnNodeWithString(graph.GetNodes(), cityName2);
		
		//check if cities exist
		if(firstCity == null || secondCity == null) { return Integer.MAX_VALUE;}
		else{
			graph = calculateCheapestPathFromSource(graph, firstCity);
			return secondCity.getTotalPrice();
		}
	}
	
	public static void PrintPath(List <Node> Path, String city2) {
		for(int i = 0; i < Path.size(); i++) {
			System.out.print(Path.get(i).getCity() + " ");
		}
		System.out.print(city2);
		System.out.println();
	}
}
