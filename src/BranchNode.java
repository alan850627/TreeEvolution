import java.util.ArrayList;


public class BranchNode {
	
	BranchNode parent;
	ArrayList<BranchNode> children = new ArrayList<BranchNode>();
	
	boolean leaf_alive = true;
	
	public int x = World.WORLD_WIDTH/2;
	public int y = 0;
	
	public BranchNode(){
		parent = null;
	}
	
	public BranchNode(int a, int b){
		x = a;
		y = b;
		parent = null;
	}
	
	public BranchNode(BranchNode bn, int a, int b){
		parent = bn;
		x = a;
		y = b;
	}
}
