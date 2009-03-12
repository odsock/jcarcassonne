package jCarcassonne;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class Tile {
	//name of the tile image file
	public final String name;
	
	//coordinates of this tile
	public int x;
	public int y;
	
	//token info
	private boolean hasToken = false;
	public final int tokenX = 30;
	public final int tokenY = 40;
	
	//references to neighbor tiles
	private Tile northTile;
	private Tile southTile;
	private Tile eastTile;
	private Tile westTile;
	
	//details of this tile
	
	public static enum Feature { city, road, farm, cloister, river, empty}
	private Feature northFeature;
	private Feature southFeature;
	private Feature eastFeature;
	private Feature westFeature;
	private Feature centerFeature;
	
	//image for this tile
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

	//rotate the tile 90 degrees clockwise
	//only changes the features and references, don't use this on a placed tile.
	public void rotate() {
		Feature tempf = northFeature;
		northFeature = westFeature;
		westFeature = southFeature;
		southFeature = eastFeature;
		eastFeature = tempf;

		// instantiate and apply affine transformation filter
		AffineTransform at = new AffineTransform();
		at.translate(64, 64);
		at.rotate(Math.toRadians(90));
		at.translate(-64, -64);
	    BufferedImageOp bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

	    img = bio.filter(img, null);
	}
	
	public BufferedImage getImage() {
		return img;
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

	public void setXY(int i, int j) {
		x = i;
		y = j;
	}

	public Feature getNorthFeature() {
		return northFeature;
	}

	public Feature getSouthFeature() {
		return southFeature;
	}

	public Feature getEastFeature() {
		return eastFeature;
	}

	public Feature getWestFeature() {
		return westFeature;
	}

	public Feature getCenterFeature() {
		return centerFeature;
	}
	
	public void placeToken()
	{
		hasToken = true;
	}
	
	public boolean hasToken()
	{
		return hasToken;
	}
}