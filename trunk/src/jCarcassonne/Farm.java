package jCarcassonne;

public class Farm extends TileFeature
{

	protected Farm(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(TileFeature.FeatureEnum.farm, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}

	@Override
	protected boolean isComplete()
	{
		//farm features are never complete
		return false;
	}
}
