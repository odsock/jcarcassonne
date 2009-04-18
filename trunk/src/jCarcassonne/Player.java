package jCarcassonne;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class Player {
	private String name;
	private Color color;
	private int score = 0;
	private int maxTokens = 8;
	
	private ArrayList<Token> tokens = new ArrayList<Token>();

	protected Player(String name, Color color)
	{
		this.name = name;
		this.color = color;

		for(int i = 0; i < maxTokens; i++)
			tokens.add(new Token(this));
	}
	
	public String getName()
	{
		return name;
	}

	public int getScore()
	{
		return score;
	}

	protected void setScore(int score)
	{
		this.score = score;
	}

	public Color getColor()
	{
		return color;
	}

	//return token list for scoring
	protected Iterator<Token> getTokenIterator()
	{
		return tokens.iterator();
	}
	
	//get the next token available for placement
	protected Token getToken()
	{
		for(Token token : tokens)
			if(!token.isPlaced())
				return token;
		
		//return null if all tokens have been placed
		return null;
	}

	protected boolean hasToken()
	{
		if(getToken() != null)
			return true;
		else
			return false;
	}

	protected int getTokenCount()
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
