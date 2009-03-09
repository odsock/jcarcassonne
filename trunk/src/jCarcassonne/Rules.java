package jCarcassonne;

public class Rules {
	public static boolean checkTilePlacement(Landscape l, Tile t, int x, int y)
	{
		if(l.getTile(x, y) != null || t == null)
			return false;

		Tile n = l.getTile(x, y+1);
		Tile s = l.getTile(x, y-1);
		Tile e = l.getTile(x+1, y);
		Tile w = l.getTile(x-1, y);

		if(n  == null && s == null && e == null && w == null)
			return false;
		else if(n != null && t.getNorthFeature() != n.getSouthFeature())
			return false;
		else if(s != null && t.getSouthFeature() != s.getNorthFeature())
			return false;
		else if(e != null && t.getEastFeature() != e.getWestFeature())
			return false;
		else if(w != null && t.getWestFeature() != w.getEastFeature())
			return false;
		else
			return true;
	}
	
	public static boolean checkTokenPlacement(Landscape l, Player p, int x, int y)
	{
		//stub - complete later
		return true;
	}
}
