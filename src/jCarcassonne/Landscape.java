package jCarcassonne;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Hashtable;
import javax.swing.JComponent;

public class Landscape extends JComponent{
	private Tile startTile;
	private Hashtable<Point,Tile> landscapeHash = new Hashtable<Point,Tile>();
	private int minX = 0;
	private int maxX = 0;
	private int minY = 0;
	private int maxY = 0;

	public Landscape(Tile startTile){
		this.startTile = startTile;
		placeTile(startTile,0,0);
	}

	public void paint(Graphics g)
	{
//		System.out.println("paint: " + landscapeHash.toString());
		for(Tile t : landscapeHash.values())
		{
//			System.out.println(t.getPoint());
			g.drawImage(t.getImage(),(t.x+minX)*128,-(t.y-maxY)*128,null);
		}
	}

	public void placeTile(Tile t, int x, int y){
		//add to coordinate map
		landscapeHash.put(new Point(x,y), t);
		t.setXY(x,y);

		//update min/max coordinates
		if(x > maxX) maxX = x;
		if(x < minX) minX = x;
		if(y > maxY) maxY = y;
		if(y < minY) minY = y;

		//recalculate dimensions of landscape
		setPreferredSize(new Dimension((maxX-minX)*128+128,(maxY-minY)*128+128));

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
}