package flappyBirdGame;
/*
 * Pipe coordinates (x,y) is the BOTTOM PIPE of the pair, and x,y refer to his top left corner!
 * */
public class Pipe {
	private int x;
	private int y;
	private int width;
	private int gap; //between upper pipe and bottom pipe
	private static int COLLISION_TOLERANCE=2;
	private boolean surpassed=false;
	
	public Pipe(int x,int y,int width,int gap) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.gap=gap;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	public void decrementX(int n) {
		x-=n;
	}
	
	public void setX(int x) {
		this.x=x;
	}
	
	public void setY(int y) {
		this.y=y;
	}
	
	public boolean collide(int xBird,int yBird,int birdDimension) {
		if(xBird+birdDimension-COLLISION_TOLERANCE>x && xBird<(x+width)) {
			if(yBird+birdDimension-COLLISION_TOLERANCE>y /*collide con bottom pipe*/ || yBird+COLLISION_TOLERANCE<(y-gap)/*collide con top pipe */) {
				return true;
			}
			
		}
		
		return false;
		
	}
	public void setSurpassed(boolean bol){
		surpassed=bol;
	}
	public boolean isSurpassed() {
		return surpassed;
	}
}

