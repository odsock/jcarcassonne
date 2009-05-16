package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Rules
{
	private boolean verbose = false;
	private Landscape landscape;

	protected Rules(Landscape landscape)
	{
		this.landscape = landscape;
	}

	protected void setVerbose(boolean v)
	{
		verbose = v;
	}

	protected boolean checkTilePlacement(Landscape l, Tile t, int x, int y)
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
				System.out.println("North border: " + t.getFeatureAtBorder(Tile.NORTH).featureType 
						+ " != " + n.getFeatureAtBorder(Tile.SOUTH).featureType);
			return false;
		}
		else if(s != null && 
				t.getFeatureAtBorder(Tile.SOUTH).featureType != s.getFeatureAtBorder(Tile.NORTH).featureType)
		{
			if(verbose)
				System.out.println("South border: " + t.getFeatureAtBorder(Tile.SOUTH).featureType 
						+ " != " + s.getFeatureAtBorder(Tile.NORTH).featureType);
			return false;
		}
		else if(e != null && 
				t.getFeatureAtBorder(Tile.EAST).featureType != e.getFeatureAtBorder(Tile.WEST).featureType)
		{
			if(verbose)
				System.out.println("East border: " + t.getFeatureAtBorder(Tile.EAST).featureType 
						+ " != " + e.getFeatureAtBorder(Tile.WEST).featureType);
			return false;
		}
		else if(w != null && 
				t.getFeatureAtBorder(Tile.WEST).featureType != w.getFeatureAtBorder(Tile.EAST).featureType)
		{
			if(verbose)
				System.out.println("West border: " + t.getFeatureAtBorder(Tile.WEST).featureType 
						+ " != " + w.getFeatureAtBorder(Tile.EAST).featureType);
			return false;
		}
		else
			return true;
	}

	protected boolean checkTokenPlacement(Landscape landscape, Tile tileClicked, TileFeature featureClicked, Player player)
	{	
		boolean isOKPlacement = false;
		String verboseOutput = "";

		//check inputs
		if(landscape != null && tileClicked != null && featureClicked != null && player != null)
		{
			//can only place token on tile placed this turn
			if(tileClicked == landscape.getLastTilePlaced())
			{
				//can't place on contested features
				if(isUncontestedFeature(featureClicked, player))
				{
					verboseOutput = "CheckTokenPlacement: token OK";
					isOKPlacement = true;
				}
				else
					verboseOutput = "CheckTokenPlacement: feature is contested";
			}
			else
				verboseOutput = "CheckTokenPlacement: not last tile";
		}
		else
			verboseOutput = "CheckTokenPlacement: bad input";

		if(verbose)
			System.out.println(verboseOutput);

		return isOKPlacement;
	}

	protected void scoreTile(Tile tile)
	{
		if(tile != null)
		{
			Iterator<TileFeature> featureIterator = tile.getFeatureIterator();
			while(featureIterator.hasNext())
			{
				TileFeature feature = featureIterator.next();
				if(!feature.isScored() && feature.isComplete())
				{
					HashSet<Token> tokensOnFeature = feature.getTokensOnFeatureGroup();
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
					
					//if this was a city feature, add to landscape list of complete cities
					if(feature.featureType == FeatureEnum.city)
					{
						landscape.addCompleteCity(feature);
					}
				}
			}
		}
	}

	//scores the feature based on whether it is complete
	//returns 0 for farm features, which are scored by scoreFarm() at game end
	private int scoreFeature(TileFeature feature, boolean isComplete)
	{
		HashSet<Tile> tilesInFeature = feature.getTilesInFeatureGroup();
		int score = 0;
		
		if(feature.featureType == FeatureEnum.road)
			score = tilesInFeature.size();
		else if(feature.featureType == FeatureEnum.city)
		{
			score = tilesInFeature.size()+((City)feature).getNumPennants();
			if(isComplete && tilesInFeature.size() > 2)
				score *= 2;
		}
		else if(feature.featureType == FeatureEnum.cloister)
			score = landscape.getNumSurroundingTiles(feature.getTile()) + 1;

		feature.setScored(true);
		return score;
	}

	private boolean isUncontestedFeature(TileFeature featureClicked, Player player)
	{
		HashSet<Token> tokensOnFeature = featureClicked.getTokensOnFeatureGroup();

		//if any other player has a token on the feature group, it is contested
		for(Token token : tokensOnFeature)
			if(token.getPlayer() != player)
				return false;

		return true;
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

	//used to score claimed features at game end.
	//if these features were complete, they should have been freed by scoreTile already
	protected void scoreAllTokens(Iterator<Player> playersIterator)
	{
		//score all farms
		scoreFarms();
		
		//score other tokens
		while(playersIterator.hasNext())
		{
			Player player = playersIterator.next();
			Iterator<Token> tokenIterator = player.getTokenIterator();
			while(tokenIterator.hasNext())
			{
				Token token = tokenIterator.next();
				if(token.isPlaced())
				{
					int featureScore = scoreFeature(token.getFeature(), false);
					player.setScore(player.getScore() + featureScore);
					token.getFeature().removeToken();
				}
			}
		}

	}

	private void scoreFarms()
	{
		Iterator<TileFeature> completeCityIterator = landscape.getCompleteCitiesIterator();
		while(completeCityIterator.hasNext())
		{
			TileFeature completeCity = completeCityIterator.next();
			HashSet<TileFeature> cityGroup = completeCity.getFeaturesInGroup();
			HashSet<Token> tokensOnFarmNeighbors = new HashSet<Token>();
			for(TileFeature cityFeature : cityGroup)
			{
				Iterator<TileFeature> farmNeighborIterator = ((City)cityFeature).getFarmNeighborIterator();
				while(farmNeighborIterator.hasNext())
				{
					TileFeature farmNeighbor = farmNeighborIterator.next();
					HashSet<Token> tokensFound = farmNeighbor.getTokensOnFeatureGroup();
					tokensOnFarmNeighbors.addAll(tokensFound);
				}
			}
			
			//update score of overall farm owners
			HashSet<Player> farmOwners = getFeatureOwners(tokensOnFarmNeighbors);
			for(Player player : farmOwners)
			{
				player.setScore(player.getScore() + 4);
				if(verbose)
					System.out.println("ScoreTile: " + player.getName() + " score=" + player.getScore());
			}
		}
	}
}
