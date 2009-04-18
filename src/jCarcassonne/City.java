package jCarcassonne;

import java.util.HashSet;
import java.util.Iterator;

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
		return isComplete(this, new HashSet<TileFeature>());
	}
	private boolean isComplete(TileFeature feature, HashSet<TileFeature> featuresChecked)
	{			
		//System.out.println("   " + feature.getTile().name + " " + "numNeighbors=" + feature.getNumNeighbors() + " maxNeighbors=" + feature.getMaxNeighbors());

		boolean isComplete = true;
		if(feature.getNumNeighbors() != feature.getMaxNeighbors())
			isComplete = false;
		else
		{
			Iterator<TileFeature> neighborIterator = feature.getNeighborIterator();
			while(isComplete && neighborIterator.hasNext())
			{
				TileFeature neighbor = neighborIterator.next();
				if(!featuresChecked.contains(neighbor))
				{
					featuresChecked.add(neighbor);
					if(!isComplete(neighbor, featuresChecked))
						isComplete = false;
				}
			}
		}
		
		return isComplete;
	}

	protected boolean hasPennant() {
		return hasPennant;
	}
}
