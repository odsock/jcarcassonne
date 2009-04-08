package jCarcassonne;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Landscape {
	//hash stores all the placed tiles, keyed on their coordinates relative to the start tile.
	private ConcurrentHashMap<Point,Tile> landscapeHash = new ConcurrentHashMap<Point,Tile>();

	//coordinates of the last tile placed
	private int lastX = 0;
	private int lastY = 0;
	private Tile lastTilePlaced;

	public Landscape(Tile startTile){
	//	landscapeHash.put(new Point(0,0), startTile);
	//	startTile.setXY(0,0);
		placeTile(startTile, 0,0);
	}

	public void placeTile(Tile tile, int x, int y)
	{		
		//add to coordinate map
		landscapeHash.put(new Point(x,y), tile);
		tile.setXY(x,y);

		//update last tile placement info
		lastX = x;
		lastY = y;
		lastTilePlaced = tile;
		
		//set placement flag in tile
		tile.setPlaced(true);
		
		//setup edge references
		Point p = new Point(x,y);
		p.translate(0,1);
		if(landscapeHash.containsKey(p)){
			tile.setNorthTile(landscapeHash.get(p));
			landscapeHash.get(p).setSouthTile(tile);
		}
		p.translate(0,-2);
		if(landscapeHash.containsKey(p)){
			tile.setSouthTile(landscapeHash.get(p));
			landscapeHash.get(p).setNorthTile(tile);
		}
		p.translate(1,1);
		if(landscapeHash.containsKey(p)){
			tile.setEastTile(landscapeHash.get(p));
			landscapeHash.get(p).setWestTile(tile);
		}
		p.translate(-2,0);
		if(landscapeHash.containsKey(p)){
			tile.setWestTile(landscapeHash.get(p));
			landscapeHash.get(p).setEastTile(tile);
		}
	}
	
	public void placeToken(Token token, int xInTile, int yInTile) {
		if(lastTilePlaced != null)
			lastTilePlaced.placeToken(token, xInTile, yInTile);
	}

	public Tile getTile(int x, int y){
		return landscapeHash.get(new Point(x,y));
	}
	
	public boolean hasTileAt(int x, int y){
		if(landscapeHash.containsKey(new Point(x,y)))
			return true;
		else 
			return false;
	}

	public Tile getLastTilePlaced() {
		return lastTilePlaced;
	}
	
	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public Iterator<Tile> getLandscapeIterator()
	{
		return landscapeHash.values().iterator();
	}
}