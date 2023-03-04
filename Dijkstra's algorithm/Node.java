
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Node {
	
	private String city;
	
	//Total price from the source
	private int totalPrice = Integer.MAX_VALUE;
	private List<Node> cheapestPath = new LinkedList<>();
	
	Map<Node, Integer> adjacentNodes = new HashMap<>();
	
	public void addDestination(Node destination, int distance) {
		adjacentNodes.put(destination, distance);
	}
	
	public Node(String city) {
		this.city = city;
	}
	
	
	//SETTERS
	public void setCity(String city) {
		this.city = city;
	}
	
	/*
	public void setOwner(String owner) {
		this.owner = owner;
	}*/
	
	
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public void setAdjacentNodes(Map<Node, Integer> adjacentNodes) {
		this.adjacentNodes = adjacentNodes;
	}
	
	public void setCheapestPath(List<Node> cheapestPath) {
		this.cheapestPath = cheapestPath;
	}
	
	//GETTERS
	public String getCity() {
		return city;
	}

	/*
	public String getOwner() {
		return owner;
	}*/

	public int getTotalPrice() {
		return totalPrice;
	}

	public Map<Node, Integer> GetAdjacentNodes(){
		return adjacentNodes;	
	}

	public List<Node> getCheapestPath() {
		return cheapestPath;
	}

	
}
