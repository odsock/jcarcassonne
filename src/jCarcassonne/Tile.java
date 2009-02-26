package jCarcassonne;

import java.awt.Point;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

public class Tile {
	//coordinates of this tile
	private Point p;
	
	//token info
	
	//references to neighbor tiles
	private Tile northTile;
	private Tile southTile;
	private Tile eastTile;
	private Tile westTile;
	
	//details of this tile
	public static enum Feature { city, road, farm, cloister, river }
	private Feature northFeature;
	private Feature southFeature;
	private Feature eastFeature;
	private Feature westFeature;
	private Feature centerFeature;
	
	//image file for this tile
	private BufferedImage img;	
	
	//constructor
	public Tile(Feature northFeature, Feature southFeature,
			Feature eastFeature, Feature westFeature, Feature centerFeature, 
			BufferedImage img) {
		this.northFeature = northFeature;
		this.southFeature = southFeature;
		this.eastFeature = eastFeature;
		this.westFeature = westFeature;
		this.centerFeature = centerFeature;
		
		this.img = img;
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
	
	public final Point getCoordinates() {
		return p;
	}

	public final void setCoordinates(Point p) {
		this.p = p;
	}

	public BufferedImage getImage() {
		return img;
	}
}