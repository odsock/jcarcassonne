package jCarcassonne;

import java.awt.Color;

public class Token {
	private Player player;
	private Color color;
	private Tile tile;
	
	//need stuff about what feature is claimed
	
	public Token(Player player, Tile tile){
		this.tile = tile;
		this.player = player;
		this.color = player.getColor();
	}
	
	public final Tile getTile() {
		return tile;
	}

	public final void setTile(Tile tile) {
		this.tile = tile;
	}
	
	public final Color getColor() {
		return color;
	}

	public final Player getPlayer() {
		return player;
	}

}
