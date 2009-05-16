package jCarcassonne;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class GameController
{
	//game model elements
	private Rules rules;
	private TileStack tileStack;
	private Landscape landscape;
	private ArrayList<Player> players;

	//turn control
	private Iterator<Player> playersIterator;
	private Player currentPlayer;
	private boolean tilePlacedThisTurn = false;

	//game control
	private boolean gameStarting = true; //game state for pre-game
	private boolean gameOver = false;  //game state for post-game

	public GameController()
	{
		newGame();
	}

	public void newGame()
	{
		//create the game model
		tileStack = new TileStack();
		tileStack.setIgnoreCount(false);
		tileStack.loadTileSet("tileset.txt");
		tileStack.shuffleStack();

		landscape = new Landscape(tileStack.pop());

		rules = new Rules(landscape);
		rules.setVerbose(false);

		players = new ArrayList<Player>();

		//turn control
		tilePlacedThisTurn = false;

		//game control
		gameStarting = true;
		gameOver = false;
	}

	public void addPlayer(String playerName, Color playerColor)
	{
		if(gameStarting && !gameOver)
			players.add(new Player(playerName, playerColor));
	}

	public void endTurn()
	{
		if(!gameOver && !gameStarting && tilePlacedThisTurn) //can't end turn without placing a tile
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

	public void startGame()
	{
		if(players.size() > 1)
		{
			gameStarting = false;
			gameOver = false;
			playersIterator = players.iterator();
			currentPlayer = playersIterator.next();
		}
	}

	public void endGame()
	{
		if(!gameStarting && !gameOver)
		{
			gameOver = true;
			rules.scoreTile(landscape.getLastTilePlaced());
			tilePlacedThisTurn = false;

			rules.scoreAllTokens(players.iterator());
		}
	}

	public void rotateNextTile()
	{
		if(!gameOver && !gameStarting && !tilePlacedThisTurn && !tileStack.empty())
			tileStack.peek().rotate();
	}

	private void placeTile(int xInModel, int yInModel)
	{
		//place tile if rules allow
		if(!gameOver && !gameStarting && !tilePlacedThisTurn && !tileStack.empty() && rules.checkTilePlacement(landscape, tileStack.peek(), xInModel, yInModel))
		{
			landscape.placeTile(tileStack.pop(), xInModel, yInModel);
			tilePlacedThisTurn = true;
		}
	}

	private void placeToken(int xInModel, int yInModel, int xInTile, int yInTile)
	{
		//place token if rules allow
		if(!gameOver && !gameStarting && tilePlacedThisTurn)
		{
			Tile tileClicked = landscape.getTile(xInModel, yInModel);
			if(tileClicked != null)
			{
				TileFeature featureClicked = tileClicked.getFeatureAt(xInTile, yInTile);
				if(rules.checkTokenPlacement(landscape, tileClicked, featureClicked, currentPlayer))
				{
					if(tileClicked.hasToken())
						tileClicked.getToken().getFeature().removeToken();

					if(currentPlayer.hasToken())
					{
						Token token = currentPlayer.getToken();
						tileClicked.placeToken(token, xInTile, yInTile);
					}
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
		if(currentPlayer != null)
			return currentPlayer.getName();
		else
			return null;
	}

	public int getCurrentPlayerTokenCount()
	{
		if(currentPlayer != null)
			return currentPlayer.getTokenCount();
		else
			return 0;
	}

	public Color getCurrentPlayerColor()
	{
		if(currentPlayer != null)
			return currentPlayer.getColor();
		else
			return null;
	}

	public Iterator<Tile> getLandscapeIterator()
	{
		return landscape.getLandscapeIterator();
	}

	public Iterator<Player> getPlayersIterator()
	{
		return players.iterator();
	}

	public boolean isGameOver()
	{
		return gameOver;
	}

	public boolean isGameStarting()
	{
		return gameStarting;
	}

	public Point getLastTileCoords()
	{
		return new Point(landscape.getLastX(), landscape.getLastY());
	}
}
