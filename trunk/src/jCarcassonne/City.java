package jCarcassonne;

import java.util.HashSet;
import java.util.Iterator;

public class City extends TileFeature
{
	public City(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(TileFeature.FeatureEnum.city, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}

	@Override
	public boolean isComplete()
	{
		return isComplete(this, new HashSet<TileFeature>());
	}
	private boolean isComplete(TileFeature feature, HashSet<TileFeature> featuresChecked)
	{
		if(this.getNumNeighbors() != this.getMaxNeighbors())
			return false;
		else
		{
			Iterator<TileFeature> neighborIterator = this.getNeighborIterator();
			while(neighborIterator.hasNext())
			{
				TileFeature neighbor = neighborIterator.next();
				if(!featuresChecked.contains(neighbor))
				{
					featuresChecked.add(neighbor);
					if(!isComplete(neighbor, featuresChecked))
						return false;
				}
			}
		}
		return true;
	}
}
