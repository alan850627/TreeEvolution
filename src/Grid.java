
public class Grid {
	public int sunlight;
	public int type;
	
	public Grid(){
		type = World.OBJ_EMPTY;
		sunlight = World.FULL_SUNLIGHT;
	}
	
	public Grid(int t, int s){
		type = t;
		sunlight = s;
	}
}
