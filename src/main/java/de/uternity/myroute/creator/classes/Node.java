package de.uternity.myroute.creator.classes;

public class Node
{
	private long id;
	private double lat;
	private double lon;
	private byte[] tags;

	public Node(){}

	public Node(long id, double lat, double lon, byte[] tags)
	{
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.tags = tags;
	}

	public long getId()
	{
		return id;
	}

	public double getLat()
	{
		return lat;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public void setLat(double lat)
	{
		this.lat = lat;
	}

	public void setLon(double lon)
	{
		this.lon = lon;
	}

	public void setTags(byte[] tags)
	{
		this.tags = tags;
	}

	public double getLon()
	{
		return lon;
	}

	public byte[] getTags()
	{
		return tags;
	}
}