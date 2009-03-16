package jCarcassonne;

import java.util.ArrayList;

public class TileFeature {
	
	public static enum Feature { city, road, farm, cloister, river, empty}
	public final Feature featureType;
	private boolean flag;
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
	
	public ArrayList<TileFeature> getNeighbors()
	{
		return neighbors;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;		
	}
	
	public boolean getFlag() {
		return flag;
	}
	
	public String toString()
	{
		return featureType.toString();
	}
}
