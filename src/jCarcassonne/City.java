package jCarcassonne;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class City extends TileFeature
{
	private final boolean hasPennant;
	private ArrayList<Integer> farmNeighborColorCodes = new ArrayList<Integer>();
	private ArrayList<TileFeature> farmNeighbors = null;

	protected City(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode, boolean hasPennant, int...farmColorCodes)
	{
		super(TileFeature.FeatureEnum.city, maxNeighbors, tokenX, tokenY, tile, colorCode);
		this.hasPennant = hasPennant;
		
		for(int farmColorCode : farmColorCodes)
			farmNeighborColorCodes.add(farmColorCode);
	}
	
	protected boolean hasFarmNeighbors()
	{
		return !farmNeighborColorCodes.isEmpty();
	}

	protected Iterator<TileFeature> getFarmNeighborIterator()
	{
		//farmNeighbors is null at construction because tile must be complete to lookup features by colorCode
		if(farmNeighbors == null)
		{
			farmNeighbors = new ArrayList<TileFeature>();
			for(int colorCode : farmNeighborColorCodes)
				farmNeighbors.add(this.tile.getFeatureByColorCode(colorCode));
		}

		return farmNeighbors.iterator();
	}

	protected HashSet<TileFeature> getFarmNeighborsInGroup()
	{
		HashSet<TileFeature> cityFeatureGroup = this.getFeaturesInGroup();
		HashSet<TileFeature> farmNeighborsInGroup = new HashSet<TileFeature>();
		for(TileFeature feature : cityFeatureGroup)
		{
			City farmFeature = (City) feature;
			Iterator<TileFeature> farmNeighborIterator = farmFeature.getFarmNeighborIterator();
			while(farmNeighborIterator.hasNext())
			{
				farmNeighborsInGroup.add(farmNeighborIterator.next());
			}
		}

		return farmNeighborsInGroup;
	}

	@Override
	protected boolean isComplete()
	{
		if(isComplete)  //avoid traversing feature group if known complete
			return true;
		else
		{
			HashSet<TileFeature> featuresInGroup = this.getFeaturesInGroup();
			boolean allComplete = true;
			for(TileFeature feature : featuresInGroup)
				if(feature.getNumNeighbors() != feature.getMaxNeighbors())
					allComplete = false;
			
			if(allComplete)
				for(TileFeature feature : featuresInGroup)
					feature.setComplete(true);
			
			return allComplete;
		}
	}

	protected boolean hasPennant()
	{
		return hasPennant;
	}

	protected int getNumPennants()
	{
		int numPennants = 0;
		HashSet<TileFeature> featuresInGroup = this.getFeaturesInGroup();
		for(TileFeature feature : featuresInGroup)
			if(((City)feature).hasPennant())
				numPennants++;

		return numPennants;
	}
}
