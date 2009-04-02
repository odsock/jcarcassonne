package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;

public class TileFeatureFactory 
{
	public TileFeature newTileFeature(FeatureEnum featureType, int tokenX, int tokenY, String flag)
	{
		TileFeature feature;
		
		if(featureType == FeatureEnum.road)
		{
			Road roadFeature = new Road(tokenX, tokenY);
			if("end".equals(flag))
				roadFeature.setEndPoint(true);
			
			feature = roadFeature;
		}
		else
		{
			feature = new TileFeature(featureType, tokenX, tokenY);
		}
			
		return feature;
	}
	
	public TileFeature newTileFeature(FeatureEnum featureType, int tokenX, int tokenY)
	{
		return new TileFeature(featureType, tokenX, tokenY);
	}
}