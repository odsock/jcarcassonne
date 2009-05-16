package jCarcassonne;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Landscape {
	//hash stores all the placed tiles, keyed on their coordinates relative to the start tile.
	private ConcurrentHashMap<Point,Tile> landscapeHash = new ConcurrentHashMap<Point,Tile>();
	
	//list of all complete cities.  Needed to ease farm scoring.
	private HashSet<TileFeature> completeCities = new HashSet<TileFeature>();

	//coordinates of the last tile placed
	private int lastX = 0;
	private int lastY = 0;
	private Tile lastTilePlaced;

	protected Landscape(Tile startTile)
	{
		landscapeHash.put(new Point(0,0), startTile);
		startTile.setXY(0,0);
	}

	protected void placeTile(Tile tile, int x, int y)
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

	protected Tile getTile(int x, int y){
		return landscapeHash.get(new Point(x,y));
	}
	
	protected boolean hasTileAt(int x, int y){
		if(landscapeHash.containsKey(new Point(x,y)))
			return true;
		else 
			return false;
	}

	protected Tile getLastTilePlaced() {
		return lastTilePlaced;
	}
	
	protected int getLastX() {
		return lastX;
	}

	protected int getLastY() {
		return lastY;
	}

	protected Iterator<Tile> getLandscapeIterator()
	{
		return landscapeHash.values().iterator();
	}

	protected int getNumSurroundingTiles(Tile tile)
	{
		Point p = tile.getPoint();
		int x = p.x;
		int y = p.y;
		
		int numSurroundingTiles = 0;
		if(hasTileAt(x, y-1))
			numSurroundingTiles++;
		if(hasTileAt(x+1, y-1))
			numSurroundingTiles++;
		if(hasTileAt(x+1, y))
			numSurroundingTiles++;
		if(hasTileAt(x+1, y+1))
			numSurroundingTiles++;
		if(hasTileAt(x, y+1))
			numSurroundingTiles++;
		if(hasTileAt(x-1, y+1))
			numSurroundingTiles++;
		if(hasTileAt(x-1, y))
			numSurroundingTiles++;
		if(hasTileAt(x-1, y-1))
			numSurroundingTiles++;
		
		return numSurroundingTiles;
	}
	
	protected void addCompleteCity(TileFeature cityFeature)
	{
		completeCities.add(cityFeature);
	}
	
	protected Iterator<TileFeature> getCompleteCitiesIterator()
	{
		return completeCities.iterator();
	}
}