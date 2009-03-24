package jCarcassonne;

import java.util.ArrayList;

public class TileFeature {

	public static enum Feature { city, road, farm, cloister, river, empty }

	public final Feature featureType;

	private Token token;
	private boolean scored;

	//list of features which can be traversed to/from.
	//doesn't know directions, could be an issue.
	private ArrayList<TileFeature> neighbors; 

	public TileFeature(Feature f) {
		featureType = f;
		neighbors = new ArrayList<TileFeature>();
	}

	//add a neighbor feature to the list
	//no need to ever remove a neighbor since tiles cannot be moved/removed once placed
	public void addNeighbor(TileFeature tf) {
		neighbors.add(tf);
	}

	//probably should change this, bad to give out the whole list
	//will work fine until implementation of scoring
	public ArrayList<TileFeature> getNeighbors() {
		return neighbors;
	}

	//set a token for this feature
	//doesn't know or care about rules, just sets.
	public void setToken(Token token) {
		this.token = token;
	}
	
	public Token getToken() {
		return token;
	}
	
	public boolean hasToken()
	{
		if(token != null)
			return true;
		else
			return false;
	}

	public String toString() {
		return featureType.toString();
	}

	public void setScored(boolean scored) {
		this.scored = scored;
	}

	public boolean isScored() {
		return scored;
	}

	public boolean isContested(Player p) {
		//traverse tile feature neighbors to determine whether player p owns the feature group
		//return false if feature is contested at all(can't place on contested features)

		return false;  //for now
	}
}
