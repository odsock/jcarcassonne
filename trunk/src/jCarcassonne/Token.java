package jCarcassonne;

import java.awt.Color;

public class Token {
	private Player player;
	private Color color;
	
	//need stuff about what feature is claimed
	private boolean isPlaced;
	private TileFeature featureClaimed;
	
	public Token(Player player){
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

	public void setPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;
	}

	public boolean isPlaced() {
		return isPlaced;
	}
}
