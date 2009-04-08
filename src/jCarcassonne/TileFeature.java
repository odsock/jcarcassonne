package jCarcassonne;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class TileFeature
{
	public static enum FeatureEnum { city, road, farm, cloister, river, empty }

	public final FeatureEnum featureType;
	private Point tokenCoordinates;  //not final, changes during tile rotation
	public final Tile tile;
	public final int colorCode;

	private Token token = null;
	private boolean scored = false;

	//list of features which can be traversed to/from.
	//doesn't know directions, could be an issue.
	private ArrayList<TileFeature> neighbors; 

	public TileFeature(FeatureEnum f, int tokenX, int tokenY, Tile tile, int colorCode)
	{
		featureType = f;
		tokenCoordinates = new Point(tokenX,tokenY);
		this.tile = tile;
		this.colorCode = colorCode;
		neighbors = new ArrayList<TileFeature>();
	}

	//add a neighbor feature to the list
	//no need to ever remove a neighbor since tiles cannot be moved/removed once placed
	public void addNeighbor(TileFeature tf)
	{
		neighbors.add(tf);
	}

	public Iterator<TileFeature> getNeighborIterator()
	{
		return neighbors.iterator();
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
		return colorCode;
	}

	//stub method, meant to be overridden once TileFeature is an abstract class
	public boolean isComplete()
	{
		return true;
	}
}
