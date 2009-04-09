package jCarcassonne;

import java.awt.Color;

public class Player {
	private String name;
	private Color color;
	private int score = 0;
	private Token[] tokens = new Token[8];

	public Player(String name, Color color)
	{
		this.name = name;
		this.color = color;

		for(int i = 0; i < tokens.length; i++)
			tokens[i] = new Token(this);
	}
	
	public String getName()
	{
		return name;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public Color getColor()
	{
		return color;
	}

	//return token list for scoring
	public Token[] getTokenList()
	{
		return tokens;
	}
	
	//get the next token available for placement
	public Token getToken()
	{
		for(int i = 0; i < tokens.length; i++)
			if(!tokens[i].isPlaced())
				return tokens[i];
		
		//return null if all tokens have been placed
		return null;
	}

	public boolean hasToken()
	{
		if(getToken() != null)
			return true;
		else
			return false;
	}

	public int getTokenCount()
	{
		int tokenCount = 0;
		for(Token token : tokens)
		{
			if(!token.isPlaced())
				tokenCount++;
		}
		return tokenCount;
	}
}
