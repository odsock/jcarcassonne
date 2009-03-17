package jCarcassonne;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class Landscape {
	private ConcurrentHashMap<Point,Tile> landscapeHash = new ConcurrentHashMap<Point,Tile>();

	private int lastX = 0;
	private int lastY = 0;

	public Landscape(Tile startTile){
		placeTile(startTile,0,0);
	}

	public void paintLandscape(Graphics g)
	{
		for(Tile t : landscapeHash.values())
		{
			g.drawImage(t.getImage(),((t.x)*128),-(t.y)*128,null);
			if(t.hasToken())
			{
				g.setColor(Color.red);
				g.fillOval((t.x)*128+t.tokenX, -(t.y)*128+t.tokenY, 20, 20);
			}
		}
	}

	public void placeTile(Tile t, int x, int y){
		//error check
		if(t == null)
			System.out.println("Error: tried to place null tile.");
		
		//add to coordinate map
		landscapeHash.put(new Point(x,y), t);
		t.setXY(x,y);

		//update last placement
		lastX = x;
		lastY = y;
		
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

	public Tile getTile(int x, int y){
		return landscapeHash.get(new Point(x,y));
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void placeToken() {
		getTile(lastX, lastY).placeToken();
	}
}