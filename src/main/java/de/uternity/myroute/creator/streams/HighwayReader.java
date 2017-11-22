package de.uternity.myroute.creator.streams;

import de.uternity.myroute.creator.classes.Highway;
import de.uternity.myroute.creator.classes.Node;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HighwayReader implements Iterable<Highway>
{
	private DataInputStream in;
	private String path, sig, format;
	private int version;

	public HighwayReader(String path) throws IOException
	{
		this.in = new DataInputStream(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(path)));
		this.path = path;
		readHeader();
	}

	private void readHeader() throws IOException
	{
		byte[] data = new byte[4];
		in.readFully(data);
		sig = new String(data, "ISO-8859-1");
		in.readFully(data);
		format = new String(data, "ISO-8859-1");
		version = in.readByte();
	}

	public void close() throws IOException
	{
		in.close();
	}

	@Override
	public Iterator<Highway> iterator()
	{
		return new Iterator<Highway>()
		{
			@Override
			public void remove()
			{}

			@Override
			public boolean hasNext()
			{
				if (!sig.equals("UTR!") || !format.equals("EXHW"))
				{
					System.err.println("Input file is not a valid highway file");
					return false;
				}

				try
				{
					if (in.available() >= 43)
						return true;
				}

				catch (IOException e)
				{
					e.printStackTrace();
				}

				return false;
			}

			@Override
			public Highway next()
			{
				Highway highway = new Highway();

				try
				{
					highway.setId(in.readLong());

					List<Node> nodes = new ArrayList<>();

					short size = in.readShort();

					for (short i = 0; i < size; i++)
					{
						Node node = new Node();
						node.setId(in.readLong());
						node.setLat(in.readDouble());
						node.setLon(in.readDouble());
						nodes.add(node);
					}

					highway.setWayNodes(nodes);
					//highwayEntry.setType(in.readByte());
					highway.setLength(in.readDouble());
					//highwayEntry.setOneWay(in.readByte());

					byte[] tags = new byte[in.readShort()];
					in.readFully(tags);

					highway.setTags(tags);
				}

				catch (IOException e)
				{
					e.printStackTrace();
				}

				return highway;
			}
		};
	}
}