package main;

import lawt.engine.Util;

public class GridWorld {
	
	private byte[][] map;
	private double mapHeight;
	private double mapWidth;
	private double[] player = new double[3];
	
	public double lightx=.1,lighty=.1;
	
	public GridWorld(){
		setPlayerPos(0.1,0.1);
		
		map = new byte[20][20];
		mapHeight = map.length;  
	    mapWidth = map[0].length;
	    
	    for(int y = 0; y < mapHeight; ++y) {
	    	for(int x = 0; x < mapWidth; ++x) {
		    	map[x][y] = (byte) ((Util.random(1.0)>x/20.0) ? 0 : 255*Math.sin(x+y*2));
		    }
	    }
	}
	
	//returns {finalX, finalY, finalX-1step, finalY-1step}
	public double[] castRay(double startingX, double startingY, double ang) {
		double step = 1/(200.0);
		//double e = 1.01;
		double x = startingX;
		double y = startingY;
		double cos = Math.cos(ang);
		double sin = Math.sin(ang);
		double dx = cos*step;
		double dy = sin*step;
		
		
		
		do{
			double xx = x+dx;
			double yy = y+dy;
			
			//dx *= e;
			//dy *= e;
			
			if(Util.inRectangle(xx, yy, 0, 0, mapWidth, mapHeight) && Util.dist(xx, yy, lightx, lighty) > 1/100.0){
				x = xx;
				y = yy;
			}else{
				break;
			}
		}while(map[(int)x][(int)y] == 0 /*&& (y+Math.sin(x))%10.0 > 0.01*/);
		
		double[] result = {x,y, x-cos/100.0, y-sin/100.0};
		return result;
	}
	
	//returns {finalX, finalY, finalX-1step, finalY-1step}
	public double[] ncastRay(double startingX, double startingY, double ang) {
		double x = startingX;
		double y = startingY;
		
		double stepTolerance = 1/10.0;
		double step = stepTolerance;
		double stepMultiplier = 1.01;
		
		double cos = Math.cos(ang);
		double sin = Math.sin(ang);
		
		double dx=0;
		double dy=0;
		double xx;
		double yy;
		
		while(step > stepTolerance) {		
			
			if(Util.inRectangle(x, y, 0, 0, mapWidth, mapHeight) && Util.dist(x, y, lightx, lighty) > 1/100.0 && map[(int)x][(int)y] == 0) {		
				step *= stepMultiplier;
				dx = cos*step;
				dy = sin*step;
				
				xx = x+dx;
				yy = y+dy;
				x = xx;
				y = yy;
			}else{
				xx = x-dx;
				yy = y-dy;
				x = xx;
				y = yy;
				
				step /= stepMultiplier;
				dx = cos*step;
				dy = sin*step;
			}
			
		}
		
		System.out.println(x+" "+y);
		
		double[] result = {x, y, x-dx, y-dy};
		return result;
	}
	
	public double[] castRayFromPlayer(double ang){
		double[] result = new double[5];
		
		double distance = 0;
		
		double[] ray = castRay(getPlayerX(), getPlayerY(), ang); //cast ray from player
		
		distance = Util.dist(getPlayerX(), getPlayerY(), ray[0], ray[1]); //set distance to length of ray
		result[0] = distance; //distance

		result[1] = map[(int)ray[0]][(int)ray[1]]; //block type 0-255
		result[2] = (ray[0])%1.0; //individual block x 0-1
		result[3] = (ray[1])%1.0; //individual block y 0-1
		
		double lightAng = Util.angle(ray[2], ray[3], lightx, lighty);
		double lightDist = 1/(Util.dist(ray[2], ray[3], lightx, lighty)+1);
		double[] lightRay = castRay(ray[2], ray[3], lightAng); //cast ray from endray to light
		double lightness = Util.dist( lightRay[0], lightRay[1], lightx, lighty );
		result[4] = (lightness<1/50.0)?0.1+3*Math.atan(lightDist*Util.clamp(1/lightness,0,1))/Math.PI:0.1; // block's light visibility
		
		return result;
	}
	
	public void poke(double x, double y, byte val) {
		map[(int)x][(int)y] = val;
	}
	
	public double getPlayerX(){
		return player[0];
	}
	
	public double getPlayerY(){
		return player[1];
	}
	
	public double getPlayerRot(){
		return player[2];
	}
	
	public void setPlayerX(double val){
		player[0] = val;
	}
	
	public void setPlayerY(double val){
		player[1] = val;
	}
	
	public void setPlayerRot(double val){
		player[2] = val;
	}
	
	public void addPlayerX(double val){
		double xx = player[0] + val;
		if(xx > 0 && xx < mapWidth && map[(int)xx][(int)getPlayerY()] == 0){
			player[0] = xx;
		}
	}
	
	public void addPlayerY(double val){
		double yy = player[1] + val;
		if(yy > 0 && yy < mapHeight && map[(int)getPlayerX()][(int)yy] == 0){
			player[1] = yy;
		}
	}
	
	public void addPlayerRot(double val){
		player[2] += val;
	}
	
	public void setPlayerPos(double x, double y){
		player[0] = x;
		player[1] = y;
	}
	
	public void addPlayerPos(double x, double y){
		addPlayerX(x);
		addPlayerY(y);
	}
}
