package jCarcassonne;

import java.awt.Point;
import java.awt.image.*;

public class Tile {
	//name of the tile image file
	public final String name;
	
	//coordinates of this tile
	public int x;
	public int y;
	
	//token info
	
	//references to neighbor tiles
	private Tile northTile;
	private Tile southTile;
	private Tile eastTile;
	private Tile westTile;
	
	//details of this tile
	public static enum Feature { city, road, farm, cloister, river }
	public final Feature northFeature;
	public final Feature southFeature;
	public final Feature eastFeature;
	public final Feature westFeature;
	public final Feature centerFeature;
	
	//image file for this tile
	private BufferedImage img;	
	
	//constructor
	public Tile(Feature northFeature, Feature southFeature,
			Feature eastFeature, Feature westFeature, Feature centerFeature, 
			BufferedImage img, String name) {
		this.northFeature = northFeature;
		this.southFeature = southFeature;
		this.eastFeature = eastFeature;
		this.westFeature = westFeature;
		this.centerFeature = centerFeature;
		
		this.img = img;
		this.name = name;
	}
	
	public String toString()
	{
		return name + " " + x + " " + y;
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
	
	public Point getPoint() {
		return new Point(x,y);
	}

	public void setPoint(Point p) {
		x = p.x;
		y = p.y;
	}

	public BufferedImage getImage() {
		return img;
	}

	public void setXY(int i, int j) {
		x = i;
		y = j;
	}
}