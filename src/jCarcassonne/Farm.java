package jCarcassonne;

public class Farm extends TileFeature
{
	public Farm(int tokenX, int maxNeighbors, int tokenY, Tile tile, int colorCode)
	{
		super(TileFeature.FeatureEnum.farm, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}
	
	
}
