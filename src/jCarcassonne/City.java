package jCarcassonne;

import java.util.HashSet;

public class City extends TileFeature
{
	private final boolean hasPennant;

	protected City(int maxNeighbors, int tokenX, int tokenY, Tile tile, boolean hasPennant, int colorCode)
	{
		super(TileFeature.FeatureEnum.city, maxNeighbors, tokenX, tokenY, tile, colorCode);
		this.hasPennant = hasPennant;
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
