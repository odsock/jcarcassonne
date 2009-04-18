package jCarcassonne;

import java.awt.Color;

public class Token {
	private Player player;
	private Color color;
	
	//need stuff about what feature is claimed
	private boolean isPlaced;
	private TileFeature featureClaimed;
	
	protected Token(Player player){
		this.player = player;
		this.color = player.getColor();
	}
	
	protected final TileFeature getFeature() {
		return featureClaimed;
	}

	protected final void setFeature(TileFeature tf) {
		this.featureClaimed = tf;
	}
	
	public final Color getColor() {
		return color;
	}

	protected final Player getPlayer() {
		return player;
	}

	protected void setPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;
	}

	protected boolean isPlaced() {
		return isPlaced;
	}
}
