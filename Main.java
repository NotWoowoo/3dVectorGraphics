package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import lawt.engine.Window;

public class Main {
	
	public static Window w;
	public static float s = 1.4f;
	public static GridWorld map;
	
	public static Window.DrawCall drawScene;
	
	public static double d = 1;
	
	public static void init(){
		map = new GridWorld();
		
		w = new Window((int)(1280*s), (int)(720*s), "yes");
		drawScene = (Graphics g)->{
			Graphics2D g2 = (Graphics2D) g;
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, w.getWidth(), w.getHeight());
			
			int collumns = 500;
			double collumnPercent;
			double proximity;
			double brightness;
			double ang;
			for(int x = 0; x < collumns; ++x){
				collumnPercent = x/(double)collumns;
				ang = (collumnPercent - .5)*2;
				
				double[] cast = map.castRayFromPlayer(map.getPlayerRot() + ang);
				proximity = cast[0];
				brightness = /*Math.pow(1/(1+proximity),0.5) */Math.pow(cast[4], cast[4]+1);
				proximity = 1/proximity;
				
				if(cast[1]==0) {
					g.setColor( Color.getHSBColor(0.0f, 0.0f, (float)brightness) );
				}else{
					g.setColor( Color.getHSBColor((float)(cast[1]/255.0 + cast[2]/10.0 + cast[3]/10.0), (float)(0.5 - (cast[4]*cast[4])/2.0), (float)brightness) );
				}
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g.fillRect( (int)(collumnPercent * w.getWidth()), (int) ((w.getHeight()/2) * (1 - proximity)), w.getWidth()/collumns + 1, (int) (w.getHeight() * proximity));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		};
		w.setDrawCall(drawScene);
	}
	
	public static void run(){
		w.executeFrame();
		
		double spd = 0.04;
		if(w.keysDown.contains(KeyEvent.VK_SHIFT)) {
			spd = 0.01;
		}
		if(w.keysDown.contains(KeyEvent.VK_CONTROL)) {
			spd = 0.1;
		}
		
		if(w.keysDown.contains(Window.key('W'))){
			map.addPlayerPos(Math.cos(map.getPlayerRot())*spd, Math.sin(map.getPlayerRot())*spd);
		}
		
		if(w.keysDown.contains(Window.key('S'))){
			map.addPlayerPos(-Math.cos(map.getPlayerRot())*spd, -Math.sin(map.getPlayerRot())*spd);
		}
		
		if(w.keysDown.contains(Window.key('A'))){
			map.addPlayerRot(-spd);
		}
		
		if(w.keysDown.contains(Window.key('D'))){
			map.addPlayerRot(spd);
		}
		
		if(w.keysDown.contains(KeyEvent.VK_SPACE)){
			map.lightx = map.getPlayerX();
			map.lighty = map.getPlayerY();
		}
		
		if(w.keysDown.contains(Window.key('Q')) && w.step % 10 == 0) {
			double[] ray = map.castRay(map.getPlayerX(), map.getPlayerY(), map.getPlayerRot());
			map.poke(ray[0]-Math.cos(map.getPlayerRot()), ray[1]-Math.sin(map.getPlayerRot()), (byte) 100);
		}
		
		if(w.keysDown.contains(Window.key('E')) && w.step % 10 == 0) {
			double[] ray = map.castRay(map.getPlayerX(), map.getPlayerY(), map.getPlayerRot());
			map.poke(ray[0], ray[1], (byte) 0);
		}
		
		if(w.mouseScrollTotal != 0) {
			d -= w.mouseScrollTotal/5;
			w.mouseScrollTotal = 0;
			System.out.println(d);
		}
	}
	
	public static void main(String[] args) {
		
		init();
		
		while(true){
			run();
		}
		
	}

}
