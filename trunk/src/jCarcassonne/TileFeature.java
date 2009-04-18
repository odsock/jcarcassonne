package jCarcassonne;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class TileFeature
{
	protected static enum FeatureEnum { city, road, farm, cloister, river, empty }

	protected final FeatureEnum featureType;
	private int tokenX, tokenY;
	protected final Tile tile;
	protected final int indexColorCode;  //color in the feature map image for tile

	private Token token = null;
	private boolean scored = false;

	//list of features which can be traversed to/from.
	private ArrayList<TileFeature> neighbors; 
	private int numNeighbors = 0;
	private final int maxNeighbors;
	
	protected TileFeature(FeatureEnum f, int maxNeighbors, int tokenX, int tokenY, Tile tile, int indexColorCode)
	{
		featureType = f;
		this.maxNeighbors = maxNeighbors;
		this.tokenX = tokenX;
		this.tokenY = tokenY;
		this.tile = tile;
		this.indexColorCode = indexColorCode;
		neighbors = new ArrayList<TileFeature>();
	}

	//add a neighbor feature to the list
	//no need to ever remove a neighbor since tiles cannot be moved/removed once placed
	protected void addNeighbor(TileFeature tf)
	{
		neighbors.add(tf);
		numNeighbors++;
	}

	protected Iterator<TileFeature> getNeighborIterator()
	{
		return neighbors.iterator();
	}
	
	protected int getNumNeighbors()
	{
		return numNeighbors;
	}

	protected int getMaxNeighbors()
	{
		return maxNeighbors;
	}

	//set a token for this feature
	protected void placeToken(Token token)
	{
		this.token = token;
		token.setPlaced(true);
	}
	
	protected void removeToken()
	{
		this.token.setPlaced(false);
		this.token = null;
	}
	
	protected Token getToken()
	{
		return token;
	}
	
	protected boolean hasToken()
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

	protected void setScored(boolean scored)
	{
		this.scored = scored;
	}

	protected boolean isScored()
	{
		return scored;
	}

	protected Point getTokenCoordinates()
	{
		return new Point(tokenX, tokenY);
	}
	
	protected void setTokenCoordinates(int x, int y)
	{
		tokenX = x;
		tokenY = y;
	}

	protected Tile getTile()
	{
		return tile;
	}
	
	protected int getColorCode()
	{
		return indexColorCode;
	}

	//stub method, meant to be overridden
	protected abstract boolean isComplete();
	
	protected HashSet<Tile> getTilesInFeatureGroup()
	{
		HashSet<TileFeature> featuresInGroup = getFeaturesInGroup();
		HashSet<Tile> tilesInGroup = new HashSet<Tile>();
		
		for(TileFeature feature : featuresInGroup)
			tilesInGroup.add(feature.getTile());
		
		return tilesInGroup;
	}
	
	protected HashSet<Token> getTokensOnFeatureGroup()
	{
		HashSet<TileFeature> featuresInGroup = getFeaturesInGroup();
		HashSet<Token> tokensInGroup = new HashSet<Token>();
		
		for(TileFeature feature : featuresInGroup)
			if(feature.hasToken())
				tokensInGroup.add(feature.getToken());
		
		return tokensInGroup;
	}
	
	protected HashSet<TileFeature> getFeaturesInGroup()
	{
		return getFeaturesInGroup(this, new HashSet<TileFeature>());
	}
	private HashSet<TileFeature> getFeaturesInGroup(TileFeature feature, HashSet<TileFeature> featuresFound)
	{
		Iterator<TileFeature> neighborIterator = feature.getNeighborIterator();
		while(neighborIterator.hasNext())
		{
			TileFeature neighbor = neighborIterator.next();
			if(!featuresFound.contains(neighbor))
			{
				featuresFound.add(neighbor);
				getFeaturesInGroup(neighbor, featuresFound);
			}
		}
		
		return featuresFound;
	}
}
