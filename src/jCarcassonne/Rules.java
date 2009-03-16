package jCarcassonne;

public class Rules {
	private boolean verbose = false;

	public void setVerbose(boolean v)
	{
		verbose = v;
	}

	public boolean checkTilePlacement(Landscape l, Tile t, int x, int y)
	{
		if(l.getTile(x, y) != null || t == null)
			return false;

		Tile n = l.getTile(x, y+1);
		Tile s = l.getTile(x, y-1);
		Tile e = l.getTile(x+1, y);
		Tile w = l.getTile(x-1, y);

		if(n  == null && s == null && e == null && w == null)
			return false;
		else if(n != null && t.getNorthFeatureType() != n.getSouthFeatureType())
		{
			if(verbose)
				System.out.println(t.getNorthFeatureType() + " " + n.getSouthFeatureType());
			return false;
		}
		else if(s != null && t.getSouthFeatureType() != s.getNorthFeatureType())
		{
			if(verbose)
				System.out.println(t.getSouthFeatureType() + " " + s.getNorthFeatureType());
			return false;
		}
		else if(e != null && t.getEastFeatureType() != e.getWestFeatureType())
		{
			if(verbose)
				System.out.println(t.getEastFeatureType() + " " + e.getWestFeatureType());
			return false;
		}
		else if(w != null && t.getWestFeatureType() != w.getEastFeatureType())
		{
			if(verbose)
				System.out.println(t.getWestFeatureType() + " " + w.getEastFeatureType());
			return false;
		}
		else
			return true;
	}

	public boolean checkTokenPlacement(Landscape l, Player p, int x, int y)
	{
		if(l.getLastX() == x && l.getLastY() == y)
			return true;
		else
			return false;
	}
}
