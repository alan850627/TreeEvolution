import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class World extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	public static final int SCREEN_WIDTH = 1000;
	public static final int SCREEN_HEIGHT = 1000;
	public static final int SQUARE_SIZE = 5;
	public static final int WORLD_WIDTH = SCREEN_WIDTH / SQUARE_SIZE;
	public static final int WORLD_HEIGHT = SCREEN_HEIGHT / SQUARE_SIZE;
	public static final int DELAY = 1;
	public static final int SPEED_UP = 1;
	public static final int INIT_NUM_TREES = 100; // numbers of trees in the
													// initial state.

	public static final int FULL_SUNLIGHT = 20;
	public static final int LEAF_BLOCK = 5;

	public static final boolean BRANCH_WIDTH_MUTATION = true;
	public static final boolean BRANCH_HEIGHT_MUTATION = true;
	public static final boolean REPRODUCE_TIME_MUTATION = true;
	public static final boolean GROW_TIME_MUTATION = true;
	public static final boolean LEAF_SIZE_MUTATION = true;
	public static final boolean MAX_BRANCH_PER_NODE_MUTATION = true;

	public static final int OBJ_EMPTY = 0;
	public static final int OBJ_NODE = 1;
	public static final int OBJ_LEAF = 2;

	public int[] total_width_gene;
	public int[] total_height_gene;
	public int total_reproduce_time = 0;
	public int total_grow_time = 0;
	public int total_leaf_size = 0;
	public int total_max_branch_per_node = 0;
	public int total_generation = 0;
	public int total_age = 0;
	public int total_health = 0;
	public int total_tree_size = 0;
	public int time = 0;

	public Timer timer;
	public Grid[][] map = new Grid[WORLD_WIDTH][WORLD_HEIGHT]; // typical
																// coordinate
																// system, (0,0)
																// at bottom
																// left
	public ArrayList<Tree> trees = new ArrayList<Tree>();
	public Start parentClass;

	public World(Start start) {
		// TODO Auto-generated constructor stub
		parentClass = start;
		timer = new Timer(DELAY, this);
		timer.start();

		resetState();
	}

	public void resetState() {
		for (int i = 0; i < WORLD_WIDTH; i++) {
			for (int j = 0; j < WORLD_HEIGHT; j++) {
				map[i][j] = new Grid();
			}
		}
		trees.clear();
		total_width_gene = new int[5];
		total_height_gene = new int[5];
		total_reproduce_time = 0;
		total_grow_time = 0;
		total_leaf_size = 0;
		total_max_branch_per_node = 0;
		total_generation = 0;
		total_age = 0;
		total_health = 0;
		total_tree_size = 0;
		time = 0;

		// INITIAL STATE.
		for (int i = 0; i < INIT_NUM_TREES; i++) {
			trees.add(new Tree(WORLD_WIDTH / INIT_NUM_TREES * i, 0, map));
		}
	}

	private void collectData(Tree t) {
		for (int i = 0; i < 5; i++) {
			total_width_gene[i] += t.width_gene[i];
			total_height_gene[i] += t.height_gene[i];
		}
		total_reproduce_time += t.reproduce_time;
		total_grow_time += t.grow_time;
		total_leaf_size += t.leaf_size;
		total_max_branch_per_node += t.max_branch_per_node;
		total_generation += t.generation;
		total_age += t.age;
		total_health += t.health;
		total_tree_size += t.amount_of_tree;
	}

	public void actionPerformed(ActionEvent arg0) {
		total_width_gene = new int[5];//
		total_height_gene = new int[5];//
		total_reproduce_time = 0;
		total_grow_time = 0;
		total_leaf_size = 0; //
		total_max_branch_per_node = 0; //
		total_generation = 0; //
		total_age = 0; //
		total_health = 0; //
		total_tree_size = 0; //

		for (int i = 0; i < trees.size(); i++) {
			trees.get(i).processTree(map, trees);
			collectData(trees.get(i));
			if (trees.get(i).health <= 0) { // kill.
				trees.get(i).kill(map);
				trees.remove(i);
				i--;
			}
		}

		// Process Sunlight
		for (int i = 0; i < WORLD_WIDTH; i++) {
			int currentSunlight = FULL_SUNLIGHT;
			for (int j = WORLD_HEIGHT - 1; j >= 0; j--) {
				map[i][j].sunlight = currentSunlight;
				if (map[i][j].type == OBJ_LEAF && currentSunlight > 0) {
					currentSunlight -= LEAF_BLOCK;
				}
			}
		}

		time += 1;
	}

	public void paint(Graphics g) {
		if (time % SPEED_UP == 0) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			// ////////////TURN ON SUNLIGHT HERE////////////////
			g2.setStroke(new BasicStroke(SQUARE_SIZE));
			paintSunlight(g2);
			// /////////////////////////////////////////////////

			// Draws all the branches of the trees
			g2.setStroke(new BasicStroke(SQUARE_SIZE / 2));
			for (int i = 0; i < trees.size(); i++) {
				trees.get(i).paintTree(g2, i);
			}

			g2.setStroke(new BasicStroke(SQUARE_SIZE));
			for (int i = 0; i < WORLD_WIDTH; i++) {
				for (int j = 0; j < WORLD_HEIGHT; j++) {
					if (map[i][j].type == OBJ_LEAF) {
						g2.setColor(Color.GREEN);
						g2.drawLine(i * SQUARE_SIZE, (WORLD_HEIGHT - j)
								* SQUARE_SIZE, i * SQUARE_SIZE,
								(WORLD_HEIGHT - j) * SQUARE_SIZE);
					}
				}
			}

			// ///////////////////////PRINT STATS////////////////
			if (trees.size() > 0) {
				g2.setFont(new Font("TimesRoman", Font.BOLD, 15));
				g2.setColor(Color.BLACK);
				g2.drawString("AVERAGES:", SCREEN_WIDTH, 20);
				g2.setFont(new Font("TimesRoman", Font.PLAIN, 15));				
				g2.drawString("GENERATION: "
						+ (total_generation / trees.size()), SCREEN_WIDTH, 40);
				g2.drawString("AGE: " + (total_age / trees.size()),
						SCREEN_WIDTH, 60);
				g2.drawString("SIZE: " + (total_tree_size / trees.size()),
						SCREEN_WIDTH, 80);
				g2.drawString("HEALTH: " + (total_health / trees.size()),
						SCREEN_WIDTH, 100);
				g2.drawString("WIDTH GENE: ", SCREEN_WIDTH, 120);
				g2.drawString("HEIGHT GENE: ", SCREEN_WIDTH, 140);
				for (int i = 0; i < 5; i++) {
					g2.drawString("" + (total_width_gene[i] / trees.size())
							+ ", ", SCREEN_WIDTH + 110 + i * 20, 120);
					g2.drawString("" + (total_height_gene[i] / trees.size())
							+ ", ", SCREEN_WIDTH + 110 + i * 20, 140);
				}
				g2.drawString("LEAF SIZE: " + (total_leaf_size / trees.size()),
						SCREEN_WIDTH, 160);
				g2.drawString("MAX BRANCH PER NODE: "
						+ (total_max_branch_per_node / trees.size()),
						SCREEN_WIDTH, 180);
				g2.drawString("GROW TIME: " + (total_grow_time / trees.size()),
						SCREEN_WIDTH, 200);
				g2.drawString("REPRODUCE TIME: "
						+ (total_reproduce_time / trees.size()), SCREEN_WIDTH,
						220);

				// /////INFO FOR RED TREE//////////
				g2.setFont(new Font("TimesRoman", Font.BOLD, 15));
				g2.setColor(Color.RED);
				g2.drawString("RED TREE:", SCREEN_WIDTH, 300);
				g2.setFont(new Font("TimesRoman", Font.PLAIN, 15));
				Tree t = trees.get(0);
				g2.drawString("GENERATION: " + t.generation, SCREEN_WIDTH, 320);
				g2.drawString("AGE: " + t.age, SCREEN_WIDTH, 340);
				g2.drawString("SIZE: " + t.amount_of_tree, SCREEN_WIDTH, 360);
				g2.drawString("HEALTH: " + t.health, SCREEN_WIDTH, 380);
				g2.drawString("WIDTH GENE: ", SCREEN_WIDTH, 400);
				g2.drawString("HEIGHT GENE: ", SCREEN_WIDTH, 420);
				for (int i = 0; i < 5; i++) {
					g2.drawString("" + (t.width_gene[i]) + ", ", SCREEN_WIDTH
							+ 110 + i * 20, 400);
					g2.drawString("" + (t.height_gene[i]) + ", ", SCREEN_WIDTH
							+ 110 + i * 20, 420);
				}
				g2.drawString("LEAF SIZE: " + t.leaf_size, SCREEN_WIDTH, 440);
				g2.drawString("MAX BRANCH PER NODE: " + t.max_branch_per_node,
						SCREEN_WIDTH, 460);
				g2.drawString("GROW TIME: " + t.grow_time, SCREEN_WIDTH, 480);
				g2.drawString("REPRODUCE TIME: " + t.reproduce_time,
						SCREEN_WIDTH, 500);
				g2.setFont(new Font("TimesRoman", Font.BOLD, 15));
				g2.setColor(Color.BLACK);
				g2.drawString("TIME: " + time, SCREEN_WIDTH, 600);
				g2.drawString("POPULATION: " + trees.size(), SCREEN_WIDTH, 620);
			}
		}
		repaint();

	}

	private void paintSunlight(Graphics2D g2) {
		int temp;
		for (int i = 0; i < WORLD_WIDTH; i++) {
			for (int j = 0; j < WORLD_HEIGHT; j++) {
				temp = (255 * map[i][j].sunlight) / FULL_SUNLIGHT;
				g2.setColor(new Color(temp, temp, 0));
				g2.drawLine(i * SQUARE_SIZE, (WORLD_HEIGHT - j) * SQUARE_SIZE,
						i * SQUARE_SIZE, (WORLD_HEIGHT - j) * SQUARE_SIZE);
			}
		}
	}

}
