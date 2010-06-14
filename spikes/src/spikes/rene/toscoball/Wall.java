package spikes.rene.toscoball;
//a rectangular wall with collision.
//Wall(X pos ,Y pos, Width, Height)

import java.awt.Graphics;


public class Wall {

	int x,y,w,h;
	
	public Wall(int i, int j, int k, int l) {
		x=i;
		y=j;
		w=k;
		h=l;
	}
	
	public void draw(Graphics g) {
		g.fillRect(x,y,w,h);
	}
	
	public void checkCollision(Ball other) {
		if (!other.isAlive) return; 

		//Contact before collision to increase precision.
		other.contact(this);
		
		if ((other.x>x & other.x<x+w) & (other.y>y-Ball.radius & other.y<y+h+Ball.radius)) {
			//vertical collision
			other.bounce(0);
			return;
		}
		if ((other.y>y & other.y<y+h) & (other.x>x-Ball.radius & other.x<x+w+Ball.radius)) {
			//horizontal collision
			other.bounce(90);
			return;
		}
		if (M.pointDistance(x,y,other.x,other.y)<Ball.radius) {
			//top-left collision
			other.bounce(M.pointDirection(x,y,other.x,other.y)-90);
			return;
		}
		if (M.pointDistance(x+w,y,other.x,other.y)<Ball.radius) {
			//top-right collision
			other.bounce(M.pointDirection(x+w,y,other.x,other.y)-90);
			return;
		}
		if (M.pointDistance(x+w,y+h,other.x,other.y)<Ball.radius) {
			//bottom-right collision
			other.bounce(M.pointDirection(x+w,y+h,other.x,other.y)-90);
			return;
		}
		if (M.pointDistance(x,y+h,other.x,other.y)<Ball.radius) {
			//bottom-left collision
			other.bounce(M.pointDirection(x,y+h,other.x,other.y)-90);
			return;
		}
	}
	
	public boolean isIn(Ball other) {
		//required for Ball.contact(Wall)
		if ((other.x>x & other.x<x+w) & (other.y>y-Ball.radius & other.y<y+h+Ball.radius)) return true;
		if ((other.y>y & other.y<y+h) & (other.x>x-Ball.radius & other.x<x+w+Ball.radius)) return true;
		if (M.pointDistance(x,y,other.x,other.y)<Ball.radius) return true;
		if (M.pointDistance(x+w,y,other.x,other.y)<Ball.radius) return true;
		if (M.pointDistance(x+w,y+h,other.x,other.y)<Ball.radius) return true;
		if (M.pointDistance(x,y+h,other.x,other.y)<Ball.radius) return true;
		return false;
	}
}