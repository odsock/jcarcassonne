package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Iterator;

public class Tile
{
	//name of the tile image file
	public final String name;
	
	//image for this tile
	private BufferedImage img;
	private BufferedImage imgFeatureMap;
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

	//array numbered as clockwise tile borders from top left
	//tileFeatures[12] is center feature, only used for cloister
	private TileFeature[] tileBorders = new TileFeature[13];
	private HashMap<Integer, TileFeature> tileFeatureHash = new HashMap<Integer, TileFeature>();
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
	public Tile(BufferedImage img, BufferedImage imgFeatureMap, String name)
	{
		this.img = img;
		this.imgFeatureMap = imgFeatureMap;
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
		imgFeatureMap = bio.filter(imgFeatureMap, null);
	}

	public BufferedImage getImage()
	{
		return img;
	}

	//link feature to a tile border, or add center feature at tileFeatures[12]
	public void addFeature(TileFeature f, int b)
	{
		tileBorders[b] = f;
		tileFeatureHash.put(f.getColorCode(), f);
	}
	
	//return feature at border b, or center feature at tileFeatures[12]
	public TileFeature getFeatureAtBorder(int b)
	{
		return tileBorders[b];
	}

	public void setNorthTile(Tile northTile)
	{
			TileFeature sswFeature = northTile.getFeatureAtBorder(SSW);
			TileFeature southFeature = northTile.getFeatureAtBorder(SOUTH);
			TileFeature sseFeature = northTile.getFeatureAtBorder(SSE);
			tileBorders[NNW].addNeighbor(sswFeature);
			tileBorders[NORTH].addNeighbor(southFeature);
			tileBorders[NNE].addNeighbor(sseFeature);
	}

	public void setSouthTile(Tile southTile)
	{
			TileFeature nnwFeature = southTile.getFeatureAtBorder(NNW);
			TileFeature northFeature = southTile.getFeatureAtBorder(NORTH);
			TileFeature nneFeature = southTile.getFeatureAtBorder(NNE);
			tileBorders[SSW].addNeighbor(nnwFeature);
			tileBorders[SOUTH].addNeighbor(northFeature);
			tileBorders[SSE].addNeighbor(nneFeature);
	}

	public void setEastTile(Tile eastTile)
	{
			TileFeature f11 = eastTile.getFeatureAtBorder(11);
			TileFeature f10 = eastTile.getFeatureAtBorder(10);
			TileFeature f9 = eastTile.getFeatureAtBorder(9);
			tileBorders[ENE].addNeighbor(f11);
			tileBorders[EAST].addNeighbor(f10);
			tileBorders[ESE].addNeighbor(f9);
	}

	public void setWestTile(Tile westTile)
	{
			TileFeature f3 = westTile.getFeatureAtBorder(3);
			TileFeature f4 = westTile.getFeatureAtBorder(4);
			TileFeature f5 = westTile.getFeatureAtBorder(5);
			tileBorders[11].addNeighbor(f3);
			tileBorders[10].addNeighbor(f4);
			tileBorders[9].addNeighbor(f5);
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

	public void placeToken(Token token, int xInTile, int yInTile)
	{
		TileFeature featureClicked = getFeatureAt(xInTile, yInTile);
		featureClicked.setToken(token);
		
		this.tokenX = featureClicked.getTokenCoordinates().x;
		this.tokenY = featureClicked.getTokenCoordinates().y;
		this.hasToken = true;
		this.token = token;
	}

	//uses pixel coordinates to look up color in imgFeatureMap
	//uses that color to look up a tileFeature in the hash table
	public TileFeature getFeatureAt(int xInTile, int yInTile)
	{
		int rgb = imgFeatureMap.getRGB(xInTile, yInTile) - 0xFF000000; //subtract off the alpha channel
		return tileFeatureHash.get(rgb);
	}
	
	public Iterator<TileFeature> getFeatureIterator()
	{
		return tileFeatureHash.values().iterator();
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
