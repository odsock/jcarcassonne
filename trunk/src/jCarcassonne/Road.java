package jCarcassonne;

import java.util.Iterator;

public class Road extends TileFeature
{
	public Road(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(TileFeature.FeatureEnum.road, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}

	private boolean isEndPoint;

	public void setEndPoint(boolean isEndPoint)
	{
		this.isEndPoint = isEndPoint;
	}

	public boolean isEndPoint()
	{
		return isEndPoint;
	}

	@Override
	public boolean isComplete()
	{
		System.out.println("Road.isComplete called");
		boolean firstHasEndPoint = false, secondHasEndPoint = false;
		int numNeighbors = getNumNeighbors();

		if(isEndPoint && numNeighbors == 1)
		{
			Iterator<TileFeature> neighborsIterator = this.getNeighborIterator();
			firstHasEndPoint = true;
			secondHasEndPoint = hasEndPoint(this, neighborsIterator.next());
		}
		else if(!isEndPoint && numNeighbors == 2)
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
