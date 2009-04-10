package jCarcassonne;

import java.util.HashSet;

public class Cloister extends TileFeature
{
	public Cloister(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(FeatureEnum.cloister, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}
	
	@Override
	public boolean isComplete()
	{
		boolean isComplete = true;
		Tile tile = this.getTile();

		if(tile.getNorthTile() == null)
			isComplete = false;
		else if(tile.getSouthTile() == null)
			isComplete = false;
		else if(tile.getEastTile() == null)
			isComplete = false;
		else if(tile.getWestTile() == null)
			isComplete = false;
		
		return isComplete;
	}
	
	@Override
	public HashSet<Tile> getTilesInFeature()
	{
		HashSet<Tile> tilesInFeature = new HashSet<Tile>();
		Tile tile = this.getTile();

		Tile n = tile.getNorthTile();
		Tile s = tile.getSouthTile();
		Tile e = tile.getEastTile();
		Tile w = tile.getWestTile();
		
		if(n != null)
			tilesInFeature.add(n);
		if(s != null)
			tilesInFeature.add(s);
		else if(e != null)
			tilesInFeature.add(e);
		else if(w != null)
			tilesInFeature.add(w);
		
		return tilesInFeature;
	}
}