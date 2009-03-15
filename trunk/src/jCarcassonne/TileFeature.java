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
	
	//shallow copy of tile TileFeature.  Skips neighbors.
	public TileFeature copy()
	{
		TileFeature tf = new TileFeature(featureType);
		tf.setFlag(flag);
		
		return tf;
	}
}
