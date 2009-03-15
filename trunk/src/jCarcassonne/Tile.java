package jCarcassonne;

import jCarcassonne.TileFeature.Feature;

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

	private Feature northFeature;
	private Feature southFeature;
	private Feature eastFeature;
	private Feature westFeature;
	private Feature centerFeature;

	//array numbered as clockwise tile borders from top left clockwise
	//tileFeatures[12] is center feature, only used for cloister
	private TileFeature[] tileFeatures = new TileFeature[13];


	//image for this tile
	private BufferedImage img;	

	//constructors
	public Tile(BufferedImage img, String name)
	{
		this.img = img;
		this.name = name;
	}

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
	//don't use this on a placed tile.
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

	//link feature to a tile border, or add center feature at tileFeatures[12]
	public void addFeature(TileFeature f, int b) {
		tileFeatures[b] = f;
	}
	//return feature at border b, or center tile at tileFeatures[12]
	public TileFeature getFeature(int b) {
		return tileFeatures[b];
	}

	public Tile getNorthTile() {
		return northTile;
	}

	public void setNorthTile(Tile northTile) {
		this.northTile = northTile;
		tileFeatures[0].addNeighbor(northTile.getFeature(8));
		tileFeatures[1].addNeighbor(northTile.getFeature(7));
		tileFeatures[2].addNeighbor(northTile.getFeature(6));
	}

	public Tile getSouthTile() {
		return southTile;
	}

	public void setSouthTile(Tile southTile) {
		this.southTile = southTile;
		tileFeatures[8].addNeighbor(northTile.getFeature(0));
		tileFeatures[7].addNeighbor(northTile.getFeature(1));
		tileFeatures[6].addNeighbor(northTile.getFeature(2));
	}

	public Tile getEastTile() {
		return eastTile;
	}

	public void setEastTile(Tile eastTile) {
		this.eastTile = eastTile;
		tileFeatures[3].addNeighbor(northTile.getFeature(11));
		tileFeatures[4].addNeighbor(northTile.getFeature(10));
		tileFeatures[5].addNeighbor(northTile.getFeature(9));
	}

	public Tile getWestTile() {
		return westTile;
	}

	public void setWestTile(Tile westTile) {
		this.westTile = westTile;
		tileFeatures[11].addNeighbor(northTile.getFeature(3));
		tileFeatures[10].addNeighbor(northTile.getFeature(4));
		tileFeatures[9].addNeighbor(northTile.getFeature(5));
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
		return tileFeatures[1].featureType;
	}

	public Feature getSouthFeature() {
		return tileFeatures[7].featureType;
	}

	public Feature getEastFeature() {
		return tileFeatures[4].featureType;
	}

	public Feature getWestFeature() {
		return tileFeatures[10].featureType;
	}

	public Feature getCenterFeature() {
		return tileFeatures[12].featureType;
	}

	//simple test token placement
	//work on this
	public void placeToken() {
		hasToken = true;
	}

	//simple test token check
	//needs to check a feature, not the tile
	public boolean hasToken() {
		return hasToken;
	}

	public String toString() {
		return name + " " + x + " " + y;
	}
}