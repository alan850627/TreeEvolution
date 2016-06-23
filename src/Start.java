import java.awt.Color;

import javax.swing.JFrame;

public class Start extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Main parentMain = new Main();
	
	public Start(){
		World world = new World(this);
		add(world);

		setTitle("Tree Evolution");
		setSize(1500, 1050);
		setResizable(false);
		setLocation(0, 0);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(new Color(255, 255, 255));
	}
	
	

}
