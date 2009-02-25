package javaCarcassonne;

public class Tile {
	//references to neighbor tiles
	private Tile northTile;
	private Tile southTile;
	private Tile eastTile;
	private Tile westTile;
	
	//details of this tile
	static enum Feature { CITY, ROAD, RIVER, FARM, CLOISTER }
	private Feature northFeature;
	private Feature southFeature;
	private Feature eastFeature;
	private Feature westFeature;
	private Feature centerFeature;
	
	//image file for this tile
	//fill this in!
	
	public Tile(Feature northFeature, Feature southFeature,
			Feature eastFeature, Feature westFeature, Feature centerFeature) {
		this.northFeature = northFeature;
		this.southFeature = southFeature;
		this.eastFeature = eastFeature;
		this.westFeature = westFeature;
		this.centerFeature = centerFeature;
	}

	public Tile getNorthTile() {
		return northTile;
	}

	public void setNorthTile(Tile northTile) {
		this.northTile = northTile;
	}

	public Tile getSouthTile() {
		return southTile;
	}

	public void setSouthTile(Tile southTile) {
		this.southTile = southTile;
	}

	public Tile getEastTile() {
		return eastTile;
	}

	public void setEastTile(Tile eastTile) {
		this.eastTile = eastTile;
	}

	public Tile getWestTile() {
		return westTile;
	}

	public void setWestTile(Tile westTile) {
		this.westTile = westTile;
	}
	
	
}