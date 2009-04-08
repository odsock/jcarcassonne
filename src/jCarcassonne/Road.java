package jCarcassonne;

public class Road extends TileFeature {
	public Road(int tokenX, int tokenY, Tile tile, int colorCode) {
		super(TileFeature.FeatureEnum.road, tokenX, tokenY, tile, colorCode);
	}

	private boolean endPoint;
	
	public void setEndPoint(boolean endPoint)
	{
		this.endPoint = endPoint;
	}
	
	public boolean isEndPoint()
	{
		return endPoint;
	}
}
