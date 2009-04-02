package jCarcassonne;

public class Road extends TileFeature {
	public Road(int tokenX, int tokenY) {
		super(TileFeature.FeatureEnum.road, tokenX, tokenY);
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
