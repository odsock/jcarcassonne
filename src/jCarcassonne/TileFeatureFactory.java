package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;

public class TileFeatureFactory 
{
	public TileFeature newTileFeature(FeatureEnum featureType, int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode, String flag)
	{
		TileFeature feature;
		
		if(featureType == FeatureEnum.road)
		{
			Road roadFeature = new Road(maxNeighbors, tokenX, tokenY, tile, colorCode);
			if("end".equals(flag))
				roadFeature.setEndPoint(true);
			
			feature = roadFeature;
		}
		else if(featureType == FeatureEnum.cloister)
			feature = new Cloister(maxNeighbors, tokenX, tokenY, tile, colorCode);
		else
		{
			feature = new TileFeature(featureType, maxNeighbors, tokenX, tokenY, tile, colorCode);
		}
			
		return feature;
	}
}