package jCarcassonne;

public class Cloister extends TileFeature
{
	protected Cloister(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(FeatureEnum.cloister, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}
	
	@Override
	protected boolean isComplete()
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
	
	protected int getNumSurroundingTiles()
	{
		Tile tile = this.getTile();

		Tile n = tile.getNorthTile();
		Tile s = tile.getSouthTile();
		Tile e = tile.getEastTile();
		Tile w = tile.getWestTile();
		
		int numSurroundingTiles = 0;
		if(n != null)
			numSurroundingTiles++;
		if(s != null)
			numSurroundingTiles++;
		else if(e != null)
			numSurroundingTiles++;
		else if(w != null)
			numSurroundingTiles++;
		
		return numSurroundingTiles;
	}
}