package jCarcassonne;

import java.util.ArrayList;

public class TileFeature {
	
	public static enum Feature { city, road, farm, cloister, river, empty}
	
	public final Feature featureType;
	private boolean flag;
	
	//list of features which can be traversed to/from.
	//doesn't know directions, could be an issue.
	private ArrayList<TileFeature> neighbors; 
	
	public TileFeature(Feature f)
	{
		featureType = f;
		neighbors = new ArrayList<TileFeature>();
	}

	public void addNeighbor(TileFeature tf)
	{
		neighbors.add(tf);
	}
	
	//probably should change this, bad to give out the whole list
	//will work fine until implementation of scoring
	public ArrayList<TileFeature> getNeighbors()
	{
		return neighbors;
	}

	//flag is the little shield on the tiles
	public void setFlag(boolean flag) {
		this.flag = flag;		
	}
	
	public boolean hasFlag() {
		return flag;
	}
	
	public String toString()
	{
		return featureType.toString();
	}
}
