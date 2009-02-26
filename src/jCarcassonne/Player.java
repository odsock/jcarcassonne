package jCarcassonne;

import java.awt.Color;

public class Player {
	private String name;
	private Color color;
	private int score = 0;
	private Token[] tokens = new Token[8];

	public Player(String name, Color color){
		this.name = name;
		this.color = color;

		for(int i = 0; i < tokens.length; i++)
			tokens[i] = new Token(this, null);
	}
	
	public String getName()
	{
		return name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Color getColor()
	{
		return color;
	}

	public Token[] getTokenList() {
		return tokens;
	}
}
