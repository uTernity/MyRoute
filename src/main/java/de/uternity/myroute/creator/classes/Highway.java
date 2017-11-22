package de.uternity.myroute.creator.classes;

import java.util.ArrayList;
import java.util.List;

public class Highway
{
	private long id;
	private double length;
	private int type;
	private List<Node> wayNodes;
	private byte[] tags;

	public Highway()
	{
		this.wayNodes = new ArrayList<>();
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id) { this.id = id; }

	public double getLength()
	{
		return length;
	}

	public void setLength(double length)
	{
		this.length = length;
	}

	public List<Node> getWayNodes()
	{
		return wayNodes;
	}

	public void setWayNodes(List<Node> wayNodes)
	{
		this.wayNodes = wayNodes;
	}

	public byte[] getTags() { return tags; }

	public void setTags(byte[] tags)
	{
		this.tags = tags;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
}