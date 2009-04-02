package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class Tile {
	//name of the tile image file
	public final String name;
	
	//image for this tile
	private BufferedImage img;

	//coordinates of this tile
	private int x = Integer.MAX_VALUE;  //initialized out of range
	private int y = Integer.MAX_VALUE;
	private boolean isPlaced = false;

	//coordinates of token on this tile
	//arbitrary for now, needs to be set by token placement on a feature
	public final int tokenX = 30;
	public final int tokenY = 40;

	//references to neighbor tiles
	private Tile northTile = null;
	private Tile southTile = null;
	private Tile eastTile = null;
	private Tile westTile = null;

	//array numbered as clockwise tile borders from top left
	//tileFeatures[12] is center feature, only used for cloister
	private TileFeature[] tileFeatures = new TileFeature[13];
	private final int NNW = 0; 
	private final int NORTH = 1;
	private final int NNE = 2;
	private final int ENE = 3;
	private final int EAST = 4;
	private final int ESE = 5;
	private final int SSE = 6;
	private final int SOUTH = 7;
	private final int SSW = 8;
	private final int WSW = 9;
	private final int WEST = 10;
	private final int WNW = 11;
	private final int CENTER = 12; 

	//constructor
	public Tile(BufferedImage img, String name) {
		this.img = img;
		this.name = name;
	}	

	//rotate the tile 90 degrees clockwise
	public void rotate()
	{
		//can't rotate a placed tile
		if(isPlaced)
			return;
		
		TileFeature wswFeature = tileFeatures[WSW];
		TileFeature westFeature = tileFeatures[WEST];
		TileFeature wnwFeature = tileFeatures[WNW];
		for(int i = 11; i >= 3; i--)
		{
			tileFeatures[i] = tileFeatures[i-3];
		}
		tileFeatures[NNE] = wnwFeature;
		tileFeatures[NORTH] = westFeature;
		tileFeatures[NNW] = wswFeature;

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
		if(isPlaced)
			return;
		
		x = p.x;
		y = p.y;
	}
	public void setXY(int x, int y) {
		if(isPlaced)
			return;
		
		this.x = x;
		this.y = y;
	}

	public FeatureEnum getNorthFeatureType() {
		//System.out.println(tileFeatures[1].featureType);
		return tileFeatures[1].featureType;
	}

	public FeatureEnum getSouthFeatureType() {
		//System.out.println(tileFeatures[7].featureType);
		return tileFeatures[7].featureType;
	}

	public FeatureEnum getEastFeatureType() {
		//System.out.println(tileFeatures[4].featureType);
		return tileFeatures[4].featureType;
	}

	public FeatureEnum getWestFeatureType() {
		//System.out.println(tileFeatures[10].featureType);
		return tileFeatures[10].featureType;
	}

	public FeatureEnum getCenterFeature() {
		//System.out.println(tileFeatures[12].featureType);
		return tileFeatures[12].featureType;
	}

	public void placeToken(Token token, int xInTile, int yIntTile) {
		TileFeature featureClicked = getFeatureAt(xInTile, yIntTile);
		featureClicked.setToken(token);
	}

	public TileFeature getFeatureAt(int px, int py) {
		//insert code to check feature map image here
		//using monocolor verion of tile image
		//then return feature at px,py
		
		//for testing just always return the top center feature
		return tileFeatures[1];
	}
	
	public void setPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;
	}
	public boolean isPlaced() {
		return isPlaced;
	}

	//check tile for null features to verify initialization
	public String verifyFeatures()
	{
		String err = "";
		for(int i = 0; i < tileFeatures.length-1; i++ )
			if(tileFeatures[i] == null)
				err += "Error: " + name + " - null tileFeature at " + i + "\n";
		
		return err.equals("") ? null : err;
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
}