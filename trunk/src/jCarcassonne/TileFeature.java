package jCarcassonne;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class TileFeature
{

	public static enum FeatureEnum { city, road, farm, cloister, river, empty }

	public final FeatureEnum featureType;
	public final Tile tile;

	private Token token;
	private Point tokenCoordinates;
	private boolean scored;

	//list of features which can be traversed to/from.
	//doesn't know directions, could be an issue.
	private ArrayList<TileFeature> neighbors; 

	public TileFeature(FeatureEnum f, int tokenX, int tokenY, Tile tile)
	{
		featureType = f;
		tokenCoordinates = new Point(tokenX,tokenY);
		this.tile = tile;
		neighbors = new ArrayList<TileFeature>();
	}

	//add a neighbor feature to the list
	//no need to ever remove a neighbor since tiles cannot be moved/removed once placed
	public void addNeighbor(TileFeature tf)
	{
		neighbors.add(tf);
	}

	//probably should change this, bad to give out the whole list
	//will work fine until implementation of scoring
	public Iterator<TileFeature> getNeighborIterator()
	{
		return neighbors.iterator();
	}

	//set a token for this feature
	public void setToken(Token token)
	{
		this.token = token;
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

	public Tile getTile()
	{
		return tile;
	}
}
