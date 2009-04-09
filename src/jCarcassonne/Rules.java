package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;

import java.util.HashMap;
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
		else if(n != null && 
				t.getFeatureAtBorder(Tile.NORTH).featureType != n.getFeatureAtBorder(Tile.SOUTH).featureType)
		{
			if(verbose)
				System.out.println("North border: " + t.getFeatureAtBorder(Tile.NORTH).featureType + " != " + n.getFeatureAtBorder(Tile.SOUTH).featureType);
			return false;
		}
		else if(s != null && 
				t.getFeatureAtBorder(Tile.SOUTH).featureType != s.getFeatureAtBorder(Tile.NORTH).featureType)
		{
			if(verbose)
				System.out.println("South border: " + t.getFeatureAtBorder(Tile.SOUTH).featureType + " != " + s.getFeatureAtBorder(Tile.NORTH).featureType);
			return false;
		}
		else if(e != null && 
				t.getFeatureAtBorder(Tile.EAST).featureType != e.getFeatureAtBorder(Tile.WEST).featureType)
		{
			if(verbose)
				System.out.println("East border: " + t.getFeatureAtBorder(Tile.EAST).featureType + " != " + e.getFeatureAtBorder(Tile.WEST).featureType);
			return false;
		}
		else if(w != null && 
				t.getFeatureAtBorder(Tile.WEST).featureType != w.getFeatureAtBorder(Tile.EAST).featureType)
		{
			if(verbose)
				System.out.println("West border: " + t.getFeatureAtBorder(Tile.WEST).featureType + " != " + w.getFeatureAtBorder(Tile.EAST).featureType);
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
				System.out.println("CheckTokenPlacement: not last tile");
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

		boolean isContested = isContestedFeature(featureClicked, player);
		if(isContested)
		{
			if(verbose)
				System.out.println("CheckTokenPlacement: feature is contested");
			return false;
		}
		else
		{
			if(verbose)
				System.out.println("CheckTokenPlacement: token OK");
			return true;
		}
	}

	private boolean isContestedFeature(TileFeature featureClicked, Player player)
	{
		HashSet<Token> tokensOnFeature = getTokensOnFeatureGroup(featureClicked);
		
		//if any other player has a token on the feature group, it is contested
		for(Token token : tokensOnFeature)
			if(token.getPlayer() != player)
				return true;
		
		return false;
	}

	private HashSet<Token> getTokensOnFeatureGroup(TileFeature f)
	{
		return getTokensOnFeature(f, new HashSet<TileFeature>());
	}
	private HashSet<Token> getTokensOnFeature(TileFeature f, HashSet<TileFeature> featuresChecked)
	{
		HashSet<Token> tokensFound = new HashSet<Token>();
		Iterator<TileFeature> featureIterator = f.getNeighborIterator();
		while(featureIterator.hasNext())
		{
			TileFeature neighbor = featureIterator.next();
			if(!featuresChecked.contains(neighbor))
			{
				featuresChecked.add(neighbor);
				if(neighbor.hasToken())
					tokensFound.add(neighbor.getToken());

				tokensFound.addAll(getTokensOnFeature(neighbor, featuresChecked));
			}
		}
		return tokensFound;
	}

	public void scoreTile(Tile tile)
	{
		Iterator<TileFeature> featureIterator = tile.getFeatureIterator();
		while(featureIterator.hasNext())
		{
			TileFeature feature = featureIterator.next();
			if(!feature.isScored() && feature.isComplete())
			{
				HashSet<Token> tokensOnFeature = getTokensOnFeatureGroup(feature);
				HashSet<Player> featureOwners = getFeatureOwners(tokensOnFeature);

				//score feature and update player scores
				int featureScore = scoreFeature(feature, true);
				for(Player player : featureOwners)
				{
					player.setScore(player.getScore() + featureScore);
					if(verbose)
						System.out.println("ScoreTile: " + player.getName() + " score=" + player.getScore());
				}
				
				//free placed tokens
				for(Token token : tokensOnFeature)
					token.getFeature().removeToken();
			}
		}
	}

	private int scoreFeature(TileFeature feature, boolean isComplete)
	{
		HashSet<Tile> tilesInFeature = feature.getTilesInFeature();
		
		if(feature.featureType == FeatureEnum.road)
			return isComplete ? tilesInFeature.size() * 2 : tilesInFeature.size();
		else if(feature.featureType == FeatureEnum.city)
			return isComplete ? tilesInFeature.size() * 2 : tilesInFeature.size();
		else if(feature.featureType == FeatureEnum.cloister)
			return tilesInFeature.size() + 1;
/*		else if(feature.featureType == FeatureEnum.farm)
			return scoreFarm(feature);*/
		else
			return 0; //other features do not score points
	}

	private HashSet<Player> getFeatureOwners(HashSet<Token> tokensOnFeature)
	{
		//count tokens for each player, determine maxTokenCount
		HashMap<Player, Integer> tokensPerPlayer = new HashMap<Player, Integer>();
		int maxTokenCount = 0;
		for(Token token : tokensOnFeature)
		{
			Player player = token.getPlayer();
			if(tokensPerPlayer.containsKey(player))
			{
				Integer tokenCount = tokensPerPlayer.get(player);
				tokenCount = new Integer(tokenCount.intValue() + 1);
				tokensPerPlayer.put(player, tokenCount);
			}
			else
				tokensPerPlayer.put(player, new Integer(1));

			if(tokensPerPlayer.get(player).intValue() > maxTokenCount)
				maxTokenCount = tokensPerPlayer.get(player).intValue();
		}

		//add players with the most tokens to featureOwners set
		HashSet<Player> featureOwners = new HashSet<Player>();
		for(Player player: tokensPerPlayer.keySet())
		{
			if(tokensPerPlayer.get(player).intValue() == maxTokenCount)
				featureOwners.add(player);
		}

		//return set of owners (more than one if a tie in token count)
		return featureOwners;
	}
}