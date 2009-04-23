package jCarcassonne;

import java.util.Iterator;

public class Road extends TileFeature
{
	protected Road(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(TileFeature.FeatureEnum.road, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}

	protected boolean isEndPoint()
	{
		if(getMaxNeighbors() == 1)
			return true;
		else
			return false;
	}

	@Override
	protected boolean isComplete()
	{
		boolean firstHasEndPoint = false, secondHasEndPoint = false;
		int numNeighbors = getNumNeighbors();

		if(isEndPoint() && numNeighbors == 1)
		{
			firstHasEndPoint = true;
			Iterator<TileFeature> neighborsIterator = this.getNeighborIterator();
			secondHasEndPoint = hasEndPoint(this, neighborsIterator.next());
		}
		else if(!isEndPoint() && numNeighbors == 2)
		{
			Iterator<TileFeature> neighborsIterator = this.getNeighborIterator();
			firstHasEndPoint = hasEndPoint(this, neighborsIterator.next());
			secondHasEndPoint = hasEndPoint(this, neighborsIterator.next());
		}

		return firstHasEndPoint && secondHasEndPoint;
	}

	//recursively travels down road either finding an end point or a Road with no neighbor other than featureFrom
	private boolean hasEndPoint(TileFeature featureFrom, TileFeature featureTo)
	{
		if(((Road)featureTo).isEndPoint())
			return true;
		else
		{
			Iterator<TileFeature> neighborIterator = featureTo.getNeighborIterator();
			while(neighborIterator.hasNext())
			{
				TileFeature neighbor = neighborIterator.next();
				if(neighbor == featureFrom)
					continue;
				else
					return hasEndPoint(featureTo, neighbor);
			}
		}
		
		return false;
	}
}
