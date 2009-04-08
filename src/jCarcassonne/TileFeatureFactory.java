package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;

public class TileFeatureFactory 
{
	public TileFeature newTileFeature(FeatureEnum featureType, int tokenX, int tokenY, Tile tile, int colorCode, String flag)
	{
		TileFeature feature;
		
		if(featureType == FeatureEnum.road)
		{
			Road roadFeature = new Road(tokenX, tokenY, tile, colorCode);
			if("end".equals(flag))
				roadFeature.setEndPoint(true);
			
			feature = roadFeature;
		}
		else
		{
			feature = new TileFeature(featureType, tokenX, tokenY, tile, colorCode);
		}
			
		return feature;
	}
	
	public TileFeature newTileFeature(FeatureEnum featureType, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		return new TileFeature(featureType, tokenX, tokenY, tile, colorCode);
	}
}