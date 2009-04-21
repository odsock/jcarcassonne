package jCarcassonne;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class GameController
{
	//game model elements
	private Rules rules;
	private TileStack tileStack;
	private Landscape landscape;
	private ArrayList<Player> players = new ArrayList<Player>();

	//turn control
	private Iterator<Player> playersIterator;
	private Player currentPlayer;
	private boolean tilePlacedThisTurn = false;
	private boolean gameOver = false;

	public GameController()
	{
		//create the game model
		tileStack = new TileStack();
		tileStack.setIgnoreCount(false);
		tileStack.loadTileSet("tileset.txt");
		tileStack.shuffleStack();
		
		landscape = new Landscape(tileStack.pop());
		
		rules = new Rules(landscape);
		rules.setVerbose(false);
		
		players.add(new Player("player1", Color.red));
		players.add(new Player("player2", Color.blue));
		playersIterator = players.iterator();
		currentPlayer = playersIterator.next();
	}

	public void endTurn()
	{
		if(!gameOver && tilePlacedThisTurn) //can't end turn without placing a tile
		{
			rules.scoreTile(landscape.getLastTilePlaced());
			tilePlacedThisTurn = false;

			if(playersIterator.hasNext())
				currentPlayer = playersIterator.next();
			else
			{
				playersIterator = players.iterator();
				currentPlayer = playersIterator.next();
			}
		}
	}
	
	public void endGame() {
		rules.scoreTile(landscape.getLastTilePlaced());
		tilePlacedThisTurn = false;
		gameOver = true;
		
		rules.scoreAllTokens(players.iterator());
	}

	public void rotateNextTile()
	{
		if(!gameOver && !tilePlacedThisTurn)
			tileStack.peek().rotate();
	}

	private void placeTile(int xInModel, int yInModel)
	{
		//place tile if rules allow
		if(!gameOver && !tilePlacedThisTurn && !tileStack.empty() && rules.checkTilePlacement(landscape, tileStack.peek(), xInModel, yInModel))
		{
			landscape.placeTile(tileStack.pop(), xInModel, yInModel);
			tilePlacedThisTurn = true;
		}
	}

	private void placeToken(int xInModel, int yInModel, int xInTile, int yInTile)
	{
		//place token if rules allow
		if(!gameOver && tilePlacedThisTurn)
		{
			Tile tileClicked = landscape.getTile(xInModel, yInModel);
			if(tileClicked != null)
			{
				TileFeature featureClicked = tileClicked.getFeatureAt(xInTile, yInTile);
				if(rules.checkTokenPlacement(landscape, tileClicked, featureClicked, currentPlayer))
				{
					if(tileClicked.hasToken())
						tileClicked.getToken().getFeature().removeToken();
					
					Token token = currentPlayer.getToken();
					tileClicked.placeToken(token, xInTile, yInTile);
				}
			}
		}
	}

	public void handleLandscapeClick(int xInModel, int yInModel, int xInTile, int yInTile)
	{
		//determine if tile or token attempt
		if(!tilePlacedThisTurn)
			placeTile(xInModel, yInModel);
		else
			placeToken(xInModel, yInModel, xInTile, yInTile);
	}

	public BufferedImage getNextTileImage()
	{
		if(!tileStack.empty())
			return tileStack.peek().getImage();
		else
			return null;
	}

	public String getCurrentPlayerName()
	{
		return currentPlayer.getName();
	}

	public int getCurrentPlayerScore()
	{
		return currentPlayer.getScore();
	}

	public int getCurrentPlayerTokenCount()
	{
		return currentPlayer.getTokenCount();
	}

	public Color getCurrentPlayerColor()
	{
		return currentPlayer.getColor();
	}

	public Iterator<Tile> getLandscapeIterator()
	{
		return landscape.getLandscapeIterator();
	}

	public Iterator<Player> getPlayersIterator()
	{
		return players.iterator();
	}
}
