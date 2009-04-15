package jCarcassonne;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Farm extends TileFeature
{
	ArrayList<Integer> cityNeighborColorCodes = new ArrayList<Integer>();
	ArrayList<TileFeature> cityNeighbors = null;
	
	public Farm(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(TileFeature.FeatureEnum.farm, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}

	@Override
	public boolean isComplete() {
		//farm features are never complete
		return false;
	}

	public void addCityNeighbor(int cityColorCode) {
		cityNeighborColorCodes.add(cityColorCode);
	}
	
	public boolean hasCityNeighbors()
	{
		return !cityNeighborColorCodes.isEmpty();
	}
	
	public Iterator<TileFeature> getCityNeighborIterator()
	{
		//cityNeighbors is null at construction because tile must be complete to lookup features by colorCode
		if(cityNeighbors == null)
		{
			cityNeighbors = new ArrayList<TileFeature>();
			for(int colorCode : cityNeighborColorCodes)
				cityNeighbors.add(this.tile.getFeatureByColorCode(colorCode));
		}
		
		return cityNeighbors.iterator();
	}
	
	public int getNumCompleteCityNeighbors()
	{
		HashSet<TileFeature> farmFeatureGroup = this.getFeaturesInGroup();
		
		int numCompleteCityNeighbors = 0;
		for(TileFeature feature : farmFeatureGroup)
		{
			Farm farmFeature = (Farm) feature;
			if(farmFeature.hasCityNeighbors())
			{
				Iterator<TileFeature> cityNeighborIterator = farmFeature.getCityNeighborIterator();
				while(cityNeighborIterator.hasNext())
				{
					City cityNeighbor = (City) cityNeighborIterator.next();
					if(cityNeighbor.isComplete())
						numCompleteCityNeighbors++;
				}
			}
		}
		
		return numCompleteCityNeighbors;
	}
}
