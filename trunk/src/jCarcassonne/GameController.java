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

	public GameController()
	{
		//create the game model
		rules = new Rules();
		rules.setVerbose(true);
		tileStack = new TileStack();
		tileStack.setIgnoreCount(true);  //testing aid, load only one of each tile
		tileStack.loadTileSet("tileset.txt");
		//tileStack.shuffleStack();
		landscape = new Landscape(tileStack.pop());
		players.add(new Player("player1", Color.red));
		players.add(new Player("player2", Color.blue));
		playersIterator = players.iterator();
		currentPlayer = playersIterator.next();
	}

	public void endTurn()
	{
		if(tilePlacedThisTurn) //can't end turn without placing a tile
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

	public void rotateNextTile()
	{
		if(!tilePlacedThisTurn)
			tileStack.peek().rotate();
	}
	
	private void placeTile(int xInModel, int yInModel)
	{
		//place tile if rules allow
		if(!tilePlacedThisTurn && rules.checkTilePlacement(landscape, tileStack.peek(), xInModel, yInModel))
		{
			landscape.placeTile(tileStack.pop(), xInModel, yInModel);
			tilePlacedThisTurn = true;
		}
	}
	
	private void placeToken(int xInModel, int yInModel, int xInTile, int yInTile)
	{
		//place token if rules allow
		if(tilePlacedThisTurn && rules.checkTokenPlacement(landscape, currentPlayer, xInModel, yInModel, xInTile, yInTile))
		{
			Token token = currentPlayer.getToken();
			landscape.getLastTilePlaced().placeToken(token, xInTile, yInTile);
			endTurn();
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
}
