package jCarcassonne;

import java.awt.Color;

public class Token {
	private Player player;
	private Color color;
	private Tile tile;
	
	//need stuff about what feature is claimed
	private boolean placed;
	private TileFeature featureClaimed;
	
	public Token(Player player, Tile tile){
		this.tile = tile;
		this.player = player;
		this.color = player.getColor();
	}
	
	public final TileFeature getFeature() {
		return featureClaimed;
	}

	public final void setFeature(TileFeature tf) {
		this.featureClaimed = tf;
	}
	
	public final Color getColor() {
		return color;
	}

	public final Player getPlayer() {
		return player;
	}

	public void setPlaced(boolean placed) {
		this.placed = placed;
	}

	public boolean isPlaced() {
		return placed;
	}
}
