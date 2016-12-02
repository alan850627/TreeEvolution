import java.util.ArrayList;


public class BranchNode {
	
	BranchNode parent;
	ArrayList<BranchNode> children = new ArrayList<BranchNode>();
	
	boolean leaf_alive = true;
	public int leaf_age = 0;
	public int leaf_size = 1;
	
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
	
	public void addLeaf(Tree tree, Grid[][] map) {
		// assumes x and y are in the grid, only bound check x when leaves are
		// big
		for (int i = 0; i < tree.leaf_size; i++) {
			int l = x + i - tree.leaf_size / 2;
			if (l >= 0 && l < World.WORLD_WIDTH) {
				map[l][y].type = World.OBJ_LEAF;
			}
		}
	}
	
	public void killLeaf(Tree tree, Grid[][] map) {
		leaf_alive = false;
		for (int i = 0; i < tree.leaf_size; i++) {
			int l = x + i - tree.leaf_size / 2;
			if (l >= 0 && l < World.WORLD_WIDTH
					&& map[l][y].type == World.OBJ_LEAF) {
				map[l][y].type = World.OBJ_EMPTY;
			}
		}
		map[x][y].type = World.OBJ_NODE;
	}
}
