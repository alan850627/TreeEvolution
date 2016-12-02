import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Tree {

	private static final int WIDTH_VARIATION = 5; // how many grids each sector
													// the gene represents.
	private static final int HEIGHT_VARIATION = 5; // how many grids each
													// sector the gene
													// represents.
	private static final int GENE_TOTAL = 10; // must be multiple of 5.
	private static final int LEAF_LIFETIME = 1000; // number of frames before
													// a leaf dies.
	private static final int COLOR_CHANGE_TIME = 10; // number of frames before
														// a color changes
	private static final int INITIAL_HEALTH = 1000;// not sure if I want to use
													// this.
	private static final int OLD_AGE_FACTOR = 1000; // the bigger the number,
													// the slower it'll die.

	public int[] width_gene = { 2, 2, 2, 2, 2 }; // must add up to
													// GENE_TOTAL
	public int[] height_gene = { 2, 2, 2, 2, 2 }; // must add up to
														// GENE_TOTAL

	public int max_branch_per_node = 2;
	public int reproduce_time = 1000;
	public int grow_time = 100;
	public int health = 1000;
	public int leaf_size = 1;

	public int amount_of_tree = 1;
	public int age = 1;
	public int generation = 1;
	public int color = 255;

	BranchNode root;

	public Tree() {
		root = new BranchNode();
	}

	public Tree(int x, int y, Grid[][] map) {
		root = new BranchNode(x, y);
		root.addLeaf(this, map);
	}

	public Tree(Grid[][] map) {
		root = new BranchNode();
		root.addLeaf(this, map);
	}

	// Reproduce from parent
	public Tree(Tree parent, int x, int y, Grid[][] map) {
		// mutate!
		height_gene = parent.height_gene.clone();
		if (World.BRANCH_HEIGHT_MUTATION) {
			int r = (int) (Math.random() * 5);
			height_gene[r] += 1;
			r = (int) (Math.random() * 5);
			while (height_gene[r] == 0) {
				r = (int) (Math.random() * 5);
			}
			height_gene[r] -= 1;
		}

		width_gene = parent.width_gene.clone();
		if (World.BRANCH_WIDTH_MUTATION) {
			int r = (int) (Math.random() * 5);
			width_gene[r] += 1;
			r = (int) (Math.random() * 5);
			while (width_gene[r] == 0) {
				r = (int) (Math.random() * 5);
			}
			width_gene[r] -= 1;
		}

		reproduce_time = parent.reproduce_time;
		if (World.REPRODUCE_TIME_MUTATION) {
			int r = (int) (Math.random() * 11) - 5;
			if (reproduce_time + r >= 0) {
				reproduce_time += r;
			}
		}

		grow_time = parent.grow_time;
		if (World.GROW_TIME_MUTATION) {
			int r = (int) (Math.random() * 11) - 5;
			if (grow_time + r >= 0) {
				grow_time += r;
			}
		}

		leaf_size = parent.leaf_size;
		if (World.LEAF_SIZE_MUTATION) {
			int r = (int) (Math.random() * 3) - 1;
			if (leaf_size + r >= 0) {
				leaf_size += r;
			}
		}

		max_branch_per_node = parent.max_branch_per_node;
		if (World.MAX_BRANCH_PER_NODE_MUTATION) {
			int r = (int) (Math.random() * 3) - 1;
			if (max_branch_per_node + r > 0) {
				max_branch_per_node += r;
			}
		}

		generation = parent.generation + 1;
		health = INITIAL_HEALTH;
		root = new BranchNode(x, y);

		// make new leaf on new branch
		root.addLeaf(this, map);
	}

	public void paintTree(Graphics2D g2, int i) {
		// g2.setColor(new Color(139, 69, 19));
		g2.setColor(new Color(color, color, color));

		if (i == 0) {
			g2.setColor(Color.RED);
		}
		paintBranch(g2, root);
	}

	private void paintBranch(Graphics2D g2, BranchNode bn) {
		for (int i = 0; i < bn.children.size(); i += 1) {
			paintBranch(g2, bn.children.get(i));
		}

		if (bn.parent != null) {
			g2.drawLine(bn.x * World.SQUARE_SIZE, (World.WORLD_HEIGHT - bn.y)
					* World.SQUARE_SIZE, bn.parent.x * World.SQUARE_SIZE,
					(World.WORLD_HEIGHT - bn.parent.y) * World.SQUARE_SIZE);

		}
	}

	public void processTree(Grid[][] map, ArrayList<Tree> trees) {
		// if conditions meet, grow!
		// if conditions meet, reproduce
		// update health
		// update age
		// killing is done by World class.

		// change color
		if (age % COLOR_CHANGE_TIME == 0 && color > 0) {
			color -= 1;
		}

		// /////////////I don't like it growing on timer////////////////////
		if (age % grow_time == 0) {
			grow(map);
		}
		if (age % reproduce_time == 0) {
			reproduce(map, trees);
		}

		// update health based on how big the tree is, and how much light the
		// leaves are getting
		processNode(root, map);
		health -= amount_of_tree;
		health -= age / OLD_AGE_FACTOR;

		age += 1;
	}

	private void processNode(BranchNode bn, Grid[][] map) {
		// Process individual nodes here
		// Photo Synthesis
		if (bn.leaf_alive) {
			for (int i = 0; i < leaf_size; i++) {
				int l = bn.x + i - leaf_size / 2;
				if (l >= 0 && l < World.WORLD_WIDTH) {
					health += map[l][bn.y].sunlight;
					map[l][bn.y].sunlight -= World.LEAF_BLOCK;
				}
			}
			
			// Kill Leaf if the leaf is too old
			bn.leaf_age += 1;
			if (bn.leaf_age >= LEAF_LIFETIME) {
				bn.killLeaf(this, map);
			}
		}
		// If I really want to be safe, I should put a check for if bn.children.size() > 0
		// But the for loop here checks it for me already.
		for (int i = 0; i < bn.children.size(); i++) {
			processNode(bn.children.get(i), map);
		}		
	}

	public void reproduce(Grid[][] map, ArrayList<Tree> trees) {
		for (int i = 0; i < health/age; i++) { // not sure how many children each tree
										// should have...
			BranchNode bn = root;
			while (bn.children.size() > 0) {
				bn = bn.children
						.get((int) (Math.random() * bn.children.size()));
			}
			trees.add(new Tree(this, bn.x, 0, map));
		}
	}

	public void kill(Grid[][] map) {
		removeLeaves(map, root);
	}

	private void removeLeaves(Grid[][] map, BranchNode bn) {
		if (bn.leaf_alive) {
			bn.killLeaf(this, map);
		}
		for (int i = 0; i < bn.children.size(); i++) {
			removeLeaves(map, bn.children.get(i));
		}		
	}

	public void grow(Grid[][] map) {
		// grows a new branch.
		// check if any given node is full capacity. if not, grow or move to
		// child.
		// using node as reference, make a new node.
		// update map as well

		// pick an existing node to grow from.
		BranchNode parent = root;
		while (parent.children.size() > 0) {
			if (parent.children.size() >= max_branch_per_node) {
				// if no more room at this branch
				parent = parent.children
						.get((int) (Math.random() * parent.children.size()));
			} else if (Math.random() < 0.5) {
				// if there is still room, picks randomly
				parent = parent.children
						.get((int) (Math.random() * parent.children.size()));
			} else {
				break;
			}
		}

		int newX, newY; // X and Y coordinates for the new node.
		int r = (int) (Math.random() * GENE_TOTAL);
		int gene_count = width_gene[0];
		int width_select = 0;
		while (r > gene_count) {
			width_select += 1;
			gene_count += width_gene[width_select];
		}
		r = (int) (Math.random() * WIDTH_VARIATION);
		newX = parent.x - WIDTH_VARIATION / 2 + (width_select - 2)
				* WIDTH_VARIATION + r;
		if (newX < 0) {// if goes off the grid, push back.
			newX = 0;
		} else if (newX >= World.WORLD_WIDTH) {
			newX = World.WORLD_WIDTH - 1;
		}

		r = (int) (Math.random() * GENE_TOTAL);
		gene_count = height_gene[0];
		int height_select = 0;
		while (r > gene_count) {
			height_select += 1;
			gene_count += height_gene[height_select];
		}
		r = (int) (Math.random() * HEIGHT_VARIATION);
		newY = parent.y + height_select * HEIGHT_VARIATION + r;
		if (newY >= World.WORLD_HEIGHT) {
			newY = World.WORLD_HEIGHT - 1;
		}

		// add the branch to the total body.
		amount_of_tree += (int) Math.sqrt((newX - parent.x) * (newX - parent.x)
				+ (newY - parent.y) * (newY - parent.y));
		BranchNode child = new BranchNode(parent, newX, newY);
		parent.children.add(child);

		if (parent.leaf_alive) {
			// kill leaf, because branch is growing off of it
			parent.killLeaf(this, map);
		}

		// make new leaf on new branch
		child.addLeaf(this, map);
	}
}
