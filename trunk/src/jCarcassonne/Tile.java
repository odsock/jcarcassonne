package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.HashSet;
import java.util.Iterator;

public class Tile
{
	//name of the tile image file
	public final String name;
	
	//image for this tile
	private BufferedImage img;
	private int tileWidth;
	private int tileHeight;

	//coordinates of this tile
	private int x = Integer.MAX_VALUE;  //initialized out of range
	private int y = Integer.MAX_VALUE;
	private boolean isPlaced = false;

	//token info
	private int tokenX = 0;
	private int tokenY = 0;
	private boolean hasToken = false;
	private Token token = null;

	//references to neighbor tiles
	private Tile northTile = null;
	private Tile southTile = null;
	private Tile eastTile = null;
	private Tile westTile = null;

	//array numbered as clockwise tile borders from top left
	//tileFeatures[12] is center feature, only used for cloister
	private TileFeature[] tileBorders = new TileFeature[13];
	//HashSet of the same tileFeatures, used for iteration in scoring
	private HashSet<TileFeature> tileFeatureSet = new HashSet<TileFeature>();
	//directional border constants
	public static final int NNW   = 0; 
	public static final int NORTH = 1;
	public static final int NNE   = 2;
	public static final int ENE   = 3;
	public static final int EAST  = 4;
	public static final int ESE   = 5;
	public static final int SSE   = 6;
	public static final int SOUTH = 7;
	public static final int SSW   = 8;
	public static final int WSW   = 9;
	public static final int WEST  = 10;
	public static final int WNW   = 11;
	public static final int CENTER = 12; 

	//constructor
	public Tile(BufferedImage img, String name)
	{
		this.img = img;
		this.name = name;
		tileWidth = img.getWidth();
		tileHeight = img.getHeight();
	}	

	//rotate the tile 90 degrees clockwise
	public void rotate()
	{
		//can't rotate a placed tile
		if(isPlaced)
			return;
		
		TileFeature wswFeature = tileBorders[WSW];
		TileFeature westFeature = tileBorders[WEST];
		TileFeature wnwFeature = tileBorders[WNW];
		for(int i = 11; i >= 3; i--)
		{
			tileBorders[i] = tileBorders[i-3];
		}
		tileBorders[NNE] = wnwFeature;
		tileBorders[NORTH] = westFeature;
		tileBorders[NNW] = wswFeature;

		//instantiate and apply affine transformation filter
		AffineTransform at = new AffineTransform();
		at.translate(tileWidth/2, tileHeight/2);
		at.rotate(Math.toRadians(90));
		at.translate(-tileWidth/2, -tileHeight/2);
		BufferedImageOp bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		img = bio.filter(img, null);
	}

	public BufferedImage getImage()
	{
		return img;
	}

	//link feature to a tile border, or add center feature at tileFeatures[12]
	public void addFeature(TileFeature f, int b)
	{
		tileBorders[b] = f;
		tileFeatureSet.add(f);
	}
	
	//return feature at border b, or center feature at tileFeatures[12]
	public TileFeature getFeature(int b)
	{
		return tileBorders[b];
	}

	public void setNorthTile(Tile northTile)
	{
			this.northTile = northTile;
			TileFeature f8 = northTile.getFeature(8);
			TileFeature f7 = northTile.getFeature(7);
			TileFeature f6 = northTile.getFeature(6);
			tileBorders[0].addNeighbor(f8);
			tileBorders[1].addNeighbor(f7);
			tileBorders[2].addNeighbor(f6);
	}

	public void setSouthTile(Tile southTile)
	{
			this.southTile = southTile;
			TileFeature f0 = southTile.getFeature(0);
			TileFeature f1 = southTile.getFeature(1);
			TileFeature f2 = southTile.getFeature(2);
			tileBorders[8].addNeighbor(f0);
			tileBorders[7].addNeighbor(f1);
			tileBorders[6].addNeighbor(f2);
	}

	public void setEastTile(Tile eastTile)
	{
			this.eastTile = eastTile;
			TileFeature f11 = eastTile.getFeature(11);
			TileFeature f10 = eastTile.getFeature(10);
			TileFeature f9 = eastTile.getFeature(9);
			tileBorders[3].addNeighbor(f11);
			tileBorders[4].addNeighbor(f10);
			tileBorders[5].addNeighbor(f9);
	}

	public void setWestTile(Tile westTile)
	{
			this.westTile = westTile;
			TileFeature f3 = westTile.getFeature(3);
			TileFeature f4 = westTile.getFeature(4);
			TileFeature f5 = westTile.getFeature(5);
			tileBorders[11].addNeighbor(f3);
			tileBorders[10].addNeighbor(f4);
			tileBorders[9].addNeighbor(f5);
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
	public void setPoint(Point p)
	{ //convenience method
		if(isPlaced)
			return;
		
		x = p.x;
		y = p.y;
	}
	public void setXY(int x, int y)
	{
		if(isPlaced)
			return;
		
		this.x = x;
		this.y = y;
	}

	public FeatureEnum getNorthFeatureType() {
		return tileBorders[NORTH].featureType;
	}
	public FeatureEnum getSouthFeatureType() {
		return tileBorders[SOUTH].featureType;
	}
	public FeatureEnum getEastFeatureType() {
		return tileBorders[EAST].featureType;
	}
	public FeatureEnum getWestFeatureType() {
		return tileBorders[WEST].featureType;
	}
	public FeatureEnum getCenterFeature() {
		return tileBorders[CENTER].featureType;
	}

	public void placeToken(Token token, int xInTile, int yIntTile)
	{
		TileFeature featureClicked = getFeatureAt(xInTile, yIntTile);
		featureClicked.setToken(token);
		
		this.tokenX = featureClicked.getTokenCoordinates().x;
		this.tokenY = featureClicked.getTokenCoordinates().y;
		this.hasToken = true;
		this.token = token;
	}

	public TileFeature getFeatureAt(int px, int py)
	{
		//insert code to check feature map image here
		//using monocolor verion of tile image
		//then return feature at px,py
		
		//for testing just always return the top center feature
		return tileBorders[NORTH];
	}
	
	public Iterator<TileFeature> getFeatureIterator()
	{
		return tileFeatureSet.iterator();
	}
	
	public void setPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;
	}
	public boolean isPlaced() {
		return isPlaced;
	}

	public void setToken(boolean hasToken) {
		this.hasToken = hasToken;
	}
	public boolean hasToken() {
		return hasToken;
	}
	public Token getToken() {
		return token;
	}
	public int getTokenX() {
		return tokenX;
	}
	public int getTokenY() {
		return tokenY;
	}
	//check tile for null features to verify initialization
	public String verifyFeatures()
	{
		String err = "";
		for(int i = 0; i < tileBorders.length-1; i++ )
			if(tileBorders[i] == null)
				err += "Error: " + name + " - null tileFeature at " + i + "\n";
		
		return err.equals("") ? null : err;
	}

	public String toString()
	{
		String s = "";
		for(TileFeature f : tileBorders)
		{
			if(f != null)
				s += f.toString() + " ";
		}

		return name + " " + x + " " + y + " : " + s;
	}
}
