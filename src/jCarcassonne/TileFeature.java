package jCarcassonne;

import java.util.ArrayList;

public class TileFeature {
	private ArrayList<TileFeature> neighbors = new ArrayList<TileFeature>();
	
	public static enum Feature { city, road, farm, cloister, river, empty}
	public final Feature featureType;
	private boolean flag;
	
	public TileFeature(Feature f)
	{
		featureType = f;
	}

	public void addNeighbor(TileFeature tf)
	{
		neighbors.add(tf);
	}
	
	public ArrayList<TileFeature> getNeighbors()
	{
		return neighbors;
	}

	public void setFlag() {
		flag = true;		
	}
	
	public boolean getFlag() {
		return flag;
	}
}
