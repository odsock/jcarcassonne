package jCarcassonne;

import java.util.HashSet;
import java.util.Iterator;

public class Rules {
	private boolean verbose = false;

	public void setVerbose(boolean v)
	{
		verbose = v;
	}

	public boolean checkTilePlacement(Landscape l, Tile t, int x, int y)
	{
		//error check
		if(l.hasTileAt(x, y) || t == null)
			return false;

		Tile n = l.getTile(x, y+1);
		Tile s = l.getTile(x, y-1);
		Tile e = l.getTile(x+1, y);
		Tile w = l.getTile(x-1, y);

		if(n  == null && s == null && e == null && w == null)
			return false;
		else if(n != null && t.getNorthFeatureType() != n.getSouthFeatureType())
		{
			if(verbose)
				System.out.println("North border: " + t.getNorthFeatureType() + " != " + n.getSouthFeatureType());
			return false;
		}
		else if(s != null && t.getSouthFeatureType() != s.getNorthFeatureType())
		{
			if(verbose)
				System.out.println("South border: " + t.getSouthFeatureType() + " != " + s.getNorthFeatureType());
			return false;
		}
		else if(e != null && t.getEastFeatureType() != e.getWestFeatureType())
		{
			if(verbose)
				System.out.println("East border: " + t.getEastFeatureType() + " != " + e.getWestFeatureType());
			return false;
		}
		else if(w != null && t.getWestFeatureType() != w.getEastFeatureType())
		{
			if(verbose)
				System.out.println("West border: " + t.getWestFeatureType() + " != " + w.getEastFeatureType());
			return false;
		}
		else
			return true;
	}

	public boolean checkTokenPlacement(Landscape landscape, Player player, int x, int y, int xInTile, int yInTile)
	{	
		//check inputs
		if(landscape == null || player == null)
		{
			if(verbose)
				System.out.println("CheckTokenPlacement: null inputs");
			return false;
		}

		//check tile
		Tile tileClicked = landscape.getTile(x, y);
		if(tileClicked == null || tileClicked != landscape.getLastTilePlaced())
		{
			if(verbose)
				System.out.println("CheckTokenPlacement: null tile at " + x + "," + y);
			return false;
		}

		//check feature
		TileFeature featureClicked = tileClicked.getFeatureAt(xInTile, yInTile);
		if(featureClicked == null)
		{
			if(verbose)
				System.out.println("CheckTokenPlacement: null feature clicked");
			return false;
		}

		//check player
		if(!player.hasToken())
		{
			if(verbose)
				System.out.println("CheckTokenPlacement: player has no token");
			return false;
		}

		isContestedFeature(featureClicked, player);
	}

	//traverse tile feature neighbors to determine whether player p owns the feature group
	//return false if feature is contested at all(can't place on contested features)
	private boolean isContestedFeature(TileFeature featureClicked, Player player) {
		//keep list of features already checked to prevent cycle
		HashSet<TileFeature> featuresChecked = new HashSet<TileFeature>();

		return isContestedFeatureHelper(featureClicked, player, featuresChecked);
	}
	//recursive helper method for isContestedFeature
	private boolean isContestedFeatureHelper(TileFeature f, Player player, HashSet<TileFeature> featuresChecked)
	{
		Iterator<TileFeature> iterator = f.getNeighborIterator();
		while(iterator.hasNext())
		{
			TileFeature neighbor = iterator.next();
			if(!featuresChecked.contains(neighbor))
			{
				featuresChecked.add(neighbor);
				if(neighbor.hasToken() && neighbor.getToken().getPlayer() != player)
					return true;
				else
					return isContestedFeatureHelper(neighbor, player, featuresChecked);
			}
		}

		return false;
	}
}
