package jCarcassonne;

import java.awt.Point;
import java.util.Map;

public class Landscape {
	private Tile startTile;
	private Map<Point,Tile> landscape;

	public Landscape(Tile startTile){
		landscape.put(new Point(0,0), startTile);
		this.startTile = startTile;
	}
	
	public Tile getStartTile(){
		return startTile;
	}
	
	public Tile getTile(Tile t, Point p){
		return landscape.get(p);
	}

	public void placeTile(Tile t, Point p){
		//add to coordinate map
		landscape.put(p, t);

		//setup edge references
		p.translate(0,1);
		if(landscape.containsKey(p)){
			t.setNorthTile(landscape.get(p));
			t.getNorthTile().setSouthTile(t);
		}
		p.translate(0,-2);
		if(landscape.containsKey(p)){
			t.setSouthTile(landscape.get(p));
			t.getSouthTile().setNorthTile(t);
		}
		p.translate(1,1);
		if(landscape.containsKey(p)){
			t.setEastTile(landscape.get(p));
			t.getEastTile().setWestTile(t);
		}
		p.translate(-2,0);
		if(landscape.containsKey(p)){
			t.setWestTile(landscape.get(p));
			t.getWestTile().setEastTile(t);
		}
	}
}