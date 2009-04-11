package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;

public class TileFeatureFactory 
{
	public TileFeature newTileFeature(FeatureEnum featureType, int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode, String flag)
	{
		TileFeature feature = null;
		
		if(featureType == FeatureEnum.road)
		{
			Road roadFeature = new Road(maxNeighbors, tokenX, tokenY, tile, colorCode);
			
			feature = roadFeature;
		}
		else if(featureType == FeatureEnum.cloister)
			feature = new Cloister(maxNeighbors, tokenX, tokenY, tile, colorCode);
		else if(featureType == FeatureEnum.city)
		{
			City cityFeature = new City(maxNeighbors, tokenX, tokenY, tile, colorCode);
			if("pennant".equals(flag))
				cityFeature.setPennant(true);
			
			feature = cityFeature;
		}
		else if(featureType == FeatureEnum.farm)
		{
			feature = new Farm(maxNeighbors, tokenX, tokenY, tile, colorCode);
		}
			
		return feature;
	}
}