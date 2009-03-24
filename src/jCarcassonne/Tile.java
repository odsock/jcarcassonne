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

	//array numbered as clockwise tile borders from top left
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

	//rotate the tile 90 degrees clockwise
	//don't use this on a placed tile.
	public void rotate() {
		TileFeature f9 = tileFeatures[9];
		TileFeature f10 = tileFeatures[10];
		TileFeature f11 = tileFeatures[11];
		for(int i = 11; i >= 3; i--)
		{
			tileFeatures[i] = tileFeatures[i-3];
		}
		tileFeatures[2] = f11;
		tileFeatures[1] = f10;
		tileFeatures[0] = f9;

		//instantiate and apply affine transformation filter
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
	
	//return feature at border b, or center feature at tileFeatures[12]
	public TileFeature getFeature(int b) {
		return tileFeatures[b];
	}

	public void setNorthTile(Tile northTile) {
			this.northTile = northTile;
			TileFeature f8 = northTile.getFeature(8);
			TileFeature f7 = northTile.getFeature(7);
			TileFeature f6 = northTile.getFeature(6);
			tileFeatures[0].addNeighbor(f8);
			tileFeatures[1].addNeighbor(f7);
			tileFeatures[2].addNeighbor(f6);
	}

	public void setSouthTile(Tile southTile) {
			this.southTile = southTile;
			TileFeature f0 = southTile.getFeature(0);
			TileFeature f1 = southTile.getFeature(1);
			TileFeature f2 = southTile.getFeature(2);
			tileFeatures[8].addNeighbor(f0);
			tileFeatures[7].addNeighbor(f1);
			tileFeatures[6].addNeighbor(f2);
	}

	public void setEastTile(Tile eastTile) {
			this.eastTile = eastTile;
			TileFeature f11 = eastTile.getFeature(11);
			TileFeature f10 = eastTile.getFeature(10);
			TileFeature f9 = eastTile.getFeature(9);
			tileFeatures[3].addNeighbor(f11);
			tileFeatures[4].addNeighbor(f10);
			tileFeatures[5].addNeighbor(f9);
	}

	public void setWestTile(Tile westTile) {
			this.westTile = westTile;
			TileFeature f3 = westTile.getFeature(3);
			TileFeature f4 = westTile.getFeature(4);
			TileFeature f5 = westTile.getFeature(5);
			tileFeatures[11].addNeighbor(f3);
			tileFeatures[10].addNeighbor(f4);
			tileFeatures[9].addNeighbor(f5);
	}

	public Tile getNorthTile() {
		return northTile;
	}
	public Tile getSouthTile() {
		return southTile;
	}
	public Tile getEastTile() {
		return eastTile;
	}
	public Tile getWestTile() {
		return westTile;
	}

	public Point getPoint() {
		return new Point(x,y);
	}

	public void setPoint(Point p) { //convenience method
		x = p.x;
		y = p.y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Feature getNorthFeatureType() {
		//System.out.println(tileFeatures[1].featureType);
		return tileFeatures[1].featureType;
	}

	public Feature getSouthFeatureType() {
		//System.out.println(tileFeatures[7].featureType);
		return tileFeatures[7].featureType;
	}

	public Feature getEastFeatureType() {
		//System.out.println(tileFeatures[4].featureType);
		return tileFeatures[4].featureType;
	}

	public Feature getWestFeatureType() {
		//System.out.println(tileFeatures[10].featureType);
		return tileFeatures[10].featureType;
	}

	public Feature getCenterFeature() {
		//System.out.println(tileFeatures[12].featureType);
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
		String s = "";
		for(TileFeature f : tileFeatures)
		{
			if(f != null)
				s += f.toString() + " ";
		}

		return name + " " + x + " " + y + " : " + s;
	}
	
	//check tile for null features
	public String verifyFeatures()
	{
		String err = "";
		for(int i = 0; i < tileFeatures.length-1; i++ )
			if(tileFeatures[i] == null)
				err += "Error: " + name + " - null tileFeature at " + i + "\n";
		
		return err.equals("") ? null : err;
	}

	public TileFeature getFeatureAt(int px, int py) {
		//insert code to check feature map image here
		//then return feature at px,py
		return null;
	}
}