package jCarcassonne;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Iterator;

public class Tile
{
	//name of the tile image file
	protected final String name;

	//image for this tile
	private BufferedImage img;
	private BufferedImage imgFeatureMap;
	private int tileWidth;
	private int tileHeight;

	//coordinates of this tile
	private int x = Integer.MAX_VALUE;  //initialized out of range
	private int y = Integer.MAX_VALUE;
	private boolean isPlaced = false;

	//array numbered as clockwise tile borders from top left
	//tileFeatures[12] is center feature, only used for cloister
	private TileFeature[] tileBorders = new TileFeature[13];
	private HashMap<Integer, TileFeature> tileFeatureHash = new HashMap<Integer, TileFeature>();
	private Tile northTile = null, southTile = null, eastTile = null, westTile = null;

	//directional constants
	protected static final int NNW   = 0, NORTH = 1,  NNE   = 2;
	protected static final int ENE   = 3, EAST  = 4,  ESE   = 5;
	protected static final int SSE   = 6, SOUTH = 7,  SSW   = 8;
	protected static final int WSW   = 9, WEST  = 10, WNW   = 11;
	protected static final int CENTER = 12; 

	//constructor
	protected Tile(BufferedImage img, BufferedImage imgFeatureMap, String name)
	{
		this.img = img;
		this.imgFeatureMap = imgFeatureMap;
		this.name = name;
		tileWidth = img.getWidth();
		tileHeight = img.getHeight();
	}	

	//rotate the tile 90 degrees clockwise
	protected void rotate()
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

		for(TileFeature feature : tileFeatureHash.values())
		{
			Point2D tokenCoordinates = (Point2D) feature.getTokenCoordinates();
			tokenCoordinates = at.transform(tokenCoordinates, null);
			feature.setTokenCoordinates((int)(tokenCoordinates.getX()), (int)(tokenCoordinates.getY()));
		}
	}

	//link feature to a tile border, or add center feature at tileFeatures[12]
	protected void addFeature(TileFeature f, int b)
	{
		tileBorders[b] = f;
		tileFeatureHash.put(f.getColorCode(), f);
	}

	//return feature at border b, or center feature at tileFeatures[12]
	protected TileFeature getFeatureAtBorder(int directionalConstant)
	{
		return tileBorders[directionalConstant];
	}

	//uses pixel coordinates to look up color in imgFeatureMap
	//uses that color to look up a tileFeature in the hash table
	protected TileFeature getFeatureAt(int xInTile, int yInTile)
	{
		//if(xInTile >= 0 && xInTile < 128 && yInTile >= 0 && yInTile < 128)
		try
		{
			int rgb = imgFeatureMap.getRGB(xInTile, yInTile) - 0xFF000000; //subtract off the alpha channel
			return tileFeatureHash.get(rgb);
		}
		catch(Exception e)
		{
			System.out.println("TileFeature.getFeatureAt(): Error: tried to get feature at " + xInTile + " " + yInTile);
			System.out.println(e);
			return null;
		}
	}

	protected TileFeature getFeatureByColorCode(int colorCode)
	{
		return tileFeatureHash.get(colorCode);
	}

	protected Iterator<TileFeature> getFeatureIterator()
	{
		return tileFeatureHash.values().iterator();
	}

	protected void setNorthTile(Tile northTile)
	{
		this.northTile = northTile;
		TileFeature sswFeature = northTile.getFeatureAtBorder(SSW);
		TileFeature southFeature = northTile.getFeatureAtBorder(SOUTH);
		TileFeature sseFeature = northTile.getFeatureAtBorder(SSE);
		tileBorders[NNW].addNeighbor(sswFeature);
		tileBorders[NORTH].addNeighbor(southFeature);
		tileBorders[NNE].addNeighbor(sseFeature);
	}

	protected void setSouthTile(Tile southTile)
	{
		this.southTile = southTile;
		TileFeature nnwFeature = southTile.getFeatureAtBorder(NNW);
		TileFeature northFeature = southTile.getFeatureAtBorder(NORTH);
		TileFeature nneFeature = southTile.getFeatureAtBorder(NNE);
		tileBorders[SSW].addNeighbor(nnwFeature);
		tileBorders[SOUTH].addNeighbor(northFeature);
		tileBorders[SSE].addNeighbor(nneFeature);
	}

	protected void setEastTile(Tile eastTile)
	{
		this.eastTile = eastTile;
		TileFeature wnwFeature = eastTile.getFeatureAtBorder(WNW);
		TileFeature westFeature = eastTile.getFeatureAtBorder(WEST);
		TileFeature wswFeature = eastTile.getFeatureAtBorder(WSW);
		tileBorders[ENE].addNeighbor(wnwFeature);
		tileBorders[EAST].addNeighbor(westFeature);
		tileBorders[ESE].addNeighbor(wswFeature);
	}

	protected void setWestTile(Tile westTile)
	{
		this.westTile = westTile;
		TileFeature eneFeature = westTile.getFeatureAtBorder(ENE);
		TileFeature eastFeature = westTile.getFeatureAtBorder(EAST);
		TileFeature eseFeature = westTile.getFeatureAtBorder(ESE);
		tileBorders[WNW].addNeighbor(eneFeature);
		tileBorders[WEST].addNeighbor(eastFeature);
		tileBorders[WSW].addNeighbor(eseFeature);
	}

	public Point getPoint() {
		return new Point(x,y);
	}
	protected void setPoint(Point p)
	{ //convenience method
		if(isPlaced)
			return;

		x = p.x;
		y = p.y;
	}
	protected void setXY(int x, int y)
	{
		if(isPlaced)
			return;

		this.x = x;
		this.y = y;
	}

	protected void placeToken(Token token, int xInTile, int yInTile)
	{
		TileFeature featureClicked = getFeatureAt(xInTile, yInTile);
		featureClicked.placeToken(token);
		token.setFeature(featureClicked);
	}

	protected void setPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;
	}
	protected boolean isPlaced() {
		return isPlaced;
	}

	//return true if any feature has a token
	public boolean hasToken()
	{
		for(TileFeature feature : tileFeatureHash.values())
		{
			if(feature.hasToken())
				return true;
		}

		return false;
	}

	//return first token found or null if none found
	public Token getToken()
	{
		for(TileFeature feature : tileFeatureHash.values())
		{
			if(feature.hasToken())
				return feature.getToken();
		}

		return null;
	}

	public Point getTokenCoordinates()
	{
		for(TileFeature feature : tileFeatureHash.values())
		{
			if(feature.hasToken())
				return feature.getTokenCoordinates();
		}

		return null;
	}

	//check tile for null features to verify initialization
	protected String verifyFeatures()
	{
		String err = "";
		for(int i = 0; i < tileBorders.length-1; i++ )
			if(tileBorders[i] == null)
				err += "Error: " + name + " - null tileFeature at " + i + "\n";

		return err.equals("") ? null : err;
	}

	public BufferedImage getImage()
	{
		return img;
	}

	@Override
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

	protected Tile getNorthTile() {
		return northTile;
	}

	protected Tile getSouthTile() {
		return southTile;
	}

	protected Tile getEastTile() {
		return eastTile;
	}

	protected Tile getWestTile() {
		return westTile;
	}
}
