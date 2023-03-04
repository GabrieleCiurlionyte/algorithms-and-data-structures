import java.util.HashSet;
import java.util.Set;



public class Graph {

	private Set<Node> nodes = new HashSet<>();
	
	public void AddNode(Node nodeA) {
		nodes.add(nodeA);
	}
	
	//GETTERS
	public Set<Node> GetNodes(){
		return nodes;
	}
	
	//SETTERS
	public void SetNodes(Set<Node> nodes) {
		this.nodes = nodes;
	}
	
}
