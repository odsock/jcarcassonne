package jCarcassonne;

import java.awt.*;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JComponent;

public class Landscape {
	private Tile startTile;
	private ConcurrentHashMap<Point,Tile> landscapeHash = new ConcurrentHashMap<Point,Tile>();
	
	private int minX = 0;
	private int maxX = 0;
	private int minY = 0;
	private int maxY = 0;
	
	private int lastX = 0;
	private int lastY = 0;

	public Landscape(Tile startTile){
		this.startTile = startTile;
		placeTile(startTile,0,0);
	}

	public void paintLandscape(Graphics g)
	{
		//g.translate(-lastX*128-64+400, lastY*128-64+300);  //center on last tile placed
		g.translate(400-64, 300-64); //center on startTile (0,0)
		for(Tile t : landscapeHash.values())
			//g.drawImage(t.getImage(),(t.x+Math.abs(minX))*128,-(t.y-maxY)*128,null);
			g.drawImage(t.getImage(),((t.x)*128),-(t.y)*128,null);
		g.translate(-(400-64), -(300-64));
		//g.translate(lastX*128+64-400, -lastY*128+64-300);
	}

	public void placeTile(Tile t, int x, int y){
		//add to coordinate map
		landscapeHash.put(new Point(x,y), t);
		t.setXY(x,y);
		
		//set coordinates for graphics origin
		lastX = x;
		lastY = y;

		//update min/max coordinates
		if(x > maxX) maxX = x;
		if(x < minX) minX = x;
		if(y > maxY) maxY = y;
		if(y < minY) minY = y;

		//setup edge references
		Point p = new Point(x,y);
		p.translate(0,1);
		if(landscapeHash.containsKey(p)){
			t.setNorthTile(landscapeHash.get(p));
			t.getNorthTile().setSouthTile(t);
		}
		p.translate(0,-2);
		if(landscapeHash.containsKey(p)){
			t.setSouthTile(landscapeHash.get(p));
			t.getSouthTile().setNorthTile(t);
		}
		p.translate(1,1);
		if(landscapeHash.containsKey(p)){
			t.setEastTile(landscapeHash.get(p));
			t.getEastTile().setWestTile(t);
		}
		p.translate(-2,0);
		if(landscapeHash.containsKey(p)){
			t.setWestTile(landscapeHash.get(p));
			t.getWestTile().setEastTile(t);
		}
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public Tile getStartTile(){
		return startTile;
	}

	public Tile getTile(int x, int y){
		return landscapeHash.get(new Point(x,y));
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}
}