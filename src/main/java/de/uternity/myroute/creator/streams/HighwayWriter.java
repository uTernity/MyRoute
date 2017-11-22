package de.uternity.myroute.creator.streams;

import de.uternity.myroute.creator.classes.Highway;
import de.uternity.myroute.creator.classes.Node;

import java.io.*;

public class HighwayWriter
{
	private DataOutputStream os;

	public HighwayWriter(String path, boolean append) throws IOException
	{
		if (path.contains("/"))
			new File(path.substring(0, path.lastIndexOf("/"))).mkdirs();

		if (!append)
			new File(path).delete();

		this.os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path, append)));

		if (new File(path).length() == 0)
			writeHeader();
	}

	private void writeHeader() throws IOException
	{
		os.write(new byte[]{0x55, 0x54, 0x52, 0x21});
		os.write(new byte[]{0x45, 0x58, 0x48, 0x57});
		os.write(0x01);
	}

	public void writeHighway(Highway highway) throws IOException
	{
		os.writeLong(highway.getId());
		os.writeShort(highway.getWayNodes().size());

		for (Node node : highway.getWayNodes())
		{
			os.writeLong(node.getId());
			os.writeDouble(node.getLat());
			os.writeDouble(node.getLon());
		}

		//os.writeByte(highwayEntry.getType());
		os.writeDouble(highway.getLength());
		//os.writeByte(highwayEntry.getOneWay());
		os.writeShort(highway.getTags().length);
		os.write(highway.getTags());
	}

	public void flush() throws IOException
	{
		os.flush();
	}

	public void close() throws IOException
	{
		os.close();
	}
}
