package jCarcassonne;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class TileFeature
{
	public static enum FeatureEnum { city, road, farm, cloister, river, empty }

	public final FeatureEnum featureType;
	private Point tokenCoordinates;  //not final, changes during tile rotation
	public final Tile tile;
	public final int indexColorCode;  //color in the feature map image for tile

	private Token token = null;
	private boolean scored = false;

	//list of features which can be traversed to/from.
	private ArrayList<TileFeature> neighbors; 
	private int numNeighbors = 0;
	private final int maxNeighbors;
	
	public TileFeature(FeatureEnum f, int maxNeighbors, int tokenX, int tokenY, Tile tile, int indexColorCode)
	{
		featureType = f;
		this.maxNeighbors = maxNeighbors;
		tokenCoordinates = new Point(tokenX,tokenY);
		this.tile = tile;
		this.indexColorCode = indexColorCode;
		neighbors = new ArrayList<TileFeature>();
	}

	//add a neighbor feature to the list
	//no need to ever remove a neighbor since tiles cannot be moved/removed once placed
	public void addNeighbor(TileFeature tf)
	{
		neighbors.add(tf);
		numNeighbors++;
	}

	public Iterator<TileFeature> getNeighborIterator()
	{
		return neighbors.iterator();
	}
	
	public int getNumNeighbors()
	{
		return numNeighbors;
	}

	//set a token for this feature
	public void placeToken(Token token)
	{
		this.token = token;
		token.setPlaced(true);
	}
	
	public void removeToken()
	{
		this.token.setPlaced(false);
		this.token = null;
	}
	
	public Token getToken()
	{
		return token;
	}
	
	public boolean hasToken()
	{
		if(token != null)
			return true;
		else
			return false;
	}

	@Override
	public String toString()
	{
		return featureType.toString();
	}

	public void setScored(boolean scored)
	{
		this.scored = scored;
	}

	public boolean isScored()
	{
		return scored;
	}

	public Point getTokenCoordinates()
	{
		return tokenCoordinates;
	}
	
	public void setTokenCoordinates(Point tokenCoordinates)
	{
		this.tokenCoordinates = tokenCoordinates;
	}

	public Tile getTile()
	{
		return tile;
	}
	
	public int getColorCode()
	{
		return indexColorCode;
	}

	//stub method, meant to be overridden
	public boolean isComplete()
	{
		System.out.println("TileFeature.isComplete called");
		return false;
	}
	
	public HashSet<Tile> getTilesInFeature()
	{
		return getTilesInFeature(this, new HashSet<TileFeature>());
	}
	private HashSet<Tile> getTilesInFeature(TileFeature f, HashSet<TileFeature> featuresChecked)
	{
		HashSet<Tile> tilesFound = new HashSet<Tile>();
		Iterator<TileFeature> featureIterator = f.getNeighborIterator();
		while(featureIterator.hasNext())
		{
			TileFeature neighbor = featureIterator.next();
			if(!featuresChecked.contains(neighbor))
			{
				featuresChecked.add(neighbor);
				tilesFound.add(neighbor.getTile());

				tilesFound.addAll(getTilesInFeature(neighbor, featuresChecked));
			}
		}
		return tilesFound;
	}

	public int getMaxNeighbors()
	{
		return maxNeighbors;
	}
}
