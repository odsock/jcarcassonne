package jCarcassonne;

import java.util.HashSet;
import java.util.Iterator;

public class City extends TileFeature
{
	private boolean hasPennant = false;
	
	public City(int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		super(TileFeature.FeatureEnum.city, maxNeighbors, tokenX, tokenY, tile, colorCode);
	}

	@Override
	public boolean isComplete()
	{
		System.out.println("City.isComplete called");
		return isComplete(this, new HashSet<TileFeature>());
	}
	private boolean isComplete(TileFeature feature, HashSet<TileFeature> featuresChecked)
	{
		boolean isCompleteSoFar = false;
		
		if(this.getNumNeighbors() == this.getMaxNeighbors())
		{
			isCompleteSoFar = true;
			Iterator<TileFeature> neighborIterator = this.getNeighborIterator();
			while(isCompleteSoFar && neighborIterator.hasNext())
			{
				TileFeature neighbor = neighborIterator.next();
				if(!featuresChecked.contains(neighbor))
				{
					featuresChecked.add(neighbor);
					isCompleteSoFar = isComplete(neighbor, featuresChecked);
				}
			}
		}
		
		System.out.println("City.isComplete: numNeighbors=" + this.getNumNeighbors() + " maxNeighbors=" + this.getMaxNeighbors());
		
		return isCompleteSoFar;
	}

	public void setPennant(boolean hasPennant) {
		this.hasPennant = hasPennant;
	}

	public boolean hasPennant() {
		return hasPennant;
	}
}
