package jCarcassonne;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import javax.swing.JComponent;

public class Landscape extends JComponent{
	private Tile startTile;
	private Hashtable<Point,Tile> landscape = new Hashtable<Point,Tile>();
	private int minX = 0;
	private int maxX = 0;
	private int minY = 0;
	private int maxY = 0;

	public Landscape(Tile startTile){
		landscape.put(new Point(0,0), startTile);
		this.startTile = startTile;
	}

	public void paint(Graphics g)
	{
		System.out.println("paint: " + landscape.toString());
		for(int i = minX; i <= maxX; i++)
			for(int j = maxY; j >= minY; j--)
			{
				Tile temp = landscape.get(new Point(i,j));
				System.out.print("checking " + i + " " + j);
				if(temp != null) 
				{
					System.out.println(": OK");
					BufferedImage img = temp.getImage();
					g.drawImage(img,i+minX,-(j-maxY),null);
				}
				else
					System.out.println(": NULL");
			}
	}

	public Tile getStartTile(){
		return startTile;
	}

	public Tile getTile(Point p){
		return landscape.get(p);
	}

	public void placeTile(Tile t, Point p){
		//add to coordinate map
		landscape.put(p, t);
		System.out.println("placeTile: " + landscape.toString());

		//update min/max coordinates
		if(p.x > maxX) maxX = p.x;
		if(p.x < minX) minX = p.x;
		if(p.y > maxY) maxY = p.y;
		if(p.y < minY) minY = p.y;

		//recalculate dimensions of landscape
		setPreferredSize(new Dimension((maxX-minX)*128+128,(maxY-minY)*128+128));

		//setup edge references
		p.translate(0,1);
		if(landscape.containsKey(p)){
			t.setNorthTile(landscape.get(p));
			t.getNorthTile().setSouthTile(t);
		}
		p.translate(0,-2);
		if(landscape.containsKey(p)){
			t.setSouthTile(landscape.get(p));
			t.getSouthTile().setNorthTile(t);
		}
		p.translate(1,1);
		if(landscape.containsKey(p)){
			t.setEastTile(landscape.get(p));
			t.getEastTile().setWestTile(t);
		}
		p.translate(-2,0);
		if(landscape.containsKey(p)){
			t.setWestTile(landscape.get(p));
			t.getWestTile().setEastTile(t);
		}
	}

	public int getMinX() {
		return minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}
}